package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.Language;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = SubmitRequestV2.Builder.class)
public final class SubmitRequestV2 {
    private final UUID submissionId;

    private final Language language;

    private final List<SubmissionFileInfo> submissionFiles;

    private final String problemId;

    private final Optional<Integer> problemVersion;

    private final Optional<String> userId;

    private SubmitRequestV2(
            UUID submissionId,
            Language language,
            List<SubmissionFileInfo> submissionFiles,
            String problemId,
            Optional<Integer> problemVersion,
            Optional<String> userId) {
        this.submissionId = submissionId;
        this.language = language;
        this.submissionFiles = submissionFiles;
        this.problemId = problemId;
        this.problemVersion = problemVersion;
        this.userId = userId;
    }

    @JsonProperty("submissionId")
    public UUID getSubmissionId() {
        return submissionId;
    }

    @JsonProperty("language")
    public Language getLanguage() {
        return language;
    }

    @JsonProperty("submissionFiles")
    public List<SubmissionFileInfo> getSubmissionFiles() {
        return submissionFiles;
    }

    @JsonProperty("problemId")
    public String getProblemId() {
        return problemId;
    }

    @JsonProperty("problemVersion")
    public Optional<Integer> getProblemVersion() {
        return problemVersion;
    }

    @JsonProperty("userId")
    public Optional<String> getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof SubmitRequestV2 && equalTo((SubmitRequestV2) other);
    }

    private boolean equalTo(SubmitRequestV2 other) {
        return submissionId.equals(other.submissionId)
                && language.equals(other.language)
                && submissionFiles.equals(other.submissionFiles)
                && problemId.equals(other.problemId)
                && problemVersion.equals(other.problemVersion)
                && userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.submissionId,
                this.language,
                this.submissionFiles,
                this.problemId,
                this.problemVersion,
                this.userId);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SubmissionIdStage builder() {
        return new Builder();
    }

    public interface SubmissionIdStage {
        LanguageStage submissionId(UUID submissionId);

        Builder from(SubmitRequestV2 other);
    }

    public interface LanguageStage {
        ProblemIdStage language(Language language);
    }

    public interface ProblemIdStage {
        _FinalStage problemId(String problemId);
    }

    public interface _FinalStage {
        SubmitRequestV2 build();

        _FinalStage submissionFiles(List<SubmissionFileInfo> submissionFiles);

        _FinalStage addSubmissionFiles(SubmissionFileInfo submissionFiles);

        _FinalStage addAllSubmissionFiles(List<SubmissionFileInfo> submissionFiles);

        _FinalStage problemVersion(Optional<Integer> problemVersion);

        _FinalStage problemVersion(Integer problemVersion);

        _FinalStage userId(Optional<String> userId);

        _FinalStage userId(String userId);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SubmissionIdStage, LanguageStage, ProblemIdStage, _FinalStage {
        private UUID submissionId;

        private Language language;

        private String problemId;

        private Optional<String> userId = Optional.empty();

        private Optional<Integer> problemVersion = Optional.empty();

        private List<SubmissionFileInfo> submissionFiles = new ArrayList<>();

        private Builder() {}

        @Override
        public Builder from(SubmitRequestV2 other) {
            submissionId(other.getSubmissionId());
            language(other.getLanguage());
            submissionFiles(other.getSubmissionFiles());
            problemId(other.getProblemId());
            problemVersion(other.getProblemVersion());
            userId(other.getUserId());
            return this;
        }

        @Override
        @JsonSetter("submissionId")
        public LanguageStage submissionId(UUID submissionId) {
            this.submissionId = submissionId;
            return this;
        }

        @Override
        @JsonSetter("language")
        public ProblemIdStage language(Language language) {
            this.language = language;
            return this;
        }

        @Override
        @JsonSetter("problemId")
        public _FinalStage problemId(String problemId) {
            this.problemId = problemId;
            return this;
        }

        @Override
        public _FinalStage userId(String userId) {
            this.userId = Optional.of(userId);
            return this;
        }

        @Override
        @JsonSetter(value = "userId", nulls = Nulls.SKIP)
        public _FinalStage userId(Optional<String> userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public _FinalStage problemVersion(Integer problemVersion) {
            this.problemVersion = Optional.of(problemVersion);
            return this;
        }

        @Override
        @JsonSetter(value = "problemVersion", nulls = Nulls.SKIP)
        public _FinalStage problemVersion(Optional<Integer> problemVersion) {
            this.problemVersion = problemVersion;
            return this;
        }

        @Override
        public _FinalStage addAllSubmissionFiles(List<SubmissionFileInfo> submissionFiles) {
            this.submissionFiles.addAll(submissionFiles);
            return this;
        }

        @Override
        public _FinalStage addSubmissionFiles(SubmissionFileInfo submissionFiles) {
            this.submissionFiles.add(submissionFiles);
            return this;
        }

        @Override
        @JsonSetter(value = "submissionFiles", nulls = Nulls.SKIP)
        public _FinalStage submissionFiles(List<SubmissionFileInfo> submissionFiles) {
            this.submissionFiles.clear();
            this.submissionFiles.addAll(submissionFiles);
            return this;
        }

        @Override
        public SubmitRequestV2 build() {
            return new SubmitRequestV2(submissionId, language, submissionFiles, problemId, problemVersion, userId);
        }
    }
}
