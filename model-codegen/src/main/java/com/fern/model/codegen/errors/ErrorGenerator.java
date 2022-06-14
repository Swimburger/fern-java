package com.fern.model.codegen.errors;

import com.fern.codegen.GeneratedError;
import com.fern.codegen.GeneratedInterface;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.IGeneratedFile;
import com.fern.codegen.stateless.generator.ApiExceptionGenerator;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.model.codegen.Generator;
import com.fern.model.codegen.TypeDefinitionGenerator;
import com.fern.types.errors.ErrorDefinition;
import com.fern.types.types.NamedType;
import com.fern.types.types.TypeDefinition;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Modifier;

public class ErrorGenerator extends Generator {

    private static final String ERROR_SUFFIX = "Error";
    private static final String STATUS_CODE_FIELD_NAME = "STATUS_CODE";

    private final ErrorDefinition errorDefinition;
    private final GeneratorContext generatorContext;
    private final Map<NamedType, GeneratedInterface> generatedInterfaces;

    public ErrorGenerator(
            ErrorDefinition errorDefinition,
            GeneratorContext generatorContext,
            Map<NamedType, GeneratedInterface> generatedInterfaces) {
        super(generatorContext, PackageType.ERRORS);
        this.errorDefinition = errorDefinition;
        this.generatorContext = generatorContext;
        this.generatedInterfaces = generatedInterfaces;
    }

    @Override
    public GeneratedError generate() {
        ClassName errorClassName = generatorContext.getClassNameUtils()
                .getClassNameForNamedType(errorDefinition.name(), packageType,
                        errorDefinition.name().name().toLowerCase().endsWith("error")
                                ? Optional.empty()
                                : Optional.of(ERROR_SUFFIX));
        IGeneratedFile generatedTypeFile = errorDefinition.type()
                .visit(new TypeDefinitionGenerator(
                        TypeDefinition.builder()
                            .name(NamedType.builder()
                                    .fernFilepath(errorDefinition.name().fernFilepath())
                                    .name(errorClassName.simpleName())
                                    .build())
                            .shape(errorDefinition.type())
                            .build(),
                        generatorContext,
                        generatedInterfaces));
        TypeSpec.Builder errorTypeSpecBuilder = generatedTypeFile.file().typeSpec.toBuilder();
        errorTypeSpecBuilder.superclass(ClassName.get(Exception.class));
        if (errorDefinition.http().isPresent()) {
            errorTypeSpecBuilder
                    .addSuperinterface(
                            generatorContext.getHttpApiExceptionFile().className())
                    .addField(FieldSpec.builder(TypeName.INT, STATUS_CODE_FIELD_NAME)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("$L", errorDefinition.http().get().statusCode())
                            .build())
                    .addMethod(MethodSpec.methodBuilder(ApiExceptionGenerator.GET_STATUS_CODE_METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return $L", STATUS_CODE_FIELD_NAME)
                        .returns(ClassName.INT)
                        .addAnnotation(Override.class)
                        .build());
        }
        JavaFile errorFile = JavaFile.builder(errorClassName.packageName(), errorTypeSpecBuilder.build())
                .build();
        return GeneratedError.builder()
                .file(errorFile)
                .className(errorClassName)
                .errorDefinition(errorDefinition)
                .build();
    }
}
