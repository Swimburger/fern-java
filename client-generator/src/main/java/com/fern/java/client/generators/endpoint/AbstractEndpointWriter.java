/*
 * (c) Copyright 2023 Birch Solutions Inc. All rights reserved.
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

package com.fern.java.client.generators.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fern.irV16.model.environment.EnvironmentBaseUrlId;
import com.fern.irV16.model.http.HttpEndpoint;
import com.fern.irV16.model.http.HttpService;
import com.fern.irV16.model.http.PathParameter;
import com.fern.irV16.model.http.SdkRequest;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.client.GeneratedEnvironmentsClass;
import com.fern.java.client.GeneratedEnvironmentsClass.MultiUrlEnvironmentsClass;
import com.fern.java.client.GeneratedEnvironmentsClass.SingleUrlEnvironmentClass;
import com.fern.java.client.generators.endpoint.HttpUrlBuilder.PathParamInfo;
import com.fern.java.generators.object.EnrichedObjectProperty;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.*;
import okhttp3.Response;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractEndpointWriter {

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APPLICATION_JSON_HEADER = "application/json";
    public static final String HTTP_URL_NAME = "_httpUrl";
    public static final String REQUEST_NAME = "_request";
    public static final String REQUEST_BUILDER_NAME = "_requestBuilder";
    public static final String REQUEST_BODY_NAME = "_requestBody";
    public static final String RESPONSE_NAME = "_response";
    private final HttpService httpService;
    private final HttpEndpoint httpEndpoint;
    private final GeneratedClientOptions generatedClientOptions;
    private final FieldSpec clientOptionsField;
    private final ClientGeneratorContext clientGeneratorContext;
    private final MethodSpec.Builder endpointMethodBuilder;
    private final GeneratedObjectMapper generatedObjectMapper;
    private final GeneratedEnvironmentsClass generatedEnvironmentsClass;

    public AbstractEndpointWriter(
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            GeneratedObjectMapper generatedObjectMapper,
            ClientGeneratorContext clientGeneratorContext,
            FieldSpec clientOptionsField,
            GeneratedClientOptions generatedClientOptions,
            GeneratedEnvironmentsClass generatedEnvironmentsClass) {
        this.httpService = httpService;
        this.httpEndpoint = httpEndpoint;
        this.clientOptionsField = clientOptionsField;
        this.generatedClientOptions = generatedClientOptions;
        this.clientGeneratorContext = clientGeneratorContext;
        this.generatedObjectMapper = generatedObjectMapper;
        this.generatedEnvironmentsClass = generatedEnvironmentsClass;
        this.endpointMethodBuilder = MethodSpec.methodBuilder(
                        httpEndpoint.getName().get().getCamelCase().getSafeName())
                .addModifiers(Modifier.PUBLIC);
    }

    public final MethodSpec generate() {
        // Step 1: Add Path Params as parameters
        List<ParameterSpec> pathParameters = getPathParameters();
        for (ParameterSpec pathParameter : pathParameters) {
            endpointMethodBuilder.addParameter(pathParameter);
        }

        // Step 2: Add additional parameters
        endpointMethodBuilder.addParameters(additionalParameters());

        // Step 3: Get http client initializer
        HttpUrlBuilder httpUrlBuilder = new HttpUrlBuilder(
                HTTP_URL_NAME,
                sdkRequest()
                        .map(sdkRequest -> sdkRequest
                                .getRequestParameterName()
                                .getCamelCase()
                                .getSafeName())
                        .orElse(null),
                clientOptionsField,
                generatedClientOptions,
                CodeBlock.of(
                        "this.$L.$N().$L()",
                        clientOptionsField.name,
                        generatedClientOptions.environment(),
                        getEnvironmentToUrlMethod().name),
                httpEndpoint,
                httpService,
                convertPathParametersToSpecMap(httpService.getPathParameters()),
                convertPathParametersToSpecMap(httpEndpoint.getPathParameters()));
        HttpUrlBuilder.GeneratedHttpUrl generatedHttpUrl = httpUrlBuilder.generateBuilder(getQueryParams());
        endpointMethodBuilder.addCode(generatedHttpUrl.initialization());

        // Step 4: Get request initializer
        boolean sendContentType = httpEndpoint.getRequestBody().isPresent()
                || httpEndpoint.getResponse().isPresent();
        CodeBlock requestInitializer = getInitializeRequestCodeBlock(
                clientOptionsField,
                generatedClientOptions,
                httpEndpoint,
                generatedObjectMapper,
                generatedHttpUrl.inlinableBuild(),
                sendContentType);
        endpointMethodBuilder.addCode(requestInitializer);

        // Step 5: Make http request and handle responses
        CodeBlock responseParser = getResponseParserCodeBlock();
        endpointMethodBuilder.addCode(responseParser);
        return endpointMethodBuilder.build();
    }

    public abstract Optional<SdkRequest> sdkRequest();

    public abstract List<EnrichedObjectProperty> getQueryParams();

    public abstract List<ParameterSpec> additionalParameters();

    public abstract CodeBlock getInitializeRequestCodeBlock(
            FieldSpec clientOptionsMember,
            GeneratedClientOptions clientOptions,
            HttpEndpoint endpoint,
            GeneratedObjectMapper objectMapper,
            CodeBlock inlineableHttpUrl,
            boolean sendContentType);

    public final CodeBlock getResponseParserCodeBlock() {
        CodeBlock.Builder httpResponseBuilder = CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement(
                        "$T $L = $N.$N().newCall($L).execute()",
                        Response.class,
                        RESPONSE_NAME,
                        clientOptionsField,
                        generatedClientOptions.httpClient(),
                        REQUEST_NAME)
                .beginControlFlow("if ($L.isSuccessful())", RESPONSE_NAME);
        if (httpEndpoint.getResponse().isPresent()) {
            TypeName returnType = clientGeneratorContext
                    .getPoetTypeNameMapper()
                    .convertToTypeName(true, httpEndpoint.getResponse().get().getResponseBodyType());
            endpointMethodBuilder.returns(returnType);
            if (httpEndpoint.getResponse().get().getResponseBodyType().isContainer()) {
                httpResponseBuilder
                        .addStatement(
                                "return $T.$L.readValue($L.body().string(), new $T() {})",
                                generatedObjectMapper.getClassName(),
                                generatedObjectMapper.jsonMapperStaticField().name,
                                RESPONSE_NAME,
                                ParameterizedTypeName.get(ClassName.get(TypeReference.class), returnType))
                        .endControlFlow();
            } else {
                httpResponseBuilder
                        .addStatement(
                                "return $T.$L.readValue($L.body().string(), $T.class)",
                                generatedObjectMapper.getClassName(),
                                generatedObjectMapper.jsonMapperStaticField().name,
                                RESPONSE_NAME,
                                returnType)
                        .endControlFlow();
            }
        } else {
            httpResponseBuilder.addStatement("return").endControlFlow();
        }
        httpResponseBuilder.addStatement("throw new $T()", RuntimeException.class);
        httpResponseBuilder
                .endControlFlow()
                .beginControlFlow("catch ($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build();
        return httpResponseBuilder.build();
    }

    protected final MethodSpec getEnvironmentToUrlMethod() {
        if (generatedEnvironmentsClass.info() instanceof SingleUrlEnvironmentClass) {
            return ((SingleUrlEnvironmentClass) generatedEnvironmentsClass.info()).getUrlMethod();
        } else if (generatedEnvironmentsClass.info() instanceof MultiUrlEnvironmentsClass) {
            EnvironmentBaseUrlId environmentBaseUrlId = httpService.getBaseUrl().get();
            return ((MultiUrlEnvironmentsClass) generatedEnvironmentsClass.info())
                    .urlGetterMethods()
                    .get(environmentBaseUrlId);
        } else {
            throw new RuntimeException("Generated Environments class was unknown : " + generatedEnvironmentsClass);
        }
    }

    private List<ParameterSpec> getPathParameters() {
        List<ParameterSpec> pathParameterSpecs = new ArrayList<>();
        httpService.getPathParameters().forEach(pathParameter -> {
            if (pathParameter.getVariable().isPresent()) {
                return;
            }
            pathParameterSpecs.add(convertPathParameter(pathParameter).poetParam());
        });
        httpEndpoint.getPathParameters().forEach(pathParameter -> {
            if (pathParameter.getVariable().isPresent()) {
                return;
            }
            pathParameterSpecs.add(convertPathParameter(pathParameter).poetParam());
        });
        return pathParameterSpecs;
    }

    private Map<String, PathParamInfo> convertPathParametersToSpecMap(List<PathParameter> pathParameters) {
        return pathParameters.stream()
                .collect(Collectors.toMap(
                        pathParameter -> pathParameter.getName().getOriginalName(), this::convertPathParameter));
    }

    private PathParamInfo convertPathParameter(PathParameter pathParameter) {
        return PathParamInfo.builder()
                .irParam(pathParameter)
                .poetParam(ParameterSpec.builder(
                                clientGeneratorContext
                                        .getPoetTypeNameMapper()
                                        .convertToTypeName(true, pathParameter.getValueType()),
                                pathParameter.getName().getCamelCase().getSafeName())
                        .build())
                .build();
    }

    public static CodeBlock stringify(String reference, TypeName typeName) {
        if (typeName instanceof ParameterizedTypeName
                && ((ParameterizedTypeName) typeName).typeArguments.get(0).equals(ClassName.get(String.class))) {
            return CodeBlock.of(reference);
        } else if (typeName.equals(ClassName.get(String.class))) {
            return CodeBlock.of(reference);
        } else if (typeName.equals(TypeName.DOUBLE)) {
            return CodeBlock.of("$T.toString($L)", Double.class, reference);
        } else if (typeName.equals(TypeName.INT)) {
            return CodeBlock.of("$T.toString($L)", Integer.class, reference);
        } else {
            return CodeBlock.of("$L.toString()", reference);
        }
    }
}
