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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fern.codegen.generator.union.UnionSubType;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;

public final class ClientErrorUnionOtherSubType extends UnionSubType {

    private final List<FieldSpec> fieldSpecs = new ArrayList<>();
    private final List<MethodSpec> constructors = new ArrayList<>();

    public ClientErrorUnionOtherSubType(ClassName unionClassName) {
        super(unionClassName);
        this.fieldSpecs.add(getValueField());
        this.constructors.add(getFromJsonConstructor());
    }

    @Override
    public String getStaticFactoryMethodName() {
        return "other";
    }

    @Override
    public Optional<String> getDiscriminantValue() {
        return Optional.empty();
    }

    @Override
    public ClassName getUnionSubTypeClassName() {
        return ClassName.get(Object.class);
    }

    @Override
    public ClassName getUnionSubTypeWrapperClass() {
        return getUnionClassName().nestedClass("UnknownErrorValue");
    }

    @Override
    public List<FieldSpec> getFieldSpecs() {
        return fieldSpecs;
    }

    @Override
    public List<MethodSpec> getConstructors() {
        return constructors;
    }

    @Override
    public MethodSpec getVisitorMethodInterface() {
        return MethodSpec.methodBuilder("visitOtherError")
                .returns(TypeVariableName.get("T"))
                .addParameter(ParameterSpec.builder(getUnionSubTypeClassName(), "errorBody")
                        .build())
                .build();
    }

    @Override
    public MethodSpec getStaticFactory() {
        return MethodSpec.methodBuilder(getStaticFactoryMethodName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(getUnionClassName())
                .addParameter(getUnionSubTypeClassName(), getValueFieldName())
                .addParameter(int.class, "statusCode")
                .addStatement(
                        "new $T(new $T($L), $L)",
                        getUnionClassName(),
                        getUnionSubTypeWrapperClass(),
                        getValueFieldName(),
                        "statusCode")
                .build();
    }

    private FieldSpec getValueField() {
        return FieldSpec.builder(getUnionSubTypeClassName(), getValueFieldName(), Modifier.PRIVATE)
                .build();
    }

    private MethodSpec getFromJsonConstructor() {
        return MethodSpec.constructorBuilder()
                .addAnnotation(AnnotationSpec.builder(JsonCreator.class)
                        .addMember(
                                "mode",
                                "$T.$L.$L",
                                JsonCreator.class,
                                JsonCreator.Mode.class.getSimpleName(),
                                Mode.DELEGATING.name())
                        .build())
                .addParameter(ParameterSpec.builder(getUnionSubTypeClassName(), getValueFieldName())
                        .addAnnotation(AnnotationSpec.builder(JsonProperty.class)
                                .addMember("value", "$S", getValueFieldName())
                                .build())
                        .build())
                .addStatement("this.$L = $L", getValueFieldName(), getValueFieldName())
                .build();
    }

    @Override
    public String getValueFieldName() {
        return "unknownValue";
    }
}
