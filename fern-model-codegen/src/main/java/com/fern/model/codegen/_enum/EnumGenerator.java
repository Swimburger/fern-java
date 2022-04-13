package com.fern.model.codegen._enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fern.EnumTypeDefinition;
import com.fern.EnumValue;
import com.fern.NamedTypeReference;
import com.fern.model.codegen.utils.ClassNameUtils;
import com.fern.model.codegen.utils.VisitorUtils;
import com.fern.model.codegen.utils.VisitorUtils.GeneratedVisitor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

public class EnumGenerator {

    public static final EnumGenerator INSTANCE = new EnumGenerator();

    private static final String VALUE_TYPE_NAME = "Value";
    private static final String VALUE_FIELD_NAME = "value";

    private static final String STRING_FIELD_NAME = "string";

    private static final String VISITOR_TYPE_NAME = "Visitor";
    private static final String VISITOR_VISIT_UNKNOWN_METHOD_NAME = "visitUnknown";

    private static final String UNKNOWN_ENUM_CONSTANT = "UNKNOWN";

    private static final String GET_ENUM_VALUE_METHOD_NAME = "getEnumValue";
    private static final String TO_STRING_METHOD_NAME = "toString";
    private static final String EQUALS_METHOD_NAME = "equals";
    private static final String HASHCODE_METHOD_NAME = "hashCode";
    private static final String ACCEPT_METHOD_NAME = "accept";
    private static final String VALUE_OF_METHOD_NAME = "valueOf";

    private EnumGenerator() {}

    public GeneratedEnum generate(NamedTypeReference namedTypeReference, EnumTypeDefinition enumTypeDefinition) {
        ClassName generatedEnumClassName = ClassNameUtils.getClassName(namedTypeReference);
        Map<EnumValue, FieldSpec> enumConstants = getConstants(enumTypeDefinition, generatedEnumClassName);
        VisitorUtils.GeneratedVisitor generatedVisitor = getVisitor(enumTypeDefinition);
        TypeSpec enumTypeSpec = TypeSpec.classBuilder(namedTypeReference.name())
                .addModifiers(getClassModifiers())
                .addFields(enumConstants.values())
                .addFields(getPrivateMembers(generatedEnumClassName))
                .addMethod(getConstructor(generatedEnumClassName))
                .addMethod(getEnumValueMethod(generatedEnumClassName))
                .addMethod(getToStringMethod())
                .addMethod(getEqualsMethod(generatedEnumClassName))
                .addMethod(getHashCodeMethod())
                .addMethod(getAcceptMethod(generatedEnumClassName, generatedVisitor))
                .addMethod(getValueOfMethod(generatedEnumClassName, enumConstants))
                .addType(getNestedValueEnum(enumTypeDefinition))
                .addType(generatedVisitor.typeSpec())
                .build();
        JavaFile enumFile = JavaFile.builder(generatedEnumClassName.packageName(), enumTypeSpec)
                .build();
        return GeneratedEnum.builder()
                .file(enumFile)
                .definition(enumTypeDefinition)
                .className(generatedEnumClassName)
                .build();
    }

    private static Modifier[] getClassModifiers() {
        return new Modifier[] {Modifier.PUBLIC, Modifier.FINAL};
    }

    private static Map<EnumValue, FieldSpec> getConstants(
            EnumTypeDefinition enumTypeDefinition, ClassName generatedEnumClassName) {
        // Generate public static final constant for each enum value
        return enumTypeDefinition.values().stream()
                .collect(Collectors.toMap(Function.identity(), enumValue -> FieldSpec.builder(
                                generatedEnumClassName,
                                enumValue.value(),
                                Modifier.PUBLIC,
                                Modifier.STATIC,
                                Modifier.FINAL)
                        .initializer(
                                "new $T($T.$L, $S)",
                                generatedEnumClassName,
                                getValueClassName(generatedEnumClassName),
                                enumValue.value(),
                                enumValue.value())
                        .build()));
    }

