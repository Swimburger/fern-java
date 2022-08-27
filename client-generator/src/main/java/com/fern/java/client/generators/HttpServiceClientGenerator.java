/*
 * (c) Copyright 2022 Birch Solutions Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fern.java.client.generators;

import com.fern.ir.model.errors.DeclaredErrorName;
import com.fern.ir.model.services.http.HttpEndpoint;
import com.fern.ir.model.services.http.HttpService;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedEndpointRequestOutput;
import com.fern.java.client.GeneratedJerseyServiceInterfaceOutput;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.output.AbstractGeneratedFileOutput;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public final class HttpServiceClientGenerator extends AbstractFileGenerator {

    private static final String SERVICE_FIELD_NAME = "service";
    private static final String AUTH_FIELD_NAME = "auth";
    private static final String REQUEST_PARAMETER_NAME = "request";

    private final HttpService httpService;
    private final Map<DeclaredErrorName, AbstractGeneratedFileOutput> generatedErrors;
    private final JerseyServiceInterfaceGenerator jerseyServiceInterfaceGenerator;
    private final ClientGeneratorContext clientGeneratorContext;

    public HttpServiceClientGenerator(
            ClientGeneratorContext clientGeneratorContext,
            HttpService httpService,
            Map<DeclaredErrorName, AbstractGeneratedFileOutput> generatedErrors) {
        super(
                clientGeneratorContext.getPoetClassNameFactory().getServiceClientClassname(httpService),
                clientGeneratorContext);
        this.httpService = httpService;
        this.generatedErrors = generatedErrors;
        this.clientGeneratorContext = clientGeneratorContext;
    }

    @Override
    public AbstractGeneratedFileOutput generateFile() {
        JerseyServiceInterfaceGenerator jerseyServiceInterfaceGenerator = new JerseyServiceInterfaceGenerator(
                clientGeneratorContext, generatedErrors, Optional.empty(), httpService);
        GeneratedJerseyServiceInterfaceOutput jerseyServiceInterfaceOutput =
                jerseyServiceInterfaceGenerator.generateFile();
        TypeSpec.Builder serviceClientBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(
                                jerseyServiceInterfaceOutput.getClassName(),
                                SERVICE_FIELD_NAME,
                                Modifier.PRIVATE,
                                Modifier.FINAL)
                        .build())
                .addMethod(getUrlConstructor(jerseyServiceInterfaceOutput));
        boolean atleastOneEndpointHasAuth = httpService.getEndpoints().stream().anyMatch(HttpEndpoint::getAuth);
        if (atleastOneEndpointHasAuth) {
            // add auth field
            // add url + auth constructor
        }
        for (HttpEndpoint httpEndpoint : httpService.getEndpoints()) {
            MethodSpec endpointInterfaceMethod =
                    jerseyServiceInterfaceOutput.endpointMethods().get(httpEndpoint.getId());
            Optional<GeneratedEndpointRequestOutput> wrappedRequestFile =
                    getWrappedRequest(httpEndpoint, endpointInterfaceMethod);
            MethodSpec.Builder endpointMethodBuilder =
                    MethodSpec.methodBuilder(httpEndpoint.getId().get()).addModifiers(Modifier.PUBLIC);

            if (wrappedRequestFile.isPresent()) {

            } else {
                generateCallWithoutRequest(endpointMethodBuilder, endpointInterfaceMethod);
            }

            endpointMethodBuilder.addExceptions(endpointInterfaceMethod.exceptions);
            endpointMethodBuilder.returns(endpointInterfaceMethod.returnType);
            serviceClientBuilder.addMethod(endpointMethodBuilder.build());
        }

        return null;
    }

    @Override
    public GeneratedHttpServiceClient generate() {
        HttpServiceInterfaceGenerator httpServiceInterfaceGenerator = new HttpServiceInterfaceGenerator(
                generatorContext, generatedEndpointModels, generatedErrors, maybeGeneratedAuthSchemes, httpService);
        GeneratedHttpServiceInterface generatedHttpServiceInterface = httpServiceInterfaceGenerator.generate();
        TypeSpec.Builder serviceClientBuilder = TypeSpec.classBuilder(generatedServiceClientClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(
                                generatedHttpServiceInterface.className(),
                                SERVICE_FIELD_NAME,
                                Modifier.PRIVATE,
                                Modifier.FINAL)
                        .build());
        maybeGeneratedAuthSchemes.ifPresent(generatedAuthSchemes -> serviceClientBuilder.addField(FieldSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(Optional.class), generatedAuthSchemes.className()),
                        AUTH_FIELD_NAME,
                        Modifier.PRIVATE,
                        Modifier.FINAL)
                .build()));
        serviceClientBuilder.addMethod(getUrlConstructor(generatedHttpServiceInterface));
        maybeGeneratedAuthSchemes.ifPresent(generatedAuthSchemes -> serviceClientBuilder.addMethod(
                getUrlAuthConstructor(generatedHttpServiceInterface, generatedAuthSchemes)));

        for (HttpEndpoint httpEndpoint : httpService.getEndpoints()) {
            Optional<GeneratedEndpointClient> endpointFile =
                    generatedHttpServiceInterface.endpointFiles().get(httpEndpoint.getId());
            MethodSpec interfaceMethod =
                    generatedHttpServiceInterface.endpointMethods().get(httpEndpoint.getId());
            MethodSpec.Builder endpointMethodBuilder =
                    MethodSpec.methodBuilder(httpEndpoint.getId().get()).addModifiers(Modifier.PUBLIC);

            generateCallWithWrappedRequest(
                    httpEndpoint,
                    interfaceMethod,
                    endpointFile.map(GeneratedEndpointClient::generatedNestedRequest),
                    endpointMethodBuilder);

            endpointMethodBuilder.addExceptions(interfaceMethod.exceptions);
            endpointMethodBuilder.returns(interfaceMethod.returnType);
            serviceClientBuilder.addMethod(endpointMethodBuilder.build());
        }

        TypeSpec serviceClientTypeSpec = serviceClientBuilder.build();
        JavaFile serviceClientJavaFile = JavaFile.builder(
                        generatedServiceClientClassName.packageName(), serviceClientTypeSpec)
                .build();
        return GeneratedHttpServiceClient.builder()
                .file(serviceClientJavaFile)
                .className(generatedServiceClientClassName)
                .serviceInterface(generatedHttpServiceInterface)
                .build();
    }

    private MethodSpec getUrlConstructor(GeneratedJerseyServiceInterfaceOutput jerseyServiceInterfaceOutput) {
        MethodSpec.Builder constructorBuilder =
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameter(String.class, "url");
        constructorBuilder.addStatement(
                "this.$L = $T.$L($L)",
                SERVICE_FIELD_NAME,
                jerseyServiceInterfaceOutput.getClassName(),
                JerseyServiceInterfaceGenerator.GET_CLIENT_METHOD_NAME,
                "url");
        // if (maybeGeneratedAuthSchemes.isPresent()) {
        //     constructorBuilder
        //             .addStatement("this.$L = $T.empty()", AUTH_FIELD_NAME, Optional.class)
        //             .build();
        // }
        return constructorBuilder.build();
    }

    // private MethodSpec getUrlAuthConstructor(
    //         GeneratedJerseyServiceInterfaceOutput jerseyServiceInterfaceOutput, GeneratedAuthSchemes
    // generatedAuthSchemes) {
    //     return MethodSpec.constructorBuilder()
    //             .addModifiers(Modifier.PUBLIC)
    //             .addParameter(String.class, "url")
    //             .addParameter(generatedAuthSchemes.className(), AUTH_FIELD_NAME)
    //             .addStatement(
    //                     "this.$L = $T.$L($L)",
    //                     SERVICE_FIELD_NAME,
    //                     generatedHttpServiceInterface.className(),
    //                     HttpServiceInterfaceGenerator.GET_CLIENT_METHOD_NAME,
    //                     "url")
    //             .addStatement("this.$L = $T.of($L)", AUTH_FIELD_NAME, Optional.class, AUTH_FIELD_NAME)
    //             .build();
    // }

    private void generateCallWithoutRequest(MethodSpec.Builder endpointMethodBuilder, MethodSpec interfaceMethod) {
        endpointMethodBuilder.addStatement("this.$L.$L()", SERVICE_FIELD_NAME, interfaceMethod.name);
        return;
    }

    private void generateCallWithWrappedRequest(
            HttpEndpoint httpEndpoint,
            MethodSpec interfaceMethod,
            GeneratedEndpointRequestOutput generatedRequest,
            MethodSpec.Builder endpointMethodBuilder) {
        endpointMethodBuilder.addParameter(
                ParameterSpec.builder(generatedRequest.requestClassName(), REQUEST_PARAMETER_NAME)
                        .build());
        String args;
        // if (httpEndpoint.getAuth() && maybeGeneratedAuthSchemes.isPresent()) {
        //     GeneratedAuthSchemes generatedAuthSchemes = maybeGeneratedAuthSchemes.get();
        //     endpointMethodBuilder.addStatement(
        //             "$T authValue = $L.$L().orElseGet(() -> this.$L.orElseThrow(() -> new $T($S)))",
        //             maybeGeneratedAuthSchemes.get().className(),
        //             REQUEST_PARAMETER_NAME,
        //             generatedRequestInfo.authMethodSpec().get().name,
        //             AUTH_FIELD_NAME,
        //             RuntimeException.class,
        //             "Auth is required for " + httpEndpoint.getId().get());
        //     List<String> argTokens = new ArrayList<>();
        //     if (generatedAuthSchemes.generatedAuthSchemes().size() == 1) {
        //         argTokens.add("authValue");
        //     } else if (generatorContext.getApiAuth().getRequirement().equals(AuthSchemesRequirement.ALL)) {
        //         generatedAuthSchemes.generatedAuthSchemes().forEach(((authScheme, generatedFile) -> {
        //             argTokens.add("authValue." + AuthSchemeUtils.getAuthSchemeCamelCaseName(authScheme) + "()");
        //         }));
        //     } else if (generatorContext.getApiAuth().getRequirement().equals(AuthSchemesRequirement.ANY)) {
        //         generatedAuthSchemes
        //                 .generatedAuthSchemes()
        //                 .forEach((authScheme, generatedFile) ->
        //                         argTokens.add("authValue." + AnyAuthGenerator.GET_AUTH_PREFIX
        //                                 + AuthSchemeUtils.getAuthSchemePascalCaseName(authScheme) + "()"));
        //     }
        //     for (int i = generatedAuthSchemes.generatedAuthSchemes().size();
        //             i < generatedRequestInfo.enrichedObjectProperty().size();
        //             ++i) {
        //         MethodSpec propertyMethodSpec =
        //                 generatedRequestInfo.enrichedObjectProperty().get(i).getterProperty();
        //         argTokens.add(REQUEST_PARAMETER_NAME + "." + propertyMethodSpec.name + "()");
        //     }
        //     args = argTokens.stream().collect(Collectors.joining(", "));
        // } else {
        //
        // }
        args = generatedRequest.enrichedObjectProperty().stream()
                .map(enrichedObjectProperty ->
                        REQUEST_PARAMETER_NAME + "." + enrichedObjectProperty.getterProperty().name + "()")
                .collect(Collectors.joining(", "));
        String codeBlockFormat = "this.$L.$L(" + args + ")";
        if (interfaceMethod.returnType.equals(TypeName.VOID)) {
            endpointMethodBuilder.addStatement(codeBlockFormat, SERVICE_FIELD_NAME, interfaceMethod.name);
        } else {
            endpointMethodBuilder.addStatement("return " + codeBlockFormat, SERVICE_FIELD_NAME, interfaceMethod.name);
        }
    }

    private Optional<GeneratedEndpointRequestOutput> getWrappedRequest(
            HttpEndpoint httpEndpoint, MethodSpec endpointInterfaceMethod) {
        if (endpointInterfaceMethod.parameters.isEmpty()) {
            return Optional.empty();
        }
        HttpEndpointFileGenerator httpEndpointFileGenerator = new HttpEndpointFileGenerator(
                clientGeneratorContext, httpService, httpEndpoint, endpointInterfaceMethod.parameters, generatedErrors);
        return Optional.of(httpEndpointFileGenerator.generateFile());
    }
}
