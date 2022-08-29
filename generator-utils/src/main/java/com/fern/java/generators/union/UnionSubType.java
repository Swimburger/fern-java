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

package com.fern.java.generators.union;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fern.java.ObjectMethodFactory;
import com.fern.java.ObjectMethodFactory.EqualsMethod;
import com.fern.java.utils.KeyWordUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.List;
import java.util.Optional;
import javax.lang.model.element.Modifier;

public abstract class UnionSubType {

    private static final String VISITOR_CLASS_NAME = "Visitor";

    private static final TypeVariableName VISITOR_RETURN_TYPE = TypeVariableName.get("T");

    private final ClassName unionClassName;
    private final ParameterizedTypeName visitorInterfaceClassName;

    public UnionSubType(ClassName unionClassName) {
        this.unionClassName = unionClassName;
        this.visitorInterfaceClassName =
                ParameterizedTypeName.get(unionClassName.nestedClass(VISITOR_CLASS_NAME), VISITOR_RETURN_TYPE);
    }

    public final ClassName getUnionClassName() {
        return unionClassName;
    }

    public abstract String getCamelCaseName();

    public abstract String getPascalCaseName();

    public abstract Optional<String> getDiscriminantValue();

    public abstract Optional<TypeName> getUnionSubTypeTypeName();

    public abstract ClassName getUnionSubTypeWrapperClass();

    public abstract List<FieldSpec> getFieldSpecs();

    public abstract List<MethodSpec> getConstructors();

    @SuppressWarnings("checkstyle:DesignForExtension")
    public String getValueFieldName() {
        return "value";
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    public Optional<MethodSpec> getStaticFactory() {
        MethodSpec.Builder staticFactoryBuilder = MethodSpec.methodBuilder(
                        KeyWordUtils.getKeyWordCompatibleName(getCamelCaseName()))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(unionClassName);
        if (getUnionSubTypeTypeName().isPresent()) {
            staticFactoryBuilder
                    .addParameter(getUnionSubTypeTypeName().get(), getValueFieldName())
                    .addStatement(
                            "return new $T(new $T($L))",
                            unionClassName,
                            getUnionSubTypeWrapperClass(),
                            getValueFieldName());
        } else {
            staticFactoryBuilder.addStatement("return new $T(new $T())", unionClassName, getUnionSubTypeWrapperClass());
        }
        return Optional.of(staticFactoryBuilder.build());
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    public String getVisitMethodName() {
        return "visit" + getPascalCaseName();
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    public String getIsMethodName() {
        return "is" + getPascalCaseName();
    }

    public final MethodSpec getVisitorInterfaceVisitMethod() {
        MethodSpec.Builder visitMethodBuilder = MethodSpec.methodBuilder(getVisitMethodName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(TypeVariableName.get("T"));
        if (getUnionSubTypeTypeName().isPresent()) {
            visitMethodBuilder.addParameter(ParameterSpec.builder(
                            getUnionSubTypeTypeName().get(), KeyWordUtils.getKeyWordCompatibleName(getCamelCaseName()))
                    .build());
        }
        return visitMethodBuilder.build();
    }

    public final MethodSpec getVisitMethod() {
        MethodSpec.Builder visitMethodBuilder = MethodSpec.methodBuilder("visit")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addTypeVariable(VISITOR_RETURN_TYPE)
                .returns(VISITOR_RETURN_TYPE)
                .addParameter(ParameterSpec.builder(visitorInterfaceClassName, "visitor")
                        .build());
        if (getUnionSubTypeTypeName().isPresent()) {
            visitMethodBuilder.addStatement(
                    "return $L.$N($L)", "visitor", getVisitorInterfaceVisitMethod(), getValueFieldName());
        } else {
            visitMethodBuilder.addStatement("return $L.$N()", "visitor", getVisitorInterfaceVisitMethod());
        }
        return visitMethodBuilder.build();
    }

    public final EqualsMethod getEqualsMethod() {
        return ObjectMethodFactory.createEqualsMethod(getUnionSubTypeWrapperClass(), getFieldSpecs());
    }

    public final Optional<MethodSpec> getHashCodeMethod() {
        return ObjectMethodFactory.createHashCodeMethod(getFieldSpecs(), false);
    }

    public final MethodSpec getToStringMethod() {
        return ObjectMethodFactory.createToStringMethod(unionClassName, getFieldSpecs());
    }

    public final TypeSpec getUnionSubTypeWrapper(ClassName unionWrapperInterface) {
        ClassName subTypeWrapperClassName = getUnionSubTypeWrapperClass();
        TypeSpec.Builder unionSubTypeBuilder = TypeSpec.classBuilder(subTypeWrapperClassName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addSuperinterface(unionWrapperInterface);
        if (getDiscriminantValue().isPresent()) {
            unionSubTypeBuilder.addAnnotation(AnnotationSpec.builder(JsonTypeName.class)
                    .addMember("value", "$S", getDiscriminantValue().get())
                    .build());
        }
        EqualsMethod equalsMethod = getEqualsMethod();
        unionSubTypeBuilder
                .addFields(getFieldSpecs())
                .addMethods(getConstructors())
                .addMethod(getVisitMethod())
                .addMethod(equalsMethod.getEqualsMethodSpec());
        equalsMethod.getEqualToMethodSpec().ifPresent(unionSubTypeBuilder::addMethod);
        getHashCodeMethod().ifPresent(unionSubTypeBuilder::addMethod);
        return unionSubTypeBuilder.addMethod(getToStringMethod()).build();
    }
}
