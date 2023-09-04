package com.seed.trace.resources.v2.problem.types;

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
@JsonDeserialize(builder = GetFunctionSignatureResponse.Builder.class)
public final class GetFunctionSignatureResponse {
    private final Map<Language, String> functionByLanguage;

    private GetFunctionSignatureResponse(Map<Language, String> functionByLanguage) {
        this.functionByLanguage = functionByLanguage;
    }

    @JsonProperty("functionByLanguage")
    public Map<Language, String> getFunctionByLanguage() {
        return functionByLanguage;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GetFunctionSignatureResponse && equalTo((GetFunctionSignatureResponse) other);
    }

    private boolean equalTo(GetFunctionSignatureResponse other) {
        return functionByLanguage.equals(other.functionByLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.functionByLanguage);
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
        private Map<Language, String> functionByLanguage = new LinkedHashMap<>();

        private Builder() {}

        public Builder from(GetFunctionSignatureResponse other) {
            functionByLanguage(other.getFunctionByLanguage());
            return this;
        }

        @JsonSetter(value = "functionByLanguage", nulls = Nulls.SKIP)
        public Builder functionByLanguage(Map<Language, String> functionByLanguage) {
            this.functionByLanguage.clear();
            this.functionByLanguage.putAll(functionByLanguage);
            return this;
        }

        public Builder putAllFunctionByLanguage(Map<Language, String> functionByLanguage) {
            this.functionByLanguage.putAll(functionByLanguage);
            return this;
        }

        public Builder functionByLanguage(Language key, String value) {
            this.functionByLanguage.put(key, value);
            return this;
        }

        public GetFunctionSignatureResponse build() {
            return new GetFunctionSignatureResponse(functionByLanguage);
        }
    }
}
