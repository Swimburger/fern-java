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
@JsonDeserialize(builder = CustomTestCasesUnsupported.Builder.class)
public final class CustomTestCasesUnsupported {
    private final String problemId;

    private final UUID submissionId;

    private CustomTestCasesUnsupported(String problemId, UUID submissionId) {
        this.problemId = problemId;
        this.submissionId = submissionId;
    }

    @JsonProperty("problemId")
    public String getProblemId() {
        return problemId;
    }

    @JsonProperty("submissionId")
    public UUID getSubmissionId() {
        return submissionId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof CustomTestCasesUnsupported && equalTo((CustomTestCasesUnsupported) other);
    }

    private boolean equalTo(CustomTestCasesUnsupported other) {
        return problemId.equals(other.problemId) && submissionId.equals(other.submissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.problemId, this.submissionId);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ProblemIdStage builder() {
        return new Builder();
    }

    public interface ProblemIdStage {
        SubmissionIdStage problemId(String problemId);

        Builder from(CustomTestCasesUnsupported other);
    }

    public interface SubmissionIdStage {
        _FinalStage submissionId(UUID submissionId);
    }

    public interface _FinalStage {
        CustomTestCasesUnsupported build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements ProblemIdStage, SubmissionIdStage, _FinalStage {
        private String problemId;

        private UUID submissionId;

        private Builder() {}

        @Override
        public Builder from(CustomTestCasesUnsupported other) {
            problemId(other.getProblemId());
            submissionId(other.getSubmissionId());
            return this;
        }

        @Override
        @JsonSetter("problemId")
        public SubmissionIdStage problemId(String problemId) {
            this.problemId = problemId;
            return this;
        }

        @Override
        @JsonSetter("submissionId")
        public _FinalStage submissionId(UUID submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        @Override
        public CustomTestCasesUnsupported build() {
            return new CustomTestCasesUnsupported(problemId, submissionId);
        }
    }
}
