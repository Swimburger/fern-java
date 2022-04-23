package com.fern.model.codegen.interfaces;

import com.fern.ObjectTypeDefinition;
import com.fern.codegen.GeneratedFileWithDefinition;
import com.fern.immutables.StagedBuilderStyle;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedInterfaceWithDefinition extends GeneratedFileWithDefinition<ObjectTypeDefinition> {

    static ImmutableGeneratedInterfaceWithDefinition.FileBuildStage builder() {
        return ImmutableGeneratedInterfaceWithDefinition.builder();
    }
}
