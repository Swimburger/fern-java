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

import com.fern.ir.v9.model.http.HttpEndpoint;
import com.fern.ir.v9.model.http.HttpService;
import com.fern.ir.v9.model.http.SdkRequest;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedClientOptions;
import com.fern.java.client.GeneratedWrappedRequest;
import com.fern.java.client.GeneratedWrappedRequest.InlinedRequestBodyGetters;
import com.fern.java.client.GeneratedWrappedRequest.ReferencedRequestBodyGetter;
import com.fern.java.generators.object.EnrichedObjectProperty;
import com.fern.java.output.GeneratedObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class WrappedRequestEndpointWriter extends AbstractEndpointWriter {

    public static final String REQUEST_BODY_PROPERTIES_NAME = "_requestBodyProperties";

    private final GeneratedWrappedRequest generatedWrappedRequest;
    private final ClientGeneratorContext clientGeneratorContext;
    private final SdkRequest sdkRequest;

    private final String requestParameterName;

    public WrappedRequestEndpointWriter(
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            FieldSpec okHttpClientField,
            FieldSpec urlField,
            GeneratedObjectMapper generatedObjectMapper,
            ClientGeneratorContext clientGeneratorContext,
            Optional<GeneratedClientOptions> generatedClientOptions,
            Optional<ClientAuthFieldSpec> authFieldSpec,
            SdkRequest sdkRequest,
            GeneratedWrappedRequest generatedWrappedRequest) {
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
        this.generatedWrappedRequest = generatedWrappedRequest;
        this.sdkRequest = sdkRequest;
        this.requestParameterName =
                sdkRequest.getRequestParameterName().getSafeName().getCamelCase();
    }

    @Override
    public List<ParameterSpec> additionalParameters() {
        return Collections.singletonList(ParameterSpec.builder(
                        generatedWrappedRequest.getClassName(),
                        sdkRequest.getRequestParameterName().getSafeName().getCamelCase())
                .build());
    }

    @Override
    public CodeBlock getInitializeHttpUrlCodeBlock(FieldSpec urlField, List<ParameterSpec> pathParameters) {
        CodeBlock.Builder httpUrlBuilder = CodeBlock.builder()
                .add(
                        "$T.Builder $L = $T.parser(this.$L).newBuilder()\n",
                        HttpUrl.class,
                        AbstractEndpointWriter.HTTP_URL_BUILDER_NAME,
                        HttpUrl.class,
                        urlField.name)
                .indent()
                .add(".scheme(this.$L.getProtocol())\n", urlField.name);

        if (pathParameters.isEmpty()) {
            httpUrlBuilder.add(".host(this.$L.getHost());\n", urlField.name).unindent();
        } else {
            httpUrlBuilder.add(".host(this.$L.getHost())\n", urlField.name);
        }

        for (int i = 0; i < pathParameters.size(); ++i) {
            ParameterSpec pathParameter = pathParameters.get(i);
            if (i == pathParameters.size() - 1) {
                httpUrlBuilder.add(".addPathSegment($L);\n", pathParameter.name).unindent();
            } else {
                httpUrlBuilder.add(".addPathSegment($L)\n", pathParameter.name);
            }
        }

        for (EnrichedObjectProperty queryParam : generatedWrappedRequest.queryParams()) {
            if (typeNameIsOptional(queryParam.poetTypeName())) {
                httpUrlBuilder
                        .beginControlFlow("if ($L.$N.isPresent())", requestParameterName, queryParam.getterProperty())
                        .addStatement(
                                "$L.addQueryParameter($S, $L)",
                                AbstractEndpointWriter.HTTP_URL_BUILDER_NAME,
                                queryParam.wireKey(),
                                "$L.$N().get()",
                                "request",
                                queryParam.getterProperty())
                        .endControlFlow();
            } else {
                httpUrlBuilder.addStatement(
                        "$L.addQueryParameter($S, $L.$N())",
                        AbstractEndpointWriter.HTTP_URL_BUILDER_NAME,
                        queryParam.wireKey(),
                        requestParameterName,
                        queryParam.getterProperty());
            }
        }
        httpUrlBuilder.addStatement(
                "$T $L = $L.build()",
                HttpUrl.class,
                AbstractEndpointWriter.HTTP_URL_NAME,
                AbstractEndpointWriter.HTTP_URL_BUILDER_NAME);
        return httpUrlBuilder.build();
    }

    @Override
    public CodeBlock getInitializeRequestCodeBlock(
            FieldSpec urlField,
            Optional<ClientAuthFieldSpec> authField,
            HttpEndpoint httpEndpoint,
            GeneratedObjectMapper generatedObjectMapper) {
        CodeBlock.Builder requestInitializerBuilder = CodeBlock.builder();
        if (generatedWrappedRequest.requestBodyGetter().isPresent()) {
            String requestBodyArgument = "";
            if (generatedWrappedRequest.requestBodyGetter().get() instanceof ReferencedRequestBodyGetter) {
                requestBodyArgument = requestParameterName + "."
                        + ((ReferencedRequestBodyGetter) generatedWrappedRequest
                                        .requestBodyGetter()
                                        .get())
                                .requestBodyGetter()
                                .name;
            } else if (generatedWrappedRequest.requestBodyGetter().get() instanceof InlinedRequestBodyGetters) {
                requestInitializerBuilder.addStatement(
                        "$T $L = new $T<>()",
                        ParameterizedTypeName.get(Map.class, String.class, Object.class),
                        REQUEST_BODY_PROPERTIES_NAME,
                        HashMap.class);
                InlinedRequestBodyGetters inlinedRequestBodyGetter = ((InlinedRequestBodyGetters)
                        generatedWrappedRequest.requestBodyGetter().get());
                for (EnrichedObjectProperty bodyProperty : inlinedRequestBodyGetter.properties()) {
                    requestInitializerBuilder.addStatement(
                            "$L.put($S, $L)",
                            REQUEST_BODY_PROPERTIES_NAME,
                            bodyProperty.wireKey().get(),
                            requestParameterName + "." + bodyProperty.getterProperty().name + "()");
                }
                requestBodyArgument = REQUEST_BODY_PROPERTIES_NAME;
            }
            requestInitializerBuilder
                    .addStatement("$T $L", RequestBody.class, AbstractEndpointWriter.REQUEST_BODY_NAME)
                    .beginControlFlow("try")
                    .addStatement(
                            "$L = $T.create($T.$L.writeValueAsBytes($L), $T.APPLICATION_JSON)",
                            AbstractEndpointWriter.REQUEST_BODY_NAME,
                            RequestBody.class,
                            generatedObjectMapper.getClassName(),
                            generatedObjectMapper.jsonMapperStaticField().name,
                            requestBodyArgument,
                            okhttp3.MediaType.class)
                    .endControlFlow()
                    .beginControlFlow("catch($T e)", Exception.class)
                    .addStatement("throw new $T(e)", RuntimeException.class)
                    .endControlFlow();
        } else {
            requestInitializerBuilder.addStatement(
                    "$T $L = $T.create($S, null)",
                    RequestBody.class,
                    AbstractEndpointWriter.REQUEST_BODY_NAME,
                    RequestBody.class,
                    "");
        }
        requestInitializerBuilder
                .add(
                        "$T.Builder $L = new $T.Builder()\n",
                        Request.class,
                        AbstractEndpointWriter.REQUEST_BUILDER_NAME,
                        Request.class)
                .indent()
                .add(".url($L)\n", AbstractEndpointWriter.HTTP_URL_NAME)
                .add(
                        ".method($S, $L);\n",
                        httpEndpoint.getMethod().toString(),
                        AbstractEndpointWriter.REQUEST_BODY_NAME)
                .unindent();
        authField.ifPresent(clientAuthFieldSpec -> requestInitializerBuilder.add(
                ".addHeader($S, this.$L.toString())",
                clientAuthFieldSpec.getHeaderKey(),
                clientAuthFieldSpec.getAuthField()));
        for (EnrichedObjectProperty header : generatedWrappedRequest.headerParams()) {
            if (typeNameIsOptional(header.poetTypeName())) {
                requestInitializerBuilder
                        .beginControlFlow("if ($L.$N.isPresent())", requestParameterName, header.getterProperty())
                        .addStatement(
                                "$L.addHeader($S, $L.$N().get())",
                                AbstractEndpointWriter.HTTP_URL_BUILDER_NAME,
                                header.wireKey(),
                                "request",
                                header.getterProperty())
                        .endControlFlow();
            } else {
                requestInitializerBuilder.addStatement(
                        "$L.addHeader($S, $L)",
                        AbstractEndpointWriter.HTTP_URL_BUILDER_NAME,
                        header.wireKey(),
                        "$L.$N()",
                        sdkRequest.getRequestParameterName().getSafeName().getCamelCase(),
                        header.getterProperty());
            }
        }
        requestInitializerBuilder.addStatement("$T $L = $L.build()", Request.class, REQUEST_NAME, REQUEST_BUILDER_NAME);
        return requestInitializerBuilder.build();
    }

    private static boolean typeNameIsOptional(TypeName typeName) {
        return typeName instanceof ParameterizedTypeName
                && ((ParameterizedTypeName) typeName).rawType.equals(ClassName.get(Optional.class));
    }
}
