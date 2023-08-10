package resources.types.object.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(
    builder = NestedObjectWithOptionalField.Builder.class
)
public final class NestedObjectWithOptionalField {
  private final Optional<String> string;

  private final Optional<ObjectWithOptionalField> nestedObject;

  private NestedObjectWithOptionalField(Optional<String> string,
      Optional<ObjectWithOptionalField> nestedObject) {
    this.string = string;
    this.nestedObject = nestedObject;
  }

  @JsonProperty("string")
  public Optional<String> getString() {
    return string;
  }

  @JsonProperty("NestedObject")
  public Optional<ObjectWithOptionalField> getNestedObject() {
    return nestedObject;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    return other instanceof NestedObjectWithOptionalField && equalTo((NestedObjectWithOptionalField) other);
  }

  private boolean equalTo(NestedObjectWithOptionalField other) {
    return string.equals(other.string) && nestedObject.equals(other.nestedObject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.string, this.nestedObject);
  }

  @Override
  public String toString() {
    return "NestedObjectWithOptionalField{" + "string: " + string + ", nestedObject: " + nestedObject + "}";
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonIgnoreProperties(
      ignoreUnknown = true
  )
  public static final class Builder {
    private Optional<String> string = Optional.empty();

    private Optional<ObjectWithOptionalField> nestedObject = Optional.empty();

    private Builder() {
    }

    public Builder from(NestedObjectWithOptionalField other) {
      string(other.getString());
      nestedObject(other.getNestedObject());
      return this;
    }

    @JsonSetter(
        value = "string",
        nulls = Nulls.SKIP
    )
    public Builder string(Optional<String> string) {
      this.string = string;
      return this;
    }

    public Builder string(String string) {
      this.string = Optional.of(string);
      return this;
    }

    @JsonSetter(
        value = "NestedObject",
        nulls = Nulls.SKIP
    )
    public Builder nestedObject(Optional<ObjectWithOptionalField> nestedObject) {
      this.nestedObject = nestedObject;
      return this;
    }

    public Builder nestedObject(ObjectWithOptionalField nestedObject) {
      this.nestedObject = Optional.of(nestedObject);
      return this;
    }

    public NestedObjectWithOptionalField build() {
      return new NestedObjectWithOptionalField(string, nestedObject);
    }
  }
}
