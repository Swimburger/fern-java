package com.fern.model.codegen.alias;

import com.fern.AliasTypeDefinition;
import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedAliasWithDefinition extends GeneratedFileWithDefinition<AliasTypeDefinition> {

    static ImmutableGeneratedAliasWithDefinition.FileBuildStage builder() {
        return ImmutableGeneratedAliasWithDefinition.builder();
    }
}
