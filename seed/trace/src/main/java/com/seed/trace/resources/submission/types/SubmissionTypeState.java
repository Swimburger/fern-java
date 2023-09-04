package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.Optional;

public final class SubmissionTypeState {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private SubmissionTypeState(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static SubmissionTypeState test(TestSubmissionState value) {
        return new SubmissionTypeState(new TestValue(value));
    }

    public static SubmissionTypeState workspace(WorkspaceSubmissionState value) {
        return new SubmissionTypeState(new WorkspaceValue(value));
    }

    public boolean isTest() {
        return value instanceof TestValue;
    }

    public boolean isWorkspace() {
        return value instanceof WorkspaceValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<TestSubmissionState> getTest() {
        if (isTest()) {
            return Optional.of(((TestValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<WorkspaceSubmissionState> getWorkspace() {
        if (isWorkspace()) {
            return Optional.of(((WorkspaceValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<Object> _getUnknown() {
        if (_isUnknown()) {
            return Optional.of(((_UnknownValue) value).value);
        }
        return Optional.empty();
    }

    @JsonValue
    private Value getValue() {
        return this.value;
    }

    public interface Visitor<T> {
        T visitTest(TestSubmissionState test);

        T visitWorkspace(WorkspaceSubmissionState workspace);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({@JsonSubTypes.Type(TestValue.class), @JsonSubTypes.Type(WorkspaceValue.class)})
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("test")
    private static final class TestValue implements Value {
        @JsonUnwrapped
        private TestSubmissionState value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private TestValue() {}

        private TestValue(TestSubmissionState value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitTest(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof TestValue && equalTo((TestValue) other);
        }

        private boolean equalTo(TestValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionTypeState{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("workspace")
    private static final class WorkspaceValue implements Value {
        @JsonUnwrapped
        private WorkspaceSubmissionState value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private WorkspaceValue() {}

        private WorkspaceValue(WorkspaceSubmissionState value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitWorkspace(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof WorkspaceValue && equalTo((WorkspaceValue) other);
        }

        private boolean equalTo(WorkspaceValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionTypeState{" + "value: " + value + "}";
        }
    }

    private static final class _UnknownValue implements Value {
        private String type;

        @JsonValue
        private Object value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private _UnknownValue(@JsonProperty("value") Object value) {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor._visitUnknown(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof _UnknownValue && equalTo((_UnknownValue) other);
        }

        private boolean equalTo(_UnknownValue other) {
            return type.equals(other.type) && value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.value);
        }

        @Override
        public String toString() {
            return "SubmissionTypeState{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
