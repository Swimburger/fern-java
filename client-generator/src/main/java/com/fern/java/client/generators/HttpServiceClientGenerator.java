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

import com.fern.ir.v3.model.errors.DeclaredErrorName;
import com.fern.ir.v3.model.services.http.HttpEndpoint;
import com.fern.ir.v3.model.services.http.HttpRequestBodyReference;
import com.fern.ir.v3.model.services.http.HttpService;
import com.fern.ir.v3.model.services.http.SdkRequestShape;
import com.fern.ir.v3.model.services.http.SdkRequestWrapper;
import com.fern.ir.v3.model.types.DeclaredTypeName;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.client.GeneratedServiceClient;
import com.fern.java.client.GeneratedWrappedRequest;
import com.fern.java.client.generators.endpoint.ClientAuthFieldSpec;
import com.fern.java.client.generators.endpoint.NoRequestEndpointWriter;
import com.fern.java.client.generators.endpoint.OnlyRequestEndpointWriter;
import com.fern.java.client.generators.endpoint.WrappedRequestEndpointWriter;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.output.GeneratedAuthFiles;
import com.fern.java.output.GeneratedJavaFile;
import com.fern.java.output.GeneratedJavaInterface;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import okhttp3.OkHttpClient;

public final class HttpServiceClientGenerator extends AbstractFileGenerator {
    private static final String AUTH_FIELD_NAME = "auth";
    private static final String OPTIONS_FIELD_NAME = "options";
    private static final String URL_FIELD_NAME = "url";
    private final HttpService httpService;
    private final Optional<GeneratedAuthFiles> maybeAuth;
    private final Map<DeclaredErrorName, GeneratedJavaFile> generatedErrors;
    private final ClientGeneratorContext clientGeneratorContext;
    private final GeneratedObjectMapper objectMapper;
    private final boolean atleastOneEndpointHasAuth;
    private final Optional<GeneratedClientOptions> generatedClientOptionsClass;
    private final Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces;

    public HttpServiceClientGenerator(
            ClientGeneratorContext clientGeneratorContext,
            HttpService httpService,
            Map<DeclaredErrorName, GeneratedJavaFile> generatedErrors,
            Optional<GeneratedAuthFiles> maybeAuth,
            Optional<GeneratedClientOptions> generatedClientOptionsClass,
            Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces,
            GeneratedObjectMapper objectMapper) {
        super(
                clientGeneratorContext.getPoetClassNameFactory().getServiceClientClassname(httpService),
                clientGeneratorContext);
        this.httpService = httpService;
        this.generatedErrors = generatedErrors;
        this.clientGeneratorContext = clientGeneratorContext;
        this.maybeAuth = maybeAuth;
        this.atleastOneEndpointHasAuth = httpService.getEndpoints().stream().anyMatch(HttpEndpoint::getAuth);
        this.objectMapper = objectMapper;
        this.generatedClientOptionsClass = generatedClientOptionsClass;
        this.allGeneratedInterfaces = allGeneratedInterfaces;
    }

