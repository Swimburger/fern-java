package com.fern.java.spring;

import com.fern.java.immutables.StagedBuilderImmutablesStyle;
import com.fern.java.output.AbstractGeneratedJavaFile;
import com.fern.java.output.GeneratedJavaFile;
import com.squareup.javapoet.ClassName;
import org.immutables.value.Value;

@Value.Immutable
@StagedBuilderImmutablesStyle
public abstract class GeneratedSpringException extends AbstractGeneratedJavaFile {

    public abstract GeneratedJavaFile controllerAdvice();

    public static ImmutableGeneratedSpringException.ClassNameBuildStage builder() {
        return ImmutableGeneratedSpringException.builder();
    }
}
