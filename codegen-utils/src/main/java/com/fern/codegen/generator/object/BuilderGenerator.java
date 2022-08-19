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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fern.codegen.PoetTypeWithClassName;
import com.fern.java.immutables.StagedBuilderImmutablesStyle;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.immutables.value.Value;

public final class BuilderGenerator {

    private static final String NESTED_BUILDER_CLASS_NAME = "Builder";

    private final ClassName objectClassName;
    private final ClassName nestedBuilderClassName;
    private final List<EnrichedObjectProperty> objectPropertyWithFields;

    public BuilderGenerator(ClassName objectClassName, List<EnrichedObjectProperty> objectPropertyWithFields) {
        this.objectClassName = objectClassName;
        this.objectPropertyWithFields = objectPropertyWithFields;
        this.nestedBuilderClassName = objectClassName.nestedClass(NESTED_BUILDER_CLASS_NAME);
    }

    public void generate() {
        Optional<BuilderConfig> maybeBuilderConfig = getBuilderConfig();
        if (maybeBuilderConfig.isEmpty()) {
            return;
        }
        BuilderConfig builderConfig = maybeBuilderConfig.get();
        if (builderConfig instanceof DefaultBuilderConfig) {

        } else if (builderConfig instanceof StagedBuilderConfig) {
            StagedBuilderConfig stagedBuilderConfig = (StagedBuilderConfig) builderConfig;
            List<PoetTypeWithClassName> stageInterfaces = getStageInterfaces(stagedBuilderConfig);
        }
    }

    private PoetTypeWithClassName getStagedBuilderImplementation(StagedBuilderConfig stagedBuilderConfig) {
        TypeSpec.Builder builderImplTypeSpec = TypeSpec.classBuilder(NESTED_BUILDER_CLASS_NAME)
                .addModifiers(Modifier.STATIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(JsonIgnoreProperties.class)
                        .addMember("ignoreUnknown", "true")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build());
        buildStagedBuilder(stagedBuilderConfig, ImmutableBuilderImplBuilder.builder());
    }