    private static List<FieldSpec> getPrivateMembers(ClassName generatedEnumClassName) {
        List<FieldSpec> privateMembers = new ArrayList<>();
        // Add private Value Field
        ClassName valueFieldClassName = getValueClassName(generatedEnumClassName);
        FieldSpec valueField = FieldSpec.builder(
                        valueFieldClassName, VALUE_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .build();
        privateMembers.add(valueField);
        // Add private String Field
        FieldSpec stringField = FieldSpec.builder(
                        ClassNameUtils.STRING_TYPE_NAME, STRING_FIELD_NAME, Modifier.PRIVATE, Modifier.FINAL)
                .build();
        privateMembers.add(stringField);
        return privateMembers;
    }

    private static MethodSpec getConstructor(ClassName generatedEnumClassName) {
        return MethodSpec.constructorBuilder()
                .addParameter(getValueClassName(generatedEnumClassName), VALUE_FIELD_NAME)
                .addParameter(ClassNameUtils.STRING_TYPE_NAME, STRING_FIELD_NAME)
                .addStatement("this.value = value")
                .addStatement("this.string = string")
                .build();
    }

    private static MethodSpec getEnumValueMethod(ClassName generatedEnumClassName) {
        return MethodSpec.methodBuilder(GET_ENUM_VALUE_METHOD_NAME)
                .addCode("return value;")
                .returns(getValueClassName(generatedEnumClassName))
                .build();
    }

    private static MethodSpec getToStringMethod() {
        return MethodSpec.methodBuilder(TO_STRING_METHOD_NAME)
                .addAnnotation(Override.class)
                .addAnnotation(JsonValue.class)
                .addCode("return this.string;")
                .returns(ClassName.get(String.class))
                .build();
    }

    private static MethodSpec getEqualsMethod(ClassName generatedEnumClassName) {
        return MethodSpec.methodBuilder(EQUALS_METHOD_NAME)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(Object.class), "other")
                .addCode(CodeBlock.builder()
                        .add(
                                "return (this == other) \n"
                                        + "  || (other instanceof $T && this.string.equals((($T) other).string));",
                                generatedEnumClassName,
                                generatedEnumClassName)
                        .build())
                .returns(boolean.class)
                .build();
    }

    private static MethodSpec getHashCodeMethod() {
        return MethodSpec.methodBuilder(HASHCODE_METHOD_NAME)
                .addAnnotation(Override.class)
                .addCode("return this.string.hashCode();")
                .returns(int.class)
                .build();
    }

    /**
     * Generates an accept method that visits the enum.
     * Example:
     * public <T> T accept(Visitor<T> visitor) {
     *     switch (value) {
     *         case ON:
     *             return visitor.visitOn();
     *         case OFF:
     *             return visitor.visitOff();
     *         case UNKNOWN:
     *         default:
     *             return visitor.visitUnknown(string);
     *     }
     * }
     */
    private static MethodSpec getAcceptMethod(ClassName generatedEnumClassName, GeneratedVisitor generatedVisitor) {
        CodeBlock.Builder acceptMethodImplementation = CodeBlock.builder().beginControlFlow("switch (value)");
        generatedVisitor.visitMethodsByKeyName().forEach((keyName, visitMethod) -> {
            acceptMethodImplementation
                    .add("case $L:\n", keyName)
                    .indent()
                    .addStatement("return visitor.$L()", visitMethod.name)
                    .unindent();
        });
        CodeBlock acceptCodeBlock = acceptMethodImplementation
                .add("case UNKNOWN:\n")
                .add("default:\n")
                .indent()
                .addStatement("return visitor.visitUnknown(string)")
                .unindent()
                .endControlFlow()
                .build();
        ClassName nestedVisitor = generatedEnumClassName.nestedClass(VISITOR_TYPE_NAME);
        TypeVariableName acceptReturnType = TypeVariableName.get("T");
        return MethodSpec.methodBuilder(ACCEPT_METHOD_NAME)
                .addParameter(ParameterizedTypeName.get(nestedVisitor, acceptReturnType), "visitor")
                .addCode(acceptCodeBlock)
                .returns(acceptReturnType)
                .build();
    }

