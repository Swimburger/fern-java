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
package com.fern.model.codegen.types;

import com.fern.codegen.GeneratedInterface;
import com.fern.codegen.Generator;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.ir.model.types.DeclaredTypeName;
import com.fern.ir.model.types.ObjectProperty;
import com.fern.ir.model.types.ObjectTypeDeclaration;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public final class InterfaceGenerator extends Generator {

    private static final String INTERFACE_PREFIX = "I";

    private final ObjectTypeDeclaration objectTypeDeclaration;
    private final DeclaredTypeName declaredTypeName;

    public InterfaceGenerator(
            ObjectTypeDeclaration objectTypeDeclaration,
            DeclaredTypeName declaredTypeName,
            GeneratorContext generatorContext) {
        super(generatorContext);
        this.objectTypeDeclaration = objectTypeDeclaration;
        this.declaredTypeName = DeclaredTypeName.builder()
                .fernFilepath(declaredTypeName.getFernFilepath())
                .fernFilepathV2(declaredTypeName.getFernFilepathV2())
                .name(INTERFACE_PREFIX + declaredTypeName.getName())
                .nameV2(declaredTypeName.getNameV2())
                .nameV3(declaredTypeName.getNameV3())
                .build();
    }

    @Override
    public GeneratedInterface generate() {
        ClassName generatedInterfaceClassName = generatorContext
                .getClassNameUtils()
                .getClassNameFromDeclaredTypeName(declaredTypeName, PackageType.MODEL);
        Map<ObjectProperty, MethodSpec> methodSpecsByProperties = getPropertyGetters();
        TypeSpec interfaceTypeSpec = TypeSpec.interfaceBuilder(generatedInterfaceClassName.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecsByProperties.values())
                .build();
        JavaFile interfaceFile = JavaFile.builder(generatedInterfaceClassName.packageName(), interfaceTypeSpec)
                .build();
        return GeneratedInterface.builder()
                .file(interfaceFile)
                .className(generatedInterfaceClassName)
                .objectTypeDeclaration(objectTypeDeclaration)
                .putAllMethodSpecsByProperties(methodSpecsByProperties)
                .build();
    }

    private Map<ObjectProperty, MethodSpec> getPropertyGetters() {
        return objectTypeDeclaration.getProperties().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        objectProperty -> {
                            TypeName poetTypeName = generatorContext
                                    .getClassNameUtils()
                                    .getTypeNameFromTypeReference(true, objectProperty.getValueType());
                            return MethodSpec.methodBuilder(
                                            "get" + objectProperty.getName().getPascalCase())
                                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                    .returns(poetTypeName)
                                    .build();
                        },
                        (u, _v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }
}
