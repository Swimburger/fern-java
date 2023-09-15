/**
 * This file was auto-generated by Fern from our API Definition.
 */
package com.seed.trace.resources.v2.v3.problem.types;

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
@JsonDeserialize(builder = GeneratedFiles.Builder.class)
public final class GeneratedFiles {
    private final Map<Language, Files> generatedTestCaseFiles;

    private final Map<Language, Files> generatedTemplateFiles;

    private final Map<Language, Files> other;

    private GeneratedFiles(
            Map<Language, Files> generatedTestCaseFiles,
            Map<Language, Files> generatedTemplateFiles,
            Map<Language, Files> other) {
        this.generatedTestCaseFiles = generatedTestCaseFiles;
        this.generatedTemplateFiles = generatedTemplateFiles;
        this.other = other;
    }

    @JsonProperty("generatedTestCaseFiles")
    public Map<Language, Files> getGeneratedTestCaseFiles() {
        return generatedTestCaseFiles;
    }

    @JsonProperty("generatedTemplateFiles")
    public Map<Language, Files> getGeneratedTemplateFiles() {
        return generatedTemplateFiles;
    }

    @JsonProperty("other")
    public Map<Language, Files> getOther() {
        return other;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GeneratedFiles && equalTo((GeneratedFiles) other);
    }

    private boolean equalTo(GeneratedFiles other) {
        return generatedTestCaseFiles.equals(other.generatedTestCaseFiles)
                && generatedTemplateFiles.equals(other.generatedTemplateFiles)
                && other.equals(other.other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.generatedTestCaseFiles, this.generatedTemplateFiles, this.other);
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
        private Map<Language, Files> generatedTestCaseFiles = new LinkedHashMap<>();

        private Map<Language, Files> generatedTemplateFiles = new LinkedHashMap<>();

        private Map<Language, Files> other = new LinkedHashMap<>();

        private Builder() {}

        public Builder from(GeneratedFiles other) {
            generatedTestCaseFiles(other.getGeneratedTestCaseFiles());
            generatedTemplateFiles(other.getGeneratedTemplateFiles());
            other(other.getOther());
            return this;
        }

        @JsonSetter(value = "generatedTestCaseFiles", nulls = Nulls.SKIP)
        public Builder generatedTestCaseFiles(Map<Language, Files> generatedTestCaseFiles) {
            this.generatedTestCaseFiles.clear();
            this.generatedTestCaseFiles.putAll(generatedTestCaseFiles);
            return this;
        }

        public Builder putAllGeneratedTestCaseFiles(Map<Language, Files> generatedTestCaseFiles) {
            this.generatedTestCaseFiles.putAll(generatedTestCaseFiles);
            return this;
        }

        public Builder generatedTestCaseFiles(Language key, Files value) {
            this.generatedTestCaseFiles.put(key, value);
            return this;
        }

        @JsonSetter(value = "generatedTemplateFiles", nulls = Nulls.SKIP)
        public Builder generatedTemplateFiles(Map<Language, Files> generatedTemplateFiles) {
            this.generatedTemplateFiles.clear();
            this.generatedTemplateFiles.putAll(generatedTemplateFiles);
            return this;
        }

        public Builder putAllGeneratedTemplateFiles(Map<Language, Files> generatedTemplateFiles) {
            this.generatedTemplateFiles.putAll(generatedTemplateFiles);
            return this;
        }

        public Builder generatedTemplateFiles(Language key, Files value) {
            this.generatedTemplateFiles.put(key, value);
            return this;
        }

        @JsonSetter(value = "other", nulls = Nulls.SKIP)
        public Builder other(Map<Language, Files> other) {
            this.other.clear();
            this.other.putAll(other);
            return this;
        }

        public Builder putAllOther(Map<Language, Files> other) {
            this.other.putAll(other);
            return this;
        }

        public Builder other(Language key, Files value) {
            this.other.put(key, value);
            return this;
        }

        public GeneratedFiles build() {
            return new GeneratedFiles(generatedTestCaseFiles, generatedTemplateFiles, other);
        }
    }
}
