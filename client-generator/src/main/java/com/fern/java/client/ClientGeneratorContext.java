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

package com.fern.java.client;

import com.fern.generator.exec.model.config.GeneratorConfig;
import com.fern.ir.model.ir.IntermediateRepresentation;
import com.fern.ir.model.types.DeclaredTypeName;
import com.fern.java.AbstractGeneratorContext;
import com.fern.java.output.AbstractGeneratedFileOutput;
import com.fern.java.output.GeneratedInterfaceOutput;
import java.util.HashMap;
import java.util.Map;

public final class ClientGeneratorContext extends AbstractGeneratorContext {

    private final Map<DeclaredTypeName, GeneratedInterfaceOutput> generatedInterfaces = new HashMap<>();
    private final Map<DeclaredTypeName, AbstractGeneratedFileOutput> generatedTypes = new HashMap<>();

    public ClientGeneratorContext(IntermediateRepresentation ir, GeneratorConfig generatorConfig) {
        super(ir, generatorConfig, new ClientPoetClassNameFactory(ir, generatorConfig.getOrganization()));
    }

    public void addAllGeneratedInterfaces(Map<DeclaredTypeName, GeneratedInterfaceOutput> values) {
        this.generatedInterfaces.putAll(values);
    }

    public void addAllGeneratedTypes(Map<DeclaredTypeName, AbstractGeneratedFileOutput> values) {
        this.generatedTypes.putAll(values);
    }
}
