/*
 * (c) Copyright 2023 Birch Solutions Inc. All rights reserved.
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

package com.fern.java.client.generators;

import com.fern.irV20.model.commons.Availability;
import com.fern.irV20.model.commons.AvailabilityStatus;
import com.fern.irV20.model.commons.Name;
import com.fern.irV20.model.commons.NameAndWireValue;
import com.fern.irV20.model.commons.TypeId;
import com.fern.irV20.model.http.FileUploadRequest;
import com.fern.irV20.model.http.HttpEndpoint;
import com.fern.irV20.model.http.HttpRequestBody;
import com.fern.irV20.model.http.HttpRequestBodyReference;
import com.fern.irV20.model.http.HttpService;
import com.fern.irV20.model.http.InlinedRequestBody;
import com.fern.irV20.model.http.SdkRequestWrapper;
import com.fern.irV20.model.types.DeclaredTypeName;
import com.fern.irV20.model.types.ObjectProperty;
import com.fern.irV20.model.types.ObjectTypeDeclaration;
import com.fern.java.RequestBodyUtils;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.client.GeneratedWrappedRequest;
import com.fern.java.client.GeneratedWrappedRequest.InlinedRequestBodyGetters;
import com.fern.java.client.GeneratedWrappedRequest.ReferencedRequestBodyGetter;
import com.fern.java.client.GeneratedWrappedRequest.RequestBodyGetter;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.generators.ObjectGenerator;
import com.fern.java.output.GeneratedJavaInterface;
import com.fern.java.output.GeneratedObject;
import com.squareup.javapoet.ClassName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class WrappedRequestGenerator extends AbstractFileGenerator {
    private final HttpService httpService;
    private final HttpEndpoint httpEndpoint;
    private final SdkRequestWrapper sdkRequestWrapper;
    private final Map<TypeId, GeneratedJavaInterface> allGeneratedInterfaces;

    public WrappedRequestGenerator(
            SdkRequestWrapper sdkRequestWrapper,
            HttpService httpService,
            HttpEndpoint httpEndpoint,
            ClassName className,
            Map<TypeId, GeneratedJavaInterface> allGeneratedInterfaces,
            ClientGeneratorContext generatorContext) {
        super(className, generatorContext);
        this.httpService = httpService;
        this.httpEndpoint = httpEndpoint;
        this.sdkRequestWrapper = sdkRequestWrapper;
        this.allGeneratedInterfaces = allGeneratedInterfaces;
    }

    @Override
    public GeneratedWrappedRequest generateFile() {
        List<ObjectProperty> headerObjectProperties = new ArrayList<>();
        List<ObjectProperty> queryParameterObjectProperties = new ArrayList<>();
        List<DeclaredTypeName> extendedInterfaces = new ArrayList<>();
        httpService.getHeaders().forEach(httpHeader -> {
            headerObjectProperties.add(ObjectProperty.builder()
                    .availability(Availability.builder()
                            .status(AvailabilityStatus.GENERAL_AVAILABILITY)
                            .build())
                    .name(httpHeader.getName())
                    .valueType(httpHeader.getValueType())
                    .docs(httpHeader.getDocs())
                    .build());
        });
        httpEndpoint.getHeaders().forEach(httpHeader -> {
            headerObjectProperties.add(ObjectProperty.builder()
                    .availability(Availability.builder()
                            .status(AvailabilityStatus.GENERAL_AVAILABILITY)
                            .build())
                    .name(httpHeader.getName())
                    .valueType(httpHeader.getValueType())
                    .docs(httpHeader.getDocs())
                    .build());
        });
        httpEndpoint.getQueryParameters().forEach(queryParameter -> {
            queryParameterObjectProperties.add(ObjectProperty.builder()
                    .availability(Availability.builder()
                            .status(AvailabilityStatus.GENERAL_AVAILABILITY)
                            .build())
                    .name(queryParameter.getName())
                    .valueType(queryParameter.getValueType())
                    .docs(queryParameter.getDocs())
                    .build());
        });

        RequestBodyPropertiesComputer requestBodyPropertiesComputer =
                new RequestBodyPropertiesComputer(extendedInterfaces);
        List<ObjectProperty> objectProperties = httpEndpoint
                .getRequestBody()
                .map(httpRequestBody -> httpRequestBody.visit(requestBodyPropertiesComputer))
                .orElseGet(Collections::emptyList);
        ObjectTypeDeclaration objectTypeDeclaration = ObjectTypeDeclaration.builder()
                .addAllExtends(extendedInterfaces)
                .addAllProperties(headerObjectProperties)
                .addAllProperties(queryParameterObjectProperties)
                .addAllProperties(objectProperties)
                .build();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                objectTypeDeclaration,
                Optional.empty(),
                extendedInterfaces.stream()
                        .map(DeclaredTypeName::getTypeId)
                        .map(allGeneratedInterfaces::get)
                        .collect(Collectors.toList()),
                generatorContext,
                allGeneratedInterfaces,
                className,
                false);
        GeneratedObject generatedObject = objectGenerator.generateFile();
        RequestBodyGetterFactory requestBodyGetterFactory =
                new RequestBodyGetterFactory(objectProperties, generatedObject);
        return GeneratedWrappedRequest.builder()
                .className(generatedObject.getClassName())
                .javaFile(generatedObject.javaFile())
                .requestBodyGetter(httpEndpoint
                        .getRequestBody()
                        .map(httpRequestBody -> httpRequestBody.visit(requestBodyGetterFactory)))
                .addAllHeaderParams(headerObjectProperties.stream()
                        .map(objectProperty ->
                                generatedObject.objectPropertyGetters().get(objectProperty))
                        .collect(Collectors.toList()))
                .addAllQueryParams(queryParameterObjectProperties.stream()
                        .map(objectProperty ->
                                generatedObject.objectPropertyGetters().get(objectProperty))
                        .collect(Collectors.toList()))
                .build();
    }

    private static final class RequestBodyGetterFactory implements HttpRequestBody.Visitor<RequestBodyGetter> {

        private final List<ObjectProperty> requestBodyProperties;
        private final GeneratedObject generatedObject;

        RequestBodyGetterFactory(List<ObjectProperty> requestBodyProperties, GeneratedObject generatedObject) {
            this.requestBodyProperties = requestBodyProperties;
            this.generatedObject = generatedObject;
        }

        @Override
        public RequestBodyGetter visitInlinedRequestBody(InlinedRequestBody _inlinedRequestBody) {
            return InlinedRequestBodyGetters.builder()
                    .addAllProperties(requestBodyProperties.stream()
                            .map(objectProperty ->
                                    generatedObject.objectPropertyGetters().get(objectProperty))
                            .collect(Collectors.toList()))
                    .build();
        }

        @Override
        public RequestBodyGetter visitReference(HttpRequestBodyReference reference) {
            return ReferencedRequestBodyGetter.builder()
                    .requestBodyGetter(generatedObject
                            .objectPropertyGetters()
                            .get(requestBodyProperties.get(0))
                            .getterProperty())
                    .build();
        }

        @Override
        public RequestBodyGetter visitFileUpload(FileUploadRequest fileUpload) {
            return null;
        }

        @Override
        public RequestBodyGetter _visitUnknown(Object unknownType) {
            return null;
        }
    }

    private final class RequestBodyPropertiesComputer implements HttpRequestBody.Visitor<List<ObjectProperty>> {

        private final List<DeclaredTypeName> extendedInterfaces;

        private RequestBodyPropertiesComputer(List<DeclaredTypeName> extendedInterfaces) {
            this.extendedInterfaces = extendedInterfaces;
        }

        @Override
        public List<ObjectProperty> visitInlinedRequestBody(InlinedRequestBody inlinedRequestBody) {
            List<ObjectProperty> inlinedObjectProperties = new ArrayList<>();
            extendedInterfaces.addAll(inlinedRequestBody.getExtends());
            inlinedObjectProperties.addAll(RequestBodyUtils.convertToObjectProperties(inlinedRequestBody));
            return inlinedObjectProperties;
        }

        @Override
        public List<ObjectProperty> visitReference(HttpRequestBodyReference reference) {
            return List.of(ObjectProperty.builder()
                    .availability(Availability.builder()
                            .status(AvailabilityStatus.GENERAL_AVAILABILITY)
                            .build())
                    .name(NameAndWireValue.builder()
                            .wireValue(sdkRequestWrapper.getBodyKey().getOriginalName())
                            .name(Name.builder()
                                    .originalName(sdkRequestWrapper.getBodyKey().getOriginalName())
                                    .camelCase(sdkRequestWrapper.getBodyKey().getCamelCase())
                                    .pascalCase(sdkRequestWrapper.getBodyKey().getPascalCase())
                                    .snakeCase(sdkRequestWrapper.getBodyKey().getSnakeCase())
                                    .screamingSnakeCase(
                                            sdkRequestWrapper.getBodyKey().getScreamingSnakeCase())
                                    .build())
                            .build())
                    .valueType(reference.getRequestBodyType())
                    .docs(reference.getDocs())
                    .build());
        }

        @Override
        public List<ObjectProperty> visitFileUpload(FileUploadRequest fileUpload) {
            List<ObjectProperty> inlinedObjectProperties = new ArrayList<>();
            inlinedObjectProperties.addAll(RequestBodyUtils.convertToObjectProperties(fileUpload));
            return inlinedObjectProperties;
        }

        @Override
        public List<ObjectProperty> _visitUnknown(Object unknownType) {
            return null;
        }
    }
}
