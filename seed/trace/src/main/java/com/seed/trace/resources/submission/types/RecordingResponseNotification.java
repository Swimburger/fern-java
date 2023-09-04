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
@JsonDeserialize(builder = RecordingResponseNotification.Builder.class)
public final class RecordingResponseNotification {
    private final UUID submissionId;

    private final Optional<String> testCaseId;

    private final int lineNumber;

    private final LightweightStackframeInformation lightweightStackInfo;

    private final Optional<TracedFile> tracedFile;

    private RecordingResponseNotification(
            UUID submissionId,
            Optional<String> testCaseId,
            int lineNumber,
            LightweightStackframeInformation lightweightStackInfo,
            Optional<TracedFile> tracedFile) {
        this.submissionId = submissionId;
        this.testCaseId = testCaseId;
        this.lineNumber = lineNumber;
        this.lightweightStackInfo = lightweightStackInfo;
        this.tracedFile = tracedFile;
    }

    @JsonProperty("submissionId")
    public UUID getSubmissionId() {
        return submissionId;
    }

    @JsonProperty("testCaseId")
    public Optional<String> getTestCaseId() {
        return testCaseId;
    }

    @JsonProperty("lineNumber")
    public int getLineNumber() {
        return lineNumber;
    }

    @JsonProperty("lightweightStackInfo")
    public LightweightStackframeInformation getLightweightStackInfo() {
        return lightweightStackInfo;
    }

    @JsonProperty("tracedFile")
    public Optional<TracedFile> getTracedFile() {
        return tracedFile;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof RecordingResponseNotification && equalTo((RecordingResponseNotification) other);
    }

    private boolean equalTo(RecordingResponseNotification other) {
        return submissionId.equals(other.submissionId)
                && testCaseId.equals(other.testCaseId)
                && lineNumber == other.lineNumber
                && lightweightStackInfo.equals(other.lightweightStackInfo)
                && tracedFile.equals(other.tracedFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.submissionId, this.testCaseId, this.lineNumber, this.lightweightStackInfo, this.tracedFile);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SubmissionIdStage builder() {
        return new Builder();
    }

    public interface SubmissionIdStage {
        LineNumberStage submissionId(UUID submissionId);

        Builder from(RecordingResponseNotification other);
    }

    public interface LineNumberStage {
        LightweightStackInfoStage lineNumber(int lineNumber);
    }

    public interface LightweightStackInfoStage {
        _FinalStage lightweightStackInfo(LightweightStackframeInformation lightweightStackInfo);
    }

    public interface _FinalStage {
        RecordingResponseNotification build();

        _FinalStage testCaseId(Optional<String> testCaseId);

        _FinalStage testCaseId(String testCaseId);

        _FinalStage tracedFile(Optional<TracedFile> tracedFile);

        _FinalStage tracedFile(TracedFile tracedFile);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
            implements SubmissionIdStage, LineNumberStage, LightweightStackInfoStage, _FinalStage {
        private UUID submissionId;

        private int lineNumber;

        private LightweightStackframeInformation lightweightStackInfo;

        private Optional<TracedFile> tracedFile = Optional.empty();

        private Optional<String> testCaseId = Optional.empty();

        private Builder() {}

        @Override
        public Builder from(RecordingResponseNotification other) {
            submissionId(other.getSubmissionId());
            testCaseId(other.getTestCaseId());
            lineNumber(other.getLineNumber());
            lightweightStackInfo(other.getLightweightStackInfo());
            tracedFile(other.getTracedFile());
            return this;
        }

        @Override
        @JsonSetter("submissionId")
        public LineNumberStage submissionId(UUID submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        @Override
        @JsonSetter("lineNumber")
        public LightweightStackInfoStage lineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        @Override
        @JsonSetter("lightweightStackInfo")
        public _FinalStage lightweightStackInfo(LightweightStackframeInformation lightweightStackInfo) {
            this.lightweightStackInfo = lightweightStackInfo;
            return this;
        }

        @Override
        public _FinalStage tracedFile(TracedFile tracedFile) {
            this.tracedFile = Optional.of(tracedFile);
            return this;
        }

        @Override
        @JsonSetter(value = "tracedFile", nulls = Nulls.SKIP)
        public _FinalStage tracedFile(Optional<TracedFile> tracedFile) {
            this.tracedFile = tracedFile;
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
        public RecordingResponseNotification build() {
            return new RecordingResponseNotification(
                    submissionId, testCaseId, lineNumber, lightweightStackInfo, tracedFile);
        }
    }
}
