package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseImplementation.Builder.class)
public final class TestCaseImplementation {
    private final TestCaseImplementationDescription description;

    private final TestCaseFunction function;

    private TestCaseImplementation(TestCaseImplementationDescription description, TestCaseFunction function) {
        this.description = description;
        this.function = function;
    }

    @JsonProperty("description")
    public TestCaseImplementationDescription getDescription() {
        return description;
    }

    @JsonProperty("function")
    public TestCaseFunction getFunction() {
        return function;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseImplementation && equalTo((TestCaseImplementation) other);
    }

    private boolean equalTo(TestCaseImplementation other) {
        return description.equals(other.description) && function.equals(other.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.description, this.function);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static DescriptionStage builder() {
        return new Builder();
    }

    public interface DescriptionStage {
        FunctionStage description(TestCaseImplementationDescription description);

        Builder from(TestCaseImplementation other);
    }

    public interface FunctionStage {
        _FinalStage function(TestCaseFunction function);
    }

    public interface _FinalStage {
        TestCaseImplementation build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements DescriptionStage, FunctionStage, _FinalStage {
        private TestCaseImplementationDescription description;

        private TestCaseFunction function;

        private Builder() {}

        @Override
        public Builder from(TestCaseImplementation other) {
            description(other.getDescription());
            function(other.getFunction());
            return this;
        }

        @Override
        @JsonSetter("description")
        public FunctionStage description(TestCaseImplementationDescription description) {
            this.description = description;
            return this;
        }

        @Override
        @JsonSetter("function")
        public _FinalStage function(TestCaseFunction function) {
            this.function = function;
            return this;
        }

        @Override
        public TestCaseImplementation build() {
            return new TestCaseImplementation(description, function);
        }
    }
}
