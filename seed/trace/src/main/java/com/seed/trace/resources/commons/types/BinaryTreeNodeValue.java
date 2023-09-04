package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = BinaryTreeNodeValue.Builder.class)
public final class BinaryTreeNodeValue {
    private final String nodeId;

    private final double val;

    private final Optional<String> right;

    private final Optional<String> left;

    private BinaryTreeNodeValue(String nodeId, double val, Optional<String> right, Optional<String> left) {
        this.nodeId = nodeId;
        this.val = val;
        this.right = right;
        this.left = left;
    }

    @JsonProperty("nodeId")
    public String getNodeId() {
        return nodeId;
    }

    @JsonProperty("val")
    public double getVal() {
        return val;
    }

    @JsonProperty("right")
    public Optional<String> getRight() {
        return right;
    }

    @JsonProperty("left")
    public Optional<String> getLeft() {
        return left;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof BinaryTreeNodeValue && equalTo((BinaryTreeNodeValue) other);
    }

    private boolean equalTo(BinaryTreeNodeValue other) {
        return nodeId.equals(other.nodeId) && val == other.val && right.equals(other.right) && left.equals(other.left);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nodeId, this.val, this.right, this.left);
    }

    @Override
    public String toString() {
        return ObjectMappers.stringify(this);
    }

    public static NodeIdStage builder() {
        return new Builder();
    }

    public interface NodeIdStage {
        ValStage nodeId(String nodeId);

        Builder from(BinaryTreeNodeValue other);
    }

    public interface ValStage {
        _FinalStage val(double val);
    }

    public interface _FinalStage {
        BinaryTreeNodeValue build();

        _FinalStage right(Optional<String> right);

        _FinalStage right(String right);

        _FinalStage left(Optional<String> left);

        _FinalStage left(String left);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder implements NodeIdStage, ValStage, _FinalStage {
        private String nodeId;

        private double val;

        private Optional<String> left = Optional.empty();

        private Optional<String> right = Optional.empty();

        private Builder() {}

        @Override
        public Builder from(BinaryTreeNodeValue other) {
            nodeId(other.getNodeId());
            val(other.getVal());
            right(other.getRight());
            left(other.getLeft());
            return this;
        }

        @Override
        @JsonSetter("nodeId")
        public ValStage nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        @Override
        @JsonSetter("val")
        public _FinalStage val(double val) {
            this.val = val;
            return this;
        }

        @Override
        public _FinalStage left(String left) {
            this.left = Optional.of(left);
            return this;
        }

        @Override
        @JsonSetter(value = "left", nulls = Nulls.SKIP)
        public _FinalStage left(Optional<String> left) {
            this.left = left;
            return this;
        }

        @Override
        public _FinalStage right(String right) {
            this.right = Optional.of(right);
            return this;
        }

        @Override
        @JsonSetter(value = "right", nulls = Nulls.SKIP)
        public _FinalStage right(Optional<String> right) {
            this.right = right;
            return this;
        }

        @Override
        public BinaryTreeNodeValue build() {
            return new BinaryTreeNodeValue(nodeId, val, right, left);
        }
    }
}
