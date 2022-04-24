package com.fern.model.codegen.interfaces;

import com.fern.codegen.GeneratedFile;
import com.fern.immutables.StagedBuilderStyle;
import com.types.ObjectTypeDefinition;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedInterface extends GeneratedFile {

    ObjectTypeDefinition objectTypeDefinition();

    static ImmutableGeneratedInterface.FileBuildStage builder() {
        return ImmutableGeneratedInterface.builder();
    }
}
