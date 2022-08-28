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

package com.fern.java.generators.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fern.java.AbstractGeneratorContext;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.output.GeneratedFileOutput;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.lang.model.element.Modifier;

public final class BasicAuthGenerator extends AbstractFileGenerator {

    private static final String TOKEN_FIELD_NAME = "token";
    private static final String USERNAME_FIELD_NAME = "username";
    private static final String PASSWORD_FIELD_NAME = "password";
    private static final String GET_TOKEN_METHOD_NAME = "getToken";
    private static final String DECODE_METHOD_NAME = "decode";

    public BasicAuthGenerator(AbstractGeneratorContext<?> generatorContext) {
        super(generatorContext.getPoetClassNameFactory().getTopLevelClassName("BasicAuth"), generatorContext);
    }

    @Override
    public GeneratedFileOutput generateFile() {
        TypeSpec basicAuthTypeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(String.class, TOKEN_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addField(FieldSpec.builder(String.class, USERNAME_FIELD_NAME, Modifier.PRIVATE)
                        .initializer("null")
                        .build())
                .addField(FieldSpec.builder(String.class, PASSWORD_FIELD_NAME, Modifier.PRIVATE)
                        .initializer("null")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(String.class, TOKEN_FIELD_NAME)
                        .addStatement("this.$L = $L", TOKEN_FIELD_NAME, TOKEN_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder(GET_TOKEN_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(JsonValue.class)
                        .returns(String.class)
                        .addStatement("return this.$L", TOKEN_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder(USERNAME_FIELD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .returns(String.class)
                        .addStatement("$L()", DECODE_METHOD_NAME)
                        .addStatement("this.$L", USERNAME_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder(PASSWORD_FIELD_NAME)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .returns(String.class)
                        .addStatement("$L()", DECODE_METHOD_NAME)
                        .addStatement("this.$L", PASSWORD_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder(DECODE_METHOD_NAME)
                        .beginControlFlow(
                                "if (this.$L == null || this.$L == null)", USERNAME_FIELD_NAME, PASSWORD_FIELD_NAME)
                        .addStatement(
                                "$T[] $L = $T.getDecoder().decode($L())",
                                byte.class,
                                "decodedToken",
                                Base64.class,
                                GET_TOKEN_METHOD_NAME)
                        .addStatement(
                                "$T $L = new String($L, $T.$L)",
                                String.class,
                                "credentials",
                                "decodedToken",
                                StandardCharsets.class,
                                StandardCharsets.UTF_8.name())
                        .addStatement("String[] values = credentials.split(\":\", 2)")
                        .beginControlFlow("if (values.length != 2)")
                        .addStatement("throw new $T($S)", IllegalStateException.class, "Failed to decode basic token")
                        .endControlFlow()
                        .addStatement("this.$L = values[0]", USERNAME_FIELD_NAME)
                        .addStatement("this.$L = values[1]", PASSWORD_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder("toString")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return $S + $L()", "Basic ", GET_TOKEN_METHOD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder("of")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(String.class, USERNAME_FIELD_NAME)
                        .addParameter(String.class, PASSWORD_FIELD_NAME)
                        .returns(className)
                        .addStatement(
                                "String unencodedToken = $L + \":\" + $L", USERNAME_FIELD_NAME, PASSWORD_FIELD_NAME)
                        .addStatement(
                                "return new $T($T.getEncoder().encodeToString(unencodedToken.getBytes()))",
                                className,
                                Base64.class)
                        .build())
                .build();
        JavaFile basicAuthJavaFile =
                JavaFile.builder(className.packageName(), basicAuthTypeSpec).build();
        return GeneratedFileOutput.builder()
                .className(className)
                .javaFile(basicAuthJavaFile)
                .build();
    }
}
