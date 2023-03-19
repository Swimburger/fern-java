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

package com.fern.java.spring.generators;

import com.fern.ir.v3.model.commons.WireStringWithAllCasings;
import com.fern.ir.v3.model.declaration.Availability;
import com.fern.ir.v3.model.declaration.AvailabilityStatus;
import com.fern.ir.v3.model.services.http.HttpService;
import com.fern.ir.v3.model.services.http.InlinedRequestBody;
import com.fern.ir.v3.model.types.DeclaredTypeName;
import com.fern.ir.v3.model.types.ObjectProperty;
import com.fern.ir.v3.model.types.ObjectTypeDeclaration;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.generators.ObjectGenerator;
import com.fern.java.output.AbstractGeneratedJavaFile;
import com.fern.java.output.GeneratedJavaInterface;
import com.fern.java.spring.SpringGeneratorContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class InlinedRequestBodyGenerator extends AbstractFileGenerator {

    private final InlinedRequestBody inlinedRequestBody;
    private final Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces;
    private final List<GeneratedJavaInterface> extendedInterfaces;

    public InlinedRequestBodyGenerator(
            HttpService httpService,
            InlinedRequestBody inlinedRequestBody,
            Map<DeclaredTypeName, GeneratedJavaInterface> allGeneratedInterfaces,
            SpringGeneratorContext springGeneratorContext) {
        super(
                springGeneratorContext
                        .getPoetClassNameFactory()
                        .getInlinedRequestBodyClassName(httpService, inlinedRequestBody),
                springGeneratorContext);
        this.inlinedRequestBody = inlinedRequestBody;
        this.allGeneratedInterfaces = allGeneratedInterfaces;
        this.extendedInterfaces =
                inlinedRequestBody.getExtends().stream().map(allGeneratedInterfaces::get).collect(Collectors.toList());
    }

    @Override
    public AbstractGeneratedJavaFile generateFile() {
        ObjectTypeDeclaration objectTypeDeclaration = ObjectTypeDeclaration.builder()
                .addAllExtends(inlinedRequestBody.getExtends())
                .addAllProperties(inlinedRequestBody.getProperties().stream()
                        .map(inlinedRequestBodyProperty -> ObjectProperty.builder()
                                .availability(Availability.builder()
                                        .status(AvailabilityStatus.GENERAL_AVAILABILITY)
                                        .build())
                                .name(WireStringWithAllCasings.builder()
                                        .originalValue(inlinedRequestBodyProperty
                                                .getName()
                                                .getWireValue())
                                        .camelCase(inlinedRequestBodyProperty
                                                .getName()
                                                .getName()
                                                .getSafeName()
                                                .getCamelCase())
                                        .pascalCase(inlinedRequestBodyProperty
                                                .getName()
                                                .getName()
                                                .getSafeName()
                                                .getPascalCase())
                                        .snakeCase(inlinedRequestBodyProperty
                                                .getName()
                                                .getName()
                                                .getSafeName()
                                                .getSnakeCase())
                                        .screamingSnakeCase(inlinedRequestBodyProperty
                                                .getName()
                                                .getName()
                                                .getSafeName()
                                                .getScreamingSnakeCase())
                                        .wireValue(inlinedRequestBodyProperty
                                                .getName()
                                                .getWireValue())
                                        .build())
                                .nameV2(inlinedRequestBodyProperty.getName())
                                .valueType(inlinedRequestBodyProperty.getValueType())
                                .docs(inlinedRequestBodyProperty.getDocs())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        ObjectGenerator objectGenerator = new ObjectGenerator(
                objectTypeDeclaration,
                Optional.empty(),
                extendedInterfaces,
                generatorContext,
                allGeneratedInterfaces,
                className);
        return objectGenerator.generateFile();
    }
}
