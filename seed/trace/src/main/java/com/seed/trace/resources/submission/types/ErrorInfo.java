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

public final class ErrorInfo {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private ErrorInfo(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static ErrorInfo compileError(CompileError value) {
        return new ErrorInfo(new CompileErrorValue(value));
    }

    public static ErrorInfo runtimeError(RuntimeError value) {
        return new ErrorInfo(new RuntimeErrorValue(value));
    }

    public static ErrorInfo internalError(InternalError value) {
        return new ErrorInfo(new InternalErrorValue(value));
    }

    public boolean isCompileError() {
        return value instanceof CompileErrorValue;
    }

    public boolean isRuntimeError() {
        return value instanceof RuntimeErrorValue;
    }

    public boolean isInternalError() {
        return value instanceof InternalErrorValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<CompileError> getCompileError() {
        if (isCompileError()) {
            return Optional.of(((CompileErrorValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<RuntimeError> getRuntimeError() {
        if (isRuntimeError()) {
            return Optional.of(((RuntimeErrorValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<InternalError> getInternalError() {
        if (isInternalError()) {
            return Optional.of(((InternalErrorValue) value).value);
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
        T visitCompileError(CompileError compileError);

        T visitRuntimeError(RuntimeError runtimeError);

        T visitInternalError(InternalError internalError);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(CompileErrorValue.class),
        @JsonSubTypes.Type(RuntimeErrorValue.class),
        @JsonSubTypes.Type(InternalErrorValue.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("compileError")
    private static final class CompileErrorValue implements Value {
        @JsonUnwrapped
        private CompileError value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private CompileErrorValue() {}

        private CompileErrorValue(CompileError value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitCompileError(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof CompileErrorValue && equalTo((CompileErrorValue) other);
        }

        private boolean equalTo(CompileErrorValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ErrorInfo{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("runtimeError")
    private static final class RuntimeErrorValue implements Value {
        @JsonUnwrapped
        private RuntimeError value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private RuntimeErrorValue() {}

        private RuntimeErrorValue(RuntimeError value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitRuntimeError(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof RuntimeErrorValue && equalTo((RuntimeErrorValue) other);
        }

        private boolean equalTo(RuntimeErrorValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ErrorInfo{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("internalError")
    private static final class InternalErrorValue implements Value {
        @JsonUnwrapped
        private InternalError value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private InternalErrorValue() {}

        private InternalErrorValue(InternalError value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitInternalError(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof InternalErrorValue && equalTo((InternalErrorValue) other);
        }

        private boolean equalTo(InternalErrorValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "ErrorInfo{" + "value: " + value + "}";
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
            return "ErrorInfo{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
