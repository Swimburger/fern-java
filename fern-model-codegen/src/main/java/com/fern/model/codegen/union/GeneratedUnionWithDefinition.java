package com.fern.model.codegen.union;

import com.fern.UnionTypeDefinition;
import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedUnionWithDefinition extends GeneratedFileWithDefinition<UnionTypeDefinition> {

    static ImmutableGeneratedUnionWithDefinition.FileBuildStage builder() {
        return ImmutableGeneratedUnionWithDefinition.builder();
    }
}
