package com.seed.api.resources.inlinedrequests.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.api.resources.types.object.types.ObjectWithOptionalField;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = PostWithObjectBody.Builder.class)
public final class PostWithObjectBody {
    private final String string;

    private final int integer;

    private final ObjectWithOptionalField nestedObject;

    private PostWithObjectBody(String string, int integer, ObjectWithOptionalField nestedObject) {
        this.string = string;
        this.integer = integer;
        this.nestedObject = nestedObject;
    }

    @JsonProperty("string")
    public String getString() {
        return string;
    }

    @JsonProperty("integer")
    public int getInteger() {
        return integer;
    }

    @JsonProperty("NestedObject")
    public ObjectWithOptionalField getNestedObject() {
        return nestedObject;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof PostWithObjectBody && equalTo((PostWithObjectBody) other);
    }

    private boolean equalTo(PostWithObjectBody other) {
        return string.equals(other.string) && integer == other.integer && nestedObject.equals(other.nestedObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.string, this.integer, this.nestedObject);
    }

    @Override
    public String toString() {
        return "PostWithObjectBody{" + "string: " + string + ", integer: " + integer + ", nestedObject: " + nestedObject
                + "}";
    }

    public static StringStage builder() {
        return new Builder();
    }

    public interface StringStage {
        IntegerStage string(String string);

        Builder from(PostWithObjectBody other);
    }

    public interface IntegerStage {
        NestedObjectStage integer(int integer);
    }

    public interface NestedObjectStage {
        _FinalStage nestedObject(ObjectWithOptionalField nestedObject);
    }

    public interface _FinalStage {
        PostWithObjectBody build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements StringStage, IntegerStage, NestedObjectStage, _FinalStage {
        private String string;

        private int integer;

        private ObjectWithOptionalField nestedObject;

        private Builder() {}

        @Override
        public Builder from(PostWithObjectBody other) {
            string(other.getString());
            integer(other.getInteger());
            nestedObject(other.getNestedObject());
            return this;
        }

        @Override
        @JsonSetter("string")
        public IntegerStage string(String string) {
            this.string = string;
            return this;
        }

        @Override
        @JsonSetter("integer")
        public NestedObjectStage integer(int integer) {
            this.integer = integer;
            return this;
        }

        @Override
        @JsonSetter("NestedObject")
        public _FinalStage nestedObject(ObjectWithOptionalField nestedObject) {
            this.nestedObject = nestedObject;
            return this;
        }

        @Override
        public PostWithObjectBody build() {
            return new PostWithObjectBody(string, integer, nestedObject);
        }
    }
}
