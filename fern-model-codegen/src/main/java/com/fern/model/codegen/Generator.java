package com.fern.model.codegen;

public abstract class Generator<D> {

    protected final GeneratorContext generatorContext;

    public Generator(GeneratorContext generatorContext) {
        this.generatorContext = generatorContext;
    }

    public abstract GeneratedFile<D> generate();
}
