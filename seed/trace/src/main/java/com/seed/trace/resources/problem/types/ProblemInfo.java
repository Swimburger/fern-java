package com.seed.trace.resources.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.resources.commons.types.Language;
import com.seed.trace.resources.commons.types.TestCaseWithExpectedResult;
import com.seed.trace.resources.commons.types.VariableType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = ProblemInfo.Builder.class)
public final class ProblemInfo {
    private final String problemId;

    private final ProblemDescription problemDescription;

    private final String problemName;

    private final int problemVersion;

    private final Map<Language, ProblemFiles> files;

    private final List<VariableTypeAndName> inputParams;

    private final VariableType outputType;

    private final List<TestCaseWithExpectedResult> testcases;

    private final String methodName;

    private final boolean supportsCustomTestCases;

    private ProblemInfo(
            String problemId,
            ProblemDescription problemDescription,
            String problemName,
            int problemVersion,
            Map<Language, ProblemFiles> files,
            List<VariableTypeAndName> inputParams,
            VariableType outputType,
            List<TestCaseWithExpectedResult> testcases,
            String methodName,
            boolean supportsCustomTestCases) {
        this.problemId = problemId;
        this.problemDescription = problemDescription;
        this.problemName = problemName;
        this.problemVersion = problemVersion;
        this.files = files;
        this.inputParams = inputParams;
        this.outputType = outputType;
        this.testcases = testcases;
        this.methodName = methodName;
        this.supportsCustomTestCases = supportsCustomTestCases;
    }

    @JsonProperty("problemId")
    public String getProblemId() {
        return problemId;
    }

    @JsonProperty("problemDescription")
    public ProblemDescription getProblemDescription() {
        return problemDescription;
    }

    @JsonProperty("problemName")
    public String getProblemName() {
        return problemName;
    }

    @JsonProperty("problemVersion")
    public int getProblemVersion() {
        return problemVersion;
    }

    @JsonProperty("files")
    public Map<Language, ProblemFiles> getFiles() {
        return files;
    }

    @JsonProperty("inputParams")
    public List<VariableTypeAndName> getInputParams() {
        return inputParams;
    }

    @JsonProperty("outputType")
    public VariableType getOutputType() {
        return outputType;
    }

    @JsonProperty("testcases")
    public List<TestCaseWithExpectedResult> getTestcases() {
        return testcases;
    }

    @JsonProperty("methodName")
    public String getMethodName() {
        return methodName;
    }

    @JsonProperty("supportsCustomTestCases")
    public boolean getSupportsCustomTestCases() {
        return supportsCustomTestCases;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ProblemInfo && equalTo((ProblemInfo) other);
    }

    private boolean equalTo(ProblemInfo other) {
        return problemId.equals(other.problemId)
                && problemDescription.equals(other.problemDescription)
                && problemName.equals(other.problemName)
                && problemVersion == other.problemVersion
                && files.equals(other.files)
                && inputParams.equals(other.inputParams)
                && outputType.equals(other.outputType)
                && testcases.equals(other.testcases)
                && methodName.equals(other.methodName)
                && supportsCustomTestCases == other.supportsCustomTestCases;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.problemId,
                this.problemDescription,
                this.problemName,
                this.problemVersion,
                this.files,
                this.inputParams,
                this.outputType,
                this.testcases,
                this.methodName,
                this.supportsCustomTestCases);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static ProblemIdStage builder() {
        return new Builder();
    }

    public interface ProblemIdStage {
        ProblemDescriptionStage problemId(String problemId);

        Builder from(ProblemInfo other);
    }

    public interface ProblemDescriptionStage {
        ProblemNameStage problemDescription(ProblemDescription problemDescription);
    }

    public interface ProblemNameStage {
        ProblemVersionStage problemName(String problemName);
    }

    public interface ProblemVersionStage {
        OutputTypeStage problemVersion(int problemVersion);
    }

    public interface OutputTypeStage {
        MethodNameStage outputType(VariableType outputType);
    }

    public interface MethodNameStage {
        SupportsCustomTestCasesStage methodName(String methodName);
    }

    public interface SupportsCustomTestCasesStage {
        _FinalStage supportsCustomTestCases(boolean supportsCustomTestCases);
    }

    public interface _FinalStage {
        ProblemInfo build();

        _FinalStage files(Map<Language, ProblemFiles> files);

        _FinalStage putAllFiles(Map<Language, ProblemFiles> files);

        _FinalStage files(Language key, ProblemFiles value);

        _FinalStage inputParams(List<VariableTypeAndName> inputParams);

