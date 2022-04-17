package com.fern.model.codegen.enums;

import com.fern.EnumTypeDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.fern.codegen.GeneratedFile;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedEnum extends GeneratedFile<EnumTypeDefinition> {

    static ImmutableGeneratedEnum.FileBuildStage builder() {
        return ImmutableGeneratedEnum.builder();
    }
}
