package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = FileInfo.Builder.class)
public final class FileInfo {
    private final String filename;

    private final String contents;

    private FileInfo(String filename, String contents) {
        this.filename = filename;
        this.contents = contents;
    }

    @JsonProperty("filename")
    public String getFilename() {
        return filename;
    }

    @JsonProperty("contents")
    public String getContents() {
        return contents;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof FileInfo && equalTo((FileInfo) other);
    }

    private boolean equalTo(FileInfo other) {
        return filename.equals(other.filename) && contents.equals(other.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.filename, this.contents);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static FilenameStage builder() {
        return new Builder();
    }

    public interface FilenameStage {
        ContentsStage filename(String filename);

        Builder from(FileInfo other);
    }

    public interface ContentsStage {
        _FinalStage contents(String contents);
    }

    public interface _FinalStage {
        FileInfo build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements FilenameStage, ContentsStage, _FinalStage {
        private String filename;

        private String contents;

        private Builder() {}

        @Override
        public Builder from(FileInfo other) {
            filename(other.getFilename());
            contents(other.getContents());
            return this;
        }

        @Override
        @JsonSetter("filename")
        public ContentsStage filename(String filename) {
            this.filename = filename;
            return this;
        }

        @Override
        @JsonSetter("contents")
        public _FinalStage contents(String contents) {
            this.contents = contents;
            return this;
        }

        @Override
        public FileInfo build() {
            return new FileInfo(filename, contents);
        }
    }
}
