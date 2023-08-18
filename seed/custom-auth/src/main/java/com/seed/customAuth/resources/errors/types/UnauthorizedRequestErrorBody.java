package com.seed.customAuth.resources.errors.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.customAuth.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = UnauthorizedRequestErrorBody.Builder.class)
public final class UnauthorizedRequestErrorBody {
    private final String message;

    private UnauthorizedRequestErrorBody(String message) {
        this.message = message;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof UnauthorizedRequestErrorBody && equalTo((UnauthorizedRequestErrorBody) other);
    }

    private boolean equalTo(UnauthorizedRequestErrorBody other) {
        return message.equals(other.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.message);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static MessageStage builder() {
        return new Builder();
    }

    public interface MessageStage {
        _FinalStage message(String message);

        Builder from(UnauthorizedRequestErrorBody other);
    }

    public interface _FinalStage {
        UnauthorizedRequestErrorBody build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements MessageStage, _FinalStage {
        private String message;

        private Builder() {}

        @Override
        public Builder from(UnauthorizedRequestErrorBody other) {
            message(other.getMessage());
            return this;
        }

        @Override
        @JsonSetter("message")
        public _FinalStage message(String message) {
            this.message = message;
            return this;
        }

        @Override
        public UnauthorizedRequestErrorBody build() {
            return new UnauthorizedRequestErrorBody(message);
        }
    }
}
