package com.fern.model.codegen.interfaces;

import com.fern.FernFilepath;
import com.fern.NamedType;
import com.fern.ObjectTypeDefinition;
import com.fern.model.codegen.Generator;
import com.fern.model.codegen.GeneratorContext;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public final class InterfaceGenerator extends Generator<ObjectTypeDefinition> {

    private static final String INTERFACE_PREFIX = "I";
    private static final String INTERFACES_PACKAGE_NAME = "interfaces";

    private final ObjectTypeDefinition objectTypeDefinition;
    private final NamedType namedType;

    public InterfaceGenerator(
            ObjectTypeDefinition objectTypeDefinition,
            NamedType namedType,
            GeneratorContext generatorContext) {
        super(generatorContext);
        this.objectTypeDefinition = objectTypeDefinition;
        this.namedType = namedType;
    }

    public GeneratedInterface generate() {
        ClassName generatedInterfaceClassName = getInterfaceClassName();
        TypeSpec interfaceTypeSpec = TypeSpec.interfaceBuilder(INTERFACE_PREFIX + namedType.name())
                .addMethods(generatorContext.getImmutablesUtils().getImmutablesPropertyMethods(objectTypeDefinition))
                .build();
        JavaFile interfaceFile = JavaFile.builder(generatedInterfaceClassName.packageName(), interfaceTypeSpec)
                .build();
        return GeneratedInterface.builder()
                .file(interfaceFile)
                .definition(objectTypeDefinition)
                .className(generatedInterfaceClassName)
                .build();
    }

    private ClassName getInterfaceClassName() {
        String nonInterfacePackageName =
                generatorContext.getFilepathUtils().convertFilepathToPackage(namedType.fernFilepath());
        return ClassName.get(nonInterfacePackageName + "." + INTERFACES_PACKAGE_NAME, namedType.name());
    }
}
