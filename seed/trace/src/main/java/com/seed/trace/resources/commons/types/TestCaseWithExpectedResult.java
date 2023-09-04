package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseWithExpectedResult.Builder.class)
public final class TestCaseWithExpectedResult {
    private final TestCase testCase;

    private final VariableValue expectedResult;

    private TestCaseWithExpectedResult(TestCase testCase, VariableValue expectedResult) {
        this.testCase = testCase;
        this.expectedResult = expectedResult;
    }

    @JsonProperty("testCase")
    public TestCase getTestCase() {
        return testCase;
    }

    @JsonProperty("expectedResult")
    public VariableValue getExpectedResult() {
        return expectedResult;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseWithExpectedResult && equalTo((TestCaseWithExpectedResult) other);
    }

    private boolean equalTo(TestCaseWithExpectedResult other) {
        return testCase.equals(other.testCase) && expectedResult.equals(other.expectedResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.testCase, this.expectedResult);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static TestCaseStage builder() {
        return new Builder();
    }

    public interface TestCaseStage {
        ExpectedResultStage testCase(TestCase testCase);

        Builder from(TestCaseWithExpectedResult other);
    }

    public interface ExpectedResultStage {
        _FinalStage expectedResult(VariableValue expectedResult);
    }

    public interface _FinalStage {
        TestCaseWithExpectedResult build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements TestCaseStage, ExpectedResultStage, _FinalStage {
        private TestCase testCase;

        private VariableValue expectedResult;

        private Builder() {}

        @Override
        public Builder from(TestCaseWithExpectedResult other) {
            testCase(other.getTestCase());
            expectedResult(other.getExpectedResult());
            return this;
        }

        @Override
        @JsonSetter("testCase")
        public ExpectedResultStage testCase(TestCase testCase) {
            this.testCase = testCase;
            return this;
        }

        @Override
        @JsonSetter("expectedResult")
        public _FinalStage expectedResult(VariableValue expectedResult) {
            this.expectedResult = expectedResult;
            return this;
        }

        @Override
        public TestCaseWithExpectedResult build() {
            return new TestCaseWithExpectedResult(testCase, expectedResult);
        }
    }
}
