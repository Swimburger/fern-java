package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = LightweightStackframeInformation.Builder.class)
public final class LightweightStackframeInformation {
    private final int numStackFrames;

    private final String topStackFrameMethodName;

    private LightweightStackframeInformation(int numStackFrames, String topStackFrameMethodName) {
        this.numStackFrames = numStackFrames;
        this.topStackFrameMethodName = topStackFrameMethodName;
    }

    @JsonProperty("numStackFrames")
    public int getNumStackFrames() {
        return numStackFrames;
    }

    @JsonProperty("topStackFrameMethodName")
    public String getTopStackFrameMethodName() {
        return topStackFrameMethodName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof LightweightStackframeInformation && equalTo((LightweightStackframeInformation) other);
    }

    private boolean equalTo(LightweightStackframeInformation other) {
        return numStackFrames == other.numStackFrames && topStackFrameMethodName.equals(other.topStackFrameMethodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.numStackFrames, this.topStackFrameMethodName);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static NumStackFramesStage builder() {
        return new Builder();
    }

    public interface NumStackFramesStage {
        TopStackFrameMethodNameStage numStackFrames(int numStackFrames);

        Builder from(LightweightStackframeInformation other);
    }

    public interface TopStackFrameMethodNameStage {
        _FinalStage topStackFrameMethodName(String topStackFrameMethodName);
    }

    public interface _FinalStage {
        LightweightStackframeInformation build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements NumStackFramesStage, TopStackFrameMethodNameStage, _FinalStage {
        private int numStackFrames;

        private String topStackFrameMethodName;

        private Builder() {}

        @Override
        public Builder from(LightweightStackframeInformation other) {
            numStackFrames(other.getNumStackFrames());
            topStackFrameMethodName(other.getTopStackFrameMethodName());
            return this;
        }

        @Override
        @JsonSetter("numStackFrames")
        public TopStackFrameMethodNameStage numStackFrames(int numStackFrames) {
            this.numStackFrames = numStackFrames;
            return this;
        }

        @Override
        @JsonSetter("topStackFrameMethodName")
        public _FinalStage topStackFrameMethodName(String topStackFrameMethodName) {
            this.topStackFrameMethodName = topStackFrameMethodName;
            return this;
        }

        @Override
        public LightweightStackframeInformation build() {
            return new LightweightStackframeInformation(numStackFrames, topStackFrameMethodName);
        }
    }
}
