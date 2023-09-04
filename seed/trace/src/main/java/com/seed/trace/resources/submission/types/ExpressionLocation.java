package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = ExpressionLocation.Builder.class)
public final class ExpressionLocation {
    private final int start;

    private final int offset;

    private ExpressionLocation(int start, int offset) {
        this.start = start;
        this.offset = offset;
    }

    @JsonProperty("start")
    public int getStart() {
        return start;
    }

    @JsonProperty("offset")
    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ExpressionLocation && equalTo((ExpressionLocation) other);
    }

    private boolean equalTo(ExpressionLocation other) {
        return start == other.start && offset == other.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.offset);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static StartStage builder() {
        return new Builder();
    }

    public interface StartStage {
        OffsetStage start(int start);

        Builder from(ExpressionLocation other);
    }

    public interface OffsetStage {
        _FinalStage offset(int offset);
    }

    public interface _FinalStage {
        ExpressionLocation build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements StartStage, OffsetStage, _FinalStage {
        private int start;

        private int offset;

        private Builder() {}

        @Override
        public Builder from(ExpressionLocation other) {
            start(other.getStart());
            offset(other.getOffset());
            return this;
        }

        @Override
        @JsonSetter("start")
        public OffsetStage start(int start) {
            this.start = start;
            return this;
        }

        @Override
        @JsonSetter("offset")
        public _FinalStage offset(int offset) {
            this.offset = offset;
            return this;
        }

        @Override
        public ExpressionLocation build() {
            return new ExpressionLocation(start, offset);
        }
    }
}
