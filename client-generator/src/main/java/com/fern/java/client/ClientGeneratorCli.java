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
import com.fern.java.AbstractGeneratorCli;
import com.fern.java.DefaultGeneratorExecClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientGeneratorCli extends AbstractGeneratorCli {

    private static final Logger log = LoggerFactory.getLogger(ClientGeneratorCli.class);

    @Override
    public void run(DefaultGeneratorExecClient defaultGeneratorExecClient, GeneratorConfig generatorConfig) {}

    public static void main(String... args) {
        ClientGeneratorCli cli = new ClientGeneratorCli();
        cli.run(args);
    }
}
