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

import com.fern.generator.exec.model.config.GeneratorConfig;
import com.fern.generator.exec.model.config.GeneratorEnvironment;
import com.fern.generator.exec.model.config.GeneratorOutputConfig;
import com.fern.generator.exec.model.config.GithubOutputMode;
import com.fern.generator.exec.model.config.GithubPublishInfo;
import com.fern.generator.exec.model.config.MavenGithubPublishInfo;
import com.fern.generator.exec.model.config.OutputMode;
import com.fern.irV20.model.auth.ApiAuth;
import com.fern.irV20.model.auth.AuthScheme;
import com.fern.irV20.model.http.HttpHeader;
import com.fern.irV20.model.ir.IntermediateRepresentation;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.output.RawGeneratedFile;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReadmeGeneratorTest {

    @Test
    public void test_generateReadme() {
        List<HttpHeader> headerList = new ArrayList<HttpHeader>();
        List<AuthScheme> authSchemeList = new ArrayList<AuthScheme>();
        IntermediateRepresentation ir = IntermediateRepresentation.builder()
                .apiName(null)
                .auth(ApiAuth.builder()
                        .requirement(null)
                        .addAllSchemes(authSchemeList)
                        .build())
                .rootPackage(null)
                .constants(null)
                .errorDiscriminationStrategy(null)
                .sdkConfig(null)
                .addAllHeaders(headerList)
                .build();
        MavenGithubPublishInfo mavenGithubPublishInfo = MavenGithubPublishInfo.builder()
                .registryUrl("acme.co")
                .coordinate("test-artifact:1.0.0")
                .usernameEnvironmentVariable(null)
                .passwordEnvironmentVariable(null)
                .build();

        OutputMode outputMode = OutputMode.github(GithubOutputMode.builder()
                .version(null)
                .repoUrl(null)
                .publishInfo(GithubPublishInfo.maven(mavenGithubPublishInfo))
                .build());
        GeneratorOutputConfig generatorOutputConfig =
                GeneratorOutputConfig.builder().path(null).mode(outputMode).build();
        GeneratorConfig generatorConfig = GeneratorConfig.builder()
                .dryRun(false)
                .irFilepath(null)
                .output(generatorOutputConfig)
                .workspaceName("workspace")
                .organization("fern")
                .environment(GeneratorEnvironment.local())
                .build();
        ClientGeneratorContext clientGeneratorContext = new ClientGeneratorContext(ir, generatorConfig, null, null);
        ReadmeGenerator readmeGenerator = new ReadmeGenerator(clientGeneratorContext);
        RawGeneratedFile rawGeneratedFile = readmeGenerator.generateFile();
        Assertions.assertThat(rawGeneratedFile.contents()).isNotEmpty();
        Assertions.assertThat(rawGeneratedFile.contents())
                .contains("title", "badges", "summary", "installation", "instantiation", "usage", "status");
    }

    @Test
    public void test_generateReadmeWithoutMaven() {
        List<HttpHeader> headerList = new ArrayList<HttpHeader>();
        List<AuthScheme> authSchemeList = new ArrayList<AuthScheme>();
        IntermediateRepresentation ir = IntermediateRepresentation.builder()
                .apiName(null)
                .auth(ApiAuth.builder()
                        .requirement(null)
                        .addAllSchemes(authSchemeList)
                        .build())
                .rootPackage(null)
                .constants(null)
                .errorDiscriminationStrategy(null)
                .sdkConfig(null)
                .addAllHeaders(headerList)
                .build();
        OutputMode outputMode = OutputMode.github(
                GithubOutputMode.builder().version(null).repoUrl(null).build());
        GeneratorOutputConfig generatorOutputConfig =
                GeneratorOutputConfig.builder().path(null).mode(outputMode).build();
        GeneratorConfig generatorConfig = GeneratorConfig.builder()
                .dryRun(false)
                .irFilepath(null)
                .output(generatorOutputConfig)
                .workspaceName("workspace")
                .organization("fern")
                .environment(GeneratorEnvironment.local())
                .build();
        ClientGeneratorContext clientGeneratorContext = new ClientGeneratorContext(ir, generatorConfig, null, null);
        ReadmeGenerator readmeGenerator = new ReadmeGenerator(clientGeneratorContext);
        RawGeneratedFile rawGeneratedFile = readmeGenerator.generateFile();
        Assertions.assertThat(rawGeneratedFile.contents()).isNotEmpty();
        Assertions.assertThat(rawGeneratedFile.contents())
                .contains("title", "summary", "instantiation", "usage", "status");
    }
}
