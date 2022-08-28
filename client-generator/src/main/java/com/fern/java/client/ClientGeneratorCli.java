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
import com.fern.ir.model.errors.DeclaredErrorName;
import com.fern.ir.model.errors.ErrorDeclaration;
import com.fern.ir.model.ir.IntermediateRepresentation;
import com.fern.java.AbstractGeneratorCli;
import com.fern.java.DefaultGeneratorExecClient;
import com.fern.java.client.generators.ClientErrorGenerator;
import com.fern.java.client.generators.ClientWrapperGenerator;
import com.fern.java.client.generators.HttpServiceClientGenerator;
import com.fern.java.generators.AuthGenerator;
import com.fern.java.generators.TypesGenerator;
import com.fern.java.generators.TypesGenerator.Result;
import com.fern.java.output.AbstractGeneratedFileOutput;
import com.fern.java.output.GeneratedAuthFilesOutput;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientGeneratorCli extends AbstractGeneratorCli {

    private static final String UTILS_VERSION = "0.0.82";

    private static final Logger log = LoggerFactory.getLogger(ClientGeneratorCli.class);

    @Override
    public void run(
            DefaultGeneratorExecClient defaultGeneratorExecClient,
            GeneratorConfig generatorConfig,
            IntermediateRepresentation ir) {
        ClientGeneratorContext context = new ClientGeneratorContext(ir, generatorConfig);

        // auth
        AuthGenerator authGenerator = new AuthGenerator(context);
        Optional<GeneratedAuthFilesOutput> maybeAuth = authGenerator.generate();
        maybeAuth.ifPresent(addGeneratedFile);

        // types
        TypesGenerator typesGenerator = new TypesGenerator(context);
        Result generatedTypes = typesGenerator.generateFiles();
        generatedTypes.getTypes().values().forEach(addGeneratedFile);
        generatedTypes.getInterfaces().values().forEach(addGeneratedFile);

        // errors
        Map<DeclaredErrorName, AbstractGeneratedFileOutput> errors = ir.getErrors().stream()
                .collect(Collectors.toMap(ErrorDeclaration::getName, errorDeclaration -> {
                    ClientErrorGenerator clientErrorGenerator =
                            new ClientErrorGenerator(errorDeclaration, context, generatedTypes.getInterfaces());
                    return clientErrorGenerator.generateFile();
                }));
        errors.values().forEach(addGeneratedFile);

        // services
        List<GeneratedServiceClientOutput> generatedServiceClients = ir.getServices().getHttp().stream()
                .map(httpService -> {
                    HttpServiceClientGenerator httpServiceClientGenerator =
                            new HttpServiceClientGenerator(context, httpService, errors, maybeAuth);
                    return httpServiceClientGenerator.generateFile();
                })
                .collect(Collectors.toList());
        generatedServiceClients.forEach(addGeneratedFile);
        generatedServiceClients.forEach(generatedServiceClientOutput -> {
            addGeneratedFile.accept(generatedServiceClientOutput);
            addGeneratedFile.accept(generatedServiceClientOutput.jerseyServiceInterfaceOutput());
            addGeneratedFile.accept(
                    generatedServiceClientOutput.jerseyServiceInterfaceOutput().errorDecoder());
            generatedServiceClientOutput.generatedEndpointRequestOutputs().forEach(addGeneratedFile);
            generatedServiceClientOutput
                    .jerseyServiceInterfaceOutput()
                    .endpointExceptions()
                    .values()
                    .forEach(addGeneratedFile);
        });

        // client wrapper
        ClientWrapperGenerator clientWrapperGenerator =
                new ClientWrapperGenerator(context, generatedServiceClients, maybeAuth);
        GeneratedClientWrapperOutput generatedClientWrapperOutput = clientWrapperGenerator.generateFile();
        addGeneratedFile.accept(generatedClientWrapperOutput);
        generatedClientWrapperOutput.nestedClients().forEach(addGeneratedFile);
    }

    @Override
    public List<String> getBuildGradleDependencies() {
        return List.of(
                "    api 'io.github.fern-api:jackson-utils:" + UTILS_VERSION + "'",
                "    implementation 'io.github.fern-api:jersey-utils:" + UTILS_VERSION + "'",
                "    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.3'",
                "    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.3'",
                "    implementation 'io.github.openfeign:feign-jackson:11.8'",
                "    implementation 'io.github.openfeign:feign-core:11.8'",
                "    implementation 'io.github.openfeign:feign-jaxrs2:11.8'");
    }

    public static void main(String... args) {
        ClientGeneratorCli cli = new ClientGeneratorCli();
        cli.run(args);
    }
}