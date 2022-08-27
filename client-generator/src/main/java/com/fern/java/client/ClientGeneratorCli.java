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
import com.fern.java.generators.TypesGenerator;
import com.fern.java.generators.TypesGenerator.Result;
import com.fern.java.output.AbstractGeneratedFileOutput;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientGeneratorCli extends AbstractGeneratorCli {

    private static final Logger log = LoggerFactory.getLogger(ClientGeneratorCli.class);

    @Override
    public void run(
            DefaultGeneratorExecClient defaultGeneratorExecClient,
            GeneratorConfig generatorConfig,
            IntermediateRepresentation ir) {
        ClientGeneratorContext context = new ClientGeneratorContext(ir, generatorConfig);

        // types
        TypesGenerator typesGenerator = new TypesGenerator(context);
        Result generatedTypes = typesGenerator.generateFiles();

        // errors
        Map<DeclaredErrorName, AbstractGeneratedFileOutput> errors = ir.getErrors().stream()
                .collect(Collectors.toMap(ErrorDeclaration::getName, errorDeclaration -> {
                    ClientErrorGenerator clientErrorGenerator =
                            new ClientErrorGenerator(errorDeclaration, context, generatedTypes.getInterfaces());
                    return clientErrorGenerator.generateFile();
                }));

        // services
        ir.getServices().getHttp().stream().map(httpService -> {
            return null;
        });
    }

    public static void main(String... args) {
        ClientGeneratorCli cli = new ClientGeneratorCli();
        cli.run(args);
    }
}
