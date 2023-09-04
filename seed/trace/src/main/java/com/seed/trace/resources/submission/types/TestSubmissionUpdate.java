package com.seed.trace.resources.submission.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.time.OffsetDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestSubmissionUpdate.Builder.class)
public final class TestSubmissionUpdate {
    private final OffsetDateTime updateTime;

    private final TestSubmissionUpdateInfo updateInfo;

    private TestSubmissionUpdate(OffsetDateTime updateTime, TestSubmissionUpdateInfo updateInfo) {
        this.updateTime = updateTime;
        this.updateInfo = updateInfo;
    }

    @JsonProperty("updateTime")
    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }

    @JsonProperty("updateInfo")
    public TestSubmissionUpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestSubmissionUpdate && equalTo((TestSubmissionUpdate) other);
    }

    private boolean equalTo(TestSubmissionUpdate other) {
        return updateTime.equals(other.updateTime) && updateInfo.equals(other.updateInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.updateTime, this.updateInfo);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static UpdateTimeStage builder() {
        return new Builder();
    }

    public interface UpdateTimeStage {
        UpdateInfoStage updateTime(OffsetDateTime updateTime);

        Builder from(TestSubmissionUpdate other);
    }

    public interface UpdateInfoStage {
        _FinalStage updateInfo(TestSubmissionUpdateInfo updateInfo);
    }

    public interface _FinalStage {
        TestSubmissionUpdate build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements UpdateTimeStage, UpdateInfoStage, _FinalStage {
        private OffsetDateTime updateTime;

        private TestSubmissionUpdateInfo updateInfo;

        private Builder() {}

        @Override
        public Builder from(TestSubmissionUpdate other) {
            updateTime(other.getUpdateTime());
            updateInfo(other.getUpdateInfo());
            return this;
        }

        @Override
        @JsonSetter("updateTime")
        public UpdateInfoStage updateTime(OffsetDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        @Override
        @JsonSetter("updateInfo")
        public _FinalStage updateInfo(TestSubmissionUpdateInfo updateInfo) {
            this.updateInfo = updateInfo;
            return this;
        }

        @Override
        public TestSubmissionUpdate build() {
            return new TestSubmissionUpdate(updateTime, updateInfo);
        }
    }
}
