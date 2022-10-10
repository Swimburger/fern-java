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

package com.fern.codegen.generator.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fern.codegen.PoetTypeWithClassName;
import com.fern.codegen.generator.DefaultMethodGenerators;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public final class GenericObjectGenerator {

    private final ClassName objectClassName;
    private final List<EnrichedObjectProperty> allEnrichedProperties = new ArrayList<>();
    private final List<ImplementsInterface> interfaces;
    private final boolean isSerialized;
    private final Optional<ClassName> enclosingClass;

    public GenericObjectGenerator(
            ClassName objectClassName,
            List<EnrichedObjectProperty> enrichedObjectProperties,
            List<ImplementsInterface> interfaces,
            boolean isSerialized,
            Optional<ClassName> enclosingClass) {
        this.objectClassName = objectClassName;
        this.interfaces = interfaces;
        for (ImplementsInterface implementsInterface : interfaces) {
            allEnrichedProperties.addAll(implementsInterface.interfaceProperties());
        }
        allEnrichedProperties.addAll(enrichedObjectProperties);
        this.isSerialized = isSerialized;
        this.enclosingClass = enclosingClass;
    }

    public GenericObjectGenerator(
            ClassName objectClassName,
            List<EnrichedObjectProperty> enrichedObjectProperties,
            List<ImplementsInterface> interfaces,
            boolean isSerialized) {
        this(objectClassName, enrichedObjectProperties, interfaces, isSerialized, Optional.empty());
    }

    public TypeSpec generate() {
        Optional<MethodSpec> equalToMethod = generateEqualToMethod();
        Optional<ObjectBuilder> maybeObjectBuilder = generateBuilder();
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(objectClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addFields(allEnrichedProperties.stream()
                        .map(EnrichedObjectProperty::fieldSpec)
                        .collect(Collectors.toList()))
                .addField(int.class, HashCodeConstants.CACHED_HASH_CODE_FIELD_NAME, Modifier.PRIVATE)
                .addSuperinterfaces(interfaces.stream()
                        .map(ImplementsInterface::interfaceClassName)
                        .collect(Collectors.toList()))
                .addMethod(generatePrivateConstructor())
                .addMethods(allEnrichedProperties.stream()
                        .map(EnrichedObjectProperty::getterProperty)
                        .collect(Collectors.toList()))
                .addMethod(generateEqualsMethod(equalToMethod));
        equalToMethod.ifPresent(typeSpecBuilder::addMethod);
        typeSpecBuilder.addMethod(generateHashCode()).addMethod(generateToString());
        if (maybeObjectBuilder.isPresent()) {
            ObjectBuilder objectBuilder = maybeObjectBuilder.get();
            typeSpecBuilder.addMethod(objectBuilder.getBuilderStaticMethod());
            typeSpecBuilder.addTypes(objectBuilder.getGeneratedTypes().stream()
                    .map(PoetTypeWithClassName::typeSpec)
                    .collect(Collectors.toList()));
            if (isSerialized) {
                typeSpecBuilder.addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
                        .addMember("builder", "$T.class", objectBuilder.getBuilderImplClassName())
                        .build());
            }
        }
        if (enclosingClass.isPresent()) {
            typeSpecBuilder.addModifiers(Modifier.STATIC);
        }
        return typeSpecBuilder.build();
    }

    private MethodSpec generatePrivateConstructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        allEnrichedProperties.stream().map(EnrichedObjectProperty::fieldSpec).forEach(fieldSpec -> {
            ParameterSpec parameterSpec =
                    ParameterSpec.builder(fieldSpec.type, fieldSpec.name).build();
            constructorBuilder.addParameter(parameterSpec);
            constructorBuilder.addStatement("this.$L = $L", fieldSpec.name, fieldSpec.name);
        });
        return constructorBuilder.build();
    }

    private Optional<MethodSpec> generateEqualToMethod() {
        return DefaultMethodGenerators.generateEqualToMethod(
                objectClassName,
                allEnrichedProperties.stream()
                        .map(EnrichedObjectProperty::fieldSpec)
                        .collect(Collectors.toList()));
    }

    private MethodSpec generateEqualsMethod(Optional<MethodSpec> equalToMethod) {
        return DefaultMethodGenerators.generateEqualsMethod(objectClassName, equalToMethod);
    }

    private MethodSpec generateHashCode() {
        return DefaultMethodGenerators.generateHashCode(
                allEnrichedProperties.stream()
                        .map(EnrichedObjectProperty::fieldSpec)
                        .collect(Collectors.toList()),
                true);
    }

    private MethodSpec generateToString() {
        return DefaultMethodGenerators.generateToString(
                enclosingClass,
                objectClassName,
                allEnrichedProperties.stream()
                        .map(EnrichedObjectProperty::fieldSpec)
                        .collect(Collectors.toList()));
    }

    private Optional<ObjectBuilder> generateBuilder() {
        BuilderGenerator builderGenerator = new BuilderGenerator(objectClassName, allEnrichedProperties, isSerialized);
        return builderGenerator.generate();
    }

    private static final class HashCodeConstants {
        private static final String CACHED_HASH_CODE_FIELD_NAME = "_cachedHashCode";
    }
}
