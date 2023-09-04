package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = VoidFunctionDefinition.Builder.class)
public final class VoidFunctionDefinition {
    private final List<Parameter> parameters;

    private final FunctionImplementationForMultipleLanguages code;

    private VoidFunctionDefinition(List<Parameter> parameters, FunctionImplementationForMultipleLanguages code) {
        this.parameters = parameters;
        this.code = code;
    }

    @JsonProperty("parameters")
    public List<Parameter> getParameters() {
        return parameters;
    }

    @JsonProperty("code")
    public FunctionImplementationForMultipleLanguages getCode() {
        return code;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof VoidFunctionDefinition && equalTo((VoidFunctionDefinition) other);
    }

    private boolean equalTo(VoidFunctionDefinition other) {
        return parameters.equals(other.parameters) && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parameters, this.code);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static CodeStage builder() {
        return new Builder();
    }

    public interface CodeStage {
        _FinalStage code(FunctionImplementationForMultipleLanguages code);

        Builder from(VoidFunctionDefinition other);
    }

    public interface _FinalStage {
        VoidFunctionDefinition build();

        _FinalStage parameters(List<Parameter> parameters);

        _FinalStage addParameters(Parameter parameters);

        _FinalStage addAllParameters(List<Parameter> parameters);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements CodeStage, _FinalStage {
        private FunctionImplementationForMultipleLanguages code;

        private List<Parameter> parameters = new ArrayList<>();

        private Builder() {}

        @Override
        public Builder from(VoidFunctionDefinition other) {
            parameters(other.getParameters());
            code(other.getCode());
            return this;
        }

        @Override
        @JsonSetter("code")
        public _FinalStage code(FunctionImplementationForMultipleLanguages code) {
            this.code = code;
            return this;
        }

        @Override
        public _FinalStage addAllParameters(List<Parameter> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }

        @Override
        public _FinalStage addParameters(Parameter parameters) {
            this.parameters.add(parameters);
            return this;
        }

        @Override
        @JsonSetter(value = "parameters", nulls = Nulls.SKIP)
        public _FinalStage parameters(List<Parameter> parameters) {
            this.parameters.clear();
            this.parameters.addAll(parameters);
            return this;
        }

        @Override
        public VoidFunctionDefinition build() {
            return new VoidFunctionDefinition(parameters, code);
        }
    }
}
