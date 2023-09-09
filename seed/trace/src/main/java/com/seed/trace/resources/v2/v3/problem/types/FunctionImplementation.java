package com.seed.trace.resources.v2.v3.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = FunctionImplementation.Builder.class)
public final class FunctionImplementation {
    private final String impl;

    private final Optional<String> imports;

    private FunctionImplementation(String impl, Optional<String> imports) {
        this.impl = impl;
        this.imports = imports;
    }

    @JsonProperty("impl")
    public String getImpl() {
        return impl;
    }

    @JsonProperty("imports")
    public Optional<String> getImports() {
        return imports;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof FunctionImplementation && equalTo((FunctionImplementation) other);
    }

    private boolean equalTo(FunctionImplementation other) {
        return impl.equals(other.impl) && imports.equals(other.imports);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.impl, this.imports);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ImplStage builder() {
        return new Builder();
    }

    public interface ImplStage {
        _FinalStage impl(String impl);

        Builder from(FunctionImplementation other);
    }

    public interface _FinalStage {
        FunctionImplementation build();

        _FinalStage imports(Optional<String> imports);

        _FinalStage imports(String imports);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements ImplStage, _FinalStage {
        private String impl;

        private Optional<String> imports = Optional.empty();

        private Builder() {}

        @Override
        public Builder from(FunctionImplementation other) {
            impl(other.getImpl());
            imports(other.getImports());
            return this;
        }

        @Override
        @JsonSetter("impl")
        public _FinalStage impl(String impl) {
            this.impl = impl;
            return this;
        }

        @Override
        public _FinalStage imports(String imports) {
            this.imports = Optional.of(imports);
            return this;
        }

        @Override
        @JsonSetter(value = "imports", nulls = Nulls.SKIP)
        public _FinalStage imports(Optional<String> imports) {
            this.imports = imports;
            return this;
        }

        @Override
        public FunctionImplementation build() {
            return new FunctionImplementation(impl, imports);
        }
    }
}