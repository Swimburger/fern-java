package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.VariableType;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = LightweightProblemInfoV2.Builder.class)
public final class LightweightProblemInfoV2 {
    private final String problemId;

    private final String problemName;

    private final int problemVersion;

    private final Set<VariableType> variableTypes;

    private LightweightProblemInfoV2(
            String problemId, String problemName, int problemVersion, Set<VariableType> variableTypes) {
        this.problemId = problemId;
        this.problemName = problemName;
        this.problemVersion = problemVersion;
        this.variableTypes = variableTypes;
    }

    @JsonProperty("problemId")
    public String getProblemId() {
        return problemId;
    }

    @JsonProperty("problemName")
    public String getProblemName() {
        return problemName;
    }

    @JsonProperty("problemVersion")
    public int getProblemVersion() {
        return problemVersion;
    }

    @JsonProperty("variableTypes")
    public Set<VariableType> getVariableTypes() {
        return variableTypes;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof LightweightProblemInfoV2 && equalTo((LightweightProblemInfoV2) other);
    }

    private boolean equalTo(LightweightProblemInfoV2 other) {
        return problemId.equals(other.problemId)
                && problemName.equals(other.problemName)
                && problemVersion == other.problemVersion
                && variableTypes.equals(other.variableTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.problemId, this.problemName, this.problemVersion, this.variableTypes);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ProblemIdStage builder() {
        return new Builder();
    }

    public interface ProblemIdStage {
        ProblemNameStage problemId(String problemId);

        Builder from(LightweightProblemInfoV2 other);
    }

    public interface ProblemNameStage {
        ProblemVersionStage problemName(String problemName);
    }

    public interface ProblemVersionStage {
        _FinalStage problemVersion(int problemVersion);
    }

    public interface _FinalStage {
        LightweightProblemInfoV2 build();

        _FinalStage variableTypes(Set<VariableType> variableTypes);

        _FinalStage addVariableTypes(VariableType variableTypes);

        _FinalStage addAllVariableTypes(Set<VariableType> variableTypes);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements ProblemIdStage, ProblemNameStage, ProblemVersionStage, _FinalStage {
        private String problemId;

        private String problemName;

        private int problemVersion;

        private Set<VariableType> variableTypes = new LinkedHashSet<>();

        private Builder() {}

        @Override
        public Builder from(LightweightProblemInfoV2 other) {
            problemId(other.getProblemId());
            problemName(other.getProblemName());
            problemVersion(other.getProblemVersion());
            variableTypes(other.getVariableTypes());
            return this;
        }

        @Override
        @JsonSetter("problemId")
        public ProblemNameStage problemId(String problemId) {
            this.problemId = problemId;
            return this;
        }

        @Override
        @JsonSetter("problemName")
        public ProblemVersionStage problemName(String problemName) {
            this.problemName = problemName;
            return this;
        }

        @Override
        @JsonSetter("problemVersion")
        public _FinalStage problemVersion(int problemVersion) {
            this.problemVersion = problemVersion;
            return this;
        }

        @Override
        public _FinalStage addAllVariableTypes(Set<VariableType> variableTypes) {
            this.variableTypes.addAll(variableTypes);
            return this;
        }

        @Override
        public _FinalStage addVariableTypes(VariableType variableTypes) {
            this.variableTypes.add(variableTypes);
            return this;
        }

        @Override
        @JsonSetter(value = "variableTypes", nulls = Nulls.SKIP)
        public _FinalStage variableTypes(Set<VariableType> variableTypes) {
            this.variableTypes.clear();
            this.variableTypes.addAll(variableTypes);
            return this;
        }

        @Override
        public LightweightProblemInfoV2 build() {
            return new LightweightProblemInfoV2(problemId, problemName, problemVersion, variableTypes);
        }
    }
}
