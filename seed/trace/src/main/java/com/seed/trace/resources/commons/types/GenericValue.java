package com.seed.trace.resources.commons.types;

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
@JsonDeserialize(builder = GenericValue.Builder.class)
public final class GenericValue {
    private final Optional<String> stringifiedType;

    private final String stringifiedValue;

    private GenericValue(Optional<String> stringifiedType, String stringifiedValue) {
        this.stringifiedType = stringifiedType;
        this.stringifiedValue = stringifiedValue;
    }

    @JsonProperty("stringifiedType")
    public Optional<String> getStringifiedType() {
        return stringifiedType;
    }

    @JsonProperty("stringifiedValue")
    public String getStringifiedValue() {
        return stringifiedValue;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GenericValue && equalTo((GenericValue) other);
    }

    private boolean equalTo(GenericValue other) {
        return stringifiedType.equals(other.stringifiedType) && stringifiedValue.equals(other.stringifiedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.stringifiedType, this.stringifiedValue);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static StringifiedValueStage builder() {
        return new Builder();
    }

    public interface StringifiedValueStage {
        _FinalStage stringifiedValue(String stringifiedValue);

        Builder from(GenericValue other);
    }

    public interface _FinalStage {
        GenericValue build();

        _FinalStage stringifiedType(Optional<String> stringifiedType);

        _FinalStage stringifiedType(String stringifiedType);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements StringifiedValueStage, _FinalStage {
        private String stringifiedValue;

        private Optional<String> stringifiedType = Optional.empty();

        private Builder() {}

        @Override
        public Builder from(GenericValue other) {
            stringifiedType(other.getStringifiedType());
            stringifiedValue(other.getStringifiedValue());
            return this;
        }

        @Override
        @JsonSetter("stringifiedValue")
        public _FinalStage stringifiedValue(String stringifiedValue) {
            this.stringifiedValue = stringifiedValue;
            return this;
        }

        @Override
        public _FinalStage stringifiedType(String stringifiedType) {
            this.stringifiedType = Optional.of(stringifiedType);
            return this;
        }

        @Override
        @JsonSetter(value = "stringifiedType", nulls = Nulls.SKIP)
        public _FinalStage stringifiedType(Optional<String> stringifiedType) {
            this.stringifiedType = stringifiedType;
            return this;
        }

        @Override
        public GenericValue build() {
            return new GenericValue(stringifiedType, stringifiedValue);
        }
    }
}
