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

package com.fern.jersey.client;

import com.fern.codegen.GeneratedEndpointClient;
import com.fern.codegen.GeneratedEndpointClient.GeneratedRequestInfo;
import com.fern.codegen.GeneratedEndpointModel;
import com.fern.codegen.GeneratedError;
import com.fern.codegen.GeneratedHttpServiceClient;
import com.fern.codegen.GeneratedHttpServiceInterface;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.java.immutables.StagedBuilderImmutablesStyle;
import com.fern.jersey.JerseyServiceGeneratorUtils;
import com.fern.model.codegen.Generator;
import com.fern.types.ErrorName;
import com.fern.types.services.EndpointId;
import com.fern.types.services.HttpEndpoint;
import com.fern.types.services.HttpService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

public final class HttpServiceClientGenerator extends Generator {

    private static final String BUILD_STAGE_SUFFIX = "BuildStage";
    private static final String STATIC_BUILDER_METHOD_NAME = "builder";
    private static final String SERVICE_FIELD_NAME = "service";
    private static final String CLIENT_SUFFIX = "Client";
    private static final String REQUEST_CLASS_NAME = "Request";

    private static final String REQUEST_PARAMETER_NAME = "request";

    private final HttpService httpService;

    private final ClassName generatedServiceClientClassName;

    private final Map<EndpointId, GeneratedEndpointModel> generatedEndpointModels;

    private final Map<ErrorName, GeneratedError> generatedErrors;

    private final JerseyServiceGeneratorUtils jerseyServiceGeneratorUtils;

    public HttpServiceClientGenerator(
            GeneratorContext generatorContext,
            HttpService httpService,
            Map<EndpointId, GeneratedEndpointModel> generatedEndpointModels,
            Map<ErrorName, GeneratedError> generatedErrors) {
        super(generatorContext);
        this.httpService = httpService;
        this.generatedEndpointModels = generatedEndpointModels;
        this.generatedServiceClientClassName = generatorContext
                .getClassNameUtils()
                .getClassNameFromServiceName(httpService.name(), CLIENT_SUFFIX, PackageType.CLIENT);
        this.generatedErrors = generatedErrors;
        this.jerseyServiceGeneratorUtils = new JerseyServiceGeneratorUtils(generatorContext);
    }

    @Override
    public GeneratedHttpServiceClient generate() {
        HttpServiceInterfaceGenerator httpServiceInterfaceGenerator = new HttpServiceInterfaceGenerator(
                generatorContext, generatedEndpointModels, generatedErrors, httpService);
        GeneratedHttpServiceInterface generatedHttpServiceInterface = httpServiceInterfaceGenerator.generate();
        TypeSpec.Builder serviceClientBuilder = TypeSpec.classBuilder(generatedServiceClientClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(
                                generatedHttpServiceInterface.className(),
                                SERVICE_FIELD_NAME,
                                Modifier.PRIVATE,
                                Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "url")
                        .addStatement(
                                "this.$L = $T.$L($L)",
                                SERVICE_FIELD_NAME,
                                generatedHttpServiceInterface.className(),
                                HttpServiceInterfaceGenerator.GET_CLIENT_METHOD_NAME,
                                "url")
                        .build());

        List<GeneratedEndpointClient> endpointFiles = new ArrayList<>();
        for (HttpEndpoint httpEndpoint : httpService.endpoints()) {
            MethodSpec interfaceMethod =
                    generatedHttpServiceInterface.endpointMethods().get(httpEndpoint.endpointId());

            GeneratedEndpointClient generatedEndpointFile = generateEndpointFile(httpEndpoint);
            endpointFiles.add(generatedEndpointFile);

            MethodSpec.Builder endpointMethodBuilder =
                    MethodSpec.methodBuilder(httpEndpoint.endpointId().value()).addModifiers(Modifier.PUBLIC);

            GeneratedEndpointModel generatedEndpointModel = generatedEndpointModels.get(httpEndpoint.endpointId());
            jerseyServiceGeneratorUtils
                    .getPayloadTypeName(generatedEndpointModel.generatedHttpResponse())
                    .ifPresent(endpointMethodBuilder::returns);
            List<ClassName> errorClassNames = httpEndpoint.errors().value().stream()
                    .map(responseError ->
                            generatedErrors.get(responseError.error()).className())
                    .collect(Collectors.toList());
            endpointMethodBuilder
                    .addParameter(ParameterSpec.builder(
                                    generatedEndpointFile.generatedRequestInfo().requestClassName(),
                                    REQUEST_PARAMETER_NAME)
                            .build())
                    .addExceptions(errorClassNames);
            String args = generatedEndpointFile.generatedRequestInfo().propertyMethodSpecs().stream()
                    .map(requestMethodSpec -> REQUEST_PARAMETER_NAME + "." + requestMethodSpec.name + "()")
                    .collect(Collectors.joining(", "));
            CodeBlock methodCodeBlock = CodeBlock.builder()
                    .addStatement("this.$L.$L(" + args + ")", SERVICE_FIELD_NAME, interfaceMethod.name)
                    .build();

            endpointMethodBuilder.addCode(methodCodeBlock);
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
                .addAllEndpointFiles(endpointFiles)
                .build();
    }

    private GeneratedEndpointClient generateEndpointFile(HttpEndpoint httpEndpoint) {
        ClassName endpointClassName = generatorContext
                .getClassNameUtils()
                .getClassNameFromEndpointId(httpService.name(), httpEndpoint.endpointId(), PackageType.CLIENT);
        ClassName immutablesEndpointClassName =
                generatorContext.getImmutablesUtils().getImmutablesClassName(endpointClassName);
        TypeSpec.Builder endpointTypeSpecBuilder = TypeSpec.classBuilder(endpointClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Value.Enclosing.class).build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build());
        GeneratedRequestInfo generatedRequestInfo = generateRequestType(httpEndpoint, immutablesEndpointClassName);
        endpointTypeSpecBuilder.addType(generatedRequestInfo.requestTypeSpec());
        TypeSpec endpointTypeSpec = endpointTypeSpecBuilder.build();
        JavaFile endpointJavaFile = JavaFile.builder(endpointClassName.packageName(), endpointTypeSpec)
                .build();
        return GeneratedEndpointClient.builder()
                .file(endpointJavaFile)
                .className(endpointClassName)
                .generatedRequestInfo(generatedRequestInfo)
                .build();
    }

