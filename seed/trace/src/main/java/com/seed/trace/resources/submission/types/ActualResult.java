package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.seed.trace.resources.commons.types.VariableValue;
import java.util.Objects;
import java.util.Optional;

public final class ActualResult {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private ActualResult(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static ActualResult value(VariableValue value) {
        return new ActualResult(new ValueValue(value));
    }

    public static ActualResult exception(ExceptionInfo value) {
        return new ActualResult(new ExceptionValue(value));
    }

    public static ActualResult exceptionV2(ExceptionV2 value) {
        return new ActualResult(new ExceptionV2Value(value));
    }

    public boolean isValue() {
        return value instanceof ValueValue;
    }

    public boolean isException() {
        return value instanceof ExceptionValue;
    }

    public boolean isExceptionV2() {
        return value instanceof ExceptionV2Value;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<VariableValue> getValue() {
        if (isValue()) {
            return Optional.of(((ValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<ExceptionInfo> getException() {
        if (isException()) {
            return Optional.of(((ExceptionValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<ExceptionV2> getExceptionV2() {
        if (isExceptionV2()) {
            return Optional.of(((ExceptionV2Value) value).value);
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
        T visitValue(VariableValue value);

        T visitException(ExceptionInfo exception);

        T visitExceptionV2(ExceptionV2 exceptionV2);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(ValueValue.class),
        @JsonSubTypes.Type(ExceptionValue.class),
        @JsonSubTypes.Type(ExceptionV2Value.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("value")
    private static final class ValueValue implements Value {
        @JsonProperty("value")
        private VariableValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ValueValue(@JsonProperty("value") VariableValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ValueValue && equalTo((ValueValue) other);
        }

        private boolean equalTo(ValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ActualResult{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("exception")
    private static final class ExceptionValue implements Value {
        @JsonUnwrapped
        private ExceptionInfo value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ExceptionValue() {}

        private ExceptionValue(ExceptionInfo value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitException(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ExceptionValue && equalTo((ExceptionValue) other);
        }

        private boolean equalTo(ExceptionValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ActualResult{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("exceptionV2")
    private static final class ExceptionV2Value implements Value {
        @JsonProperty("value")
        private ExceptionV2 value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ExceptionV2Value(@JsonProperty("value") ExceptionV2 value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitExceptionV2(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ExceptionV2Value && equalTo((ExceptionV2Value) other);
        }

        private boolean equalTo(ExceptionV2Value other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ActualResult{" + "value: " + value + "}";
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
            return "ActualResult{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
