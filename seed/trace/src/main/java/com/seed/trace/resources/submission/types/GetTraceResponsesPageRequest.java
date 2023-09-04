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

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = GetTraceResponsesPageRequest.Builder.class)
public final class GetTraceResponsesPageRequest {
    private final Optional<Integer> offset;

    private GetTraceResponsesPageRequest(Optional<Integer> offset) {
        this.offset = offset;
    }

    @JsonProperty("offset")
    public Optional<Integer> getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GetTraceResponsesPageRequest && equalTo((GetTraceResponsesPageRequest) other);
    }

    private boolean equalTo(GetTraceResponsesPageRequest other) {
        return offset.equals(other.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.offset);
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
        private Optional<Integer> offset = Optional.empty();

        private Builder() {}

        public Builder from(GetTraceResponsesPageRequest other) {
            offset(other.getOffset());
            return this;
        }

        @JsonSetter(value = "offset", nulls = Nulls.SKIP)
        public Builder offset(Optional<Integer> offset) {
            this.offset = offset;
            return this;
        }

        public Builder offset(Integer offset) {
            this.offset = Optional.of(offset);
            return this;
        }

        public GetTraceResponsesPageRequest build() {
            return new GetTraceResponsesPageRequest(offset);
        }
    }
}