        _FinalStage addInputParams(VariableTypeAndName inputParams);

        _FinalStage addAllInputParams(List<VariableTypeAndName> inputParams);

        _FinalStage testcases(List<TestCaseWithExpectedResult> testcases);

        _FinalStage addTestcases(TestCaseWithExpectedResult testcases);

        _FinalStage addAllTestcases(List<TestCaseWithExpectedResult> testcases);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder
            implements ProblemIdStage,
                    ProblemDescriptionStage,
                    ProblemNameStage,
                    ProblemVersionStage,
                    OutputTypeStage,
                    MethodNameStage,
                    SupportsCustomTestCasesStage,
                    _FinalStage {
        private String problemId;

        private ProblemDescription problemDescription;

        private String problemName;

        private int problemVersion;

        private VariableType outputType;

        private String methodName;

        private boolean supportsCustomTestCases;

        private List<TestCaseWithExpectedResult> testcases = new ArrayList<>();

        private List<VariableTypeAndName> inputParams = new ArrayList<>();

        private Map<Language, ProblemFiles> files = new LinkedHashMap<>();

        private Builder() {}

        @Override
        public Builder from(ProblemInfo other) {
            problemId(other.getProblemId());
            problemDescription(other.getProblemDescription());
            problemName(other.getProblemName());
            problemVersion(other.getProblemVersion());
            files(other.getFiles());
            inputParams(other.getInputParams());
            outputType(other.getOutputType());
            testcases(other.getTestcases());
            methodName(other.getMethodName());
            supportsCustomTestCases(other.getSupportsCustomTestCases());
            return this;
        }

        @Override
        @JsonSetter("problemId")
        public ProblemDescriptionStage problemId(String problemId) {
            this.problemId = problemId;
            return this;
        }

        @Override
        @JsonSetter("problemDescription")
        public ProblemNameStage problemDescription(ProblemDescription problemDescription) {
            this.problemDescription = problemDescription;
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
        public OutputTypeStage problemVersion(int problemVersion) {
            this.problemVersion = problemVersion;
            return this;
        }

        @Override
        @JsonSetter("outputType")
        public MethodNameStage outputType(VariableType outputType) {
            this.outputType = outputType;
            return this;
        }

        @Override
        @JsonSetter("methodName")
        public SupportsCustomTestCasesStage methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        @Override
        @JsonSetter("supportsCustomTestCases")
        public _FinalStage supportsCustomTestCases(boolean supportsCustomTestCases) {
            this.supportsCustomTestCases = supportsCustomTestCases;
            return this;
        }

        @Override
        public _FinalStage addAllTestcases(List<TestCaseWithExpectedResult> testcases) {
            this.testcases.addAll(testcases);
            return this;
        }

        @Override
        public _FinalStage addTestcases(TestCaseWithExpectedResult testcases) {
            this.testcases.add(testcases);
            return this;
        }

        @Override
        @JsonSetter(value = "testcases", nulls = Nulls.SKIP)
        public _FinalStage testcases(List<TestCaseWithExpectedResult> testcases) {
            this.testcases.clear();
            this.testcases.addAll(testcases);
            return this;
        }

        @Override
        public _FinalStage addAllInputParams(List<VariableTypeAndName> inputParams) {
            this.inputParams.addAll(inputParams);
            return this;
        }

        @Override
        public _FinalStage addInputParams(VariableTypeAndName inputParams) {
            this.inputParams.add(inputParams);
            return this;
        }

        @Override
        @JsonSetter(value = "inputParams", nulls = Nulls.SKIP)
        public _FinalStage inputParams(List<VariableTypeAndName> inputParams) {
            this.inputParams.clear();
            this.inputParams.addAll(inputParams);
            return this;
        }

        @Override
        public _FinalStage files(Language key, ProblemFiles value) {
            this.files.put(key, value);
            return this;
        }

        @Override
        public _FinalStage putAllFiles(Map<Language, ProblemFiles> files) {
            this.files.putAll(files);
            return this;
        }

        @Override
        @JsonSetter(value = "files", nulls = Nulls.SKIP)
        public _FinalStage files(Map<Language, ProblemFiles> files) {
            this.files.clear();
            this.files.putAll(files);
            return this;
        }

        @Override
        public ProblemInfo build() {
            return new ProblemInfo(
                    problemId,
                    problemDescription,
                    problemName,
                    problemVersion,
                    files,
                    inputParams,
                    outputType,
                    testcases,
                    methodName,
                    supportsCustomTestCases);
        }
    }
}
