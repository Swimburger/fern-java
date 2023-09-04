package com.seed.trace.resources.commons.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class VariableValue {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private VariableValue(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static VariableValue integerValue(int value) {
        return new VariableValue(new IntegerValueValue(value));
    }

    public static VariableValue booleanValue(boolean value) {
        return new VariableValue(new BooleanValueValue(value));
    }

    public static VariableValue doubleValue(double value) {
        return new VariableValue(new DoubleValueValue(value));
    }

    public static VariableValue stringValue(String value) {
        return new VariableValue(new StringValueValue(value));
    }

    public static VariableValue charValue(String value) {
        return new VariableValue(new CharValueValue(value));
    }

    public static VariableValue mapValue(MapValue value) {
        return new VariableValue(new MapValueValue(value));
    }

    public static VariableValue listValue(List<VariableValue> value) {
        return new VariableValue(new ListValueValue(value));
    }

    public static VariableValue binaryTreeValue(BinaryTreeValue value) {
        return new VariableValue(new BinaryTreeValueValue(value));
    }

    public static VariableValue singlyLinkedListValue(SinglyLinkedListValue value) {
        return new VariableValue(new SinglyLinkedListValueValue(value));
    }

    public static VariableValue doublyLinkedListValue(DoublyLinkedListValue value) {
        return new VariableValue(new DoublyLinkedListValueValue(value));
    }

    public static VariableValue nullValue() {
        return new VariableValue(new NullValueValue());
    }

    public boolean isIntegerValue() {
        return value instanceof IntegerValueValue;
    }

    public boolean isBooleanValue() {
        return value instanceof BooleanValueValue;
    }

    public boolean isDoubleValue() {
        return value instanceof DoubleValueValue;
    }

    public boolean isStringValue() {
        return value instanceof StringValueValue;
    }

    public boolean isCharValue() {
        return value instanceof CharValueValue;
    }

    public boolean isMapValue() {
        return value instanceof MapValueValue;
    }

    public boolean isListValue() {
        return value instanceof ListValueValue;
    }

    public boolean isBinaryTreeValue() {
        return value instanceof BinaryTreeValueValue;
    }

    public boolean isSinglyLinkedListValue() {
        return value instanceof SinglyLinkedListValueValue;
    }

    public boolean isDoublyLinkedListValue() {
        return value instanceof DoublyLinkedListValueValue;
    }

    public boolean isNullValue() {
        return value instanceof NullValueValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<Integer> getIntegerValue() {
        if (isIntegerValue()) {
            return Optional.of(((IntegerValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<Boolean> getBooleanValue() {
        if (isBooleanValue()) {
            return Optional.of(((BooleanValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<Double> getDoubleValue() {
        if (isDoubleValue()) {
            return Optional.of(((DoubleValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<String> getStringValue() {
        if (isStringValue()) {
            return Optional.of(((StringValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<String> getCharValue() {
        if (isCharValue()) {
            return Optional.of(((CharValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<MapValue> getMapValue() {
        if (isMapValue()) {
            return Optional.of(((MapValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<List<VariableValue>> getListValue() {
        if (isListValue()) {
            return Optional.of(((ListValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<BinaryTreeValue> getBinaryTreeValue() {
        if (isBinaryTreeValue()) {
            return Optional.of(((BinaryTreeValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<SinglyLinkedListValue> getSinglyLinkedListValue() {
        if (isSinglyLinkedListValue()) {
            return Optional.of(((SinglyLinkedListValueValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<DoublyLinkedListValue> getDoublyLinkedListValue() {
        if (isDoublyLinkedListValue()) {
            return Optional.of(((DoublyLinkedListValueValue) value).value);
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
        T visitIntegerValue(int integerValue);

        T visitBooleanValue(boolean booleanValue);

        T visitDoubleValue(double doubleValue);

        T visitStringValue(String stringValue);

        T visitCharValue(String charValue);

        T visitMapValue(MapValue mapValue);

        T visitListValue(List<VariableValue> listValue);

        T visitBinaryTreeValue(BinaryTreeValue binaryTreeValue);

        T visitSinglyLinkedListValue(SinglyLinkedListValue singlyLinkedListValue);

        T visitDoublyLinkedListValue(DoublyLinkedListValue doublyLinkedListValue);

        T visitNullValue();

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(IntegerValueValue.class),
        @JsonSubTypes.Type(BooleanValueValue.class),
        @JsonSubTypes.Type(DoubleValueValue.class),
        @JsonSubTypes.Type(StringValueValue.class),
        @JsonSubTypes.Type(CharValueValue.class),
        @JsonSubTypes.Type(MapValueValue.class),
        @JsonSubTypes.Type(ListValueValue.class),
        @JsonSubTypes.Type(BinaryTreeValueValue.class),
        @JsonSubTypes.Type(SinglyLinkedListValueValue.class),
        @JsonSubTypes.Type(DoublyLinkedListValueValue.class),
        @JsonSubTypes.Type(NullValueValue.class)
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("integerValue")
    private static final class IntegerValueValue implements Value {
        @JsonProperty("value")
        private int value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private IntegerValueValue(@JsonProperty("value") int value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitIntegerValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof IntegerValueValue && equalTo((IntegerValueValue) other);
        }

        private boolean equalTo(IntegerValueValue other) {
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("booleanValue")
    private static final class BooleanValueValue implements Value {
        @JsonProperty("value")
        private boolean value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private BooleanValueValue(@JsonProperty("value") boolean value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitBooleanValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof BooleanValueValue && equalTo((BooleanValueValue) other);
        }

        private boolean equalTo(BooleanValueValue other) {
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("doubleValue")
    private static final class DoubleValueValue implements Value {
        @JsonProperty("value")
        private double value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private DoubleValueValue(@JsonProperty("value") double value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitDoubleValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof DoubleValueValue && equalTo((DoubleValueValue) other);
        }

        private boolean equalTo(DoubleValueValue other) {
            return value == other.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("stringValue")
    private static final class StringValueValue implements Value {
        @JsonProperty("value")
        private String value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private StringValueValue(@JsonProperty("value") String value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitStringValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof StringValueValue && equalTo((StringValueValue) other);
        }

        private boolean equalTo(StringValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("charValue")
    private static final class CharValueValue implements Value {
        @JsonProperty("value")
        private String value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private CharValueValue(@JsonProperty("value") String value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitCharValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof CharValueValue && equalTo((CharValueValue) other);
        }

        private boolean equalTo(CharValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("mapValue")
    private static final class MapValueValue implements Value {
        @JsonUnwrapped
        private MapValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private MapValueValue() {}

        private MapValueValue(MapValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitMapValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof MapValueValue && equalTo((MapValueValue) other);
        }

        private boolean equalTo(MapValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("listValue")
    private static final class ListValueValue implements Value {
        @JsonProperty("value")
        private List<VariableValue> value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private ListValueValue(@JsonProperty("value") List<VariableValue> value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitListValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof ListValueValue && equalTo((ListValueValue) other);
        }

        private boolean equalTo(ListValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("binaryTreeValue")
    private static final class BinaryTreeValueValue implements Value {
        @JsonUnwrapped
        private BinaryTreeValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private BinaryTreeValueValue() {}

        private BinaryTreeValueValue(BinaryTreeValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitBinaryTreeValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof BinaryTreeValueValue && equalTo((BinaryTreeValueValue) other);
        }

        private boolean equalTo(BinaryTreeValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("singlyLinkedListValue")
    private static final class SinglyLinkedListValueValue implements Value {
        @JsonUnwrapped
        private SinglyLinkedListValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private SinglyLinkedListValueValue() {}

        private SinglyLinkedListValueValue(SinglyLinkedListValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitSinglyLinkedListValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof SinglyLinkedListValueValue && equalTo((SinglyLinkedListValueValue) other);
        }

        private boolean equalTo(SinglyLinkedListValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("doublyLinkedListValue")
    private static final class DoublyLinkedListValueValue implements Value {
        @JsonUnwrapped
        private DoublyLinkedListValue value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private DoublyLinkedListValueValue() {}

        private DoublyLinkedListValueValue(DoublyLinkedListValue value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitDoublyLinkedListValue(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof DoublyLinkedListValueValue && equalTo((DoublyLinkedListValueValue) other);
        }

        private boolean equalTo(DoublyLinkedListValueValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "VariableValue{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("nullValue")
    private static final class NullValueValue implements Value {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private NullValueValue() {}

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitNullValue();
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof NullValueValue;
        }

        @Override
        public String toString() {
            return "VariableValue{" + "}";
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
            return "VariableValue{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
