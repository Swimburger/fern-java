/*
 * (c) Copyright 2022 Birch Solutions Inc. All rights reserved.
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

package com.fern.java.generators.auth;

import com.fern.ir.model.services.http.HttpHeader;
import com.fern.ir.model.types.AliasTypeDeclaration;
import com.fern.java.AbstractGeneratorContext;
import com.fern.java.generators.AbstractFileGenerator;
import com.fern.java.generators.AliasGenerator;
import com.fern.java.output.AbstractGeneratedFileOutput;

public final class HeaderAuthGenerator extends AbstractFileGenerator {
    private static final String VALUE_METHOD_NAME = "value";

    private final HttpHeader httpHeader;

    public HeaderAuthGenerator(AbstractGeneratorContext<?> generatorContext, HttpHeader httpHeader) {
        super(
                generatorContext
                        .getPoetClassNameFactory()
                        .getTopLevelClassName(httpHeader.getName().getPascalCase()),
                generatorContext);
        this.httpHeader = httpHeader;
    }

    @Override
    public AbstractGeneratedFileOutput generateFile() {
        AliasTypeDeclaration aliasTypeDeclaration = AliasTypeDeclaration.builder()
                .aliasOf(httpHeader.getValueType())
                .build();
        AliasGenerator aliasGenerator = new AliasGenerator(className, generatorContext, aliasTypeDeclaration);
        return aliasGenerator.generateFile();
    }
}
