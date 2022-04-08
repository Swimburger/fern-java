package com.fern.model.codegen;


import com.fern.TypeDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.squareup.javapoet.JavaFile;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedInterface {

    JavaFile file();

    TypeDefinition typeDefinition();

    @Value.Derived
    default String className() {
        return file().typeSpec.name;
    }

    @Value.Derived
    default String packageName() {
        return file().packageName;
    }

    static ImmutableGeneratedInterface.FileBuildStage builder() {
        return ImmutableGeneratedInterface.builder();
    }
}
