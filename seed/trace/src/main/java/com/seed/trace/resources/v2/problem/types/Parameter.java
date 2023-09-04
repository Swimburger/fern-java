package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.VariableType;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Parameter.Builder.class)
public final class Parameter {
    private final String parameterId;

    private final String name;

    private final VariableType variableType;

    private Parameter(String parameterId, String name, VariableType variableType) {
        this.parameterId = parameterId;
        this.name = name;
        this.variableType = variableType;
    }

    @JsonProperty("parameterId")
    public String getParameterId() {
        return parameterId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("variableType")
    public VariableType getVariableType() {
        return variableType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Parameter && equalTo((Parameter) other);
    }

    private boolean equalTo(Parameter other) {
        return parameterId.equals(other.parameterId)
                && name.equals(other.name)
                && variableType.equals(other.variableType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parameterId, this.name, this.variableType);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ParameterIdStage builder() {
        return new Builder();
    }

    public interface ParameterIdStage {
        NameStage parameterId(String parameterId);

        Builder from(Parameter other);
    }

    public interface NameStage {
        VariableTypeStage name(String name);
    }

    public interface VariableTypeStage {
        _FinalStage variableType(VariableType variableType);
    }

    public interface _FinalStage {
        Parameter build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements ParameterIdStage, NameStage, VariableTypeStage, _FinalStage {
        private String parameterId;

        private String name;

        private VariableType variableType;

        private Builder() {}

        @Override
        public Builder from(Parameter other) {
            parameterId(other.getParameterId());
            name(other.getName());
            variableType(other.getVariableType());
            return this;
        }

        @Override
        @JsonSetter("parameterId")
        public NameStage parameterId(String parameterId) {
            this.parameterId = parameterId;
            return this;
        }

        @Override
        @JsonSetter("name")
        public VariableTypeStage name(String name) {
            this.name = name;
            return this;
        }

        @Override
        @JsonSetter("variableType")
        public _FinalStage variableType(VariableType variableType) {
            this.variableType = variableType;
            return this;
        }

        @Override
        public Parameter build() {
            return new Parameter(parameterId, name, variableType);
        }
    }
}
