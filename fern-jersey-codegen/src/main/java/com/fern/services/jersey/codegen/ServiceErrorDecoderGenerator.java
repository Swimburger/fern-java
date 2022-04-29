package com.fern.services.jersey.codegen;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.services.http.HttpEndpoint;
import com.services.http.HttpService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.types.NamedType;
import feign.codec.ErrorDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.Modifier;
import org.immutables.value.Value;

public final class ServiceErrorDecoderGenerator {

    private static final String ERROR_DECODER_CLASSNAME_SUFFIX = "ErrorDecoder";

    private static final String NESTED_ENDPOINT_EXCEPTION_SUFFIX = "Exception";

    private static final String EXCEPTION_RETRIEVER_CLASSNAME = "ExceptionRetriever";
    private static final String GET_EXCEPTION_METHOD_NAME = "getException";

    private static final String IMMUTABLES_ERROR_ATTRIBUTE_NAME = "error";

    private final GeneratorContext generatorContext;
    private final HttpService httpService;
    private final ClassName errorDecoderClassName;
    private final ClassName exceptionRetrieverClassName;
    private final Map<NamedType, ClassName> errorClassNames = new HashMap<>();
    private final Multimap<NamedType, HttpEndpoint> errorToEndpoints = ArrayListMultimap.create();

    public ServiceErrorDecoderGenerator(
            GeneratorContext generatorContext, HttpService httpService) {
        this.generatorContext = generatorContext;
        this.httpService = httpService;
        this.errorDecoderClassName = generatorContext.getClassNameUtils().getClassName(
                httpService.name().name() + ERROR_DECODER_CLASSNAME_SUFFIX,
                Optional.of(PackageType.SERVICES),
                Optional.of(httpService.name().fernFilepath()));
        this.exceptionRetrieverClassName = errorDecoderClassName.nestedClass(EXCEPTION_RETRIEVER_CLASSNAME);
        httpService.endpoints().forEach(httpEndpoint -> {
            httpEndpoint.errors().possibleErrors().forEach(responseError -> {
                errorToEndpoints.put(responseError.error(), httpEndpoint);
            });
        });
        errorToEndpoints.keys().forEach(namedType -> {
            ClassName nestedResponseError = errorDecoderClassName.nestedClass(namedType.name());
            errorClassNames.put(namedType, nestedResponseError);
        });
    }

    public void generate() {
        TypeSpec.Builder errorDecoderTypeSpecBuilder = TypeSpec.classBuilder(errorDecoderClassName.simpleName())
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(ErrorDecoder.class);
    }

    private TypeSpec getExceptionRetrieverInterface() {
        return TypeSpec.interfaceBuilder(EXCEPTION_RETRIEVER_CLASSNAME)
                .addMethod(MethodSpec.methodBuilder(GET_EXCEPTION_METHOD_NAME)
                        .addModifiers(Modifier.ABSTRACT)
                        .returns(Exception.class)
                        .build())
                .build();
    }

    private Optional<TypeSpec> getEndpointExceptionUnionInterface(HttpEndpoint httpEndpoint) {
        if (httpEndpoint.errors().possibleErrors().isEmpty()) {
            return Optional.empty();
        }
        ClassName endpointBaseException = generatorContext.getClassNameUtils()
                    .getClassName(
                            httpEndpoint.endpointId() + NESTED_ENDPOINT_EXCEPTION_SUFFIX,
                            Optional.of(PackageType.SERVICES),
                            Optional.of(httpService.name().fernFilepath()));
        TypeSpec.Builder endpointBaseExceptionBuilder = TypeSpec.interfaceBuilder(endpointBaseException.simpleName())
                .addModifiers(Modifier.ABSTRACT)
                .addSuperinterface(exceptionRetrieverClassName)
                .addAnnotation(AnnotationSpec.builder(JsonTypeInfo.class)
                        .addMember("use", "$T.$L", ClassName.get(JsonTypeInfo.Id.class), JsonTypeInfo.Id.NAME.name())
                        .addMember(
                                "include",
                                "$T.$L",
                                ClassName.get(JsonTypeInfo.As.class),
                                JsonTypeInfo.As.EXISTING_PROPERTY.name())
                        .addMember("property", "$S", httpEndpoint.errors().discriminant())
                        .addMember("visible", "true")
                        // .addMember("defaultImpl", "$T.class", RemoteException.class)
                        .build());
        AnnotationSpec.Builder jsonSubTypeAnnotationBuilder = AnnotationSpec.builder(JsonSubTypes.class);
        httpEndpoint.errors().possibleErrors().forEach(responseError ->  {
            AnnotationSpec subTypeAnnotation = AnnotationSpec.builder(JsonSubTypes.Type.class)
                    .addMember("value", "$T.class", errorClassNames.get(responseError.error()))
                    .addMember("name", "$S", responseError.discriminantValue())
                    .build();
            jsonSubTypeAnnotationBuilder.addMember("value", "$L", subTypeAnnotation);
        });
        endpointBaseExceptionBuilder.addAnnotation(jsonSubTypeAnnotationBuilder.build());
        return Optional.of(endpointBaseExceptionBuilder.build());
    }

    private TypeSpec getNestedErrorTypeSpec(NamedType errorNamedType, List<ClassName> endpointBaseExceptions) {
        ClassName nestedErrorClassName = errorClassNames.get(errorNamedType);
        ClassName immutablesClassName =
                generatorContext.getImmutablesUtils().getImmutablesClassName(nestedErrorClassName);
        return TypeSpec.interfaceBuilder(nestedErrorClassName)
                .addAnnotation(Value.Immutable.class)
                .addAnnotation(AnnotationSpec.builder(JsonDeserialize.class)
                        .addMember("as", "$T.class", immutablesClassName)
                        .build())
                .addSuperinterfaces(endpointBaseExceptions)
                .addMethod(MethodSpec.methodBuilder(IMMUTABLES_ERROR_ATTRIBUTE_NAME)
                        .addModifiers(Modifier.ABSTRACT)
                        .addAnnotation(JsonValue.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder(GET_EXCEPTION_METHOD_NAME)
                        .addModifiers(Modifier.ABSTRACT, Modifier.DEFAULT)
                        .addAnnotation(Override.class)
                        .addStatement("return error()")
                        .build())
                .build();
    }

}
