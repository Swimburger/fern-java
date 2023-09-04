package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.Language;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = WorkspaceStarterFilesResponse.Builder.class)
public final class WorkspaceStarterFilesResponse {
    private final Map<Language, WorkspaceFiles> files;

    private WorkspaceStarterFilesResponse(Map<Language, WorkspaceFiles> files) {
        this.files = files;
    }

    @JsonProperty("files")
    public Map<Language, WorkspaceFiles> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof WorkspaceStarterFilesResponse && equalTo((WorkspaceStarterFilesResponse) other);
    }

    private boolean equalTo(WorkspaceStarterFilesResponse other) {
        return files.equals(other.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.files);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Map<Language, WorkspaceFiles> files = new LinkedHashMap<>();

        private Builder() {}

        public Builder from(WorkspaceStarterFilesResponse other) {
            files(other.getFiles());
            return this;
        }

        @JsonSetter(value = "files", nulls = Nulls.SKIP)
        public Builder files(Map<Language, WorkspaceFiles> files) {
            this.files.clear();
            this.files.putAll(files);
            return this;
        }

        public Builder putAllFiles(Map<Language, WorkspaceFiles> files) {
            this.files.putAll(files);
            return this;
        }

        public Builder files(Language key, WorkspaceFiles value) {
            this.files.put(key, value);
            return this;
        }

        public WorkspaceStarterFilesResponse build() {
            return new WorkspaceStarterFilesResponse(files);
        }
    }
}
