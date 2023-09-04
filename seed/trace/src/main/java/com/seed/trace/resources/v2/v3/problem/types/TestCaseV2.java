package com.seed.trace.resources.v2.v3.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.VariableValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseV2.Builder.class)
public final class TestCaseV2 {
    private final TestCaseMetadata metadata;

    private final TestCaseImplementationReference implementation;

    private final Map<String, VariableValue> arguments;

    private final Optional<TestCaseExpects> expects;

    private TestCaseV2(
            TestCaseMetadata metadata,
            TestCaseImplementationReference implementation,
            Map<String, VariableValue> arguments,
            Optional<TestCaseExpects> expects) {
        this.metadata = metadata;
        this.implementation = implementation;
        this.arguments = arguments;
        this.expects = expects;
    }

    @JsonProperty("metadata")
    public TestCaseMetadata getMetadata() {
        return metadata;
    }

    @JsonProperty("implementation")
    public TestCaseImplementationReference getImplementation() {
        return implementation;
    }

    @JsonProperty("arguments")
    public Map<String, VariableValue> getArguments() {
        return arguments;
    }

    @JsonProperty("expects")
    public Optional<TestCaseExpects> getExpects() {
        return expects;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseV2 && equalTo((TestCaseV2) other);
    }

    private boolean equalTo(TestCaseV2 other) {
        return metadata.equals(other.metadata)
                && implementation.equals(other.implementation)
                && arguments.equals(other.arguments)
                && expects.equals(other.expects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.metadata, this.implementation, this.arguments, this.expects);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static MetadataStage builder() {
        return new Builder();
    }

    public interface MetadataStage {
        ImplementationStage metadata(TestCaseMetadata metadata);

        Builder from(TestCaseV2 other);
    }

    public interface ImplementationStage {
        _FinalStage implementation(TestCaseImplementationReference implementation);
    }

    public interface _FinalStage {
        TestCaseV2 build();

        _FinalStage arguments(Map<String, VariableValue> arguments);

        _FinalStage putAllArguments(Map<String, VariableValue> arguments);

        _FinalStage arguments(String key, VariableValue value);

        _FinalStage expects(Optional<TestCaseExpects> expects);

        _FinalStage expects(TestCaseExpects expects);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements MetadataStage, ImplementationStage, _FinalStage {
        private TestCaseMetadata metadata;

        private TestCaseImplementationReference implementation;

        private Optional<TestCaseExpects> expects = Optional.empty();

        private Map<String, VariableValue> arguments = new LinkedHashMap<>();

        private Builder() {}

        @Override
        public Builder from(TestCaseV2 other) {
            metadata(other.getMetadata());
            implementation(other.getImplementation());
            arguments(other.getArguments());
            expects(other.getExpects());
            return this;
        }

        @Override
        @JsonSetter("metadata")
        public ImplementationStage metadata(TestCaseMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        @Override
        @JsonSetter("implementation")
        public _FinalStage implementation(TestCaseImplementationReference implementation) {
            this.implementation = implementation;
            return this;
        }

        @Override
        public _FinalStage expects(TestCaseExpects expects) {
            this.expects = Optional.of(expects);
            return this;
        }

        @Override
        @JsonSetter(value = "expects", nulls = Nulls.SKIP)
        public _FinalStage expects(Optional<TestCaseExpects> expects) {
            this.expects = expects;
            return this;
        }

        @Override
        public _FinalStage arguments(String key, VariableValue value) {
            this.arguments.put(key, value);
            return this;
        }

        @Override
        public _FinalStage putAllArguments(Map<String, VariableValue> arguments) {
            this.arguments.putAll(arguments);
            return this;
        }

        @Override
        @JsonSetter(value = "arguments", nulls = Nulls.SKIP)
        public _FinalStage arguments(Map<String, VariableValue> arguments) {
            this.arguments.clear();
            this.arguments.putAll(arguments);
            return this;
        }

        @Override
        public TestCaseV2 build() {
            return new TestCaseV2(metadata, implementation, arguments, expects);
        }
    }
}
