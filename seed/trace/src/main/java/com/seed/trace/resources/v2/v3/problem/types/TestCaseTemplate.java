package com.seed.trace.resources.v2.v3.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = TestCaseTemplate.Builder.class)
public final class TestCaseTemplate {
    private final String templateId;

    private final String name;

    private final TestCaseImplementation implementation;

    private TestCaseTemplate(String templateId, String name, TestCaseImplementation implementation) {
        this.templateId = templateId;
        this.name = name;
        this.implementation = implementation;
    }

    @JsonProperty("templateId")
    public String getTemplateId() {
        return templateId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("implementation")
    public TestCaseImplementation getImplementation() {
        return implementation;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof TestCaseTemplate && equalTo((TestCaseTemplate) other);
    }

    private boolean equalTo(TestCaseTemplate other) {
        return templateId.equals(other.templateId)
                && name.equals(other.name)
                && implementation.equals(other.implementation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.templateId, this.name, this.implementation);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static TemplateIdStage builder() {
        return new Builder();
    }

    public interface TemplateIdStage {
        NameStage templateId(String templateId);

        Builder from(TestCaseTemplate other);
    }

    public interface NameStage {
        ImplementationStage name(String name);
    }

    public interface ImplementationStage {
        _FinalStage implementation(TestCaseImplementation implementation);
    }

    public interface _FinalStage {
        TestCaseTemplate build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements TemplateIdStage, NameStage, ImplementationStage, _FinalStage {
        private String templateId;

        private String name;

        private TestCaseImplementation implementation;

        private Builder() {}

        @Override
        public Builder from(TestCaseTemplate other) {
            templateId(other.getTemplateId());
            name(other.getName());
            implementation(other.getImplementation());
            return this;
        }

        @Override
        @JsonSetter("templateId")
        public NameStage templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        @Override
        @JsonSetter("name")
        public ImplementationStage name(String name) {
            this.name = name;
            return this;
        }

        @Override
        @JsonSetter("implementation")
        public _FinalStage implementation(TestCaseImplementation implementation) {
            this.implementation = implementation;
            return this;
        }

        @Override
        public TestCaseTemplate build() {
            return new TestCaseTemplate(templateId, name, implementation);
        }
    }
}
