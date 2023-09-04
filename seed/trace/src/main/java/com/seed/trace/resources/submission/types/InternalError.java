package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = InternalError.Builder.class)
public final class InternalError {
    private final ExceptionInfo exceptionInfo;

    private InternalError(ExceptionInfo exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    @JsonProperty("exceptionInfo")
    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof InternalError && equalTo((InternalError) other);
    }

    private boolean equalTo(InternalError other) {
        return exceptionInfo.equals(other.exceptionInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.exceptionInfo);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ExceptionInfoStage builder() {
        return new Builder();
    }

    public interface ExceptionInfoStage {
        _FinalStage exceptionInfo(ExceptionInfo exceptionInfo);

        Builder from(InternalError other);
    }

    public interface _FinalStage {
        InternalError build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements ExceptionInfoStage, _FinalStage {
        private ExceptionInfo exceptionInfo;

        private Builder() {}

        @Override
        public Builder from(InternalError other) {
            exceptionInfo(other.getExceptionInfo());
            return this;
        }

        @Override
        @JsonSetter("exceptionInfo")
        public _FinalStage exceptionInfo(ExceptionInfo exceptionInfo) {
            this.exceptionInfo = exceptionInfo;
            return this;
        }

        @Override
        public InternalError build() {
            return new InternalError(exceptionInfo);
        }
    }
}