    @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:MethodLength"})
    @Override
    public GeneratedServiceClient generateFile() {
        FieldSpec okHttp3Field = FieldSpec.builder(
                        OkHttpClient.class,
                        ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME,
                        Modifier.PRIVATE,
                        Modifier.FINAL)
                .build();
        FieldSpec urlField = FieldSpec.builder(String.class, URL_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .build();
        TypeSpec.Builder serviceClientBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(okHttp3Field)
                .addField(urlField)
                .addFields(generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters().stream()
                        .map(parameterSpec -> FieldSpec.builder(parameterSpec.type, parameterSpec.name)
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build())
                        .collect(Collectors.toList()))
                .addMethod(getUrlConstructor());

        if (generatedClientOptionsClass.isPresent()) {
            serviceClientBuilder.addField(FieldSpec.builder(
                            generatedClientOptionsClass.get().getClassName(),
                            OPTIONS_FIELD_NAME,
                            Modifier.PRIVATE,
                            Modifier.FINAL)
                    .build());
            getUrlOptionsConstructor(generatedClientOptionsClass.get());
        }

        Optional<ClientAuthFieldSpec> maybeAuthField = Optional.empty();
        if (maybeAuth.isPresent() && atleastOneEndpointHasAuth) {
            FieldSpec authField = FieldSpec.builder(
                            ParameterizedTypeName.get(
                                    ClassName.get(Optional.class),
                                    maybeAuth.get().getClassName()),
                            AUTH_FIELD_NAME,
                            Modifier.PRIVATE,
                            Modifier.FINAL)
                    .build();
            maybeAuthField = Optional.of(new ClientAuthFieldSpec(maybeAuth.get().authScheme(), authField));
            serviceClientBuilder.addField(authField);
            MethodSpec urlAuthConstructor = getUrlAuthConstructor(maybeAuth.get());
            serviceClientBuilder.addMethod(urlAuthConstructor);
            generatedClientOptionsClass.ifPresent(
                    generatedClientOptions -> getUrlAuthOptionsConstructor(maybeAuth.get(), generatedClientOptions));
        }

        Optional<ClientAuthFieldSpec> maybeAuthField2 = maybeAuthField;
        List<GeneratedWrappedRequest> generatedEndpointRequests = new ArrayList<>();
        for (HttpEndpoint httpEndpoint : httpService.getEndpoints()) {
            MethodSpec endpointMethodSpec;
            if (httpEndpoint.getSdkRequest().isPresent()) {
                endpointMethodSpec = httpEndpoint
                        .getSdkRequest()
                        .get()
                        .getShape()
                        .visit(new SdkRequestShape.Visitor<MethodSpec>() {
                            @Override
                            public MethodSpec visitJustRequestBody(HttpRequestBodyReference justRequestBody) {
                                OnlyRequestEndpointWriter onlyRequestEndpointWriter = new OnlyRequestEndpointWriter(
                                        httpService,
                                        httpEndpoint,
                                        okHttp3Field,
                                        urlField,
                                        objectMapper,
                                        clientGeneratorContext,
                                        generatedClientOptionsClass,
                                        maybeAuthField2);
                                return onlyRequestEndpointWriter.generate();
                            }

                            @Override
                            public MethodSpec visitWrapper(SdkRequestWrapper wrapper) {
                                WrappedRequestGenerator wrappedRequestGenerator = new WrappedRequestGenerator(
                                        wrapper,
                                        httpService,
                                        httpEndpoint,
                                        clientGeneratorContext
                                                .getPoetClassNameFactory()
                                                .getRequestWrapperBodyClassName(httpService, wrapper),
                                        allGeneratedInterfaces,
                                        clientGeneratorContext);
                                GeneratedWrappedRequest generatedWrappedRequest =
                                        wrappedRequestGenerator.generateFile();
                                WrappedRequestEndpointWriter wrappedRequestEndpointWriter =
                                        new WrappedRequestEndpointWriter(
                                                httpService,
                                                httpEndpoint,
                                                okHttp3Field,
                                                urlField,
                                                objectMapper,
                                                clientGeneratorContext,
                                                generatedClientOptionsClass,
                                                maybeAuthField2,
                                                httpEndpoint.getSdkRequest().get(),
                                                generatedWrappedRequest);
                                return wrappedRequestEndpointWriter.generate();
                            }

                            @Override
                            public MethodSpec _visitUnknown(Object unknownType) {
                                return null;
                            }
                        });
            } else {
                NoRequestEndpointWriter noRequestEndpointWriter = new NoRequestEndpointWriter(
                        httpService,
                        httpEndpoint,
                        okHttp3Field,
                        urlField,
                        objectMapper,
                        clientGeneratorContext,
                        generatedClientOptionsClass,
                        maybeAuthField2);
                endpointMethodSpec = noRequestEndpointWriter.generate();
            }

            // endpointMethodBuilder.addExceptions(generatedEndpointMethod.methodSpec().exceptions);
            // if (!generatedEndpointMethod.methodSpec().exceptions.isEmpty()) {
            //     for (TypeName exception : generatedEndpointMethod.methodSpec().exceptions) {
            //         endpointMethodBuilder.addJavadoc(JavaDocUtils.getThrowsJavadoc(
            //                 exception, "Exception that wraps all possible endpoint errors"));
            //     }
            // }
            // endpointMethodBuilder.returns(generatedEndpointMethod.methodSpec().returnType);
            // if (httpEndpoint.getResponse().getDocs().isPresent()) {
            //     endpointMethodBuilder.addJavadoc(JavaDocUtils.getReturnDocs(
            //             httpEndpoint.getResponse().getDocs().get()));
            // } else if (httpEndpoint.getResponse().getTypeV2().isPresent()) {
            //     endpointMethodBuilder.addJavadoc("@return $T", generatedEndpointMethod.methodSpec().returnType);
            // }

            serviceClientBuilder.addMethod(endpointMethodSpec);
        }

        TypeSpec serviceClientTypeSpec = serviceClientBuilder.build();
        JavaFile serviceClientJavaFile =
                JavaFile.builder(className.packageName(), serviceClientTypeSpec).build();

        return GeneratedServiceClient.builder()
                .className(className)
                .javaFile(serviceClientJavaFile)
                .httpService(httpService)
                .addAllGeneratedEndpointRequestOutputs(generatedEndpointRequests)
                .build();
    }

    private MethodSpec getUrlConstructor() {
        if (generatedClientOptionsClass.isPresent()) {
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(OkHttpClient.class, ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                    .addParameter(String.class, "url")
                    .addParameters(generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters());
            constructorBuilder.addStatement(
                    "this($L, $T.builder().build())",
                    constructorBuilder.parameters.stream()
                            .map(parameterSpec -> parameterSpec.name)
                            .collect(Collectors.joining(", ")),
                    generatedClientOptionsClass.get());
            return constructorBuilder.build();
        }
        return withUrlConstructorBuilder().build();
    }

    private MethodSpec getUrlOptionsConstructor(GeneratedClientOptions generatedClientOptions) {
        return withUrlConstructorBuilder()
                .addParameter(generatedClientOptions.getClassName(), OPTIONS_FIELD_NAME)
                .addStatement("this.$L = $L", OPTIONS_FIELD_NAME, OPTIONS_FIELD_NAME)
                .build();
    }

    private MethodSpec.Builder withUrlConstructorBuilder() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(OkHttpClient.class, ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                .addParameter(String.class, "url")
                .addParameters(generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters())
                .addStatement(
                        "this.$L = $L",
                        ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME,
                        ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                .addStatement("this.$L = $L", URL_FIELD_NAME, URL_FIELD_NAME);
        for (ParameterSpec headerParameter : generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters()) {
            constructorBuilder.addStatement("this.$L = $L", headerParameter.name, headerParameter.name);
        }
        if (maybeAuth.isPresent() && atleastOneEndpointHasAuth) {
            constructorBuilder
                    .addStatement("this.$L = $T.empty()", AUTH_FIELD_NAME, Optional.class)
                    .build();
        }
        return constructorBuilder;
    }

    private MethodSpec getUrlAuthConstructor(GeneratedAuthFiles auth) {
        if (generatedClientOptionsClass.isPresent()) {
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(OkHttpClient.class, ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                    .addParameter(String.class, "url")
                    .addParameter(auth.getClassName(), AUTH_FIELD_NAME)
                    .addParameters(generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters());
            constructorBuilder.addStatement(
                    "this($L, $L, $T.builder().build())",
                    constructorBuilder.parameters.stream()
                            .map(parameterSpec -> parameterSpec.name)
                            .collect(Collectors.joining(", ")),
                    generatedClientOptionsClass.get());
            return constructorBuilder.build();
        }
        return withUrlAuthConstructor(auth).build();
    }

    private MethodSpec getUrlAuthOptionsConstructor(
            GeneratedAuthFiles auth, GeneratedClientOptions generatedClientOptions) {
        return withUrlAuthConstructor(auth)
                .addParameter(generatedClientOptions.getClassName(), OPTIONS_FIELD_NAME)
                .addStatement("this.$L = $L", OPTIONS_FIELD_NAME, OPTIONS_FIELD_NAME)
                .build();
    }

    private MethodSpec.Builder withUrlAuthConstructor(GeneratedAuthFiles auth) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(OkHttpClient.class, ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                .addParameter(String.class, "url")
                .addParameter(auth.getClassName(), AUTH_FIELD_NAME)
                .addParameters(generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters())
                .addStatement(
                        "this.$L = $L",
                        ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME,
                        ClientWrapperGenerator.OKHTTP3_CLIENT_FIELD_NAME)
                .addStatement("this.$L = $L", URL_FIELD_NAME, URL_FIELD_NAME)
                .addStatement("this.$L = $T.of($L)", AUTH_FIELD_NAME, Optional.class, AUTH_FIELD_NAME);
        for (ParameterSpec headerParameter : generatorContext.getGlobalHeaders().getRequiredGlobalHeaderParameters()) {
            constructorBuilder.addStatement("this.$L = $L", headerParameter.name, headerParameter.name);
        }
        return constructorBuilder;
    }
}
