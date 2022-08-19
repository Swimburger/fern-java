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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public final class GenericObjectGenerator {

    private final ClassName objectClassName;
    private final List<EnrichedObjectProperty> allEnrichedProperties = new ArrayList<>();
    private final List<ImplementsInterface> interfaces;

    public GenericObjectGenerator(
            ClassName objectClassName,
            List<EnrichedObjectProperty> enrichedObjectProperties,
            List<ImplementsInterface> interfaces) {
        this.objectClassName = objectClassName;
        this.interfaces = interfaces;
        for (ImplementsInterface implementsInterface : interfaces) {
            allEnrichedProperties.addAll(implementsInterface.interfaceProperties());
        }
        allEnrichedProperties.addAll(enrichedObjectProperties);
    }

    public void generate() {}

    private MethodSpec generatePrivateConstructor(List<FieldSpec> fields) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();
        fields.forEach(fieldSpec -> {
            ParameterSpec parameterSpec =
                    ParameterSpec.builder(fieldSpec.type, fieldSpec.name).build();
            constructorBuilder.addParameter(parameterSpec);
            constructorBuilder.addStatement("this.$L = $L", fieldSpec.name, fieldSpec.name);
        });
        return constructorBuilder.build();
    }

    private MethodSpec generateEqualToMethod() {
        MethodSpec.Builder equalToMethodBuilder = MethodSpec.methodBuilder(EqualsConstants.EQUAL_TO_METHOD_NAME)
                .addModifiers(Modifier.PRIVATE)
                .returns(boolean.class)
                .addParameter(objectClassName, EqualsConstants.OTHER_PARAMETER);
        String expression = allEnrichedProperties.stream()
                .map(enrichedObjectProperty -> {
                    FieldSpec fieldSpec = enrichedObjectProperty.fieldSpec();
                    if (fieldSpec.type.isPrimitive()) {
                        return CodeBlock.builder()
                                .add("$L == $L.$L", fieldSpec.name, EqualsConstants.OTHER_PARAMETER, fieldSpec.name)
                                .build();
                    } else {
                        return CodeBlock.builder()
                                .add(
                                        "$L.equals($L.$L)",
                                        fieldSpec.name,
                                        EqualsConstants.OTHER_PARAMETER,
                                        fieldSpec.name)
                                .build();
                    }
                })
                .map(CodeBlock::toString)
                .collect(Collectors.joining(" && "));
        return equalToMethodBuilder.addStatement("return " + expression).build();
    }

    private MethodSpec generateEqualsMethod(MethodSpec equalToMethod) {
        return MethodSpec.methodBuilder(EqualsConstants.EQUALS_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Object.class, EqualsConstants.OTHER_PARAMETER)
                .addStatement("if (this == $L) return true", EqualsConstants.OTHER_PARAMETER)
                .addStatement(
                        "return $L instanceOf $T && $N(($T) $L)",
                        EqualsConstants.OTHER_PARAMETER,
                        objectClassName,
                        equalToMethod,
                        objectClassName,
                        EqualsConstants.OTHER_PARAMETER)
                .build();
    }

    private MethodSpec generateHashCode() {
        String commaDelimitedFields = allEnrichedProperties.stream()
                .map(enrichedObjectProperty -> "this." + enrichedObjectProperty.fieldSpec().name)
                .collect(Collectors.joining(", "));
        return MethodSpec.methodBuilder(HashCodeConstants.HASHCODE_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .beginControlFlow("if ($L == 0)", HashCodeConstants.CACHED_HASH_CODE_FIELD_NAME)
                .addStatement(
                        "$L = $T.hash($L)",
                        HashCodeConstants.CACHED_HASH_CODE_FIELD_NAME,
                        Objects.class,
                        commaDelimitedFields)
                .endControlFlow()
                .addStatement("return $L", HashCodeConstants.CACHED_HASH_CODE_FIELD_NAME)
                .build();
    }

    private MethodSpec generateToString() {
        StringBuilder codeBlock = new StringBuilder("\"" + objectClassName + "{\"");
        for (int i = 0; i < allEnrichedProperties.size(); ++i) {
            FieldSpec fieldSpec = allEnrichedProperties.get(i).fieldSpec();
            if (i == 0) {
                codeBlock
                        .append("+ \"")
                        .append(fieldSpec.name)
                        .append(": \" + ")
                        .append(fieldSpec.name);
            } else {
                codeBlock
                        .append("+ \", ")
                        .append(fieldSpec.name)
                        .append(": \" + ")
                        .append(fieldSpec.name);
            }
        }
        codeBlock.append("}");
        return MethodSpec.methodBuilder(ToStringConstants.TO_STRING_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return " + codeBlock)
                .build();
    }

    private void generateStagedBuilder() {}

    // Helpers

    private static final class EqualsConstants {
        private static final String EQUALS_METHOD_NAME = "equals";
        private static final String EQUAL_TO_METHOD_NAME = "equalTo";
        private static final String OTHER_PARAMETER = "other";
    }

    private static final class HashCodeConstants {
        private static final String HASHCODE_METHOD_NAME = "hashCode";

        private static final String CACHED_HASH_CODE_FIELD_NAME = "_cachedHashCode";
    }

    private static final class ToStringConstants {
        private static final String TO_STRING_METHOD_NAME = "toString";
    }
}