    /**
     * Generates an accept method that visits the enum.
     * Example:
     * @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
     * public static Status valueOf(@Nonnull value) {
     *     String upperCasedValue = value.toUpperCase(Locale.ROOT);
     *     switch (upperCasedValue) {
     *         case "ON":
     *             return ON;
     *         case "OFF":
     *             return OFF;
     *         case "UNKNOWN":
     *         default:
     *             return  new Status(Status.Value.UNKNOWN, upperCasedValue);
     *     }
     * }
     */
    private static MethodSpec getValueOfMethod(ClassName generatedEnumClassName, Map<EnumValue, FieldSpec> constants) {
        CodeBlock.Builder valueOfCodeBlockBuilder = CodeBlock.builder()
                .addStatement("String upperCasedValue = value.toUpperCase(Locale.ROOT)")
                .beginControlFlow("switch (upperCasedValue)");
        constants.forEach((enumValue, constantField) -> {
            valueOfCodeBlockBuilder
                    .add("case $S:\n", enumValue.value())
                    .indent()
                    .addStatement("return $L", constantField.name)
                    .unindent();
        });
        CodeBlock valueOfCodeBlock = valueOfCodeBlockBuilder
                .add("default:\n")
                .indent()
                .addStatement("return new $T(Value.UNKNOWN, upperCasedValue)", generatedEnumClassName)
                .unindent()
                .endControlFlow()
                .build();
        return MethodSpec.methodBuilder(VALUE_OF_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addAnnotation(AnnotationSpec.builder(JsonCreator.class)
                        .addMember(
                                "mode",
                                "$T.$L",
                                ClassName.get(JsonCreator.Mode.class),
                                JsonCreator.Mode.DELEGATING.name())
                        .build())
                .addParameter(ParameterSpec.builder(ClassName.get(String.class), "value")
                        .addAnnotation(Nonnull.class)
                        .build())
                .addCode(valueOfCodeBlock)
                .returns(generatedEnumClassName)
                .build();
    }

    /**
     * Generates a nested enum called Value.
     * The nested enum has an UNKNOWN value in addition to configured values.
     * Example:
     * enum Value {
     *     ON,
     *     OFF,
     *     UNKNOWN
     * }
     */
    private static TypeSpec getNestedValueEnum(EnumTypeDefinition enumTypeDefinition) {
        TypeSpec.Builder nestedValueEnumBuilder =
                TypeSpec.enumBuilder(VALUE_TYPE_NAME).addModifiers(Modifier.PUBLIC);
        enumTypeDefinition.values().forEach(enumValue -> nestedValueEnumBuilder.addEnumConstant(enumValue.value()));
        nestedValueEnumBuilder.addEnumConstant(UNKNOWN_ENUM_CONSTANT);
        return nestedValueEnumBuilder.build();
    }

    /**
     * Generates a nested interface to visit all types of the enum.
     * Example:
     * interface Visitor<T> {
     *     T visitOn();
     *     T visitOff();
     *     T visitUnknownType(String unknownType);
     * }
     */
    private static GeneratedVisitor getVisitor(EnumTypeDefinition enumTypeDefinition) {
        List<VisitorUtils.VisitMethodArgs> visitMethodArgs = enumTypeDefinition.values().stream()
                .map(enumValue -> VisitorUtils.VisitMethodArgs.builder()
                        // TODO: Should we handle underscores in enum values by removing them and camelCasing?
                        .keyName(enumValue.value())
                        .build())
                .collect(Collectors.toList());
        return VisitorUtils.buildVisitorInterface(visitMethodArgs);
    }

    private static ClassName getValueClassName(ClassName generatedEnumClassName) {
        return generatedEnumClassName.nestedClass(VALUE_TYPE_NAME);
    }
}
