package com.seed.exhaustive.resources.types.union.types;

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

public final class Animal {
    private final Value value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    private Animal(Value value) {
        this.value = value;
    }

    public <T> T visit(Visitor<T> visitor) {
        return value.visit(visitor);
    }

    public static Animal dog(Dog value) {
        return new Animal(new DogValue(value));
    }

    public static Animal cat(Cat value) {
        return new Animal(new CatValue(value));
    }

    public boolean isDog() {
        return value instanceof DogValue;
    }

    public boolean isCat() {
        return value instanceof CatValue;
    }

    public boolean _isUnknown() {
        return value instanceof _UnknownValue;
    }

    public Optional<Dog> getDog() {
        if (isDog()) {
            return Optional.of(((DogValue) value).value);
        }
        return Optional.empty();
    }

    public Optional<Cat> getCat() {
        if (isCat()) {
            return Optional.of(((CatValue) value).value);
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
        T visitDog(Dog dog);

        T visitCat(Cat cat);

        T _visitUnknown(Object unknownType);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "animal", visible = true, defaultImpl = _UnknownValue.class)
    @JsonSubTypes({@JsonSubTypes.Type(DogValue.class), @JsonSubTypes.Type(CatValue.class)})
    @JsonIgnoreProperties(ignoreUnknown = true)
    private interface Value {
        <T> T visit(Visitor<T> visitor);
    }

    @JsonTypeName("dog")
    private static final class DogValue implements Value {
        @JsonUnwrapped
        private Dog value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private DogValue() {}

        private DogValue(Dog value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitDog(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof DogValue && equalTo((DogValue) other);
        }

        private boolean equalTo(DogValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "Animal{" + "value: " + value + "}";
        }
    }

    @JsonTypeName("cat")
    private static final class CatValue implements Value {
        @JsonUnwrapped
        private Cat value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        private CatValue() {}

        private CatValue(Cat value) {
            this.value = value;
        }

        @Override
        public <T> T visit(Visitor<T> visitor) {
            return visitor.visitCat(value);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            return other instanceof CatValue && equalTo((CatValue) other);
        }

        private boolean equalTo(CatValue other) {
            return value.equals(other.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        @Override
        public String toString() {
            return "Animal{" + "value: " + value + "}";
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
            return "Animal{" + "type: " + type + ", value: " + value + "}";
        }
    }
}
