package com.fern.services.jersey.codegen;

import com.fern.codegen.GeneratedFile;
import com.fern.immutables.StagedBuilderStyle;
import com.squareup.javapoet.TypeName;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface WireMessageGeneratorResult {

    TypeName typeName();

    Optional<GeneratedFile> generatedWireMessage();

    static ImmutableWireMessageGeneratorResult.TypeNameBuildStage builder() {
        return ImmutableWireMessageGeneratorResult.builder();
    }
}
