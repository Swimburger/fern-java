package com.fern.jersey;

import com.fern.codegen.GeneratedException;
import com.fern.java.test.TestConstants;
import com.fern.types.errors.ErrorDefinition;
import com.fern.types.errors.HttpErrorConfiguration;
import com.fern.types.types.FernFilepath;
import com.fern.types.types.NamedType;
import com.fern.types.types.ObjectProperty;
import com.fern.types.types.ObjectTypeDefinition;
import com.fern.types.types.PrimitiveType;
import com.fern.types.types.Type;
import com.fern.types.types.TypeReference;
import org.junit.jupiter.api.Test;

public class ExceptionGeneratorTest {

    @Test
    public void test_client() {
        ExceptionGenerator exceptionGenerator = new ExceptionGenerator(
                TestConstants.GENERATOR_CONTEXT,
                ErrorDefinition.builder()
                        .name(NamedType.builder()
                                .fernFilepath(FernFilepath.valueOf("/fern"))
                                .name("NotFoundError")
                                .build())
                        .type(Type._object(ObjectTypeDefinition.builder()
                                .addProperties(ObjectProperty.builder()
                                        .key("a")
                                        .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                        .build())
                                .build()))
                        .build(),
                false);
        GeneratedException generatedException = exceptionGenerator.generate();
        System.out.println(generatedException.file().toString());
    }

    @Test
    public void test_server() {
        ExceptionGenerator exceptionGenerator = new ExceptionGenerator(
                TestConstants.GENERATOR_CONTEXT,
                ErrorDefinition.builder()
                        .name(NamedType.builder()
                                .fernFilepath(FernFilepath.valueOf("/fern"))
                                .name("NotFoundError")
                                .build())
                        .type(Type._object(ObjectTypeDefinition.builder()
                                .addProperties(ObjectProperty.builder()
                                        .key("a")
                                        .valueType(TypeReference.primitive(PrimitiveType.STRING))
                                        .build())
                                .build()))
                        .http(HttpErrorConfiguration.builder().statusCode(500).build())
                        .build(),
                true);
        GeneratedException generatedException = exceptionGenerator.generate();
        System.out.println(generatedException.file().toString());
    }
}
