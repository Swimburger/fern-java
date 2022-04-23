package com.fern.model.codegen.object;

import com.fern.ObjectTypeDefinition;
import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedObjectWithDefinition extends GeneratedFileWithDefinition<ObjectTypeDefinition> {

    static ImmutableGeneratedObjectWithDefinition.FileBuildStage builder() {
        return ImmutableGeneratedObjectWithDefinition.builder();
    }
}
