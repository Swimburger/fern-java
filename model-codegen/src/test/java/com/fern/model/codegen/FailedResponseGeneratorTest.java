package com.fern.model.codegen;

import static org.mockito.Mockito.when;

import com.fern.codegen.GeneratedFile;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.stateless.generator.ImmutablesStyleGenerator;
import com.fern.codegen.utils.ClassNameUtils;
import com.fern.java.test.TestConstants;
import com.fern.model.codegen.services.payloads.FailedResponseGenerator;
import com.fern.types.errors.ErrorDefinition;
import com.fern.types.errors.HttpErrorConfiguration;
import com.fern.types.services.commons.FailedResponse;
import com.fern.types.services.commons.ResponseError;
import com.fern.types.services.http.HttpEndpoint;
import com.fern.types.services.http.HttpService;
import com.fern.types.types.FernFilepath;
import com.fern.types.types.NamedType;
import com.fern.types.types.ObjectProperty;
import com.fern.types.types.ObjectTypeDefinition;
import com.fern.types.types.PrimitiveType;
import com.fern.types.types.Type;
import com.fern.types.types.TypeReference;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FailedResponseGeneratorTest {

    @Mock
    HttpService httpService;

    @Mock
    HttpEndpoint httpEndpoint;

    @Test
    public void test_basic() {
        when(httpService.name()).thenReturn(NamedType.builder()
                .fernFilepath(FernFilepath.valueOf("fern"))
                .name("UntitiledService")
                .build());
        when(httpEndpoint.endpointId()).thenReturn("getPlaylist");
        NamedType noViewPermissionsError = NamedType.builder()
                .fernFilepath(FernFilepath.valueOf("fern"))
                .name("NoViewPermissionsError")
                .build();
        FailedResponse failedResponse = FailedResponse.builder()
                .discriminant("_type")
                .addErrors(ResponseError.builder()
                    .discriminantValue("notFoundError")
                    .error(TypeReference.primitive(PrimitiveType.STRING))
                    .build())
                .addErrors(ResponseError.builder()
                        .discriminantValue("noViewPermissions")
                        .error(TypeReference.named(noViewPermissionsError))
                        .build())
                .build();
        GeneratorContext generatorContext = new GeneratorContext(
                Optional.of(TestConstants.PACKAGE_PREFIX),
                Collections.emptyMap(),
                Collections.singletonMap(
                        noViewPermissionsError,
                        ErrorDefinition.builder().
                                name(noViewPermissionsError)
                                .type(Type._object(ObjectTypeDefinition.builder()
                                        .addProperties(ObjectProperty.builder()
                                                .key("msg")
                                                .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                                .build())
                                        .build()))
                                .http(HttpErrorConfiguration.builder().statusCode(500).build())
                                .build()));
        FailedResponseGenerator failedResponseGenerator =
                new FailedResponseGenerator(httpService, httpEndpoint, failedResponse, generatorContext);
        GeneratedFile generatedError = failedResponseGenerator.generate();
        System.out.println(generatedError.file().toString());
    }
}
