package com.fern.java.client.cli;

import com.fern.codegen.IGeneratedFile;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(visibility = ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public abstract class CodeGenerationResult {

    public abstract List<IGeneratedFile> modelFiles();

    public abstract List<IGeneratedFile> clientFiles();

    public abstract List<IGeneratedFile> serverFiles();

    public static String getModelBuildGradle(FernPluginConfig pluginConfig) {
        return "plugins {\n"
                + "    id 'java-library'\n"
                + "    id \"org.inferred.processors\" version \"3.6.0\"\n"
                + "}\n"
                + "\n"
                + "repositories {\n"
                + "    mavenCentral()\n"
                + "}\n"
                + "\n"
                + "dependencies {\n"
                + "    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.3'\n"
                + "    implementation 'com.google.code.findbugs:annotations:3.0.1'\n"
                + "\n"
                + "    annotationProcessor 'org.immutables:value:2.8.8'\n"
                + "    compileOnly 'org.immutables:value-annotations:2.8.8'\n"
                + "}\n";
    }

    public static String getClientBuildGradle(FernPluginConfig pluginConfig) {
        return "plugins {\n"
                + "    id 'java-library'\n"
                + "    id \"org.inferred.processors\" version \"3.6.0\"\n"
                + "}\n"
                + "\n"
                + "repositories {\n"
                + "    mavenCentral()\n"
                + "}\n"
                + "\n"
                + "dependencies {\n"
                + "    api project('" + getModelSubprojectDependency(pluginConfig) + "')\n"
                + "    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.3'\n"
                + "    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.3'\n"
                + "    implementation 'io.github.openfeign:feign-jackson:11.8'\n"
                + "    implementation 'io.github.openfeign:feign-core:11.8'\n"
                + "    implementation 'io.github.openfeign:feign-jaxrs:11.8'\n"
                + "\n"
                + "    annotationProcessor 'org.immutables:value:2.8.8'\n"
                + "    compileOnly 'org.immutables:value-annotations:2.8.8'\n"
                + "}\n";
    }

    public static String getServerBuildGradle(FernPluginConfig pluginConfig) {

        return "plugins {\n"
                + "    id 'java-library'\n"
                + "    id \"org.inferred.processors\" version \"3.6.0\"\n"
                + "}\n"
                + "\n"
                + "repositories {\n"
                + "    mavenCentral()\n"
                + "}\n"
                + "\n"
                + "dependencies {\n"
                + "    api project('" + getModelSubprojectDependency(pluginConfig) + "')\n"
                + "    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.3'\n"
                + "    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.12.3'\n"
                + "    implementation 'io.github.openfeign:feign-jackson:11.8'\n"
                + "    implementation 'io.github.openfeign:feign-core:11.8'\n"
                + "    implementation 'io.github.openfeign:feign-jaxrs:11.8'\n"
                + "\n"
                + "    annotationProcessor 'org.immutables:value:2.8.8'\n"
                + "    compileOnly 'org.immutables:value-annotations:2.8.8'\n"
                + "}\n";
    }

    static String getModelSubprojectDependency(FernPluginConfig fernPluginConfig) {
        String gradleDependency = Arrays.asList(fernPluginConfig.relativeWorkspacePathOnHost().split("/"))
                .stream()
                .collect(Collectors.joining(":"));
        return ":" + gradleDependency + ":model";
    }

    static ImmutableCodeGenerationResult.Builder builder() {
        return ImmutableCodeGenerationResult.builder();
    }
}
