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
@JsonDeserialize(builder = SinglyLinkedListValue.Builder.class)
public final class SinglyLinkedListValue {
    private final Optional<String> head;

    private final Map<String, SinglyLinkedListNodeValue> nodes;

    private SinglyLinkedListValue(Optional<String> head, Map<String, SinglyLinkedListNodeValue> nodes) {
        this.head = head;
        this.nodes = nodes;
    }

    @JsonProperty("head")
    public Optional<String> getHead() {
        return head;
    }

    @JsonProperty("nodes")
    public Map<String, SinglyLinkedListNodeValue> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        return other instanceof SinglyLinkedListValue && equalTo((SinglyLinkedListValue) other);
    }

    private boolean equalTo(SinglyLinkedListValue other) {
        return head.equals(other.head) && nodes.equals(other.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.head, this.nodes);
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
        private Optional<String> head = Optional.empty();

        private Map<String, SinglyLinkedListNodeValue> nodes = new LinkedHashMap<>();

        private Builder() {}

        public Builder from(SinglyLinkedListValue other) {
            head(other.getHead());
            nodes(other.getNodes());
            return this;
        }

        @JsonSetter(value = "head", nulls = Nulls.SKIP)
        public Builder head(Optional<String> head) {
            this.head = head;
            return this;
        }

        public Builder head(String head) {
            this.head = Optional.of(head);
            return this;
        }

        @JsonSetter(value = "nodes", nulls = Nulls.SKIP)
        public Builder nodes(Map<String, SinglyLinkedListNodeValue> nodes) {
            this.nodes.clear();
            this.nodes.putAll(nodes);
            return this;
        }

        public Builder putAllNodes(Map<String, SinglyLinkedListNodeValue> nodes) {
            this.nodes.putAll(nodes);
            return this;
        }

        public Builder nodes(String key, SinglyLinkedListNodeValue value) {
            this.nodes.put(key, value);
            return this;
        }

        public SinglyLinkedListValue build() {
            return new SinglyLinkedListValue(head, nodes);
        }
    }
}
