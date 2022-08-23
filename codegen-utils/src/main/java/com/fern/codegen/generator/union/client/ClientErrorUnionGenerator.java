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

package com.fern.codegen.generator.union.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fern.codegen.generator.union.GenericUnionGenerator;
import com.fern.codegen.generator.union.UnionSubType;
import com.fern.types.FernConstants;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.List;
import javax.lang.model.element.Modifier;

public final class ClientErrorUnionGenerator extends GenericUnionGenerator {

    private static final String DESERIALIZER_CLASS_NAME = "Deserializer";

    private final ClassName deserializerClassName;

    public ClientErrorUnionGenerator(
            ClassName unionClassName,
            List<UnionSubType> subTypes,
            UnionSubType unknownSubType,
            FernConstants fernConstants) {
        super(unionClassName, subTypes, unknownSubType, fernConstants);
        this.deserializerClassName = getUnionClassName().nestedClass(DESERIALIZER_CLASS_NAME);
    }

    @Override
    public List<FieldSpec> getAdditionalFieldSpecs() {
        return List.of(
                FieldSpec.builder(int.class, "statusCode", Modifier.PRIVATE).build());
    }

    @Override
    public TypeSpec build(TypeSpec.Builder unionBuilder) {
        return unionBuilder
                .addType(getCustomDeserializer())
                .addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
                        .addMember("using", "$T.class", deserializerClassName)
                        .build())
                .build();
    }

    private TypeSpec getCustomDeserializer() {
        return TypeSpec.classBuilder(deserializerClassName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ClassName.get(JsonDeserializer.class), getUnionClassName()))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .returns(getUnionClassName())
                        .addParameter(JsonParser.class, "p")
                        .addParameter(DeserializationContext.class, "ctx")
                        .addException(IOException.class)
                        .addStatement(
                                "$T value = $L.readValue($L, $T.class)",
                                getValueInterfaceClassName(),
                                "ctx",
                                "p",
                                getValueInterfaceClassName())
                        .addStatement("$T statusCode = (int) $L.getAttribute($S)", int.class, "statusCode")
                        .addStatement("return new $T($L, $L)", getUnionClassName(), "value", "statusCode")
                        .build())
                .build();
    }
}