    private List<PoetTypeWithClassName> buildStagedBuilder(
            StagedBuilderConfig stagedBuilderConfig, ImmutableBuilderImplBuilder.Builder implBuilder) {

        List<PoetTypeWithClassName> reverseStageInterfaces = new ArrayList<>();
        PoetTypeWithClassName finalStage = buildFinal(stagedBuilderConfig, implBuilder);
        reverseStageInterfaces.add(finalStage);

        for (int i = stagedBuilderConfig.stages().size() - 1; i >= 0; i--) {
            EnrichedObjectProperty enrichedObjectProperty =
                    stagedBuilderConfig.stages().get(i);
            PoetTypeWithClassName previousStage = reverseStageInterfaces.get(reverseStageInterfaces.size() - 1);
            String stageInterfaceName = enrichedObjectProperty.pascalCaseKey() + StageBuilderConstants.STAGE_SUFFIX;
            ClassName stageInterfaceClassName = objectClassName.nestedClass(stageInterfaceName);
            TypeSpec.Builder stageInterfaceBuilder = TypeSpec.interfaceBuilder(stageInterfaceClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addMethod(MethodSpec.methodBuilder(enrichedObjectProperty.fieldSpec().name)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(previousStage.className())
                            .build());
            if (i == 0) {
                stageInterfaceBuilder.addMethod(MethodSpec.methodBuilder(StageBuilderConstants.FROM_METHOD_NAME)
                        .returns(nestedBuilderClassName)
                        .addParameter(ParameterSpec.builder(
                                        objectClassName, StageBuilderConstants.FROM_METHOD_OTHER_PARAMETER_NAME)
                                .addModifiers(Modifier.PUBLIC)
                                .build())
                        .build());
            }
            reverseStageInterfaces.add(
                    PoetTypeWithClassName.of(stageInterfaceClassName, stageInterfaceBuilder.build()));
        }
        return reverseStageInterfaces.stream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    private PoetTypeWithClassName buildFinal(
            StagedBuilderConfig stagedBuilderConfig, ImmutableBuilderImplBuilder.Builder implBuilder) {

        implBuilder.addReversedMethods(getBaseBuildMethod()
                .addAnnotation(Override.class)
                .addStatement(
                        "return new $T($L)",
                        objectClassName,
                        objectPropertyWithFields.stream()
                                .map(enrichedObjectProperty -> enrichedObjectProperty.fieldSpec().name)
                                .collect(Collectors.joining(", ")))
                .build());

        ClassName finalStageClassName = objectClassName.nestedClass(StageBuilderConstants.FINAL_STAGE_CLASS_NAME);
        TypeSpec.Builder finalStageBuilder = TypeSpec.interfaceBuilder(
                        objectClassName.nestedClass(StageBuilderConstants.FINAL_STAGE_CLASS_NAME))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addMethod(getBaseBuildMethod().addModifiers(Modifier.ABSTRACT).build());

        List<EnrichedObjectProperty> finalStageProperties = stagedBuilderConfig.finalStage();
        for (EnrichedObjectProperty enrichedProperty : finalStageProperties) {
            TypeName poetTypeName = enrichedProperty.poetTypeName();
            if (poetTypeName instanceof ParameterizedTypeName) {
                addAdditionalSetters(
                        (ParameterizedTypeName) poetTypeName,
                        enrichedProperty,
                        finalStageClassName,
                        finalStageBuilder,
                        implBuilder);
            } else {
                finalStageBuilder.addMethod(getDefaultSetter(enrichedProperty, finalStageClassName)
                        .addModifiers(Modifier.ABSTRACT)
                        .build());
                implBuilder.addReversedMethods(getDefaultSetter(enrichedProperty, finalStageClassName)
                        .addAnnotation(Override.class)
                        .addStatement(
                                "this.$L = $L", enrichedProperty.fieldSpec().name, enrichedProperty.fieldSpec().name)
                        .addStatement("return this")
                        .build());
            }
        }
        return PoetTypeWithClassName.of(finalStageClassName, finalStageBuilder.build());
    }

    private MethodSpec.Builder getBaseBuildMethod() {
        return MethodSpec.methodBuilder(StageBuilderConstants.BUILD_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(objectClassName);
    }

    private MethodSpec.Builder getDefaultSetter(EnrichedObjectProperty enrichedProperty, ClassName returnClass) {
        TypeName poetTypeName = enrichedProperty.poetTypeName();
        FieldSpec fieldSpec = enrichedProperty.fieldSpec();
        return MethodSpec.methodBuilder(fieldSpec.name)
                .addParameter(
                        ParameterSpec.builder(poetTypeName, fieldSpec.name).build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(returnClass);
    }

    private void addAdditionalSetters(
            ParameterizedTypeName propertyTypeName,
            EnrichedObjectProperty enrichedObjectProperty,
            ClassName finalStageClassName,
            TypeSpec.Builder finalStageBuilder,
            ImmutableBuilderImplBuilder.Builder implBuilder) {
        FieldSpec fieldSpec = enrichedObjectProperty.fieldSpec();
        FieldSpec.Builder implFieldSpecBuilder = FieldSpec.builder(fieldSpec.type, fieldSpec.name, Modifier.PRIVATE);

        finalStageBuilder.addMethod(getDefaultSetter(enrichedObjectProperty, finalStageClassName)
                .addModifiers(Modifier.ABSTRACT)
                .build());
        MethodSpec.Builder defaultMethodImplBuilder = getDefaultSetter(enrichedObjectProperty, finalStageClassName)
                .addAnnotation(Override.class)
                .addAnnotation(AnnotationSpec.builder(JsonSetter.class)
                        .addMember("value", "$S", fieldSpec.name)
                        .addMember("nulls", "$T.$L", Nulls.class, Nulls.SKIP.name())
                        .build());

        if (isEqual(propertyTypeName, ClassName.get(Optional.class))) {
            finalStageBuilder.addMethod(
                    createOptionalItemTypeNameSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());

            implBuilder.addReversedFields(implFieldSpecBuilder
                    .initializer("$T.empty()", Optional.class)
                    .build());
            implBuilder.addReversedMethods(defaultMethodImplBuilder
                    .addStatement("this.$L = $L", fieldSpec.name, fieldSpec.name)
                    .addStatement("return this")
                    .build());
            implBuilder.addReversedMethods(
                    createOptionalItemTypeNameSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addAnnotation(Override.class)
                            .addStatement("this.$L = $T.of($L)", fieldSpec.name, Optional.class, fieldSpec.name)
                            .addStatement("return this")
                            .build());
        } else if (isEqual(propertyTypeName, ClassName.get(Map.class))) {
            finalStageBuilder.addMethod(
                    createMapPutAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());
            finalStageBuilder.addMethod(
                    createMapEntryAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());

            implBuilder.addReversedFields(implFieldSpecBuilder
                    .initializer("new $T<>()", LinkedHashMap.class)
                    .build());
            implBuilder.addReversedMethods(defaultMethodImplBuilder
                    .addStatement("this.$L.clear()", fieldSpec.name)
                    .addStatement("this.$L.putAll($L)", fieldSpec.name, fieldSpec.name)
                    .addStatement("return this")
                    .build());
            implBuilder.addReversedMethods(
                    createMapPutAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement("this.$L.putAll($L, $L)", fieldSpec.name, fieldSpec.name)
                            .addStatement("return this")
                            .build());
            implBuilder.addReversedMethods(
                    createMapEntryAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement(
                                    "this.$L.put($L, $L)",
                                    fieldSpec.name,
                                    StageBuilderConstants.MAP_ITEM_APPENDER_KEY_PARAMETER_NAME,
                                    StageBuilderConstants.MAP_ITEM_APPENDER_VALUE_PARAMETER_NAME)
                            .addStatement("return this")
                            .build());
        } else if (isEqual(propertyTypeName, ClassName.get(List.class))) {
            finalStageBuilder.addMethod(
                    createCollectionItemAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());
            finalStageBuilder.addMethod(
                    createCollectionAddAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());

            implBuilder.addReversedFields(implFieldSpecBuilder
                    .initializer("new $T<>()", ArrayList.class)
                    .build());
            implBuilder.addReversedMethods(defaultMethodImplBuilder
                    .addStatement("this.$L.clear()", fieldSpec.name)
                    .addStatement("this.$L.addAll($L)", fieldSpec.name, fieldSpec.name)
                    .addStatement("return this")
                    .build());
            implBuilder.addReversedMethods(
                    createCollectionItemAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement("this.$L.addAll($L)", fieldSpec.name, fieldSpec.name)
                            .addStatement("return this")
                            .build());
            implBuilder.addReversedMethods(
                    createCollectionAddAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement("this.$L.add($L)", fieldSpec.name, fieldSpec.name)
                            .addStatement("return this")
                            .build());
        } else if (isEqual(propertyTypeName, ClassName.get(Set.class))) {
            finalStageBuilder.addMethod(
                    createCollectionItemAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());
            finalStageBuilder.addMethod(
                    createCollectionAddAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addModifiers(Modifier.ABSTRACT)
                            .build());

            implBuilder.addReversedFields(implFieldSpecBuilder
                    .initializer("new $T<>()", LinkedHashSet.class)
                    .build());
            implBuilder.addReversedMethods(defaultMethodImplBuilder
                    .addStatement("this.$L.clear()", fieldSpec.name)
                    .addStatement("this.$L.addAll($L)", fieldSpec.name, fieldSpec.name)
                    .addStatement("return this")
                    .build());
            implBuilder.addReversedMethods(
                    createCollectionItemAppender(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement("this.$L.addAll($L)", fieldSpec.name, fieldSpec.name)
                            .addStatement("return this")
                            .build());
            implBuilder.addReversedMethods(
                    createCollectionAddAllSetter(enrichedObjectProperty, propertyTypeName, finalStageClassName)
                            .addStatement("this.$L.add($L)", fieldSpec.name, fieldSpec.name)
                            .addStatement("return this")
                            .build());
        }
    }

    private static MethodSpec.Builder createMapEntryAppender(
            EnrichedObjectProperty enrichedObjectProperty, ParameterizedTypeName mapTypeName, ClassName returnClass) {
        String fieldName = enrichedObjectProperty.fieldSpec().name;
        TypeName keyTypeName = getIndexedTypeArgumentOrThrow(mapTypeName, 0);
        TypeName valueTypeName = getIndexedTypeArgumentOrThrow(mapTypeName, 1);
        return defaultSetter(fieldName, returnClass)
                .addParameter(keyTypeName, StageBuilderConstants.MAP_ITEM_APPENDER_KEY_PARAMETER_NAME)
                .addParameter(valueTypeName, StageBuilderConstants.MAP_ITEM_APPENDER_VALUE_PARAMETER_NAME);
    }

    private static MethodSpec.Builder createMapPutAllSetter(
            EnrichedObjectProperty enrichedObjectProperty,
            ParameterizedTypeName collectionTypeName,
            ClassName returnClass) {
        String fieldName = enrichedObjectProperty.fieldSpec().name;
        return defaultSetter(
                        StageBuilderConstants.PUT_ALL_METHOD_PREFIX + enrichedObjectProperty.pascalCaseKey(),
                        returnClass)
                .addParameter(collectionTypeName, fieldName);
    }

    private static MethodSpec.Builder createCollectionItemAppender(
            EnrichedObjectProperty enrichedObjectProperty,
            ParameterizedTypeName collectionTypeName,
            ClassName returnClass) {
        String fieldName = enrichedObjectProperty.fieldSpec().name;
        TypeName itemTypeName = getOnlyTypeArgumentOrThrow(collectionTypeName);
        return defaultSetter(fieldName, returnClass).addParameter(itemTypeName, fieldName);
    }

    private static MethodSpec.Builder createCollectionAddAllSetter(
            EnrichedObjectProperty enrichedObjectProperty,
            ParameterizedTypeName collectionTypeName,
            ClassName returnClass) {
        String fieldName = enrichedObjectProperty.fieldSpec().name;
        return defaultSetter(
                        StageBuilderConstants.ADD_ALL_METHOD_PREFIX + enrichedObjectProperty.pascalCaseKey(),
                        returnClass)
                .addParameter(collectionTypeName, fieldName);
    }

    private static MethodSpec.Builder createOptionalItemTypeNameSetter(
            EnrichedObjectProperty enrichedObjectProperty,
            ParameterizedTypeName optionalTypeName,
            ClassName returnClass) {
        String fieldName = enrichedObjectProperty.fieldSpec().name;
        TypeName itemTypeName = getOnlyTypeArgumentOrThrow(optionalTypeName);
        return defaultSetter(fieldName, returnClass).addParameter(itemTypeName, fieldName);
    }

    private static MethodSpec.Builder defaultSetter(String methodName, ClassName returnClass) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnClass);
    }

    private static TypeName getOnlyTypeArgumentOrThrow(ParameterizedTypeName parameterizedTypeName) {
        return getIndexedTypeArgumentOrThrow(parameterizedTypeName, 0);
    }

    private static TypeName getIndexedTypeArgumentOrThrow(ParameterizedTypeName parameterizedTypeName, int index) {
        if (parameterizedTypeName.typeArguments.size() <= index) {
            throw new RuntimeException("Expected parameterizedTypeName to have " + index + 1 + " type arguments. "
                    + "rawType=" + parameterizedTypeName.rawType);
        }
        return parameterizedTypeName.typeArguments.get(index);
    }

    private Optional<BuilderConfig> getBuilderConfig() {
        List<EnrichedObjectProperty> nonRequiredFields = new ArrayList<>();
        List<EnrichedObjectProperty> requiredFields = new ArrayList<>();
        for (EnrichedObjectProperty objectPropertyWithField : objectPropertyWithFields) {
            boolean isRequired = isRequired(objectPropertyWithField);
            if (isRequired) {
                requiredFields.add(objectPropertyWithField);
            } else {
                nonRequiredFields.add(objectPropertyWithField);
            }
        }

        if (nonRequiredFields.isEmpty() && requiredFields.isEmpty()) {
            return Optional.empty();
        } else if (requiredFields.isEmpty()) {
            // default builder b/c all fields are not required
            return Optional.of(DefaultBuilderConfig.builder()
                    .addAllProperties(objectPropertyWithFields)
                    .build());
        } else if (nonRequiredFields.isEmpty() && requiredFields.size() == 1) {
            // default builder b/c only 1 required field
            return Optional.of(DefaultBuilderConfig.builder()
                    .addAllProperties(objectPropertyWithFields)
                    .build());
        } else if (nonRequiredFields.isEmpty()) {
            // staged builder
            return Optional.of(StagedBuilderConfig.builder()
                    .addAllStages(requiredFields.subList(0, requiredFields.size() - 1))
                    .addFinalStage(requiredFields.get(requiredFields.size() - 1))
                    .build());
        } else {
            // staged builder
            return Optional.of(StagedBuilderConfig.builder()
                    .addAllStages(requiredFields)
                    .addAllFinalStage(nonRequiredFields)
                    .build());
        }
    }

    private boolean isRequired(EnrichedObjectProperty enrichedObjectProperty) {
        TypeName poetTypeName = enrichedObjectProperty.poetTypeName();
        if (poetTypeName instanceof ParameterizedTypeName) {
            ParameterizedTypeName poetParameterizedTypeName = (ParameterizedTypeName) poetTypeName;
            return !isEqual(poetParameterizedTypeName, ClassName.get(Optional.class))
                    && !isEqual(poetParameterizedTypeName, ClassName.get(Map.class))
                    && !isEqual(poetParameterizedTypeName, ClassName.get(List.class))
                    && !isEqual(poetParameterizedTypeName, ClassName.get(Set.class));
        }
        return true;
    }

    @SuppressWarnings("checkstyle:ParameterName")
    private boolean isEqual(ParameterizedTypeName a, ClassName b) {
        return a.rawType.compareTo(b) == 0;
    }

    interface BuilderConfig {}

    @Value.Immutable
    @StagedBuilderImmutablesStyle
    interface DefaultBuilderConfig extends BuilderConfig {
        List<EnrichedObjectProperty> properties();

        static ImmutableDefaultBuilderConfig.Builder builder() {
            return ImmutableDefaultBuilderConfig.builder();
        }
    }

    @Value.Immutable
    @StagedBuilderImmutablesStyle
    interface StagedBuilderConfig extends BuilderConfig {
        List<EnrichedObjectProperty> stages();

        List<EnrichedObjectProperty> finalStage();

        static ImmutableStagedBuilderConfig.Builder builder() {
            return ImmutableStagedBuilderConfig.builder();
        }
    }

    private static final class StageBuilderConstants {
        private static final String FINAL_STAGE_CLASS_NAME = "_FinalStage";
        private static final String ADD_ALL_METHOD_PREFIX = "addAll";
        private static final String PUT_ALL_METHOD_PREFIX = "putAll";

        private static final String MAP_ITEM_APPENDER_KEY_PARAMETER_NAME = "key";

        private static final String MAP_ITEM_APPENDER_VALUE_PARAMETER_NAME = "value";
        private static final String BUILD_METHOD_NAME = "build";
        private static final String FROM_METHOD_NAME = "from";
        private static final String FROM_METHOD_OTHER_PARAMETER_NAME = "other";
        private static final String STAGE_SUFFIX = "Stage";
    }

    @Value.Immutable
    @StagedBuilderImmutablesStyle
    interface BuilderImplBuilder {
        List<FieldSpec> reversedFields();

        List<MethodSpec> reversedMethods();

        static ImmutableBuilderImplBuilder.Builder builder() {
            return ImmutableBuilderImplBuilder.builder();
        }
    }
}
