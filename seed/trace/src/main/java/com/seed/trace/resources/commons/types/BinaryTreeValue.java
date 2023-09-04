package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seed.trace.core.ObjectMappers;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(builder = BinaryTreeValue.Builder.class)
public final class BinaryTreeValue {
    private final Optional<String> root;

    private final Map<String, BinaryTreeNodeValue> nodes;

    private BinaryTreeValue(Optional<String> root, Map<String, BinaryTreeNodeValue> nodes) {
        this.root = root;
        this.nodes = nodes;
    }

    @JsonProperty("root")
    public Optional<String> getRoot() {
        return root;
    }

    @JsonProperty("nodes")
    public Map<String, BinaryTreeNodeValue> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof BinaryTreeValue && equalTo((BinaryTreeValue) other);
    }

    private boolean equalTo(BinaryTreeValue other) {
        return root.equals(other.root) && nodes.equals(other.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.root, this.nodes);
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
        private Optional<String> root = Optional.empty();

        private Map<String, BinaryTreeNodeValue> nodes = new LinkedHashMap<>();

        private Builder() {}

        public Builder from(BinaryTreeValue other) {
            root(other.getRoot());
            nodes(other.getNodes());
            return this;
        }

        @JsonSetter(value = "root", nulls = Nulls.SKIP)
        public Builder root(Optional<String> root) {
            this.root = root;
            return this;
        }

        public Builder root(String root) {
            this.root = Optional.of(root);
            return this;
        }

        @JsonSetter(value = "nodes", nulls = Nulls.SKIP)
        public Builder nodes(Map<String, BinaryTreeNodeValue> nodes) {
            this.nodes.clear();
            this.nodes.putAll(nodes);
            return this;
        }

        public Builder putAllNodes(Map<String, BinaryTreeNodeValue> nodes) {
            this.nodes.putAll(nodes);
            return this;
        }

        public Builder nodes(String key, BinaryTreeNodeValue value) {
            this.nodes.put(key, value);
            return this;
        }

        public BinaryTreeValue build() {
            return new BinaryTreeValue(root, nodes);
        }
    }
}
