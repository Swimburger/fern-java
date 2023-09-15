/**
 * This file was auto-generated by Fern from our API Definition.
 */
package com.seed.trace.resources.v2.v3.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = NonVoidFunctionDefinition.Builder.class)
public final class NonVoidFunctionDefinition {
    private final NonVoidFunctionSignature signature;

    private final FunctionImplementationForMultipleLanguages code;

    private NonVoidFunctionDefinition(
            NonVoidFunctionSignature signature, FunctionImplementationForMultipleLanguages code) {
        this.signature = signature;
        this.code = code;
    }

    @JsonProperty("signature")
    public NonVoidFunctionSignature getSignature() {
        return signature;
    }

    @JsonProperty("code")
    public FunctionImplementationForMultipleLanguages getCode() {
        return code;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof NonVoidFunctionDefinition && equalTo((NonVoidFunctionDefinition) other);
    }

    private boolean equalTo(NonVoidFunctionDefinition other) {
        return signature.equals(other.signature) && code.equals(other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.signature, this.code);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SignatureStage builder() {
        return new Builder();
    }

    public interface SignatureStage {
        CodeStage signature(NonVoidFunctionSignature signature);

        Builder from(NonVoidFunctionDefinition other);
    }

    public interface CodeStage {
        _FinalStage code(FunctionImplementationForMultipleLanguages code);
    }

    public interface _FinalStage {
        NonVoidFunctionDefinition build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SignatureStage, CodeStage, _FinalStage {
        private NonVoidFunctionSignature signature;

        private FunctionImplementationForMultipleLanguages code;

        private Builder() {}

        @Override
        public Builder from(NonVoidFunctionDefinition other) {
            signature(other.getSignature());
            code(other.getCode());
            return this;
        }

        @Override
        @JsonSetter("signature")
        public CodeStage signature(NonVoidFunctionSignature signature) {
            this.signature = signature;
            return this;
        }

        @Override
        @JsonSetter("code")
        public _FinalStage code(FunctionImplementationForMultipleLanguages code) {
            this.code = code;
            return this;
        }

        @Override
        public NonVoidFunctionDefinition build() {
            return new NonVoidFunctionDefinition(signature, code);
        }
    }
}
