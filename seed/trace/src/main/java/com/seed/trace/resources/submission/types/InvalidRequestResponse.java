package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = InvalidRequestResponse.Builder.class)
public final class InvalidRequestResponse {
    private final SubmissionRequest request;

    private final InvalidRequestCause cause;

    private InvalidRequestResponse(SubmissionRequest request, InvalidRequestCause cause) {
        this.request = request;
        this.cause = cause;
    }

    @JsonProperty("request")
    public SubmissionRequest getRequest() {
        return request;
    }

    @JsonProperty("cause")
    public InvalidRequestCause getCause() {
        return cause;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof InvalidRequestResponse && equalTo((InvalidRequestResponse) other);
    }

    private boolean equalTo(InvalidRequestResponse other) {
        return request.equals(other.request) && cause.equals(other.cause);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.request, this.cause);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static RequestStage builder() {
        return new Builder();
    }

    public interface RequestStage {
        CauseStage request(SubmissionRequest request);

        Builder from(InvalidRequestResponse other);
    }

    public interface CauseStage {
        _FinalStage cause(InvalidRequestCause cause);
    }

    public interface _FinalStage {
        InvalidRequestResponse build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements RequestStage, CauseStage, _FinalStage {
        private SubmissionRequest request;

        private InvalidRequestCause cause;

        private Builder() {}

        @Override
        public Builder from(InvalidRequestResponse other) {
            request(other.getRequest());
            cause(other.getCause());
            return this;
        }

        @Override
        @JsonSetter("request")
        public CauseStage request(SubmissionRequest request) {
            this.request = request;
            return this;
        }

        @Override
        @JsonSetter("cause")
        public _FinalStage cause(InvalidRequestCause cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public InvalidRequestResponse build() {
            return new InvalidRequestResponse(request, cause);
        }
    }
}
