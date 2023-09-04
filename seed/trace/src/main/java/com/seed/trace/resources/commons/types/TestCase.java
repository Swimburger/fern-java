package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCase.Builder.class)
public final class TestCase {
    private final String id;

    private final List<VariableValue> params;

    private TestCase(String id, List<VariableValue> params) {
        this.id = id;
        this.params = params;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("params")
    public List<VariableValue> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCase && equalTo((TestCase) other);
    }

    private boolean equalTo(TestCase other) {
        return id.equals(other.id) && params.equals(other.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.params);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static IdStage builder() {
        return new Builder();
    }

    public interface IdStage {
        _FinalStage id(String id);

        Builder from(TestCase other);
    }

    public interface _FinalStage {
        TestCase build();

        _FinalStage params(List<VariableValue> params);

        _FinalStage addParams(VariableValue params);

        _FinalStage addAllParams(List<VariableValue> params);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements IdStage, _FinalStage {
        private String id;

        private List<VariableValue> params = new ArrayList<>();

        private Builder() {}

        @Override
        public Builder from(TestCase other) {
            id(other.getId());
            params(other.getParams());
            return this;
        }

        @Override
        @JsonSetter("id")
        public _FinalStage id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public _FinalStage addAllParams(List<VariableValue> params) {
            this.params.addAll(params);
            return this;
        }

        @Override
        public _FinalStage addParams(VariableValue params) {
            this.params.add(params);
            return this;
        }

        @Override
        @JsonSetter(value = "params", nulls = Nulls.SKIP)
        public _FinalStage params(List<VariableValue> params) {
            this.params.clear();
            this.params.addAll(params);
            return this;
        }

        @Override
        public TestCase build() {
            return new TestCase(id, params);
        }
    }
}
