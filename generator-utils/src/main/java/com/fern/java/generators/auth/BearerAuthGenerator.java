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
import com.fern.java.output.GeneratedJavaFile;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

public final class BearerAuthGenerator extends AbstractFileGenerator {

    private static final String TOKEN_FIELD_NAME = "token";
    private static final String GET_TOKEN_METHOD_NAME = "getToken";

    public BearerAuthGenerator(AbstractGeneratorContext<?, ?> generatorContext) {
        super(generatorContext.getPoetClassNameFactory().getCoreClassName("BearerAuth"), generatorContext);
    }

    @Override
    public GeneratedJavaFile generateFile() {
        TypeSpec authHeaderTypeSpec = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(FieldSpec.builder(String.class, TOKEN_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(String.class, TOKEN_FIELD_NAME)
                        .addStatement("this.$L = $L", TOKEN_FIELD_NAME, TOKEN_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder(GET_TOKEN_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addAnnotation(JsonValue.class)
                        .addStatement("return $L", TOKEN_FIELD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder("toString")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addAnnotation(Override.class)
                        .addStatement("return $S + $L()", "Bearer ", GET_TOKEN_METHOD_NAME)
                        .build())
                .addMethod(MethodSpec.methodBuilder("of")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(String.class, "token")
                        .returns(className)
                        .addStatement(
                                "return new $T($L.startsWith($S) ? $L.substring(7) : token)",
                                className,
                                "token",
                                "Bearer ",
                                "token")
                        .build())
                .build();
        JavaFile authHeaderFile =
                JavaFile.builder(className.packageName(), authHeaderTypeSpec).build();
        return GeneratedJavaFile.builder()
                .className(className)
                .javaFile(authHeaderFile)
                .build();
    }
}
