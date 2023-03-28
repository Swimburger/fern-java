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
import com.fern.ir.v3.model.services.http.HttpRequestBodyReference;
import com.fern.ir.v3.model.services.http.HttpService;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import java.util.List;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class OnlyRequestEndpointWriter extends AbstractEndpointWriter {
    private final ClientGeneratorContext clientGeneratorContext;
    private final HttpEndpoint httpEndpoint;
    private final HttpRequestBodyReference httpRequestBodyReference;

    public OnlyRequestEndpointWriter(
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            FieldSpec okHttpClientField,
            FieldSpec urlField,
            GeneratedObjectMapper generatedObjectMapper,
            ClientGeneratorContext clientGeneratorContext,
            Optional<GeneratedClientOptions> generatedClientOptions,
            Optional<ClientAuthFieldSpec> authFieldSpec,
            HttpRequestBodyReference httpRequestBodyReference) {
        super(
                httpService,
                httpEndpoint,
                okHttpClientField,
                urlField,
                generatedObjectMapper,
                clientGeneratorContext,
                generatedClientOptions,
                authFieldSpec);
        this.clientGeneratorContext = clientGeneratorContext;
        this.httpEndpoint = httpEndpoint;
        this.httpRequestBodyReference = httpRequestBodyReference;
    }

    @Override
    public List<ParameterSpec> additionalParameters() {
        return List.of(ParameterSpec.builder(
                        clientGeneratorContext
                                .getPoetTypeNameMapper()
                                .convertToTypeName(true, httpRequestBodyReference.getRequestBodyType()),
                        "request")
                .build());
    }

    @Override
    public CodeBlock getInitializeHttpUrlCodeBlock(FieldSpec urlField, List<ParameterSpec> pathParameters) {
        CodeBlock.Builder httpUrlInitBuilder = CodeBlock.builder()
                .add(
                        "$T $L = $T.parser(this.$L).newBuilder()\n",
                        HttpUrl.class,
                        HTTP_URL_NAME,
                        HttpUrl.class,
                        urlField.name)
                .indent()
                .add(".scheme(this.$L.getProtocol())\n", urlField.name)
                .add(".host(this.$L.getHost())\n", urlField.name);
        for (ParameterSpec pathParameter : pathParameters) {
            httpUrlInitBuilder.add(".addPathSegment($L)\n", pathParameter.name);
        }
        return httpUrlInitBuilder.add(".build();").unindent().build();
    }

    @Override
    public CodeBlock getInitializeRequestCodeBlock(
            FieldSpec urlField,
            Optional<ClientAuthFieldSpec> authField,
            HttpEndpoint endpoint,
            GeneratedObjectMapper generatedObjectMapper) {
        CodeBlock.Builder requestInitBuilder = CodeBlock.builder()
                .addStatement("$T $L", RequestBody.class, AbstractEndpointWriter.REQUEST_BODY_NAME)
                .beginControlFlow("try")
                .addStatement(
                        "$L = $T.create($T.$L.writeValueAsBytes($L), $T.APPLICATION_JSON)",
                        AbstractEndpointWriter.REQUEST_BODY_NAME,
                        RequestBody.class,
                        generatedObjectMapper.getClassName(),
                        generatedObjectMapper.jsonMapperStaticField().name,
                        "request",
                        okhttp3.MediaType.class)
                .endControlFlow()
                .beginControlFlow("catch($T e)", Exception.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .add("$T $L = new $T.Builder()\n", Request.class, AbstractEndpointWriter.REQUEST_NAME, Request.class)
                .indent()
                .add(".url($L)\n", AbstractEndpointWriter.HTTP_URL_NAME)
                .add(
                        ".method($S, $L)\n",
                        httpEndpoint.getMethod().toString(),
                        AbstractEndpointWriter.REQUEST_BODY_NAME);
        authField.ifPresent(clientAuthFieldSpec -> requestInitBuilder.add(
                ".addHeader($S, this.$L.toString())",
                clientAuthFieldSpec.getHeaderKey(),
                clientAuthFieldSpec.getAuthField()));
        return requestInitBuilder.add(".build()").unindent().build();
    }
}
