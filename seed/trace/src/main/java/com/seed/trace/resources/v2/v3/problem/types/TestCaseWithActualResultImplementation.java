package com.seed.trace.resources.v2.v3.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseWithActualResultImplementation.Builder.class)
public final class TestCaseWithActualResultImplementation {
    private final NonVoidFunctionDefinition getActualResult;

    private final AssertCorrectnessCheck assertCorrectnessCheck;

    private TestCaseWithActualResultImplementation(
            NonVoidFunctionDefinition getActualResult, AssertCorrectnessCheck assertCorrectnessCheck) {
        this.getActualResult = getActualResult;
        this.assertCorrectnessCheck = assertCorrectnessCheck;
    }

    @JsonProperty("getActualResult")
    public NonVoidFunctionDefinition getGetActualResult() {
        return getActualResult;
    }

    @JsonProperty("assertCorrectnessCheck")
    public AssertCorrectnessCheck getAssertCorrectnessCheck() {
        return assertCorrectnessCheck;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseWithActualResultImplementation
                && equalTo((TestCaseWithActualResultImplementation) other);
    }

    private boolean equalTo(TestCaseWithActualResultImplementation other) {
        return getActualResult.equals(other.getActualResult)
                && assertCorrectnessCheck.equals(other.assertCorrectnessCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getActualResult, this.assertCorrectnessCheck);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static GetActualResultStage builder() {
        return new Builder();
    }

    public interface GetActualResultStage {
        AssertCorrectnessCheckStage getActualResult(NonVoidFunctionDefinition getActualResult);

        Builder from(TestCaseWithActualResultImplementation other);
    }

    public interface AssertCorrectnessCheckStage {
        _FinalStage assertCorrectnessCheck(AssertCorrectnessCheck assertCorrectnessCheck);
    }

    public interface _FinalStage {
        TestCaseWithActualResultImplementation build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements GetActualResultStage, AssertCorrectnessCheckStage, _FinalStage {
        private NonVoidFunctionDefinition getActualResult;

        private AssertCorrectnessCheck assertCorrectnessCheck;

        private Builder() {}

        @Override
        public Builder from(TestCaseWithActualResultImplementation other) {
            getActualResult(other.getGetActualResult());
            assertCorrectnessCheck(other.getAssertCorrectnessCheck());
            return this;
        }

        @Override
        @JsonSetter("getActualResult")
        public AssertCorrectnessCheckStage getActualResult(NonVoidFunctionDefinition getActualResult) {
            this.getActualResult = getActualResult;
            return this;
        }

        @Override
        @JsonSetter("assertCorrectnessCheck")
        public _FinalStage assertCorrectnessCheck(AssertCorrectnessCheck assertCorrectnessCheck) {
            this.assertCorrectnessCheck = assertCorrectnessCheck;
            return this;
        }

        @Override
        public TestCaseWithActualResultImplementation build() {
            return new TestCaseWithActualResultImplementation(getActualResult, assertCorrectnessCheck);
        }
    }
}
