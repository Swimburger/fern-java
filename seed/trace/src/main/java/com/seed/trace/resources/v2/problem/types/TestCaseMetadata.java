package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseMetadata.Builder.class)
public final class TestCaseMetadata {
    private final String id;

    private final String name;

    private final boolean hidden;

    private TestCaseMetadata(String id, String name, boolean hidden) {
        this.id = id;
        this.name = name;
        this.hidden = hidden;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("hidden")
    public boolean getHidden() {
        return hidden;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseMetadata && equalTo((TestCaseMetadata) other);
    }

    private boolean equalTo(TestCaseMetadata other) {
        return id.equals(other.id) && name.equals(other.name) && hidden == other.hidden;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.hidden);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static IdStage builder() {
        return new Builder();
    }

    public interface IdStage {
        NameStage id(String id);

        Builder from(TestCaseMetadata other);
    }

    public interface NameStage {
        HiddenStage name(String name);
    }

    public interface HiddenStage {
        _FinalStage hidden(boolean hidden);
    }

    public interface _FinalStage {
        TestCaseMetadata build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements IdStage, NameStage, HiddenStage, _FinalStage {
        private String id;

        private String name;

        private boolean hidden;

        private Builder() {}

        @Override
        public Builder from(TestCaseMetadata other) {
            id(other.getId());
            name(other.getName());
            hidden(other.getHidden());
            return this;
        }

        @Override
        @JsonSetter("id")
        public NameStage id(String id) {
            this.id = id;
            return this;
        }

        @Override
        @JsonSetter("name")
        public HiddenStage name(String name) {
            this.name = name;
            return this;
        }

        @Override
        @JsonSetter("hidden")
        public _FinalStage hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        @Override
        public TestCaseMetadata build() {
            return new TestCaseMetadata(id, name, hidden);
        }
    }
}
