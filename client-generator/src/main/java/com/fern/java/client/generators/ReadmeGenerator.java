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

import com.fern.generator.exec.model.config.MavenGithubPublishInfo;
import com.fern.generatorExec.resources.readme.types.Readme;
import com.fern.irV20.model.auth.ApiAuth;
import com.fern.irV20.model.auth.AuthScheme;
import com.fern.irV20.model.auth.BasicAuthScheme;
import com.fern.irV20.model.auth.BearerAuthScheme;
import com.fern.irV20.model.auth.HeaderAuthScheme;
import com.fern.java.client.ClientGeneratorContext;
import com.fern.java.jackson.ClientObjectMappers;
import com.fern.java.output.RawGeneratedFile;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadmeGenerator {
    private static final Logger log = LoggerFactory.getLogger(ReadmeGenerator.class);

    private ApiAuth auth;
    private String rootClientClassName;
    private String rootClientVariableName;
    private String organization;
    private String registryUrl = null;
    private String coordinate = null;
    private String artifactId = null;
    private String version = "0.x.x";

    public ReadmeGenerator(ClientGeneratorContext clientGeneratorContext) {
        String organizationName = clientGeneratorContext.getGeneratorConfig().getOrganization();

        this.auth = clientGeneratorContext.getIr().getAuth();
        this.rootClientClassName = RootClientGenerator.getRootClientName(clientGeneratorContext);
        this.rootClientVariableName =
                rootClientClassName.substring(0, 1).toLowerCase(Locale.ROOT) + rootClientClassName.substring(1);
        this.organization = organizationName.substring(0, 1).toUpperCase(Locale.ROOT) + organizationName.substring(1);

        MavenGithubPublishInfo maven = null;
        try {
            maven = clientGeneratorContext
                    .getGeneratorConfig()
                    .getOutput()
                    .getMode()
                    .getGithub()
                    .get()
                    .getPublishInfo()
                    .get()
                    .getMaven()
                    .get();
        } catch (java.util.NoSuchElementException e) {
            // We can still render a README.md without a Maven configuration.
            log.debug("Generator context does not have a Maven configuration", e);
        }
        if (maven != null) {
            String[] coordinateSplit = maven.getCoordinate().split(":", 2);
            this.registryUrl = maven.getRegistryUrl();
            this.coordinate = maven.getCoordinate();
            this.artifactId = coordinateSplit[0];
            if (coordinateSplit.length == 2) {
                this.version = coordinateSplit[1];
            }
        }
    }

    public RawGeneratedFile generateFile() {
        Readme readme = Readme.builder()
                .title(getTitle())
                .badges(getBadges())
                .summary(getSummary())
                .installation(getInstallation())
                .instantiation(getInstantiation())
                .usage(getUsage())
                .status(getStatus())
                .build();

        String contents = "";
        try {
            contents = ClientObjectMappers.JSON_MAPPER.writeValueAsString(readme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return RawGeneratedFile.builder()
                .filename("readme.json")
                .contents(contents)
                .build();
    }

    private String getTitle() {
        return String.format("# %s Java Library", organization);
    }

    private String getBadges() {
        if (registryUrl == null || artifactId == null) {
            return "";
        }
        String mavenCentralPath = String.format("%s/%s", registryUrl, artifactId);
        return String.format(
                "[![Maven Central](https://img.shields.io/maven-central/v/%s)](https://central.sonatype"
                        + ".com/artifact/%s)",
                mavenCentralPath, mavenCentralPath);
    }

    private String getSummary() {
        return String.format(
                "The %s Java SDK provides convenient access to the %s API from Java.", organization, organization);
    }

    private String getInstallation() {
        if (registryUrl == null || artifactId == null) {
            return "";
        }
        return "# Gradle\n"
                + "\n"
                + "Add the dependency in your `build.gradle`:\n"
                + "```\n"
                + "dependencies {\n"
                + String.format("    implementation '%s.%s'\n", registryUrl, coordinate)
                + "}\n"
                + "```\n"
                + "\n"
                + "# Maven\n"
                + "\n"
                + "Add the dependency in your `pom.xml`"
                + "```\n"
                + "<dependency>\n"
                + String.format("    <groupId>%s</groupId>\n", registryUrl)
                + String.format("    <artifactId>%s</artifactId>\n", artifactId)
                + String.format("    <version>%s</version>\n", version)
                + "</dependency>\n"
                + "```\n";
    }

    private String getInstantiation() {
        String instantiation = "```java\n"
                + String.format(
                        "%s %s = %s.builder()\n", rootClientClassName, rootClientVariableName, rootClientClassName);
        if (auth.getSchemes().size() > 0) {
            instantiation += auth.getSchemes().get(0).visit(new AuthSchemeHandler());
        }
        instantiation += ".build()\n```\n";
        return instantiation;
    }

    private String getUsage() {
        return "## Staged Builders\n"
                + "\n"
                + "The generated builders all follow the staged builder pattern. Read more"
                + " [here](https://immutables.github.io/immutable.html#staged-builder).\n"
                + "\n"
                + "Staged builders only allow you to build the object once all required properties have been"
                + " specified.\n";
    }

    private String getStatus() {
        return "This SDK is in beta, and there may be breaking changes between versions without a major version "
                + "update. Therefore, we recommend pinning the package version to a specific version in your "
                + "build.gradle file. This way, you can install the same version each time without breaking changes "
                + "unless you are intentionally looking for the latest version.";
    }

    private static final class AuthSchemeHandler implements AuthScheme.Visitor<String> {

        private AuthSchemeHandler() {}

        @Override
        public String visitBearer(BearerAuthScheme bearer) {
            return String.format(
                    "    .%s(\"<YOUR_%s>\")\n",
                    bearer.getToken().getCamelCase().getSafeName(),
                    bearer.getToken().getScreamingSnakeCase().getUnsafeName());
        }

        @Override
        public String visitBasic(BasicAuthScheme basic) {
            return String.format(
                    "    .credentials(\"<YOUR_%s>\", \"<YOUR_%s>\")\n",
                    basic.getUsername().getScreamingSnakeCase().getUnsafeName(),
                    basic.getPassword().getScreamingSnakeCase().getUnsafeName());
        }

        @Override
        public String visitHeader(HeaderAuthScheme header) {
            return String.format(
                    "    .%s(\"<YOUR_%s>\")\n",
                    header.getName().getName().getCamelCase().getSafeName(),
                    header.getName().getName().getScreamingSnakeCase().getUnsafeName());
        }

        @Override
        public String _visitUnknown(Object unknownType) {
            throw new RuntimeException("Encountered unknown auth scheme");
        }
    }
}
