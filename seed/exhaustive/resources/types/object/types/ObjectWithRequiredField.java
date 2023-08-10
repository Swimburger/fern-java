package resources.types.object.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonDeserialize(
    builder = ObjectWithRequiredField.Builder.class
)
public final class ObjectWithRequiredField {
  private final String string;

  private ObjectWithRequiredField(String string) {
    this.string = string;
  }

  @JsonProperty("string")
  public String getString() {
    return string;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    return other instanceof ObjectWithRequiredField && equalTo((ObjectWithRequiredField) other);
  }

  private boolean equalTo(ObjectWithRequiredField other) {
    return string.equals(other.string);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.string);
  }

  @Override
  public String toString() {
    return "ObjectWithRequiredField{" + "string: " + string + "}";
  }

  public static StringStage builder() {
    return new Builder();
  }

  public interface StringStage {
    _FinalStage string(String string);

    Builder from(ObjectWithRequiredField other);
  }

  public interface _FinalStage {
    ObjectWithRequiredField build();
  }

  @JsonIgnoreProperties(
      ignoreUnknown = true
  )
  public static final class Builder implements StringStage, _FinalStage {
    private String string;

    private Builder() {
    }

    @Override
    public Builder from(ObjectWithRequiredField other) {
      string(other.getString());
      return this;
    }

    @Override
    @JsonSetter("string")
    public _FinalStage string(String string) {
      this.string = string;
      return this;
    }

    @Override
    public ObjectWithRequiredField build() {
      return new ObjectWithRequiredField(string);
    }
  }
}
