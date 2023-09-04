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

public final class SubmissionResponse {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private SubmissionResponse(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static SubmissionResponse serverInitialized() {
        return new SubmissionResponse(new ServerInitializedValue());
    }

    public static SubmissionResponse problemInitialized(String value) {
        return new SubmissionResponse(new ProblemInitializedValue(value));
    }

    public static SubmissionResponse workspaceInitialized() {
        return new SubmissionResponse(new WorkspaceInitializedValue());
    }

    public static SubmissionResponse serverErrored(ExceptionInfo value) {
        return new SubmissionResponse(new ServerErroredValue(value));
    }

    public static SubmissionResponse codeExecutionUpdate(CodeExecutionUpdate value) {
        return new SubmissionResponse(new CodeExecutionUpdateValue(value));
    }

    public static SubmissionResponse terminated(TerminatedResponse value) {
        return new SubmissionResponse(new TerminatedValue(value));
    }

    public boolean isServerInitialized() {
        return value instanceof ServerInitializedValue;
    }

    public boolean isProblemInitialized() {
        return value instanceof ProblemInitializedValue;
    }

    public boolean isWorkspaceInitialized() {
        return value instanceof WorkspaceInitializedValue;
    }

    public boolean isServerErrored() {
        return value instanceof ServerErroredValue;
    }

    public boolean isCodeExecutionUpdate() {
        return value instanceof CodeExecutionUpdateValue;
    }

    public boolean isTerminated() {
        return value instanceof TerminatedValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<String> getProblemInitialized() {
        if (isProblemInitialized()) {
            return Optional.of(((ProblemInitializedValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<ExceptionInfo> getServerErrored() {
        if (isServerErrored()) {
            return Optional.of(((ServerErroredValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<CodeExecutionUpdate> getCodeExecutionUpdate() {
        if (isCodeExecutionUpdate()) {
            return Optional.of(((CodeExecutionUpdateValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<TerminatedResponse> getTerminated() {
        if (isTerminated()) {
            return Optional.of(((TerminatedValue) value).value);
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
        T visitServerInitialized();

        T visitProblemInitialized(String problemInitialized);

        T visitWorkspaceInitialized();

        T visitServerErrored(ExceptionInfo serverErrored);

        T visitCodeExecutionUpdate(CodeExecutionUpdate codeExecutionUpdate);

        T visitTerminated(TerminatedResponse terminated);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(ServerInitializedValue.class),
        @JsonSubTypes.Type(ProblemInitializedValue.class),
        @JsonSubTypes.Type(WorkspaceInitializedValue.class),
        @JsonSubTypes.Type(ServerErroredValue.class),
        @JsonSubTypes.Type(CodeExecutionUpdateValue.class),
        @JsonSubTypes.Type(TerminatedValue.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("serverInitialized")
    private static final class ServerInitializedValue implements Value {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ServerInitializedValue() {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitServerInitialized();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ServerInitializedValue;
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "}";
        }
    }

    @JsonTypeName("problemInitialized")
    private static final class ProblemInitializedValue implements Value {
        @JsonProperty("value")
        private String value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ProblemInitializedValue(@JsonProperty("value") String value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitProblemInitialized(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ProblemInitializedValue && equalTo((ProblemInitializedValue) other);
        }

        private boolean equalTo(ProblemInitializedValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("workspaceInitialized")
    private static final class WorkspaceInitializedValue implements Value {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private WorkspaceInitializedValue() {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitWorkspaceInitialized();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof WorkspaceInitializedValue;
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "}";
        }
    }

    @JsonTypeName("serverErrored")
    private static final class ServerErroredValue implements Value {
        @JsonUnwrapped
        private ExceptionInfo value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ServerErroredValue() {}

        private ServerErroredValue(ExceptionInfo value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitServerErrored(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ServerErroredValue && equalTo((ServerErroredValue) other);
        }

        private boolean equalTo(ServerErroredValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("codeExecutionUpdate")
    private static final class CodeExecutionUpdateValue implements Value {
        @JsonProperty("value")
        private CodeExecutionUpdate value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private CodeExecutionUpdateValue(@JsonProperty("value") CodeExecutionUpdate value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitCodeExecutionUpdate(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof CodeExecutionUpdateValue && equalTo((CodeExecutionUpdateValue) other);
        }

        private boolean equalTo(CodeExecutionUpdateValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("terminated")
    private static final class TerminatedValue implements Value {
        @JsonUnwrapped
        private TerminatedResponse value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private TerminatedValue() {}

        private TerminatedValue(TerminatedResponse value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitTerminated(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof TerminatedValue && equalTo((TerminatedValue) other);
        }

        private boolean equalTo(TerminatedValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "SubmissionResponse{" + "value: " + value + "}";
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
            return "SubmissionResponse{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
