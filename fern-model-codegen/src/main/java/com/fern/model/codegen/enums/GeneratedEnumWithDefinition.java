package com.fern.model.codegen.enums;

import com.fern.EnumTypeDefinition;
import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedEnumWithDefinition extends GeneratedFileWithDefinition<EnumTypeDefinition> {

    static ImmutableGeneratedEnumWithDefinition.FileBuildStage builder() {
        return ImmutableGeneratedEnumWithDefinition.builder();
    }
}
