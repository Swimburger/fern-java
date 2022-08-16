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

package com.fern.codegen.generator;

import com.fern.codegen.GeneratedFile;
import com.fern.codegen.GeneratedFileWithDependents;
import com.fern.codegen.Generator;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.IGeneratedFile;
import com.fern.codegen.utils.CasingUtils;
import com.fern.codegen.utils.ClassNameConstants;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.types.AuthScheme;
import com.fern.types.WithDocs;
import com.fern.types.services.HttpHeader;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;

public class AnyAuthGenerator extends Generator {

    private static final String AUTH_FIELD_NAME = "auth";
    private static final String EQUALS_METHOD_OTHER_PARAM_NAME = "other";
    private final ClassName generatedClassName;
    private final List<AuthScheme> authSchemes;
    private final AuthSchemeToGeneratedFile authSchemeToGeneratedFile;

    public AnyAuthGenerator(
            GeneratorContext generatorContext, PackageType packageType, String apiName, List<AuthScheme> authSchemes) {
        super(generatorContext);
        this.generatedClassName = generatorContext
                .getClassNameUtils()
                .getClassName(
                        CasingUtils.convertKebabCaseToUpperCamelCase(apiName),
                        Optional.of("Auth"),
                        Optional.empty(),
                        packageType);
        this.authSchemes = authSchemes;
        this.authSchemeToGeneratedFile = new AuthSchemeToGeneratedFile(generatorContext, packageType);
    }

    @Override
    public IGeneratedFile generate() {
        Map<AuthScheme, GeneratedFile> generatedAuthSchemes = authSchemes.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        authScheme -> authScheme.visit(this.authSchemeToGeneratedFile),
                        (u, _v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
        Map<AuthScheme, MethodSpec> isTypeMethods = getIsTypeMethods(generatedAuthSchemes);
        TypeSpec anyAuthTypeSpec = TypeSpec.interfaceBuilder(generatedClassName)
                .addField(Object.class, AUTH_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(Object.class, AUTH_FIELD_NAME)
                        .addStatement("this.$L = $L", AUTH_FIELD_NAME, AUTH_FIELD_NAME)
                        .build())
                .addMethods(getEqualsMethods())
                .addMethod(getHashCodeMethod())
                .addMethods(getStaticBuilderMethods(generatedAuthSchemes))
                .addMethods(isTypeMethods.values())
                .addMethods(getAuthTypeGetterMethods(isTypeMethods, generatedAuthSchemes))
                .build();
        JavaFile anyAuthFile = JavaFile.builder(generatedClassName.packageName(), anyAuthTypeSpec)
                .build();
        return GeneratedFileWithDependents.builder()
                .file(anyAuthFile)
                .className(generatedClassName)
                .addAllDependentFiles(generatedAuthSchemes.values())
                .build();
    }

    private List<MethodSpec> getEqualsMethods() {
        MethodSpec equalToMethod = MethodSpec.methodBuilder("equalTo")
                .addModifiers(Modifier.PRIVATE)
                .returns(boolean.class)
                .addParameter(generatedClassName, EQUALS_METHOD_OTHER_PARAM_NAME)
                .addStatement(
                        "return this.$L.equals($L.$L)",
                        AUTH_FIELD_NAME,
                        EQUALS_METHOD_OTHER_PARAM_NAME,
                        AUTH_FIELD_NAME)
                .build();
        MethodSpec equalsMethod = MethodSpec.methodBuilder("equals")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(Object.class, EQUALS_METHOD_OTHER_PARAM_NAME)
                .addStatement(
                        "return this == $L || ($L instanceof $T && $N(($T) $L))",
                        EQUALS_METHOD_OTHER_PARAM_NAME,
                        EQUALS_METHOD_OTHER_PARAM_NAME,
                        generatedClassName,
                        equalToMethod,
                        generatedClassName,
                        EQUALS_METHOD_OTHER_PARAM_NAME)
                .addAnnotation(Override.class)
                .build();
        return List.of(equalsMethod, equalToMethod);
    }

    private MethodSpec getHashCodeMethod() {
        return MethodSpec.methodBuilder("hashCode")
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .addStatement("return $T.hashCode(this.$L)", ClassName.get(Objects.class), AUTH_FIELD_NAME)
                .addAnnotation(Override.class)
                .build();
    }

    private List<MethodSpec> getStaticBuilderMethods(Map<AuthScheme, GeneratedFile> generatedAuthSchemes) {
        return authSchemes.stream()
                .map(authScheme -> {
                    String methodName = authScheme.visit(AuthSchemeCamelCaseName.INSTANCE);
                    return MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addParameter(generatedAuthSchemes.get(authScheme).className(), "value")
                            .addStatement("return new $T(value)", generatedClassName)
                            .returns(generatedClassName)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Map<AuthScheme, MethodSpec> getIsTypeMethods(Map<AuthScheme, GeneratedFile> generatedAuthSchemes) {
        return authSchemes.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        authScheme -> MethodSpec.methodBuilder("is" + getAuthSchemePascalCaseName(authScheme))
                                .addModifiers(Modifier.PUBLIC)
                                .returns(boolean.class)
                                .addStatement(
                                        "return value instanceof $T",
                                        generatedAuthSchemes.get(authScheme).className())
                                .build(),
                        (u, _v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    private List<MethodSpec> getAuthTypeGetterMethods(
            Map<AuthScheme, MethodSpec> isTypeMethods, Map<AuthScheme, GeneratedFile> generatedAuthSchemes) {
        return authSchemes.stream()
                .map(authScheme -> {
                    ClassName authSchemeClassName =
                            generatedAuthSchemes.get(authScheme).className();
                    return MethodSpec.methodBuilder("get" + getAuthSchemePascalCaseName(authScheme))
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParameterizedTypeName.get(
                                    ClassNameConstants.OPTIONAL_CLASS_NAME, authSchemeClassName))
                            .beginControlFlow("if ($L())", isTypeMethods.get(authScheme).name)
                            .addStatement(
                                    "return $T.of(($T) value)",
                                    ClassNameConstants.OPTIONAL_CLASS_NAME,
                                    authSchemeClassName)
                            .endControlFlow()
                            .addStatement("return $T.empty()", ClassNameConstants.OPTIONAL_CLASS_NAME)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static final String getAuthSchemePascalCaseName(AuthScheme authScheme) {
        return StringUtils.capitalize(authScheme.visit(AuthSchemeCamelCaseName.INSTANCE));
    }

    private static final class AuthSchemeCamelCaseName implements AuthScheme.Visitor<String> {
        private static final AuthSchemeCamelCaseName INSTANCE = new AuthSchemeCamelCaseName();

        @Override
        public String visitBearer(WithDocs value) {
            return "bearer";
        }

        @Override
        public String visitBasic(WithDocs value) {
            return "basic";
        }

        @Override
        public String visitHeader(HttpHeader value) {
            return value.name().camelCase();
        }

        @Override
        public String visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown authScheme: " + unknownType);
        }
    }
}
