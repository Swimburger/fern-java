package com.seed.trace.resources.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = GenericCreateProblemError.Builder.class)
public final class GenericCreateProblemError {
    private final String message;

    private final String type;

    private final String stacktrace;

    private GenericCreateProblemError(String message, String type, String stacktrace) {
        this.message = message;
        this.type = type;
        this.stacktrace = stacktrace;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("stacktrace")
    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GenericCreateProblemError && equalTo((GenericCreateProblemError) other);
    }

    private boolean equalTo(GenericCreateProblemError other) {
        return message.equals(other.message) && type.equals(other.type) && stacktrace.equals(other.stacktrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.message, this.type, this.stacktrace);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static MessageStage builder() {
        return new Builder();
    }

    public interface MessageStage {
        TypeStage message(String message);

        Builder from(GenericCreateProblemError other);
    }

    public interface TypeStage {
        StacktraceStage type(String type);
    }

    public interface StacktraceStage {
        _FinalStage stacktrace(String stacktrace);
    }

    public interface _FinalStage {
        GenericCreateProblemError build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements MessageStage, TypeStage, StacktraceStage, _FinalStage {
        private String message;

        private String type;

        private String stacktrace;

        private Builder() {}

        @Override
        public Builder from(GenericCreateProblemError other) {
            message(other.getMessage());
            type(other.getType());
            stacktrace(other.getStacktrace());
            return this;
        }

        @Override
        @JsonSetter("message")
        public TypeStage message(String message) {
            this.message = message;
            return this;
        }

        @Override
        @JsonSetter("type")
        public StacktraceStage type(String type) {
            this.type = type;
            return this;
        }

        @Override
        @JsonSetter("stacktrace")
        public _FinalStage stacktrace(String stacktrace) {
            this.stacktrace = stacktrace;
            return this;
        }

        @Override
        public GenericCreateProblemError build() {
            return new GenericCreateProblemError(message, type, stacktrace);
        }
    }
}
