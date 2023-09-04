package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = GetFunctionSignatureRequest.Builder.class)
public final class GetFunctionSignatureRequest {
    private final FunctionSignature functionSignature;

    private GetFunctionSignatureRequest(FunctionSignature functionSignature) {
        this.functionSignature = functionSignature;
    }

    @JsonProperty("functionSignature")
    public FunctionSignature getFunctionSignature() {
        return functionSignature;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GetFunctionSignatureRequest && equalTo((GetFunctionSignatureRequest) other);
    }

    private boolean equalTo(GetFunctionSignatureRequest other) {
        return functionSignature.equals(other.functionSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.functionSignature);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static FunctionSignatureStage builder() {
        return new Builder();
    }

    public interface FunctionSignatureStage {
        _FinalStage functionSignature(FunctionSignature functionSignature);

        Builder from(GetFunctionSignatureRequest other);
    }

    public interface _FinalStage {
        GetFunctionSignatureRequest build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements FunctionSignatureStage, _FinalStage {
        private FunctionSignature functionSignature;

        private Builder() {}

        @Override
        public Builder from(GetFunctionSignatureRequest other) {
            functionSignature(other.getFunctionSignature());
            return this;
        }

        @Override
        @JsonSetter("functionSignature")
        public _FinalStage functionSignature(FunctionSignature functionSignature) {
            this.functionSignature = functionSignature;
            return this;
        }

        @Override
        public GetFunctionSignatureRequest build() {
            return new GetFunctionSignatureRequest(functionSignature);
        }
    }
}
