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
package com.fern.jersey.server;

import com.fern.codegen.GeneratedEndpointModel;
import com.fern.codegen.GeneratedError;
import com.fern.codegen.GeneratedHttpServiceServer;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.jersey.HttpAuthToParameterSpec;
import com.fern.jersey.HttpMethodAnnotationVisitor;
import com.fern.jersey.HttpPathUtils;
import com.fern.jersey.JerseyServiceGeneratorUtils;
import com.fern.model.codegen.Generator;
import com.fern.types.ErrorName;
import com.fern.types.services.HttpEndpoint;
import com.fern.types.services.HttpService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public final class HttpServiceServerGenerator extends Generator {

    private final HttpService httpService;
    private final ClassName generatedServiceClassName;
    private final JerseyServiceGeneratorUtils jerseyServiceGeneratorUtils;
    private final Map<HttpEndpoint, GeneratedEndpointModel> generatedEndpointModels;
    private final Map<ErrorName, GeneratedError> generatedErrors;

    public HttpServiceServerGenerator(
            GeneratorContext generatorContext,
            Map<ErrorName, GeneratedError> generatedErrors,
            List<GeneratedEndpointModel> generatedEndpointModels,
            HttpService httpService) {
        super(generatorContext, PackageType.SERVER);
        this.httpService = httpService;
        this.generatedErrors = generatedErrors;
        this.generatedServiceClassName =
                generatorContext.getClassNameUtils().getClassNameFromServiceName(httpService.name(), packageType);
        this.jerseyServiceGeneratorUtils = new JerseyServiceGeneratorUtils(generatorContext);
        this.generatedEndpointModels = generatedEndpointModels.stream()
                .collect(Collectors.toMap(GeneratedEndpointModel::httpEndpoint, Function.identity()));
    }

    @Override
    public GeneratedHttpServiceServer generate() {
        TypeSpec.Builder jerseyServiceBuilder = TypeSpec.classBuilder(generatedServiceClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(AnnotationSpec.builder(Consumes.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Produces.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build());
        jerseyServiceBuilder.addAnnotation(AnnotationSpec.builder(Path.class)
                .addMember("value", "$S", httpService.basePath().orElse("/"))
                .build());
        List<MethodSpec> httpEndpointMethods = httpService.endpoints().stream()
                .map(this::getHttpEndpointMethodSpec)
                .collect(Collectors.toList());
        TypeSpec jerseyServiceTypeSpec =
                jerseyServiceBuilder.addMethods(httpEndpointMethods).build();
        JavaFile jerseyServiceJavaFile = JavaFile.builder(
                        generatedServiceClassName.packageName(), jerseyServiceTypeSpec)
                .build();
        return GeneratedHttpServiceServer.builder()
                .file(jerseyServiceJavaFile)
                .className(generatedServiceClassName)
                .httpService(httpService)
                .build();
    }

    private MethodSpec getHttpEndpointMethodSpec(HttpEndpoint httpEndpoint) {
        MethodSpec.Builder endpointMethodBuilder = MethodSpec.methodBuilder(
                        httpEndpoint.endpointId().value())
                .addAnnotation(httpEndpoint.method().visit(HttpMethodAnnotationVisitor.INSTANCE))
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", HttpPathUtils.getJerseyCompatiblePath(httpEndpoint.path()))
                        .build())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        httpEndpoint.auth().visit(new HttpAuthToParameterSpec()).ifPresent(endpointMethodBuilder::addParameter);
        httpEndpoint.headers().stream()
                .map(jerseyServiceGeneratorUtils::getHeaderParameterSpec)
                .forEach(endpointMethodBuilder::addParameter);
        httpEndpoint.pathParameters().stream()
                .map(jerseyServiceGeneratorUtils::getPathParameterSpec)
                .forEach(endpointMethodBuilder::addParameter);
        httpEndpoint.queryParameters().stream()
                .map(jerseyServiceGeneratorUtils::getQueryParameterSpec)
                .forEach(endpointMethodBuilder::addParameter);
        GeneratedEndpointModel generatedEndpointModel = generatedEndpointModels.get(httpEndpoint);
        jerseyServiceGeneratorUtils
                .getPayloadTypeName(generatedEndpointModel.generatedHttpRequest())
                .ifPresent(typeName -> {
                    endpointMethodBuilder.addParameter(
                            ParameterSpec.builder(typeName, "request").build());
                });
        Optional<TypeName> returnPayload =
                jerseyServiceGeneratorUtils.getPayloadTypeName(generatedEndpointModel.generatedHttpResponse());
        returnPayload.ifPresent(endpointMethodBuilder::returns);

        List<ClassName> errorClassNames = httpEndpoint.response().failed().errors().stream()
                .map(responseError -> generatedErrors.get(responseError.error()).className())
                .collect(Collectors.toList());
        endpointMethodBuilder.addExceptions(errorClassNames);
        return endpointMethodBuilder.build();
    }
}
