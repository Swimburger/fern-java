package com.fern.services.jersey.codegen;

import com.fern.HttpMethod;
import com.fern.HttpService;
import com.fern.NamedType;
import com.fern.codegen.utils.ClassNameUtils;
import com.fern.model.codegen.GeneratedFile;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;

public final class ServiceGenerator {

    private static final ClassName STRING_CLASS_NAME = ClassName.get(String.class);

    private final Map<NamedType, GeneratedFile<?>> generatedFiles;
    private final List<HttpService> httpServices;
    private final ClassNameUtils classNameUtils;

    public ServiceGenerator(Map<NamedType, GeneratedFile<?>> generatedFiles, List<HttpService> httpServices) {
        this.generatedFiles = generatedFiles;
        this.httpServices = httpServices;
        this.classNameUtils = new ClassNameUtils(Optional.empty());
    }

    public void generate() {
        List<TypeSpec> serviceTypeSpecs = httpServices.stream().map(httpService -> {
            TypeSpec.Builder jerseyServiceBuilder =
                    TypeSpec.interfaceBuilder(StringUtils.capitalize(httpService.name().name()))
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
            List<MethodSpec> httpEndpointMethods = httpService.endpoints().stream().map(httpEndpoint -> {
                MethodSpec.Builder endpointMethodBuilder = MethodSpec.methodBuilder(httpEndpoint.endpointId())
                        .addAnnotation(httpEndpoint.method().accept(HttpMethodAnnotationVisitor.INSTANCE))
                        .addAnnotation(AnnotationSpec.builder(Path.class)
                                .addMember("value", "$S", httpEndpoint.path())
                                .build())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                httpEndpoint.headers().forEach(httpHeader -> {
                        String capitalizedHeaderParameterName = StringUtils.capitalize(httpHeader.header());
                        TypeName headerTypeName = classNameUtils.getTypeNameFromTypeReference(
                            false, httpHeader.valueType());
                        endpointMethodBuilder.addParameter(ParameterSpec.builder(headerTypeName, capitalizedHeaderParameterName)
                                .addAnnotation(AnnotationSpec.builder(HeaderParam.class)
                                        .addMember("value", "$S", httpHeader.header())
                                        .build())
                                .build());
                });
                httpEndpoint.parameters().forEach(pathParameter -> {
                    String capitalizedPathParameterName = StringUtils.capitalize(pathParameter.key());
                    TypeName headerTypeName = classNameUtils.getTypeNameFromTypeReference(
                            false, pathParameter.valueType());
                    endpointMethodBuilder.addParameter(ParameterSpec.builder(headerTypeName, capitalizedPathParameterName)
                            .addAnnotation(AnnotationSpec.builder(PathParam.class)
                                    .addMember("value", "$S", pathParameter.key())
                                    .build())
                            .build());
                });
                httpEndpoint.queryParameters().forEach(queryParameter -> {
                    String capitalizedQueryParam = StringUtils.capitalize(queryParameter.key());
                    TypeName parameterTypeName = classNameUtils.getTypeNameFromTypeReference(
                            false, queryParameter.valueType());
                    endpointMethodBuilder.addParameter(ParameterSpec.builder(parameterTypeName, capitalizedQueryParam)
                            .addAnnotation(AnnotationSpec.builder(QueryParam.class)
                                    .addMember("value", "$S", queryParameter.key())
                                    .build())
                            .build());
                });
                httpEndpoint.request().ifPresent(httpRequest -> {
                    TypeName requestTypeName = classNameUtils.getTypeNameFromTypeReference(true,
                            httpRequest.bodyType());
                    endpointMethodBuilder.addParameter(requestTypeName, "request");
                });
                httpEndpoint.response().ifPresent(httpResponse -> {
                    TypeName responseTypeName = classNameUtils.getTypeNameFromTypeReference(true,
                            httpResponse.bodyType());
                    endpointMethodBuilder.returns(responseTypeName);
                });
                return endpointMethodBuilder.build();
            }).collect(Collectors.toList());
            jerseyServiceBuilder.addMethods(httpEndpointMethods);
            return jerseyServiceBuilder.build();
        }).collect(Collectors.toList());
    }

    private final static class HttpMethodAnnotationVisitor implements HttpMethod.Visitor<AnnotationSpec> {

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
        public AnnotationSpec visitUnknown(String unknownType) {
            throw new RuntimeException("Encountered unknown HttpMethod: " + unknownType);
        }
    }
}
