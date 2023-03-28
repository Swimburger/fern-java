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

import com.fern.ir.v3.model.services.http.HttpEndpoint;
import com.fern.ir.v3.model.services.http.HttpService;
import com.fern.ir.v3.model.services.http.PathParameter;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractEndpointWriter {

    public static final String HTTP_URL_NAME = "_httpUrl";
    public static final String HTTP_URL_BUILDER_NAME = "_httpUrlBuilder";
    public static final String REQUEST_NAME = "_request";
    public static final String REQUEST_BUILDER_NAME = "_requestBuilder";
    public static final String REQUEST_BODY_NAME = "_requestBody";
    public static final String RESPONSE_NAME = "_response";

    private final HttpService httpService;
    private final HttpEndpoint httpEndpoint;
    private final FieldSpec okHttpClientField;
    private final FieldSpec urlField;
    private final Optional<ClientAuthFieldSpec> authFieldSpec;
    private final Optional<GeneratedClientOptions> generatedClientOptions;
    private final ClientGeneratorContext clientGeneratorContext;
    private final MethodSpec.Builder endpointMethodBuilder;

    private final GeneratedObjectMapper generatedObjectMapper;

    public AbstractEndpointWriter(
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            FieldSpec okHttpClientField,
            FieldSpec urlField,
            GeneratedObjectMapper generatedObjectMapper,
            ClientGeneratorContext clientGeneratorContext,
            Optional<GeneratedClientOptions> generatedClientOptions,
            Optional<ClientAuthFieldSpec> authFieldSpec) {
        this.httpService = httpService;
        this.httpEndpoint = httpEndpoint;
        this.okHttpClientField = okHttpClientField;
        this.urlField = urlField;
        this.authFieldSpec = authFieldSpec;
        this.generatedClientOptions = generatedClientOptions;
        this.clientGeneratorContext = clientGeneratorContext;
        this.generatedObjectMapper = generatedObjectMapper;
        this.endpointMethodBuilder = MethodSpec.methodBuilder(
                        httpEndpoint.getNameV2().get().getSafeName().getCamelCase())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
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
        CodeBlock httpClientInitializer = getInitializeHttpUrlCodeBlock(urlField, pathParameters);
        endpointMethodBuilder.addCode(httpClientInitializer);

        // Step 4: Get request initializer
        CodeBlock requestInitializer =
                getInitializeRequestCodeBlock(urlField, authFieldSpec, httpEndpoint, generatedObjectMapper);
        endpointMethodBuilder.addCode(requestInitializer);

        // Step 5: Make http request and handle responses
        CodeBlock responseParser = getResponseParserCodeBlock();
        endpointMethodBuilder.addCode(responseParser);
        return endpointMethodBuilder.build();
    }

    public abstract List<ParameterSpec> additionalParameters();

    public abstract CodeBlock getInitializeHttpUrlCodeBlock(FieldSpec url, List<ParameterSpec> pathParameters);

    public abstract CodeBlock getInitializeRequestCodeBlock(
            FieldSpec url,
            Optional<ClientAuthFieldSpec> auth,
            HttpEndpoint endpoint,
            GeneratedObjectMapper objectMapper);

    public final CodeBlock getResponseParserCodeBlock() {
        CodeBlock.Builder httpResponseBuilder = CodeBlock.builder()
                .beginControlFlow("try")
                .addStatement(
                        "$T $L = $L.newCall($L).execute()", Response.class, RESPONSE_NAME, Request.class, urlField.name)
                .beginControlFlow("if ($L.isSuccessful())", RESPONSE_NAME);
        if (httpEndpoint.getResponse().getTypeV2().isPresent()) {
            TypeName returnType = clientGeneratorContext
                    .getPoetTypeNameMapper()
                    .convertToTypeName(
                            true, httpEndpoint.getResponse().getTypeV2().get());
            endpointMethodBuilder.returns(returnType);
            httpResponseBuilder
                    .addStatement(
                            "return $T.$L.readValue($L.body.string(), $T.class)",
                            generatedObjectMapper.getClassName(),
                            generatedObjectMapper.jsonMapperStaticField().name,
                            REQUEST_NAME,
                            returnType)
                    .endControlFlow();
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

    private List<ParameterSpec> getPathParameters() {
        List<ParameterSpec> pathParameterSpecs = new ArrayList<>();
        httpService.getPathParameters().forEach(pathParameter -> {
            pathParameterSpecs.add(convertPathParameter(pathParameter));
        });
        httpEndpoint.getPathParameters().forEach(pathParameter -> {
            pathParameterSpecs.add(convertPathParameter(pathParameter));
        });
        return pathParameterSpecs;
    }

    private ParameterSpec convertPathParameter(PathParameter pathParameter) {
        return ParameterSpec.builder(
                        clientGeneratorContext
                                .getPoetTypeNameMapper()
                                .convertToTypeName(true, pathParameter.getValueType()),
                        pathParameter.getNameV2().getSafeName().getCamelCase())
                .build();
    }
}
