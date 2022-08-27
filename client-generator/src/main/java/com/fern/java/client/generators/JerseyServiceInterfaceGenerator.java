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
import com.fern.ir.model.services.http.HttpEndpointId;
import com.fern.ir.model.services.http.HttpRequest;
import com.fern.ir.model.services.http.HttpResponse;
import com.fern.ir.model.services.http.HttpService;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedJerseyServiceInterfaceOutput;
import com.fern.java.client.generators.jersey.JerseyHttpMethodToAnnotationSpec;
import com.fern.java.client.generators.jersey.JerseyParameterSpecFactory;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.jackson.ClientObjectMappers;
import com.fern.java.jersey.contracts.OptionalAwareContract;
import com.fern.java.output.AbstractGeneratedFileOutput;
import com.fern.java.output.GeneratedFileOutput;
import com.fern.java.utils.HttpPathUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public final class JerseyServiceInterfaceGenerator extends AbstractFileGenerator {

    public static final String GET_CLIENT_METHOD_NAME = "getClient";

    private final HttpService httpService;
    private final Map<DeclaredErrorName, AbstractGeneratedFileOutput> generatedErrors;
    private final Optional<GeneratedFileOutput> maybeGeneratedAuthSchemes;
    private final ClientGeneratorContext clientGeneratorContext;
    private final JerseyParameterSpecFactory jerseyParameterSpecFactory;

    public JerseyServiceInterfaceGenerator(
            ClientGeneratorContext clientGeneratorContext,
            Map<DeclaredErrorName, AbstractGeneratedFileOutput> generatedErrors,
            Optional<GeneratedFileOutput> maybeGeneratedAuthSchemes,
            HttpService httpService) {
        super(
                clientGeneratorContext.getPoetClassNameFactory().getServiceInterfaceClassName(httpService),
                clientGeneratorContext);
        this.clientGeneratorContext = clientGeneratorContext;
        this.httpService = httpService;
        this.generatedErrors = generatedErrors;
        this.maybeGeneratedAuthSchemes = maybeGeneratedAuthSchemes;
        this.jerseyParameterSpecFactory = new JerseyParameterSpecFactory(clientGeneratorContext);
    }

    @Override
    public GeneratedJerseyServiceInterfaceOutput generateFile() {
        TypeSpec.Builder jerseyInterfaceBuilder = TypeSpec.interfaceBuilder(className)
                .addAnnotation(AnnotationSpec.builder(Consumes.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Produces.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", httpService.getBasePath().orElse("/"))
                        .build());

        Map<HttpEndpointId, MethodSpec> endpointMethods = new HashMap<>();
        Map<HttpEndpointId, AbstractGeneratedFileOutput> endpointExceptions = new HashMap<>();
        for (HttpEndpoint httpEndpoint : httpService.getEndpoints()) {
            HttpEndpointId httpEndpointId = httpEndpoint.getId();
            ClientEndpointExceptionGenerator endpointExceptionGenerator = new ClientEndpointExceptionGenerator(
                    clientGeneratorContext, httpService, httpEndpoint, generatedErrors);
            AbstractGeneratedFileOutput endpointException = endpointExceptionGenerator.generateFile();

            MethodSpec endpointMethodSpec = getEndpointMethodSpec(httpEndpoint, endpointException);
            jerseyInterfaceBuilder.addMethod(endpointMethodSpec);

            endpointExceptions.put(httpEndpointId, endpointException);
            endpointMethods.put(httpEndpointId, endpointMethodSpec);
        }

        AbstractGeneratedFileOutput errorDecoder = getErrorDecoder(endpointExceptions);
        TypeSpec jerseyInterfaceTypeSpec = jerseyInterfaceBuilder
                .addMethod(getStaticClientBuilderMethod(errorDecoder))
                .build();
        JavaFile jerseyServiceFile = JavaFile.builder(className.packageName(), jerseyInterfaceTypeSpec)
                .build();

        return GeneratedJerseyServiceInterfaceOutput.builder()
                .className(className)
                .javaFile(jerseyServiceFile)
                .errorDecoder(errorDecoder)
                .putAllEndpointExceptions(endpointExceptions)
                .putAllEndpointMethods(endpointMethods)
                .build();
    }

    private MethodSpec getEndpointMethodSpec(HttpEndpoint httpEndpoint, AbstractGeneratedFileOutput endpointException) {
        MethodSpec.Builder endpointMethodBuilder = MethodSpec.methodBuilder(
                        httpEndpoint.getId().get())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(httpEndpoint.getMethod().visit(JerseyHttpMethodToAnnotationSpec.INSTANCE))
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember(
                                "value", "$S", HttpPathUtils.getPathWithCurlyBracedPathParams(httpEndpoint.getPath()))
                        .build())
                .addParameters(getEndpointMethodParameters(httpEndpoint))
                .addException(endpointException.getClassName());

        HttpResponse httpResponse = httpEndpoint.getResponse();
        if (!httpResponse.getType().isVoid()) {
            TypeName responseTypeName =
                    generatorContext.getPoetTypeNameMapper().convertToTypeName(true, httpResponse.getType());
            endpointMethodBuilder.returns(responseTypeName);
        }

        return endpointMethodBuilder.build();
    }

    private List<ParameterSpec> getEndpointMethodParameters(HttpEndpoint httpEndpoint) {
        List<ParameterSpec> parameters = new ArrayList<>();
        // TODO(dsinghvi): add auth parameter

        // headers
        httpService.getHeaders().stream()
                .map(jerseyParameterSpecFactory::getHeaderParameterSpec)
                .forEach(parameters::add);
        httpEndpoint.getHeaders().stream()
                .map(jerseyParameterSpecFactory::getHeaderParameterSpec)
                .forEach(parameters::add);

        // path params
        httpEndpoint.getPathParameters().stream()
                .map(jerseyParameterSpecFactory::getPathParameterSpec)
                .forEach(parameters::add);

        // query params
        httpEndpoint.getQueryParameters().stream()
                .map(jerseyParameterSpecFactory::getQueryParameterSpec)
                .forEach(parameters::add);

        // request body
        HttpRequest httpRequest = httpEndpoint.getRequest();
        if (!httpRequest.getType().isVoid()) {
            TypeName requestTypeName =
                    generatorContext.getPoetTypeNameMapper().convertToTypeName(true, httpRequest.getType());
            parameters.add(ParameterSpec.builder(requestTypeName, "body").build());
        }

        return parameters;
    }

    private AbstractGeneratedFileOutput getErrorDecoder(
            Map<HttpEndpointId, AbstractGeneratedFileOutput> endpointExceptions) {
        ClientErrorDecoderGenerator clientErrorDecoderGenerator =
                new ClientErrorDecoderGenerator(clientGeneratorContext, httpService, endpointExceptions);
        return clientErrorDecoderGenerator.generateFile();
    }

    private MethodSpec getStaticClientBuilderMethod(AbstractGeneratedFileOutput generatedErrorDecoder) {
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .add("return $T.builder()\n", Feign.class)
                .indent()
                .indent()
                .add(".contract(new $T(new $T()))\n", OptionalAwareContract.class, JAXRSContract.class)
                .add(".decoder(new $T($T.$L))\n", JacksonDecoder.class, ClientObjectMappers.class, "JSON_MAPPER")
                .add(".encoder(new $T($T.$L))\n", JacksonEncoder.class, ClientObjectMappers.class, "JSON_MAPPER")
                .add(".errorDecoder(new $T())", generatedErrorDecoder.getClassName());
        codeBlockBuilder.add(".target($T.class, $L);", className, "url");
        CodeBlock codeBlock = codeBlockBuilder.unindent().unindent().build();
        return MethodSpec.methodBuilder(GET_CLIENT_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "url")
                .returns(className)
                .addCode(codeBlock)
                .build();
    }
}
