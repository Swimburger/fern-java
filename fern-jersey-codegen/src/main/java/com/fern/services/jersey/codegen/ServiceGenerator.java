package com.fern.services.jersey.codegen;

import com.fern.codegen.GeneratedHttpService;
import com.fern.codegen.GeneratorContext;
import com.fern.codegen.utils.ClassNameUtils.PackageType;
import com.fern.model.codegen.Generator;
import com.services.commons.WireMessage;
import com.services.http.HttpEndpoint;
import com.services.http.HttpHeader;
import com.services.http.HttpMethod;
import com.services.http.HttpService;
import com.services.http.PathParameter;
import com.services.http.QueryParameter;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.types.TypeReference;
import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;

public final class ServiceGenerator extends Generator {

    private final HttpService httpService;

    public ServiceGenerator(GeneratorContext generatorContext, HttpService httpService) {
        super(generatorContext, PackageType.SERVICES);
        this.httpService = httpService;
    }

    @Override
    public GeneratedHttpService generate() {
        ClassName generatedServiceClassName =
                generatorContext.getClassNameUtils().getClassNameForNamedType(httpService.name(), packageType);
        TypeSpec.Builder jerseyServiceBuilder = TypeSpec.interfaceBuilder(
                        StringUtils.capitalize(httpService.name().name()))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Consumes.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Produces.class)
                        .addMember("value", "$T.APPLICATION_JSON", MediaType.class)
                        .build())
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", httpService.basePath())
                        .build());
        List<MethodSpec> httpEndpointMethods = httpService.endpoints().stream()
                .map(this::getHttpEndpointMethodSpec)
                .collect(Collectors.toList());
        TypeSpec jerseyServiceTypeSpec =
                jerseyServiceBuilder.addMethods(httpEndpointMethods).build();
        JavaFile jerseyServiceJavaFile = JavaFile.builder(
                        generatedServiceClassName.packageName(), jerseyServiceTypeSpec)
                .build();
        return GeneratedHttpService.builder()
                .file(jerseyServiceJavaFile)
                .className(generatedServiceClassName)
                .httpService(httpService)
                .build();
    }

    private MethodSpec getHttpEndpointMethodSpec(HttpEndpoint httpEndpoint) {
        MethodSpec.Builder endpointMethodBuilder = MethodSpec.methodBuilder(httpEndpoint.endpointId())
                .addAnnotation(httpEndpoint.method().accept(HttpMethodAnnotationVisitor.INSTANCE))
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", httpEndpoint.path())
                        .build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
        httpEndpoint.headers().stream().map(this::getHeaderParameterSpec).forEach(endpointMethodBuilder::addParameter);
        httpEndpoint.parameters().stream().map(this::getPathParameterSpec).forEach(endpointMethodBuilder::addParameter);
        httpEndpoint.queryParameters().stream()
                .map(this::getQueryParameterSpec)
                .forEach(endpointMethodBuilder::addParameter);
        httpEndpoint.request().ifPresent(requestWireMessage -> {
            ServiceWireMessageGenerator serviceWireMessageGenerator =
                    new ServiceWireMessageGenerator(
                            generatorContext, httpService, httpEndpoint, requestWireMessage, true);
            WireMessageGeneratorResult wireMessageGeneratorResult = serviceWireMessageGenerator.generate();
            endpointMethodBuilder.addParameter(ParameterSpec.builder(wireMessageGeneratorResult.typeName(), "request")
                    .build());
        });
        httpEndpoint.response().ifPresent(responseWireMessage -> {
            ServiceWireMessageGenerator serviceWireMessageGenerator =
                    new ServiceWireMessageGenerator(
                            generatorContext, httpService, httpEndpoint, responseWireMessage, false);
            WireMessageGeneratorResult wireMessageGeneratorResult = serviceWireMessageGenerator.generate();
            endpointMethodBuilder.returns(wireMessageGeneratorResult.typeName());
        });
        return endpointMethodBuilder.build();
    }

    private ParameterSpec getHeaderParameterSpec(HttpHeader header) {
        return getParameterSpec(HeaderParam.class, header.header(), header.valueType());
    }

    private ParameterSpec getPathParameterSpec(PathParameter pathParameter) {
        return getParameterSpec(PathParam.class, pathParameter.key(), pathParameter.valueType());
    }

    private ParameterSpec getQueryParameterSpec(QueryParameter queryParameter) {
        return getParameterSpec(QueryParam.class, queryParameter.key(), queryParameter.valueType());
    }

    private <T> ParameterSpec getParameterSpec(Class<T> paramClass, String paramName, TypeReference paramType) {
        TypeName typeName = generatorContext.getClassNameUtils().getTypeNameFromTypeReference(false, paramType);
        return ParameterSpec.builder(typeName, paramName)
                .addAnnotation(AnnotationSpec.builder(paramClass)
                        .addMember("value", "$S", paramName)
                        .build())
                .build();
    }

    private ParameterSpec getRequestTypeName(WireMessage httpRequest) {
        throw new RuntimeException("Unsupported");
    }

    private TypeName getResponseTypeName(WireMessage httpResponse) {
        throw new RuntimeException("Unsupported");
    }

    private static final class HttpMethodAnnotationVisitor implements HttpMethod.Visitor<AnnotationSpec> {

        private static final HttpMethodAnnotationVisitor INSTANCE = new HttpMethodAnnotationVisitor();

        @Override
        public AnnotationSpec visitGet() {
            return AnnotationSpec.builder(GET.class).build();
        }

        @Override
        public AnnotationSpec visitPost() {
            return AnnotationSpec.builder(POST.class).build();
        }

        @Override
        public AnnotationSpec visitPut() {
            return AnnotationSpec.builder(PUT.class).build();
        }

        @Override
        public AnnotationSpec visitDelete() {
            return AnnotationSpec.builder(DELETE.class).build();
        }

        @Override
        public AnnotationSpec visitPatch() {
            return AnnotationSpec.builder(PATCH.class).build();
        }

        @Override
        public AnnotationSpec visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown HttpMethod: " + unknownType);
        }
    }
}
