package com.fern.model.codegen.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fern.NamedTypeReference;
import com.fern.ObjectField;
import com.fern.ObjectTypeDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.fern.model.codegen.GeneratedFile;
import com.fern.model.codegen._interface.GeneratedInterface;
import com.fern.model.codegen.utils.ClassNameUtils;
import com.fern.model.codegen.utils.ImmutablesUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.immutables.value.Value;

public final class ObjectGenerator {

    public static final ObjectGenerator INSTANCE = new ObjectGenerator();

    private static final String STATIC_BUILDER_METHOD_NAME = "builder";
    private static final String BUILD_STAGE_SUFFIX = "BuildStage";
    private static final String JSON_IGNORABLE_TYPE_PROPERTY = "type";

    private ObjectGenerator() {}

    public GeneratedObject generate(
            NamedTypeReference namedTypeReference,
            ObjectTypeDefinition objectTypeDefinition,
            List<GeneratedInterface> extendedInterfaces,
            Optional<GeneratedInterface> selfInterface) {
        ClassName generatedObjectClassName = ClassNameUtils.getClassName(namedTypeReference);
        TypeSpec objectTypeSpec = TypeSpec.interfaceBuilder(namedTypeReference.name())
                .addModifiers(getClassModifiers())
                .addAnnotations(getAnnotations(namedTypeReference))
                .addSuperinterfaces(getSuperInterfaces(extendedInterfaces, selfInterface))
                .addMethods(getMethods(extendedInterfaces, namedTypeReference, objectTypeDefinition, selfInterface))
                .build();
        JavaFile objectFile = JavaFile.builder(generatedObjectClassName.packageName(), objectTypeSpec)
                .build();
        return GeneratedObject.builder()
                .file(objectFile)
                .definition(objectTypeDefinition)
                .className(generatedObjectClassName)
                .build();
    }

    private static Modifier[] getClassModifiers() {
        return Collections.singletonList(Modifier.PUBLIC).toArray(new Modifier[0]);
    }

    private static List<AnnotationSpec> getAnnotations(NamedTypeReference namedTypeReference) {
        List<AnnotationSpec> annotationSpecs = new ArrayList<>();
        annotationSpecs.add(AnnotationSpec.builder(Value.Immutable.class).build());
        annotationSpecs.add(AnnotationSpec.builder(StagedBuilderStyle.class).build());
        annotationSpecs.add(AnnotationSpec.builder(JsonDeserialize.class)
                .addMember("as", "$T.class", ImmutablesUtils.getImmutablesClassName(namedTypeReference))
                .build());
        annotationSpecs.add(AnnotationSpec.builder(JsonIgnoreProperties.class)
                .addMember("value", "{$S}", JSON_IGNORABLE_TYPE_PROPERTY)
                .build());
        return annotationSpecs;
    }

    private static List<TypeName> getSuperInterfaces(
            List<GeneratedInterface> extendedInterfaces, Optional<GeneratedInterface> selfInterface) {
        List<TypeName> superInterfaces = new ArrayList<>();
        superInterfaces.addAll(
                extendedInterfaces.stream().map(GeneratedFile::className).collect(Collectors.toList()));
        selfInterface.ifPresent(generatedInterface -> superInterfaces.add(generatedInterface.className()));
        return superInterfaces;
    }

    private static List<MethodSpec> getMethods(
            List<GeneratedInterface> extendedInterfaces,
            NamedTypeReference namedTypeReference,
            ObjectTypeDefinition objectTypeDefinition,
            Optional<GeneratedInterface> selfInterface) {
        List<MethodSpec> methods = new ArrayList<>();
        // if no self interface, we want to add all fields as immutables attributes
        if (selfInterface.isEmpty()) {
            methods.addAll(ImmutablesUtils.getImmutablesPropertyMethods(objectTypeDefinition));
        }
        methods.add(generateStaticBuilder(extendedInterfaces, namedTypeReference, objectTypeDefinition));
        return methods;
    }

    private static MethodSpec generateStaticBuilder(
            List<GeneratedInterface> extendedInterfaces,
            NamedTypeReference namedTypeReference,
            ObjectTypeDefinition objectTypeDefinition) {
        Optional<String> firstMandatoryFieldName =
                getFirstRequiredFieldName(extendedInterfaces, objectTypeDefinition.fields());
        ClassName immutableClassName = ImmutablesUtils.getImmutablesClassName(namedTypeReference);
        ClassName builderClassName = firstMandatoryFieldName.isEmpty()
                ? immutableClassName.nestedClass("Builder")
                : immutableClassName.nestedClass(firstMandatoryFieldName.get() + BUILD_STAGE_SUFFIX);
        return MethodSpec.methodBuilder(STATIC_BUILDER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderClassName)
                .addCode("returns $T.builder();", immutableClassName)
                .build();
    }

    private static Optional<String> getFirstRequiredFieldName(
            List<GeneratedInterface> superInterfaces, List<ObjectField> fields) {
        // Required field from super interfaces take priority
        for (GeneratedInterface superInterface : superInterfaces) {
            Optional<String> firstMandatoryFieldName =
                    getFirstRequiredFieldName(superInterface.definition().fields());
            if (firstMandatoryFieldName.isPresent()) {
                return firstMandatoryFieldName;
            }
        }
        return getFirstRequiredFieldName(fields);
    }

    private static Optional<String> getFirstRequiredFieldName(List<ObjectField> fields) {
        for (ObjectField field : fields) {
            if (field.valueType().isPrimitive() || field.valueType().isNamed()) {
                return Optional.of(field.key());
            }
        }
        return Optional.empty();
    }
}
