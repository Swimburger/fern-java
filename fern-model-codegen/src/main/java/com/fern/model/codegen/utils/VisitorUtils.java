package com.fern.model.codegen.utils;

import com.fern.immutables.StagedBuilderStyle;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

public final class VisitorUtils {

    private static final String VISITOR_INTERFACE_NAME = "Visitor";
    private static final String VISITOR_VISIT_METHOD_NAME_PREFIX = "visit";
    private static final String VISITOR_VISIT_UNKNOWN_METHOD_NAME = VISITOR_VISIT_METHOD_NAME_PREFIX + "Unknown";
    private static final String VISITOR_UNKNOWN_TYPE_PARAMETER_NAME = "unknownType";
    private static final String VISITOR_TYPE_PARAMETER_NAME = "value";
    private static final TypeVariableName VISITOR_RETURN_TYPE = TypeVariableName.get("T");

    private VisitorUtils() {}

    public static GeneratedVisitor buildVisitorInterface(List<VisitMethodArgs> visitMethodArgsList) {
        Map<String, MethodSpec> visitMethodsByKeyName = new HashMap<>();
        List<MethodSpec> visitMethods = visitMethodArgsList.stream()
                .map(visitMethodArgs -> {
                    MethodSpec visitMethod = visitMethodArgs.convertToMethod();
                    visitMethodsByKeyName.put(visitMethodArgs.keyName(), visitMethod);
                    return visitMethod;
                })
                .collect(Collectors.toList());
        TypeSpec visitorTypeSpec = TypeSpec.interfaceBuilder(VISITOR_INTERFACE_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(VISITOR_RETURN_TYPE)
                .addMethods(visitMethods)
                .addMethod(MethodSpec.methodBuilder(VISITOR_VISIT_UNKNOWN_METHOD_NAME)
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(ClassNameUtils.STRING_TYPE_NAME, VISITOR_UNKNOWN_TYPE_PARAMETER_NAME)
                        .returns(VISITOR_RETURN_TYPE)
                        .build())
                .build();
        return GeneratedVisitor.builder()
                .typeSpec(visitorTypeSpec)
                .putAllVisitMethodsByKeyName(visitMethodsByKeyName)
                .build();
    }

    @Value.Immutable
    @StagedBuilderStyle
    public interface VisitMethodArgs {
        String keyName();

        Optional<TypeName> visitorType();

        default MethodSpec convertToMethod() {
            return MethodSpec.methodBuilder(VISITOR_VISIT_METHOD_NAME_PREFIX
                            + StringUtils.capitalize(keyName().toLowerCase()))
                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                    .returns(VISITOR_RETURN_TYPE)
                    .build();
        }

        static ImmutableVisitMethodArgs.KeyNameBuildStage builder() {
            return ImmutableVisitMethodArgs.builder();
        }
    }

    @Value.Immutable
    @StagedBuilderStyle
    public interface GeneratedVisitor {
        TypeSpec typeSpec();

        Map<String, MethodSpec> visitMethodsByKeyName();

        static ImmutableGeneratedVisitor.TypeSpecBuildStage builder() {
            return ImmutableGeneratedVisitor.builder();
        }
    }
}
