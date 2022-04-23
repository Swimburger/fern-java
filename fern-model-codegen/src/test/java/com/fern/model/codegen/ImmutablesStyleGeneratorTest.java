package com.fern.model.codegen;

import com.fern.codegen.utils.ClassNameUtils;
import com.squareup.javapoet.JavaFile;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ImmutablesStyleGeneratorTest {

    @Test
    public void test_generatedStagedBuilder() {
        ClassNameUtils classNameUtils = new ClassNameUtils(Optional.of("com.fern"));
        JavaFile stagedBuilderJavaFile = ImmutablesStyleGenerator.generateStagedBuilderImmutablesStyle(classNameUtils);
        System.out.println(stagedBuilderJavaFile);
    }
}
