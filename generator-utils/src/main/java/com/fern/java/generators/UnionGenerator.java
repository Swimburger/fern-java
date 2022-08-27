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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fern.ir.model.ir.FernConstants;
import com.fern.ir.model.types.DeclaredTypeName;
import com.fern.ir.model.types.SingleUnionType;
import com.fern.ir.model.types.TypeDeclaration;
import com.fern.ir.model.types.TypeReference;
import com.fern.ir.model.types.UnionTypeDeclaration;
import com.fern.java.AbstractGeneratorContext;
import com.fern.java.FernJavaAnnotations;
import com.fern.java.generators.union.UnionSubType;
import com.fern.java.generators.union.UnionTypeSpecGenerator;
import com.fern.java.output.GeneratedFileOutput;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;

public final class UnionGenerator extends AbstractFileGenerator {

    private final UnionTypeDeclaration unionTypeDeclaration;

    public UnionGenerator(
            ClassName className, AbstractGeneratorContext generatorContext, UnionTypeDeclaration unionTypeDeclaration) {
        super(className, generatorContext);
        this.unionTypeDeclaration = unionTypeDeclaration;
    }

    @Override
    public GeneratedFileOutput generateFile() {
        List<ModelUnionSubTypes> unionSubTypes = unionTypeDeclaration.getTypes().stream()
                .map(singleUnionType -> new ModelUnionSubTypes(className, singleUnionType))
                .collect(Collectors.toList());
        ModelUnionUnknownSubType unknownSubType = new ModelUnionUnknownSubType(className);
        ModelUnionTypeSpecGenerator unionTypeSpecGenerator = new ModelUnionTypeSpecGenerator(
                className,
                unionSubTypes,
                unknownSubType,
                generatorContext.getIr().getConstants());
        TypeSpec unionTypeSpec = unionTypeSpecGenerator.generateUnionTypeSpec();
        JavaFile unionFile =
                JavaFile.builder(className.packageName(), unionTypeSpec).build();
        return GeneratedFileOutput.builder()
                .className(className)
                .javaFile(unionFile)
                .build();
    }

    private static final class ModelUnionTypeSpecGenerator extends UnionTypeSpecGenerator {

        private ModelUnionTypeSpecGenerator(
                ClassName unionClassName,
                List<? extends UnionSubType> subTypes,
                UnionSubType unknownSubType,
                FernConstants fernConstants) {
            super(unionClassName, subTypes, unknownSubType, fernConstants);
        }

        @Override
        public List<FieldSpec> getAdditionalFieldSpecs() {
            return Collections.emptyList();
        }

        @Override
        public TypeSpec build(TypeSpec.Builder unionBuilder) {
            return unionBuilder
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(MethodSpec.methodBuilder("getValue")
                            .addAnnotation(JsonValue.class)
                            .addModifiers(Modifier.PRIVATE)
                            .returns(getValueInterfaceClassName())
                            .addStatement("return this.$L", getValueFieldName())
                            .build())
                    .build();
        }
    }

    private final class ModelUnionSubTypes extends UnionSubType {

        private final SingleUnionType singleUnionType;
        private final TypeName unionSubTypeTypeName;
        private final FieldSpec valueFieldSpec;

        private ModelUnionSubTypes(ClassName unionClassName, SingleUnionType singleUnionType) {
            super(unionClassName);
            this.singleUnionType = singleUnionType;
            this.unionSubTypeTypeName =
                    generatorContext.getPoetTypeNameMapper().convertToTypeName(true, singleUnionType.getValueType());
            this.valueFieldSpec = getValueField();
        }

        @Override
        public String getCamelCaseName() {
            return singleUnionType.getDiscriminantValue().getCamelCase();
        }

        @Override
        public String getPascalCaseName() {
            return singleUnionType.getDiscriminantValue().getCamelCase();
        }

        @Override
        public Optional<String> getDiscriminantValue() {
            return Optional.of(singleUnionType.getDiscriminantValue().getWireValue());
        }

        @Override
        public TypeName getUnionSubTypeTypeName() {
            return unionSubTypeTypeName;
        }

