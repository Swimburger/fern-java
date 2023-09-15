/**
 * This file was auto-generated by Fern from our API Definition.
 */
package com.seed.api.resources.ast.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import java.util.Optional;

public final class FieldValue {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private FieldValue(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static FieldValue primitiveValue(PrimitiveValue value) {
        return new FieldValue(new PrimitiveValueValue(value));
    }

    public static FieldValue objectValue(ObjectValue value) {
        return new FieldValue(new ObjectValueValue(value));
    }

    public static FieldValue containerValue(ContainerValue value) {
        return new FieldValue(new ContainerValueValue(value));
    }

    public boolean isPrimitiveValue() {
        return value instanceof PrimitiveValueValue;
    }

    public boolean isObjectValue() {
        return value instanceof ObjectValueValue;
    }

    public boolean isContainerValue() {
        return value instanceof ContainerValueValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<PrimitiveValue> getPrimitiveValue() {
        if (isPrimitiveValue()) {
            return Optional.of(((PrimitiveValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<ObjectValue> getObjectValue() {
        if (isObjectValue()) {
            return Optional.of(((ObjectValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<ContainerValue> getContainerValue() {
        if (isContainerValue()) {
            return Optional.of(((ContainerValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<Object> _getUnknown() {
        if (_isUnknown()) {
            return Optional.of(((_UnknownValue) value).value);
        }
        return Optional.empty();
    }

    @JsonValue
    private Value getValue() {
        return this.value;
    }

    public interface Visitor<T> {
        T visitPrimitiveValue(PrimitiveValue primitiveValue);

        T visitObjectValue(ObjectValue objectValue);

        T visitContainerValue(ContainerValue containerValue);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(PrimitiveValueValue.class),
        @JsonSubTypes.Type(ObjectValueValue.class),
        @JsonSubTypes.Type(ContainerValueValue.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("primitive_value")
    private static final class PrimitiveValueValue implements Value {
        @JsonProperty("value")
        private PrimitiveValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private PrimitiveValueValue(@JsonProperty("value") PrimitiveValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitPrimitiveValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof PrimitiveValueValue && equalTo((PrimitiveValueValue) other);
        }

        private boolean equalTo(PrimitiveValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "FieldValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("object_value")
    private static final class ObjectValueValue implements Value {
        @JsonUnwrapped
        private ObjectValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ObjectValueValue() {}

        private ObjectValueValue(ObjectValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitObjectValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ObjectValueValue && equalTo((ObjectValueValue) other);
        }

        private boolean equalTo(ObjectValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "FieldValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("container_value")
    private static final class ContainerValueValue implements Value {
        @JsonProperty("value")
        private ContainerValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ContainerValueValue(@JsonProperty("value") ContainerValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitContainerValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ContainerValueValue && equalTo((ContainerValueValue) other);
        }

        private boolean equalTo(ContainerValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "FieldValue{" + "value: " + value + "}";
        }
    }

    private static final class _UnknownValue implements Value {
        private String type;

        @JsonValue
        private Object value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private _UnknownValue(@JsonProperty("value") Object value) {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor._visitUnknown(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof _UnknownValue && equalTo((_UnknownValue) other);
        }

        private boolean equalTo(_UnknownValue other) {
            return type.equals(other.type) && value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.value);
        }

        @Override
        public String toString() {
            return "FieldValue{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
