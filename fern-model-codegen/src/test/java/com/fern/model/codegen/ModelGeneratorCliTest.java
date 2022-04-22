package com.fern.model.codegen;

import org.junit.jupiter.api.Test;

public class ModelGeneratorCliTest {

    @Test
    public void test_basic() {
        ModelGeneratorCli.main(
                "fern-model-codegen/src/test/resources/medplum-ir.json",
                "medplum-model-test");
    }
}
