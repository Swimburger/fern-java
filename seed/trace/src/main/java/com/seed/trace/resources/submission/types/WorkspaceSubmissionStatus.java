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

public final class WorkspaceSubmissionStatus {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private WorkspaceSubmissionStatus(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static WorkspaceSubmissionStatus stopped() {
        return new WorkspaceSubmissionStatus(new StoppedValue());
    }

    public static WorkspaceSubmissionStatus errored(ErrorInfo value) {
        return new WorkspaceSubmissionStatus(new ErroredValue(value));
    }

    public static WorkspaceSubmissionStatus running(RunningSubmissionState value) {
        return new WorkspaceSubmissionStatus(new RunningValue(value));
    }

    public static WorkspaceSubmissionStatus ran(WorkspaceRunDetails value) {
        return new WorkspaceSubmissionStatus(new RanValue(value));
    }

    public static WorkspaceSubmissionStatus traced(WorkspaceRunDetails value) {
        return new WorkspaceSubmissionStatus(new TracedValue(value));
    }

    public boolean isStopped() {
        return value instanceof StoppedValue;
    }

    public boolean isErrored() {
        return value instanceof ErroredValue;
    }

    public boolean isRunning() {
        return value instanceof RunningValue;
    }

    public boolean isRan() {
        return value instanceof RanValue;
    }

    public boolean isTraced() {
        return value instanceof TracedValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<ErrorInfo> getErrored() {
        if (isErrored()) {
            return Optional.of(((ErroredValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<RunningSubmissionState> getRunning() {
        if (isRunning()) {
            return Optional.of(((RunningValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<WorkspaceRunDetails> getRan() {
        if (isRan()) {
            return Optional.of(((RanValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<WorkspaceRunDetails> getTraced() {
        if (isTraced()) {
            return Optional.of(((TracedValue) value).value);
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
        T visitStopped();

        T visitErrored(ErrorInfo errored);

        T visitRunning(RunningSubmissionState running);

        T visitRan(WorkspaceRunDetails ran);

        T visitTraced(WorkspaceRunDetails traced);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(StoppedValue.class),
        @JsonSubTypes.Type(ErroredValue.class),
        @JsonSubTypes.Type(RunningValue.class),
        @JsonSubTypes.Type(RanValue.class),
        @JsonSubTypes.Type(TracedValue.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("stopped")
    private static final class StoppedValue implements Value {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private StoppedValue() {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitStopped();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof StoppedValue;
        }

        @Override
        public String toString() {
            return "WorkspaceSubmissionStatus{" + "}";
        }
    }

    @JsonTypeName("errored")
    private static final class ErroredValue implements Value {
        @JsonProperty("value")
        private ErrorInfo value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ErroredValue(@JsonProperty("value") ErrorInfo value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitErrored(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ErroredValue && equalTo((ErroredValue) other);
        }

        private boolean equalTo(ErroredValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "WorkspaceSubmissionStatus{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("running")
    private static final class RunningValue implements Value {
        @JsonProperty("value")
        private RunningSubmissionState value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private RunningValue(@JsonProperty("value") RunningSubmissionState value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitRunning(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof RunningValue && equalTo((RunningValue) other);
        }

        private boolean equalTo(RunningValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "WorkspaceSubmissionStatus{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("ran")
    private static final class RanValue implements Value {
        @JsonUnwrapped
        private WorkspaceRunDetails value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private RanValue() {}

        private RanValue(WorkspaceRunDetails value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitRan(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof RanValue && equalTo((RanValue) other);
        }

        private boolean equalTo(RanValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "WorkspaceSubmissionStatus{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("traced")
    private static final class TracedValue implements Value {
        @JsonUnwrapped
        private WorkspaceRunDetails value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private TracedValue() {}

        private TracedValue(WorkspaceRunDetails value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitTraced(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof TracedValue && equalTo((TracedValue) other);
        }

        private boolean equalTo(TracedValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "WorkspaceSubmissionStatus{" + "value: " + value + "}";
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
            return "WorkspaceSubmissionStatus{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
