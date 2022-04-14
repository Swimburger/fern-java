package com.fern.model.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fern.IntermediateRepresentation;
import com.fern.model.codegen.config.PluginConfig;
import java.io.File;
import java.io.IOException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public final class ModelGeneratorCli {

    private static final String PROGRAM_NAME = "Fern Java Model Generator Plugin";
    private static final String IR_ARG_NAME = "ir";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new GuavaModule())
            .registerModule(new Jdk8Module().configureAbsentsAsNulls(true));

    private static final PluginConfig HARDCODED_PLUGIN_CONFIG = PluginConfig.builder()
            .outputDirectoryName("model")
            .packagePrefix("com")
            .build();

    private ModelGeneratorCli() {}

    public static void main(String... args) {
        ArgumentParser parser = ArgumentParsers.newFor(PROGRAM_NAME)
                .build()
                .defaultHelp(true)
                .description("Generates Java objects based on types in your Fern API spec.");
        parser.addArgument(IR_ARG_NAME).nargs("1").help("Filepath to Fern IR JSON (intermediate json).");
        try {
            Namespace namespace = parser.parseArgs(args);
            String irLocation = namespace.getString(IR_ARG_NAME);
            IntermediateRepresentation intermediateRepresentation =
                    OBJECT_MAPPER.readValue(new File(irLocation), IntermediateRepresentation.class);
            ModelGenerator modelGenerator =
                    new ModelGenerator(intermediateRepresentation.types(), HARDCODED_PLUGIN_CONFIG);
            modelGenerator.buildModelSubproject();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Fern IR json", e);
        }
    }
}
