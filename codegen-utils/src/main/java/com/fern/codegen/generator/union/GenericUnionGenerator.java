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

package com.fern.codegen.generator.union;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fern.codegen.utils.ClassNameConstants;
import com.fern.types.FernConstants;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;

public abstract class GenericUnionGenerator {

    private static final String VISITOR_CLASS_NAME = "Visitor";
    private static final String IS_METHOD_NAME_PREFIX = "is";
    private static final TypeVariableName VISITOR_RETURN_TYPE = TypeVariableName.get("T");

    private final ClassName unionClassName;
    private final ClassName valueInterfaceClassName;
    private final List<? extends UnionSubType> subTypes;
    private final UnionSubType unknownSubType;
    private final FernConstants fernConstants;
    private final ParameterizedTypeName visitorInterfaceClassName;

    public GenericUnionGenerator(
            ClassName unionClassName,
            List<? extends UnionSubType> subTypes,
            UnionSubType unknownSubType,
            FernConstants fernConstants) {
        this.unionClassName = unionClassName;
        this.subTypes = subTypes;
        this.unknownSubType = unknownSubType;
        this.fernConstants = fernConstants;
        this.valueInterfaceClassName = unionClassName.nestedClass("Value");
        this.visitorInterfaceClassName =
                ParameterizedTypeName.get(unionClassName.nestedClass(VISITOR_CLASS_NAME), VISITOR_RETURN_TYPE);
    }

    public abstract List<FieldSpec> getAdditionalFieldSpecs();

    public abstract TypeSpec build(TypeSpec.Builder unionBuilder);

    @SuppressWarnings("checkstyle:DesignForExtension")
    public String getValueFieldName() {
        return "value";
    }

    public final TypeSpec generateErrorUnion() {
        TypeSpec.Builder errorUnionBuilder = TypeSpec.classBuilder(unionClassName)
                .addField(getValueFieldSpec())
                .addFields(getAdditionalFieldSpecs())
                .addMethod(getConstructor())
                .addMethod(MethodSpec.methodBuilder("visit")
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(VISITOR_RETURN_TYPE)
                        .returns(VISITOR_RETURN_TYPE)
                        .addParameter(visitorInterfaceClassName, "visitor")
                        .addStatement("return $L.$L($L)", getValueFieldName(), "visit", "visitor")
                        .build())
                .addMethods(getStaticConstructors())
                .addMethods(getIsSubTypeMethods())
                .addMethods(getSubTypeMethods())
                .addType(generateVisitorInterface())
                .addType(generateValueInterface())
                .addTypes(subTypes.stream()
                        .map(unionSubType -> unionSubType.getUnionSubTypeWrapper(valueInterfaceClassName))
                        .collect(Collectors.toList()))
                .addType(unknownSubType.getUnionSubTypeWrapper(valueInterfaceClassName));
        return build(errorUnionBuilder);
    }

