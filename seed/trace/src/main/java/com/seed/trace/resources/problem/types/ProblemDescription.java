package com.seed.trace.resources.problem.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = ProblemDescription.Builder.class)
public final class ProblemDescription {
    private final List<ProblemDescriptionBoard> boards;

    private ProblemDescription(List<ProblemDescriptionBoard> boards) {
        this.boards = boards;
    }

    @JsonProperty("boards")
    public List<ProblemDescriptionBoard> getBoards() {
        return boards;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof ProblemDescription && equalTo((ProblemDescription) other);
    }

    private boolean equalTo(ProblemDescription other) {
        return boards.equals(other.boards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.boards);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private List<ProblemDescriptionBoard> boards = new ArrayList<>();

        private Builder() {}

        public Builder from(ProblemDescription other) {
            boards(other.getBoards());
            return this;
        }

        @JsonSetter(value = "boards", nulls = Nulls.SKIP)
        public Builder boards(List<ProblemDescriptionBoard> boards) {
            this.boards.clear();
            this.boards.addAll(boards);
            return this;
        }

        public Builder addBoards(ProblemDescriptionBoard boards) {
            this.boards.add(boards);
            return this;
        }

        public Builder addAllBoards(List<ProblemDescriptionBoard> boards) {
            this.boards.addAll(boards);
            return this;
        }

        public ProblemDescription build() {
            return new ProblemDescription(boards);
        }
    }
}
