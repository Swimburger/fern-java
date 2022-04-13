package com.fern.model.codegen._interface;

import com.fern.NamedTypeReference;
import com.fern.ObjectTypeDefinition;
import com.fern.model.codegen.utils.ClassNameUtils;
import com.fern.model.codegen.utils.FilepathUtils;
import com.fern.model.codegen.utils.ImmutablesUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public class InterfaceGenerator {

    public static final InterfaceGenerator INSTANCE = new InterfaceGenerator();

    private static final String INTERFACE_PREFIX = "I";

    private InterfaceGenerator() {}

    public GeneratedInterface generate(
            ObjectTypeDefinition objectTypeDefinition, NamedTypeReference namedTypeReference) {
        ClassName generatedInterfaceClassName = getInterfaceClassName(namedTypeReference);
        TypeSpec interfaceTypeSpec = TypeSpec.interfaceBuilder(INTERFACE_PREFIX + namedTypeReference.name())
                .addMethods(ImmutablesUtils.getImmutablesPropertyMethods(objectTypeDefinition))
                .build();
        JavaFile interfaceFile = JavaFile.builder(
                        FilepathUtils.convertFilepathToPackage(namedTypeReference.filepath()), interfaceTypeSpec)
                .build();
        return GeneratedInterface.builder()
                .file(interfaceFile)
                .definition(objectTypeDefinition)
                .className(generatedInterfaceClassName)
                .build();
    }

    private static ClassName getInterfaceClassName(NamedTypeReference namedTypeReference) {
        NamedTypeReference interfaceNamedTypeReference = NamedTypeReference.builder()
                .filepath(namedTypeReference.filepath())
                .name(INTERFACE_PREFIX + namedTypeReference.name())
                .build();
        return ClassNameUtils.getClassName(interfaceNamedTypeReference);
    }
}
