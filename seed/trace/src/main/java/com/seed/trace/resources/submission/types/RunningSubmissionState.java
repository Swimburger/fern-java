package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RunningSubmissionState {
    QUEUEING_SUBMISSION("QUEUEING_SUBMISSION"),

    KILLING_HISTORICAL_SUBMISSIONS("KILLING_HISTORICAL_SUBMISSIONS"),

    WRITING_SUBMISSION_TO_FILE("WRITING_SUBMISSION_TO_FILE"),

    COMPILING_SUBMISSION("COMPILING_SUBMISSION"),

    RUNNING_SUBMISSION("RUNNING_SUBMISSION");

    private final String value;

    RunningSubmissionState(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }
}
