package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = RecordedResponseNotification.Builder.class)
public final class RecordedResponseNotification {
    private final UUID submissionId;

    private final int traceResponsesSize;

    private final Optional<String> testCaseId;

    private RecordedResponseNotification(UUID submissionId, int traceResponsesSize, Optional<String> testCaseId) {
        this.submissionId = submissionId;
        this.traceResponsesSize = traceResponsesSize;
        this.testCaseId = testCaseId;
    }

    @JsonProperty("submissionId")
    public UUID getSubmissionId() {
        return submissionId;
    }

    @JsonProperty("traceResponsesSize")
    public int getTraceResponsesSize() {
        return traceResponsesSize;
    }

    @JsonProperty("testCaseId")
    public Optional<String> getTestCaseId() {
        return testCaseId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof RecordedResponseNotification && equalTo((RecordedResponseNotification) other);
    }

    private boolean equalTo(RecordedResponseNotification other) {
        return submissionId.equals(other.submissionId)
                && traceResponsesSize == other.traceResponsesSize
                && testCaseId.equals(other.testCaseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.submissionId, this.traceResponsesSize, this.testCaseId);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SubmissionIdStage builder() {
        return new Builder();
    }

    public interface SubmissionIdStage {
        TraceResponsesSizeStage submissionId(UUID submissionId);

        Builder from(RecordedResponseNotification other);
    }

    public interface TraceResponsesSizeStage {
        _FinalStage traceResponsesSize(int traceResponsesSize);
    }

    public interface _FinalStage {
        RecordedResponseNotification build();

        _FinalStage testCaseId(Optional<String> testCaseId);

        _FinalStage testCaseId(String testCaseId);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SubmissionIdStage, TraceResponsesSizeStage, _FinalStage {
        private UUID submissionId;

        private int traceResponsesSize;

        private Optional<String> testCaseId = Optional.empty();

        private Builder() {}

        @Override
        public Builder from(RecordedResponseNotification other) {
            submissionId(other.getSubmissionId());
            traceResponsesSize(other.getTraceResponsesSize());
            testCaseId(other.getTestCaseId());
            return this;
        }

        @Override
        @JsonSetter("submissionId")
        public TraceResponsesSizeStage submissionId(UUID submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        @Override
        @JsonSetter("traceResponsesSize")
        public _FinalStage traceResponsesSize(int traceResponsesSize) {
            this.traceResponsesSize = traceResponsesSize;
            return this;
        }

        @Override
        public _FinalStage testCaseId(String testCaseId) {
            this.testCaseId = Optional.of(testCaseId);
            return this;
        }

        @Override
        @JsonSetter(value = "testCaseId", nulls = Nulls.SKIP)
        public _FinalStage testCaseId(Optional<String> testCaseId) {
            this.testCaseId = testCaseId;
            return this;
        }

        @Override
        public RecordedResponseNotification build() {
            return new RecordedResponseNotification(submissionId, traceResponsesSize, testCaseId);
        }
    }
}
