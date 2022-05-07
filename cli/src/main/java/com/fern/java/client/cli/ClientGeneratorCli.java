package com.fern.java.client.cli;

import static com.fern.codegen.utils.ObjectMappers.CLIENT_OBJECT_MAPPER;

import com.fern.IntermediateRepresentation;
import com.fern.codegen.GeneratedException;
import com.fern.codegen.GeneratedHttpServiceClient;
import com.fern.codegen.GeneratedHttpServiceServer;
import com.fern.codegen.GeneratorContext;
import com.fern.jersey.ExceptionGenerator;
import com.fern.jersey.client.HttpServiceClientGenerator;
import com.fern.jersey.server.HttpServiceServerGenerator;
import com.fern.model.codegen.ModelGenerator;
import com.fern.model.codegen.ModelGeneratorResult;
import com.squareup.javapoet.JavaFile;
import com.types.NamedType;
import com.types.TypeDefinition;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ClientGeneratorCli {

    private static final String SRC_MAIN_JAVA = "src/main/java";
    private static final String BUILD_GRADLE = "build.gradle";

    private ClientGeneratorCli() {}

    public static void main(String... args) {
        String pluginPath = args[0];
        FernPluginConfig fernPluginConfig = getPluginConfig(pluginPath);
        IntermediateRepresentation ir = getIr(fernPluginConfig);
        generate(ir, fernPluginConfig);
    }

    private static FernPluginConfig getPluginConfig(String pluginPath) {
        try {
            return CLIENT_OBJECT_MAPPER.readValue(new File(pluginPath), FernPluginConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read plugin configuration", e);
        }
    }

    private static IntermediateRepresentation getIr(FernPluginConfig fernPluginConfig) {
        try {
            return CLIENT_OBJECT_MAPPER.readValue(
                    new File(fernPluginConfig.irFilepath()), IntermediateRepresentation.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read ir", e);
        }
    }

    private static void generate(IntermediateRepresentation ir, FernPluginConfig fernPluginConfig) {
        ImmutableCodeGenerationResult.Builder resultBuilder = CodeGenerationResult.builder();
        Map<NamedType, TypeDefinition> typeDefinitionsByName =
                ir.types().stream().collect(Collectors.toUnmodifiableMap(TypeDefinition::name, Function.identity()));
        GeneratorContext generatorContext =
                new GeneratorContext(fernPluginConfig.customPluginConfig().packagePrefix(), typeDefinitionsByName);

        ModelGeneratorResult modelGeneratorResult = addModelFiles(ir, generatorContext, resultBuilder);
        switch (fernPluginConfig.customPluginConfig().mode()) {
            case CLIENT:
                addClientFiles(ir, generatorContext, modelGeneratorResult, resultBuilder);
                break;
            case SERVER:
                addServerFiles(ir, generatorContext, modelGeneratorResult, resultBuilder);
                break;
            case CLIENT_AND_SERVER:
                addClientFiles(ir, generatorContext, modelGeneratorResult, resultBuilder);
                addServerFiles(ir, generatorContext, modelGeneratorResult, resultBuilder);
                break;
        }
        CodeGenerationResult codeGenerationResult = resultBuilder.build();
        writeToFiles(codeGenerationResult, fernPluginConfig);
    }

    private static ModelGeneratorResult addModelFiles(
            IntermediateRepresentation ir,
            GeneratorContext generatorContext,
            ImmutableCodeGenerationResult.Builder resultBuilder) {
        ModelGenerator modelGenerator = new ModelGenerator(ir.types(), generatorContext);
        ModelGeneratorResult modelGeneratorResult = modelGenerator.generate();
        resultBuilder.addAllModelFiles(modelGeneratorResult.aliases());
        resultBuilder.addAllModelFiles(modelGeneratorResult.enums());
        resultBuilder.addAllModelFiles(modelGeneratorResult.interfaces().values());
        resultBuilder.addAllModelFiles(modelGeneratorResult.objects());
        resultBuilder.addAllModelFiles(modelGeneratorResult.unions());
        resultBuilder.addModelFiles(generatorContext.getStagedImmutablesFile());
        resultBuilder.addModelFiles(generatorContext.getPackagePrivateImmutablesFile());
        resultBuilder.addModelFiles(generatorContext.getAuthHeaderFile());
        resultBuilder.addModelFiles(generatorContext.getApiExceptionFile());
        resultBuilder.addModelFiles(generatorContext.getHttpApiExceptionFile());
        return modelGeneratorResult;
    }

    private static void addClientFiles(
            IntermediateRepresentation ir,
            GeneratorContext generatorContext,
            ModelGeneratorResult modelGeneratorResult,
            ImmutableCodeGenerationResult.Builder resultBuilder) {
        List<GeneratedException> generatedExceptions = ir.errors().stream()
                .map(errorDefinition -> {
                    ExceptionGenerator exceptionGenerator =
                            new ExceptionGenerator(generatorContext, errorDefinition, false);
                    return exceptionGenerator.generate();
                })
                .collect(Collectors.toList());
        List<GeneratedHttpServiceClient> generatedHttpServiceClients = ir.services().http().stream()
                .map(httpService -> {
                    HttpServiceClientGenerator httpServiceClientGenerator = new HttpServiceClientGenerator(
                            generatorContext, modelGeneratorResult.interfaces(), generatedExceptions, httpService);
                    return httpServiceClientGenerator.generate();
                })
                .collect(Collectors.toList());
        boolean serviceClientPresent = false;
        for (GeneratedHttpServiceClient generatedHttpServiceClient : generatedHttpServiceClients) {
            resultBuilder.addClientFiles(generatedHttpServiceClient);
            generatedHttpServiceClient.generatedErrorDecoder().ifPresent(resultBuilder::addClientFiles);
            resultBuilder.addAllClientFiles(generatedHttpServiceClient.generatedWireMessages());
            serviceClientPresent = true;
        }
        if (serviceClientPresent) {
            resultBuilder.addClientFiles(generatorContext.getClientObjectMappersFile());
            resultBuilder.addClientFiles(generatorContext.getUnknownRemoteExceptionFile());
        }
    }

    private static void addServerFiles(
            IntermediateRepresentation ir,
            GeneratorContext generatorContext,
            ModelGeneratorResult modelGeneratorResult,
            ImmutableCodeGenerationResult.Builder resultBuilder) {
        List<GeneratedException> generatedExceptions = ir.errors().stream()
                .map(errorDefinition -> {
                    ExceptionGenerator exceptionGenerator =
                            new ExceptionGenerator(generatorContext, errorDefinition, true);
                    return exceptionGenerator.generate();
                })
                .collect(Collectors.toList());
        List<GeneratedHttpServiceServer> generatedHttpServiceServers = ir.services().http().stream()
                .map(httpService -> {
                    HttpServiceServerGenerator httpServiceServerGenerator = new HttpServiceServerGenerator(
                            generatorContext, modelGeneratorResult.interfaces(), generatedExceptions, httpService);
                    return httpServiceServerGenerator.generate();
                })
                .collect(Collectors.toList());
        boolean serviceServerPresent = false;
        for (GeneratedHttpServiceServer generatedHttpServiceServer : generatedHttpServiceServers) {
            resultBuilder.addServerFiles(generatedHttpServiceServer);
            resultBuilder.addAllServerFiles(generatedHttpServiceServer.generatedWireMessages());
            serviceServerPresent = true;
        }
        if (serviceServerPresent) {
            resultBuilder.addClientFiles(generatorContext.getServerObjectMappersFile());
        }
    }

    private static synchronized void writeToFiles(
            CodeGenerationResult codeGenerationResult, FernPluginConfig fernPluginConfig) {
        writeFileContents(Paths.get(fernPluginConfig.outputDirectory(), ".gitignore"), "*/\n");

        if (!codeGenerationResult.modelFiles().isEmpty()) {
            codeGenerationResult
                    .modelFiles()
                    .forEach(modelFile -> writeFile(
                            Paths.get(fernPluginConfig.outputDirectory(), "model", SRC_MAIN_JAVA), modelFile.file()));
            writeFileContents(
                    Paths.get(fernPluginConfig.outputDirectory(), "model", BUILD_GRADLE),
                    CodeGenerationResult.getModelBuildGradle(fernPluginConfig));
        }

        if (!codeGenerationResult.clientFiles().isEmpty()) {
            codeGenerationResult
                    .clientFiles()
                    .forEach(clientFile -> writeFile(
                            Paths.get(fernPluginConfig.outputDirectory(), "client", SRC_MAIN_JAVA), clientFile.file()));
            writeFileContents(
                    Paths.get(fernPluginConfig.outputDirectory(), "client", BUILD_GRADLE),
                    CodeGenerationResult.getClientBuildGradle(fernPluginConfig));
        }

        if (!codeGenerationResult.serverFiles().isEmpty()) {
            codeGenerationResult
                    .serverFiles()
                    .forEach(serverFiles -> writeFile(
                            Paths.get(fernPluginConfig.outputDirectory(), "server", SRC_MAIN_JAVA),
                            serverFiles.file()));
            writeFileContents(
                    Paths.get(fernPluginConfig.outputDirectory(), "server", BUILD_GRADLE),
                    CodeGenerationResult.getServerBuildGradle(fernPluginConfig));
        }
    }

    private static void writeFileContents(Path path, String contents) {
        try {
            Files.writeString(path, contents);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write .gitignore ", e);
        }
    }

    private static void writeFile(Path path, JavaFile javaFile) {
        try {
            javaFile.writeToFile(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write generated java file: " + javaFile.typeSpec.name, e);
        }
    }
}
