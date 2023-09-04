package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = GradedTestCaseUpdate.Builder.class)
public final class GradedTestCaseUpdate {
    private final String testCaseId;

    private final TestCaseGrade grade;

    private GradedTestCaseUpdate(String testCaseId, TestCaseGrade grade) {
        this.testCaseId = testCaseId;
        this.grade = grade;
    }

    @JsonProperty("testCaseId")
    public String getTestCaseId() {
        return testCaseId;
    }

    @JsonProperty("grade")
    public TestCaseGrade getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GradedTestCaseUpdate && equalTo((GradedTestCaseUpdate) other);
    }

    private boolean equalTo(GradedTestCaseUpdate other) {
        return testCaseId.equals(other.testCaseId) && grade.equals(other.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.testCaseId, this.grade);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static TestCaseIdStage builder() {
        return new Builder();
    }

    public interface TestCaseIdStage {
        GradeStage testCaseId(String testCaseId);

        Builder from(GradedTestCaseUpdate other);
    }

    public interface GradeStage {
        _FinalStage grade(TestCaseGrade grade);
    }

    public interface _FinalStage {
        GradedTestCaseUpdate build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements TestCaseIdStage, GradeStage, _FinalStage {
        private String testCaseId;

        private TestCaseGrade grade;

        private Builder() {}

        @Override
        public Builder from(GradedTestCaseUpdate other) {
            testCaseId(other.getTestCaseId());
            grade(other.getGrade());
            return this;
        }

        @Override
        @JsonSetter("testCaseId")
        public GradeStage testCaseId(String testCaseId) {
            this.testCaseId = testCaseId;
            return this;
        }

        @Override
        @JsonSetter("grade")
        public _FinalStage grade(TestCaseGrade grade) {
            this.grade = grade;
            return this;
        }

        @Override
        public GradedTestCaseUpdate build() {
            return new GradedTestCaseUpdate(testCaseId, grade);
        }
    }
}
