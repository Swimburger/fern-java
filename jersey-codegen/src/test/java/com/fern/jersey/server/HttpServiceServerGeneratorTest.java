package com.fern.jersey.server;

import com.errors.ErrorDefinition;
import com.errors.ErrorProperty;
import com.errors.HttpErrorConfiguration;
import com.fern.codegen.GeneratedException;
import com.fern.codegen.GeneratedHttpServiceClient;
import com.fern.codegen.GeneratedHttpServiceServer;
import com.fern.codegen.GeneratorContext;
import com.fern.jersey.ExceptionGenerator;
import com.fern.jersey.client.HttpServiceClientGenerator;
import com.services.commons.ResponseError;
import com.services.commons.ResponseErrors;
import com.services.commons.WireMessage;
import com.services.http.HttpEndpoint;
import com.services.http.HttpMethod;
import com.services.http.HttpService;
import com.services.http.PathParameter;
import com.types.AliasTypeDefinition;
import com.types.FernFilepath;
import com.types.NamedType;
import com.types.PrimitiveType;
import com.types.Type;
import com.types.TypeReference;
import java.util.Collections;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class HttpServiceServerGeneratorTest {

    private static final String PACKAGE_PREFIX = "com";
    private static final GeneratorContext GENERATOR_CONTEXT = new GeneratorContext(
            Optional.of(PACKAGE_PREFIX),
            Collections.emptyMap());

    @Test
    public void test_basic() {
        HttpService testHttpService = HttpService.builder()
                .basePath("/person")
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("fern"))
                        .name("PersonCrudService")
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("getPerson")
                        .path("/{personId}")
                        .method(HttpMethod.GET)
                        .errors(ResponseErrors.builder().discriminant("").build())
                        .addParameters(PathParameter.builder()
                                .key("personId")
                                .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                .build())
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("createPerson")
                        .path("/create")
                        .method(HttpMethod.POST)
                        .errors(ResponseErrors.builder().discriminant("").build())
                        .request(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.named(NamedType.builder()
                                                .fernFilepath(FernFilepath.valueOf("fern"))
                                                .name("CreatePersonRequest")
                                                .build()))
                                        .build()))
                                .build())
                        .response(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.primitive(PrimitiveType.STRING))
                                        .build()))
                                .build())
                        .build())
                .build();
        HttpServiceServerGenerator httpServiceServerGenerator = new HttpServiceServerGenerator(
                GENERATOR_CONTEXT, Collections.emptyMap(), Collections.emptyList(), testHttpService);
        GeneratedHttpServiceServer generatedHttpServiceClient = httpServiceServerGenerator.generate();
        System.out.println(generatedHttpServiceClient.file().toString());
    }

    @Test
    public void test_withErrors() {
        ErrorDefinition personIdNotFound = ErrorDefinition.builder()
            .name(NamedType.builder()
                .fernFilepath(FernFilepath.valueOf("fern"))
                .name("PersonIdNotFound")
                .build())
            .addProperties(ErrorProperty.builder()
                .name("personId")
                .type(TypeReference.primitive(PrimitiveType.STRING))
                .build())
            .http(HttpErrorConfiguration.builder()
                .statusCode(400)
                .build())
            .build();
        HttpService testHttpService = HttpService.builder()
                .basePath("/person")
                .name(NamedType.builder()
                        .fernFilepath(FernFilepath.valueOf("fern"))
                        .name("PersonCrudService")
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("getPerson")
                        .path("/{personId}")
                        .method(HttpMethod.GET)
                        .errors(ResponseErrors.builder()
                                .discriminant("_type")
                                .addPossibleErrors(ResponseError.builder()
                                    .discriminantValue("notFound")
                                    .error(personIdNotFound.name())
                                    .build())
                                .build())
                        .addParameters(PathParameter.builder()
                                .key("personId")
                                .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                .build())
                        .build())
                .addEndpoints(HttpEndpoint.builder()
                        .endpointId("createPerson")
                        .path("/create")
                        .method(HttpMethod.POST)
                        .errors(ResponseErrors.builder()
                                .discriminant("_type")
                                .build())
                        .request(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.named(NamedType.builder()
                                                .fernFilepath(FernFilepath.valueOf("fern"))
                                                .name("CreatePersonRequest")
                                                .build()))
                                        .build()))
                                .build())
                        .response(WireMessage.builder()
                                .type(Type.alias(AliasTypeDefinition.builder()
                                        .aliasOf(TypeReference.primitive(PrimitiveType.STRING))
                                        .build()))
                                .build())
                        .build())
                .build();
        ExceptionGenerator personIdNotFoundClientExceptionGenerator =
                new ExceptionGenerator(GENERATOR_CONTEXT, personIdNotFound, false);
        GeneratedException personIdNotFoundException = personIdNotFoundClientExceptionGenerator.generate();
        HttpServiceServerGenerator httpServiceServerGenerator = new HttpServiceServerGenerator(
                GENERATOR_CONTEXT,
                Collections.emptyMap(),
                Collections.singletonList(personIdNotFoundException),
                testHttpService);
        GeneratedHttpServiceServer generatedHttpServiceServer = httpServiceServerGenerator.generate();
        System.out.println(generatedHttpServiceServer.file().toString());
    }
}
