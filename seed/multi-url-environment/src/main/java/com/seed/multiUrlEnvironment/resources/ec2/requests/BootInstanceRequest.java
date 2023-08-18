package com.seed.multiUrlEnvironment.resources.ec2.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.multiUrlEnvironment.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = BootInstanceRequest.Builder.class)
public final class BootInstanceRequest {
    private final String size;

    private BootInstanceRequest(String size) {
        this.size = size;
    }

    @JsonProperty("size")
    public String getSize() {
        return size;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof BootInstanceRequest && equalTo((BootInstanceRequest) other);
    }

    private boolean equalTo(BootInstanceRequest other) {
        return size.equals(other.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.size);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static SizeStage builder() {
        return new Builder();
    }

    public interface SizeStage {
        _FinalStage size(String size);

        Builder from(BootInstanceRequest other);
    }

    public interface _FinalStage {
        BootInstanceRequest build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements SizeStage, _FinalStage {
        private String size;

        private Builder() {}

        @Override
        public Builder from(BootInstanceRequest other) {
            size(other.getSize());
            return this;
        }

        @Override
        @JsonSetter("size")
        public _FinalStage size(String size) {
            this.size = size;
            return this;
        }

        @Override
        public BootInstanceRequest build() {
            return new BootInstanceRequest(size);
        }
    }
}
