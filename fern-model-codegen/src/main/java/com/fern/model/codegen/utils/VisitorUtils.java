package com.fern.model.codegen.utils;

import com.fern.immutables.StagedBuilderStyle;
import com.fern.model.codegen.utils.ImmutableVisitMethodArgs.MethodNameBuildStage;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

public final class VisitorUtils {

    private static final String VISITOR_TYPE_NAME = "Visitor";
    private static final String VISITOR_VISIT_METHOD_NAME_PREFIX = "visit";
    private static final String VISITOR_VISIT_UNKNOWN_METHOD_NAME = VISITOR_VISIT_METHOD_NAME_PREFIX + "Unknown";
    private static final TypeVariableName VISITOR_RETURN_TYPE = TypeVariableName.get("T");

    private VisitorUtils() {}

    public static TypeSpec buildVisitorInterface(List<VisitMethodArgs> visitorMethodArgs) {
        return TypeSpec.interfaceBuilder(VISITOR_TYPE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(VISITOR_RETURN_TYPE)
                .addMethods(visitorMethodArgs.stream()
                        .map(VisitMethodArgs::convertToMethod)
                        .collect(Collectors.toList()))
                .addMethod(MethodSpec.methodBuilder(VISITOR_VISIT_UNKNOWN_METHOD_NAME)
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(VISITOR_RETURN_TYPE)
                        .build())
                .build();
    }

    @Value.Immutable
    @StagedBuilderStyle
    public interface VisitMethodArgs {
        String methodName();

        Optional<TypeName> visitorType();

        default MethodSpec convertToMethod() {
            return MethodSpec.methodBuilder(VISITOR_VISIT_METHOD_NAME_PREFIX + StringUtils.capitalize(methodName()))
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(VISITOR_RETURN_TYPE)
                    .build();
        }

        static MethodNameBuildStage builder() {
            return ImmutableVisitMethodArgs.builder();
        }
    }
}
