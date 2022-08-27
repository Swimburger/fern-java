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
import com.fern.generator.exec.model.config.MavenRegistryConfigV2;
import com.fern.generator.exec.model.logging.ErrorExitStatusUpdate;
import com.fern.generator.exec.model.logging.ExitStatusUpdate;
import com.fern.generator.exec.model.logging.GeneratorUpdate;
import com.fern.ir.model.ir.IntermediateRepresentation;
import com.fern.java.jackson.ClientObjectMappers;
import com.fern.java.output.AbstractGeneratedFileOutput;
import com.squareup.javapoet.JavaFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGeneratorCli {

    private static final Logger log = LoggerFactory.getLogger(AbstractGeneratorCli.class);

    private static final String SRC_MAIN_JAVA = "src/main/java";
    private static final String BUILD_GRADLE = "build.gradle";

    private final List<AbstractGeneratedFileOutput> generatedFiles = new ArrayList<>();

    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final Consumer<AbstractGeneratedFileOutput> addGeneratedFile;

    protected AbstractGeneratorCli() {
        this.addGeneratedFile = generatedFiles::add;
    }

    public final void run(String... args) {
        String pluginPath = args[0];
        GeneratorConfig generatorConfig = getGeneratorConfig(pluginPath);
        DefaultGeneratorExecClient generatorExecClient = new DefaultGeneratorExecClient(generatorConfig);
        try {
            IntermediateRepresentation ir = getIr(generatorConfig);
            run(generatorExecClient, generatorConfig, ir);

            String outputDirectory = generatorConfig.getOutput().getPath();

            BuildGradleConfig buildGradleConfig = getBuildGradle(generatorConfig);
            writeFileContents(Paths.get(outputDirectory, BUILD_GRADLE), buildGradleConfig.getFileContents());

            generatedFiles.forEach(generatedFileOutput ->
                    writeFile(Paths.get(outputDirectory, SRC_MAIN_JAVA), generatedFileOutput.javaFile()));
        } catch (Exception e) {
            log.error("Encountered fatal error", e);
            generatorExecClient.sendUpdate(GeneratorUpdate.exitStatusUpdate(ExitStatusUpdate.error(
                    ErrorExitStatusUpdate.builder().message(e.getMessage()).build())));
            throw new RuntimeException(e);
        }
    }

    public abstract void run(
            DefaultGeneratorExecClient generatorExecClient,
            GeneratorConfig generatorConfig,
            IntermediateRepresentation ir);

    public abstract List<String> getBuildGradleDependencies();

    private BuildGradleConfig getBuildGradle(GeneratorConfig generatorConfig) {
        return BuildGradleConfig.builder()
                .addAllDependencies(getBuildGradleDependencies())
                .publishing(generatorConfig.getPublish().map(generatorPublishConfig -> {
                    MavenRegistryConfigV2 mavenRegistryConfigV2 =
                            generatorPublishConfig.getRegistriesV2().getMaven();
                    String[] splitCoordinate =
                            mavenRegistryConfigV2.getCoordinate().split(":");
                    if (splitCoordinate.length < 2) {
                        throw new IllegalStateException(
                                "Received invalid maven coordinate: " + mavenRegistryConfigV2.getCoordinate());
                    }
                    return ImmutablePublishingConfig.builder()
                            .version(generatorPublishConfig.getVersion())
                            .registryUrl(mavenRegistryConfigV2.getRegistryUrl())
                            .registryUsername(mavenRegistryConfigV2.getUsername())
                            .registryPassword(mavenRegistryConfigV2.getPassword())
                            .group(splitCoordinate[0])
                            .artifact(splitCoordinate[1])
                            .build();
                }))
                .build();
    }

    private static GeneratorConfig getGeneratorConfig(String pluginPath) {
        try {
            return ClientObjectMappers.JSON_MAPPER.readValue(new File(pluginPath), GeneratorConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read plugin configuration", e);
        }
    }

    private static IntermediateRepresentation getIr(GeneratorConfig generatorConfig) {
        try {
            return ClientObjectMappers.JSON_MAPPER.readValue(
                    new File(generatorConfig.getIrFilepath()), IntermediateRepresentation.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ir", e);
        }
    }

    private static void writeFile(Path path, JavaFile javaFile) {
        try {
            javaFile.writeToFile(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write generated java file: " + javaFile.typeSpec.name, e);
        }
    }

    private static void writeFileContents(Path path, String contents) {
        try {
            Files.writeString(path, contents);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write .gitignore ", e);
        }
    }
}
