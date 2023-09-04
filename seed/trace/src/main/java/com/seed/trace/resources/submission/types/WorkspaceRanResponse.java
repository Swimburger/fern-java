package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkspaceRanResponse.Builder.class)
public final class WorkspaceRanResponse {
    private final UUID submissionId;

    private final WorkspaceRunDetails runDetails;

    private WorkspaceRanResponse(UUID submissionId, WorkspaceRunDetails runDetails) {
        this.submissionId = submissionId;
        this.runDetails = runDetails;
    }

    @JsonProperty("submissionId")
    public UUID getSubmissionId() {
        return submissionId;
    }

    @JsonProperty("runDetails")
    public WorkspaceRunDetails getRunDetails() {
        return runDetails;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof WorkspaceRanResponse && equalTo((WorkspaceRanResponse) other);
    }

    private boolean equalTo(WorkspaceRanResponse other) {
        return submissionId.equals(other.submissionId) && runDetails.equals(other.runDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.submissionId, this.runDetails);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SubmissionIdStage builder() {
        return new Builder();
    }

    public interface SubmissionIdStage {
        RunDetailsStage submissionId(UUID submissionId);

        Builder from(WorkspaceRanResponse other);
    }

    public interface RunDetailsStage {
        _FinalStage runDetails(WorkspaceRunDetails runDetails);
    }

    public interface _FinalStage {
        WorkspaceRanResponse build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SubmissionIdStage, RunDetailsStage, _FinalStage {
        private UUID submissionId;

        private WorkspaceRunDetails runDetails;

        private Builder() {}

        @Override
        public Builder from(WorkspaceRanResponse other) {
            submissionId(other.getSubmissionId());
            runDetails(other.getRunDetails());
            return this;
        }

        @Override
        @JsonSetter("submissionId")
        public RunDetailsStage submissionId(UUID submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        @Override
        @JsonSetter("runDetails")
        public _FinalStage runDetails(WorkspaceRunDetails runDetails) {
            this.runDetails = runDetails;
            return this;
        }

        @Override
        public WorkspaceRanResponse build() {
            return new WorkspaceRanResponse(submissionId, runDetails);
        }
    }
}
