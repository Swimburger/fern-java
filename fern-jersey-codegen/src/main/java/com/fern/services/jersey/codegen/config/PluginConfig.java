package com.fern.services.jersey.codegen.config;

import com.fern.immutables.StagedBuilderStyle;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface PluginConfig {

    Optional<String> packagePrefix();

    @Value.Default
    default String modelSubprojectDirectoryName() {
        return "jersey-service";
    }

    static ImmutablePluginConfig.Builder builder() {
        return ImmutablePluginConfig.builder();
    }
}

