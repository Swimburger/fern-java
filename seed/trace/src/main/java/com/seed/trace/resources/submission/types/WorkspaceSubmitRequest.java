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
@JsonDeserialize(builder = WorkspaceSubmitRequest.Builder.class)
public final class WorkspaceSubmitRequest {
    private final UUID submissionId;

    private final Language language;

    private final List<SubmissionFileInfo> submissionFiles;

    private final Optional<String> userId;

    private WorkspaceSubmitRequest(
            UUID submissionId, Language language, List<SubmissionFileInfo> submissionFiles, Optional<String> userId) {
        this.submissionId = submissionId;
        this.language = language;
        this.submissionFiles = submissionFiles;
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

    @JsonProperty("userId")
    public Optional<String> getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof WorkspaceSubmitRequest && equalTo((WorkspaceSubmitRequest) other);
    }

    private boolean equalTo(WorkspaceSubmitRequest other) {
        return submissionId.equals(other.submissionId)
                && language.equals(other.language)
                && submissionFiles.equals(other.submissionFiles)
                && userId.equals(other.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.submissionId, this.language, this.submissionFiles, this.userId);
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

        Builder from(WorkspaceSubmitRequest other);
    }

    public interface LanguageStage {
        _FinalStage language(Language language);
    }

    public interface _FinalStage {
        WorkspaceSubmitRequest build();

        _FinalStage submissionFiles(List<SubmissionFileInfo> submissionFiles);

        _FinalStage addSubmissionFiles(SubmissionFileInfo submissionFiles);

        _FinalStage addAllSubmissionFiles(List<SubmissionFileInfo> submissionFiles);

        _FinalStage userId(Optional<String> userId);

        _FinalStage userId(String userId);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SubmissionIdStage, LanguageStage, _FinalStage {
        private UUID submissionId;

        private Language language;

        private Optional<String> userId = Optional.empty();

        private List<SubmissionFileInfo> submissionFiles = new ArrayList<>();

        private Builder() {}

        @Override
        public Builder from(WorkspaceSubmitRequest other) {
            submissionId(other.getSubmissionId());
            language(other.getLanguage());
            submissionFiles(other.getSubmissionFiles());
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
        public _FinalStage language(Language language) {
            this.language = language;
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
        public WorkspaceSubmitRequest build() {
            return new WorkspaceSubmitRequest(submissionId, language, submissionFiles, userId);
        }
    }
}
