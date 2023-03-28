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
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class NoRequestEndpointWriter extends AbstractEndpointWriter {

    public NoRequestEndpointWriter(
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            FieldSpec okHttpClientField,
            FieldSpec urlField,
            GeneratedObjectMapper generatedObjectMapper,
            ClientGeneratorContext clientGeneratorContext,
            Optional<GeneratedClientOptions> generatedClientOptions,
            Optional<ClientAuthFieldSpec> authFieldSpec) {
        super(
                httpService,
                httpEndpoint,
                okHttpClientField,
                urlField,
                generatedObjectMapper,
                clientGeneratorContext,
                generatedClientOptions,
                authFieldSpec);
    }

    @Override
    public List<ParameterSpec> additionalParameters() {
        return Collections.emptyList();
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
            HttpEndpoint httpEndpoint,
            GeneratedObjectMapper generatedObjectMapper) {
        CodeBlock.Builder requestInitBuilder = CodeBlock.builder()
                .add("$T $L = new $T.Builder()\n", Request.class, AbstractEndpointWriter.REQUEST_NAME, Request.class)
                .indent()
                .add(".url($L)\n", AbstractEndpointWriter.HTTP_URL_NAME)
                .add(
                        ".method($S, $T.create($S, null))\n",
                        httpEndpoint.getMethod().toString(),
                        RequestBody.class,
                        "");
        authField.ifPresent(clientAuthFieldSpec -> requestInitBuilder.add(
                ".addHeader($S, this.$L.toString())",
                clientAuthFieldSpec.getHeaderKey(),
                clientAuthFieldSpec.getAuthField()));
        return requestInitBuilder.add(".build()").unindent().build();
    }
}