    public final FieldSpec getValueFieldSpec() {
        return FieldSpec.builder(valueInterfaceClassName, getValueFieldName(), Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    public final MethodSpec getConstructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(valueInterfaceClassName, getValueFieldName())
                .addStatement("this.$L = $L", getValueFieldName(), getValueFieldName());
        getAdditionalFieldSpecs().forEach(fieldSpec -> {
            constructorBuilder.addParameter(
                    ParameterSpec.builder(fieldSpec.type, fieldSpec.name).build());
            constructorBuilder.addStatement("this.$L = $L", fieldSpec.name, fieldSpec.name);
        });
        return constructorBuilder.build();
    }

    public final List<MethodSpec> getStaticConstructors() {
        List<UnionSubType> allSubTypes = new ArrayList<>();
        allSubTypes.addAll(subTypes);
        allSubTypes.add(unknownSubType);
        return allSubTypes.stream().map(UnionSubType::getStaticFactory).collect(Collectors.toList());
    }

    public final List<MethodSpec> getIsSubTypeMethods() {
        List<UnionSubType> allSubTypes = new ArrayList<>();
        allSubTypes.addAll(subTypes);
        allSubTypes.add(unknownSubType);
        return allSubTypes.stream()
                .map(subType -> MethodSpec.methodBuilder(
                                IS_METHOD_NAME_PREFIX + StringUtils.capitalize(subType.getStaticFactoryMethodName()))
                        .addModifiers(Modifier.PUBLIC)
                        .returns(boolean.class)
                        .addStatement("return value instanceof $T", subType.getUnionSubTypeWrapperClass())
                        .build())
                .collect(Collectors.toList());
    }

    public final List<MethodSpec> getSubTypeMethods() {
        List<UnionSubType> allSubTypes = new ArrayList<>();
        allSubTypes.addAll(subTypes);
        allSubTypes.add(unknownSubType);
        return allSubTypes.stream()
                .map(subType -> {
                    String isTypeMethodName =
                            IS_METHOD_NAME_PREFIX + StringUtils.capitalize(subType.getStaticFactoryMethodName());
                    return MethodSpec.methodBuilder(
                                    "get" + StringUtils.capitalize(subType.getStaticFactoryMethodName()))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParameterizedTypeName.get(
                                    ClassNameConstants.OPTIONAL_CLASS_NAME, subType.getUnionSubTypeClassName()))
                            .beginControlFlow("if ($L())", isTypeMethodName)
                            .addStatement(
                                    "return $T.of((($T) value).$L)",
                                    ClassNameConstants.OPTIONAL_CLASS_NAME,
                                    subType.getUnionSubTypeWrapperClass(),
                                    subType.getValueFieldName())
                            .endControlFlow()
                            .addStatement("return $T.empty()", ClassNameConstants.OPTIONAL_CLASS_NAME)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public final TypeSpec generateVisitorInterface() {
        return TypeSpec.interfaceBuilder(VISITOR_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(VISITOR_RETURN_TYPE)
                .addMethods(subTypes.stream()
                        .map(UnionSubType::getVisitorMethodInterface)
                        .collect(Collectors.toList()))
                .addMethod(unknownSubType.getVisitorMethodInterface())
                .build();
    }

    public final TypeSpec generateValueInterface() {
        TypeSpec.Builder valueInterfaceBuilder = TypeSpec.interfaceBuilder(valueInterfaceClassName)
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
                        .addMember("use", "$T.Id.$L", JsonTypeInfo.class, Id.NAME.name())
                        .addMember("property", "$S", fernConstants.errorDiscriminant())
                        .addMember("visible", "$L", true)
                        .addMember("defaultImpl", "$T.class", unknownSubType.getUnionSubTypeWrapperClass())
                        .build());
        if (subTypes.size() > 0) {
            AnnotationSpec.Builder jsonSubTypeAnnotationBuilder = AnnotationSpec.builder(JsonSubTypes.class);
            subTypes.forEach(unionSubType -> {
                AnnotationSpec subTypeAnnotation = AnnotationSpec.builder(JsonSubTypes.Type.class)
                        .addMember("value", "$T.class", unionSubType.getUnionSubTypeWrapperClass())
                        .build();
                jsonSubTypeAnnotationBuilder.addMember("value", "$L", subTypeAnnotation);
            });
            valueInterfaceBuilder.addAnnotation(jsonSubTypeAnnotationBuilder.build());
        }

        return valueInterfaceBuilder
                .addAnnotation(AnnotationSpec.builder(JsonIgnoreProperties.class)
                        .addMember("ignoreUnknown", "$L", true)
                        .build())
                .addMethod(MethodSpec.methodBuilder("visit")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addTypeVariable(VISITOR_RETURN_TYPE)
                        .returns(VISITOR_RETURN_TYPE)
                        .addParameter(visitorInterfaceClassName, "visitor")
                        .build())
                .build();
    }

    public final ClassName getUnionClassName() {
        return unionClassName;
    }

    public final ClassName getValueInterfaceClassName() {
        return valueInterfaceClassName;
    }
}
