package com.fern.model.codegen;

import com.fern.*;
import com.fern.model.codegen._interface.GeneratedInterface;
import com.fern.model.codegen._interface.InterfaceGenerator;
import com.fern.model.codegen.object.GeneratedObject;
import com.fern.model.codegen.object.ObjectGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ObjectGeneratorTest {

    @Test
    public void test_basic() {
        ObjectTypeDefinition objectTypeDefinition = ObjectTypeDefinition.builder()
                .addFields(ObjectField.builder()
                        .key("docs")
                        .valueType(TypeReference.container(ContainerType.optional(
                                TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .build();
        GeneratedObject generatedObject = ObjectGenerator.generate(
                Collections.emptyList(),
                NamedTypeReference.builder().filepath("com/fern").name("WithDocs").build(),
                objectTypeDefinition,
                false);
        System.out.println(generatedObject.file().toString());
    }

    @Test
    public void test_skipOwnFields() {
        ObjectTypeDefinition withDocsObjectTypeDefinition = ObjectTypeDefinition.builder()
                .addFields(ObjectField.builder()
                        .key("docs")
                        .valueType(TypeReference.container(ContainerType.optional(
                                TypeReference.primitive(PrimitiveType.STRING))))
                        .build())
                .build();
        TypeDefinition withDocsTypeDefinition = TypeDefinition.builder()
                .name(NamedTypeReference.builder()
                        .filepath("com/fern")
                        .name("WithDocs")
                        .build())
                .shape(Type.object(withDocsObjectTypeDefinition))
                .build();
        GeneratedInterface withDocsInterface = InterfaceGenerator.generate(withDocsTypeDefinition);
        GeneratedObject withDocsObject = ObjectGenerator.generate(
                Collections.singletonList(withDocsInterface),
                withDocsTypeDefinition.name(),
                withDocsObjectTypeDefinition,
                true);
        System.out.println(withDocsObject.file().toString());
    }

}