        @Override
        public ClassName getUnionSubTypeWrapperClass() {
            return getUnionClassName()
                    .nestedClass(singleUnionType.getDiscriminantValue().getPascalCase() + "Value");
        }

        @Override
        public List<FieldSpec> getFieldSpecs() {
            return Collections.singletonList(valueFieldSpec);
        }

        @Override
        public List<MethodSpec> getConstructors() {
            MethodSpec.Builder fromJsonConstructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(FernJavaAnnotations.jacksonPropertiesCreator());
            boolean errorIsObject = isTypeReferenceAnObject(singleUnionType.getValueType());
            List<ParameterSpec> parameterSpecs = new ArrayList<>();
            if (!errorIsObject) {
                parameterSpecs.add(ParameterSpec.builder(getUnionSubTypeTypeName(), getValueFieldName())
                        .addAnnotation(AnnotationSpec.builder(JsonProperty.class)
                                .addMember("value", "$S", getValueFieldName())
                                .build())
                        .build());
                fromJsonConstructorBuilder.addStatement("this.$L = $L", getValueFieldName(), getValueFieldName());
            }
            return Collections.singletonList(
                    fromJsonConstructorBuilder.addParameters(parameterSpecs).build());
        }

        private FieldSpec getValueField() {
            boolean errorIsObject = isTypeReferenceAnObject(singleUnionType.getValueType());
            FieldSpec.Builder valueFieldSpecBuilder =
                    FieldSpec.builder(getUnionSubTypeTypeName(), getValueFieldName(), Modifier.PRIVATE);
            if (errorIsObject) {
                valueFieldSpecBuilder.addAnnotation(JsonUnwrapped.class);
            }
            return valueFieldSpecBuilder.build();
        }

        private boolean isTypeReferenceAnObject(TypeReference typeReference) {
            Optional<DeclaredTypeName> maybeNamedType = typeReference.getNamed();
            if (maybeNamedType.isPresent()) {
                TypeDeclaration typeDeclaration =
                        generatorContext.getTypeDefinitionsByName().get(maybeNamedType.get());
                if (typeDeclaration.getShape().isObject()) {
                    return true;
                } else if (typeDeclaration.getShape().isAlias()) {
                    return isTypeReferenceAnObject(
                            typeDeclaration.getShape().getAlias().get().getAliasOf());
                }
            }
            return false;
        }
    }

    private final class ModelUnionUnknownSubType extends UnionSubType {

        private ModelUnionUnknownSubType(ClassName unionClassName) {
            super(unionClassName);
        }

        @Override
        public String getCamelCaseName() {
            return "unknown";
        }

        @Override
        public String getPascalCaseName() {
            return "Unknown";
        }

        @Override
        public Optional<String> getDiscriminantValue() {
            return Optional.empty();
        }

        @Override
        public TypeName getUnionSubTypeTypeName() {
            return ClassName.get(Object.class);
        }

        @Override
        public ClassName getUnionSubTypeWrapperClass() {
            return getUnionClassName().nestedClass(getPascalCaseName() + "Value");
        }

        @Override
        public List<FieldSpec> getFieldSpecs() {
            return List.of(
                    FieldSpec.builder(String.class, "type", Modifier.PRIVATE, Modifier.FINAL)
                            .build(),
                    FieldSpec.builder(String.class, getValueFieldName(), Modifier.PRIVATE, Modifier.FINAL)
                            .addAnnotation(JsonValue.class)
                            .build());
        }

        @Override
        public List<MethodSpec> getConstructors() {
            MethodSpec.Builder fromJsonConstructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(FernJavaAnnotations.jacksonPropertiesCreator());
            List<ParameterSpec> parameterSpecs = new ArrayList<>();
            parameterSpecs.add(ParameterSpec.builder(getUnionSubTypeTypeName(), getValueFieldName())
                    .addAnnotation(AnnotationSpec.builder(JsonProperty.class)
                            .addMember("type", "$S", getValueFieldName())
                            .build())
                    .build());
            return Collections.singletonList(
                    fromJsonConstructorBuilder.addParameters(parameterSpecs).build());
        }

        @Override
        public Optional<MethodSpec> getStaticFactory() {
            return Optional.empty();
        }
    }
}
