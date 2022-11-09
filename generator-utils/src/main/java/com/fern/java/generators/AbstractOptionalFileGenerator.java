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

package com.fern.java.generators;

import com.fern.java.AbstractGeneratorContext;
import com.fern.java.output.GeneratedFile;
import com.squareup.javapoet.ClassName;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AbstractOptionalFileGenerator extends AbstractFilesGenerator {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected ClassName className;

    public AbstractOptionalFileGenerator(ClassName className, AbstractGeneratorContext<?> generatorContext) {
        super(generatorContext);
        this.className = className;
    }

    public abstract Optional<? extends GeneratedFile> generateFile();

    @Override
    public final List<GeneratedFile> generateFiles() {
        if (generateFile().isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(generateFile().get());
    }
}
