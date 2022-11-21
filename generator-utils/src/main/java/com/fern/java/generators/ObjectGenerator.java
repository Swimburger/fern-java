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
package com.fern.java.generators;

import com.fern.ir.model.types.DeclaredTypeName;
import com.fern.ir.model.types.ObjectTypeDeclaration;
import com.fern.java.AbstractGeneratorContext;
import com.fern.java.PoetTypeNameMapper;
import com.fern.java.generators.object.EnrichedObjectProperty;
import com.fern.java.generators.object.ImplementsInterface;
import com.fern.java.generators.object.ObjectTypeSpecGenerator;
import com.fern.java.output.GeneratedJavaFile;
import com.fern.java.output.GeneratedJavaInterface;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public final class ObjectGenerator extends AbstractFileGenerator {
    private final ObjectTypeDeclaration objectTypeDeclaration;
    private final Optional<GeneratedJavaInterface> selfInterface;
    private final Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces;
    private final List<GeneratedJavaInterface> extendedInterfaces = new ArrayList<>();

    public ObjectGenerator(
            ObjectTypeDeclaration objectTypeDeclaration,
            Optional<GeneratedJavaInterface> selfInterface,
            List<GeneratedJavaInterface> extendedInterfaces,
            AbstractGeneratorContext generatorContext,
            Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces,
            ClassName className) {
        super(className, generatorContext);
        this.objectTypeDeclaration = objectTypeDeclaration;
        this.selfInterface = selfInterface;
        selfInterface.ifPresent(this.extendedInterfaces::add);
        this.extendedInterfaces.addAll(extendedInterfaces);
        this.allGeneratedInterfaces = allGeneratedInterfaces;
    }

    @Override
    public GeneratedJavaFile generateFile() {
        PoetTypeNameMapper poetTypeNameMapper = generatorContext.getPoetTypeNameMapper();
        List<EnrichedObjectProperty> enrichedObjectProperties = new ArrayList<>();
        if (selfInterface.isEmpty()) {
            enrichedObjectProperties = objectTypeDeclaration.getProperties().stream()
                    .map(objectProperty -> EnrichedObjectProperty.of(
                            objectProperty,
                            false,
                            poetTypeNameMapper.convertToTypeName(true, objectProperty.getValueType())))
                    .collect(Collectors.toList());
        }
        List<ImplementsInterface> implementsInterfaces = new ArrayList<>();
        Set<GeneratedJavaInterface> visited = new HashSet<>();
        extendedInterfaces.stream()
                .map(generatedInterface -> {
                    List<EnrichedObjectProperty> enrichedProperties = new ArrayList<>();
                    Queue<GeneratedJavaInterface> interfaceQueue = new LinkedList<>();
                    interfaceQueue.add(generatedInterface);
                    while (!interfaceQueue.isEmpty()) {
                        GeneratedJavaInterface generatedJavaInterface = interfaceQueue.poll();
                        if (visited.contains(generatedJavaInterface)) {
                            continue;
                        }
                        interfaceQueue.addAll(generatedJavaInterface.extendedInterfaces().stream()
                                .map(allGeneratedInterfaces::get)
                                .collect(Collectors.toList()));
                        enrichedProperties.addAll(getEnrichedObjectProperties(generatedJavaInterface));
                        visited.add(generatedJavaInterface);
                    }
                    return ImplementsInterface.builder()
                            .interfaceClassName(generatedInterface.getClassName())
                            .addAllInterfaceProperties(enrichedProperties)
                            .build();
                })
                .forEach(implementsInterfaces::add);
        ObjectTypeSpecGenerator genericObjectGenerator =
                new ObjectTypeSpecGenerator(className, enrichedObjectProperties, implementsInterfaces, true);
        TypeSpec objectTypeSpec = genericObjectGenerator.generate();
        JavaFile javaFile =
                JavaFile.builder(className.packageName(), objectTypeSpec).build();
        return GeneratedJavaFile.builder()
                .className(className)
                .javaFile(javaFile)
                .build();
    }

    private static List<EnrichedObjectProperty> getEnrichedObjectProperties(
            GeneratedJavaInterface generatedJavaInterface) {
        return generatedJavaInterface.propertyMethodSpecs().stream()
                .map(propertyMethodSpec -> EnrichedObjectProperty.of(
                        propertyMethodSpec.objectProperty(), true, propertyMethodSpec.methodSpec().returnType))
                .collect(Collectors.toList());
    }
}