    private GeneratedRequestInfo generateRequestType(HttpEndpoint httpEndpoint, ClassName immutablesEndpointClassName) {
        ClassName requestClassName = generatorContext
                .getClassNameUtils()
                .getClassName(
                        REQUEST_CLASS_NAME,
                        Optional.empty(),
                        Optional.of(httpService.name().fernFilepath()),
                        PackageType.CLIENT);
        ClassName immutablesRequestClassName =
                generatorContext.getImmutablesUtils().getImmutablesClassName(requestClassName);
        List<ParameterSpec> endpointParameters = HttpEndpointArgumentUtils.getHttpEndpointArguments(
                httpService, httpEndpoint, generatorContext, generatedEndpointModels);
        List<MethodSpec> parameterImmutablesMethods = endpointParameters.stream()
                .map(endpointParameter -> MethodSpec.methodBuilder(endpointParameter.name)
                        .returns(endpointParameter.type)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build())
                .collect(Collectors.toList());
        TypeSpec typeSpec = TypeSpec.interfaceBuilder(requestClassName)
                .addAnnotation(Value.Immutable.class)
                .addAnnotation(StagedBuilderImmutablesStyle.class)
                .addMethods(parameterImmutablesMethods)
                .addMethod(generateStaticBuilder(
                        endpointParameters, immutablesRequestClassName, immutablesEndpointClassName))
                .build();
        return GeneratedRequestInfo.builder()
                .requestTypeSpec(typeSpec)
                .requestClassName(requestClassName)
                .addAllPropertyMethodSpecs(parameterImmutablesMethods)
                .build();
    }

    private MethodSpec generateStaticBuilder(
            List<ParameterSpec> parameterSpecs,
            ClassName immutablesRequestClassName,
            ClassName immutablesEndpointClassName) {
        Optional<ParameterSpec> firstMandatoryFieldName = getFirstRequiredFieldName(parameterSpecs);
        ClassName builderClassName = firstMandatoryFieldName.isEmpty()
                ? immutablesRequestClassName.nestedClass("Builder")
                : immutablesRequestClassName.nestedClass(
                        StringUtils.capitalize(firstMandatoryFieldName.get().name) + BUILD_STAGE_SUFFIX);
        return MethodSpec.methodBuilder(STATIC_BUILDER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderClassName)
                .addCode("return $T.$T.builder();", immutablesEndpointClassName, immutablesRequestClassName)
                .build();
    }

    private static Optional<ParameterSpec> getFirstRequiredFieldName(List<ParameterSpec> parameterSpecs) {
        for (ParameterSpec parameterSpec : parameterSpecs) {
            if (parameterSpec.type instanceof ClassName
                    && ((ClassName) parameterSpec.type).simpleName().equals(Optional.class.getSimpleName())) {
                return Optional.of(parameterSpec);
            }
        }
        return Optional.empty();
    }
}
