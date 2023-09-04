package com.seed.trace.resources.v2.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = GetGeneratedTestCaseTemplateFileRequest.Builder.class)
public final class GetGeneratedTestCaseTemplateFileRequest {
    private final TestCaseTemplate template;

    private GetGeneratedTestCaseTemplateFileRequest(TestCaseTemplate template) {
        this.template = template;
    }

    @JsonProperty("template")
    public TestCaseTemplate getTemplate() {
        return template;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof GetGeneratedTestCaseTemplateFileRequest
                && equalTo((GetGeneratedTestCaseTemplateFileRequest) other);
    }

    private boolean equalTo(GetGeneratedTestCaseTemplateFileRequest other) {
        return template.equals(other.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.template);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static TemplateStage builder() {
        return new Builder();
    }

    public interface TemplateStage {
        _FinalStage template(TestCaseTemplate template);

        Builder from(GetGeneratedTestCaseTemplateFileRequest other);
    }

    public interface _FinalStage {
        GetGeneratedTestCaseTemplateFileRequest build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements TemplateStage, _FinalStage {
        private TestCaseTemplate template;

        private Builder() {}

        @Override
        public Builder from(GetGeneratedTestCaseTemplateFileRequest other) {
            template(other.getTemplate());
            return this;
        }

        @Override
        @JsonSetter("template")
        public _FinalStage template(TestCaseTemplate template) {
            this.template = template;
            return this;
        }

        @Override
        public GetGeneratedTestCaseTemplateFileRequest build() {
            return new GetGeneratedTestCaseTemplateFileRequest(template);
        }
    }
}
