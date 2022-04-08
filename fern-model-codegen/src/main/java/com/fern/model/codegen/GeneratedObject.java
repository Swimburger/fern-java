package com.fern.model.codegen;

import com.fern.ObjectTypeDefinition;
import com.fern.immutables.StagedBuilderStyle;
import com.squareup.javapoet.JavaFile;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderStyle
public interface GeneratedObject {

    JavaFile file();

    ObjectTypeDefinition objectTypeDefinition();

    @Value.Derived
    default String className() {
        return file().typeSpec.name;
    }

    @Value.Derived
    default String packageName() {
        return file().packageName;
    }

    static ImmutableGeneratedObject.FileBuildStage builder() {
        return ImmutableGeneratedObject.builder();
    }
}
