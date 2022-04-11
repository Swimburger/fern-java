package com.fern.model.codegen;

import com.fern.EnumTypeDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.squareup.javapoet.JavaFile;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedEnum {

    JavaFile file();

    EnumTypeDefinition enumTypeDefinition();

    @Value.Derived
    default String className() {
        return file().typeSpec.name;
    }

    @Value.Derived
    default String packageName() {
        return file().packageName;
    }

    static ImmutableGeneratedEnum.FileBuildStage builder() {
        return ImmutableGeneratedEnum.builder();
    }
}
