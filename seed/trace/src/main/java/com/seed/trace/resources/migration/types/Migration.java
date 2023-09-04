package com.seed.trace.resources.migration.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = Migration.Builder.class)
public final class Migration {
    private final String name;

    private final MigrationStatus status;

    private Migration(String name, MigrationStatus status) {
        this.name = name;
        this.status = status;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("status")
    public MigrationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof Migration && equalTo((Migration) other);
    }

    private boolean equalTo(Migration other) {
        return name.equals(other.name) && status.equals(other.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.status);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static NameStage builder() {
        return new Builder();
    }

    public interface NameStage {
        StatusStage name(String name);

        Builder from(Migration other);
    }

    public interface StatusStage {
        _FinalStage status(MigrationStatus status);
    }

    public interface _FinalStage {
        Migration build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements NameStage, StatusStage, _FinalStage {
        private String name;

        private MigrationStatus status;

        private Builder() {}

        @Override
        public Builder from(Migration other) {
            name(other.getName());
            status(other.getStatus());
            return this;
        }

        @Override
        @JsonSetter("name")
        public StatusStage name(String name) {
            this.name = name;
            return this;
        }

        @Override
        @JsonSetter("status")
        public _FinalStage status(MigrationStatus status) {
            this.status = status;
            return this;
        }

        @Override
        public Migration build() {
            return new Migration(name, status);
        }
    }
}
