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

package com.fern.java;

import com.fern.generator.exec.model.config.GeneratorConfig;
import com.fern.generator.exec.model.logging.ErrorExitStatusUpdate;
import com.fern.generator.exec.model.logging.ExitStatusUpdate;
import com.fern.generator.exec.model.logging.GeneratorUpdate;
import com.fern.java.jackson.ClientObjectMappers;
import java.io.File;
import java.io.IOException;

public abstract class AbstractGeneratorCli {

    public final void run(String... args) {
        String pluginPath = args[0];
        GeneratorConfig generatorConfig = getGeneratorConfig(pluginPath);
        DefaultGeneratorExecClient generatorExecClient = new DefaultGeneratorExecClient(generatorConfig);
        try {
            run(generatorExecClient, generatorConfig);
        } catch (Exception e) {
            generatorExecClient.sendUpdate(GeneratorUpdate.exitStatusUpdate(ExitStatusUpdate.error(
                    ErrorExitStatusUpdate.builder().message(e.getMessage()).build())));
            throw new RuntimeException(e);
        }
    }

    public abstract void run(DefaultGeneratorExecClient generatorExecClient, GeneratorConfig generatorConfig);

    private static GeneratorConfig getGeneratorConfig(String pluginPath) {
        try {
            return ClientObjectMappers.JSON_MAPPER.readValue(new File(pluginPath), GeneratorConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read plugin configuration", e);
        }
    }
}
