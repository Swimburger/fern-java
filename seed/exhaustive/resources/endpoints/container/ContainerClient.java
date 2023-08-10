package resources.endpoints.container;

import com.fasterxml.jackson.core.type.TypeReference;
import core.ClientOptions;
import core.ObjectMappers;
import core.RequestOptions;
import java.lang.Exception;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import resources.types.object.types.ObjectWithRequiredField;

public class ContainerClient {
  protected final ClientOptions clientOptions;

  public ContainerClient(ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
  }

  public List<String> getAndReturnListOfPrimitives(List<String> request) {
    return getAndReturnListOfPrimitives(request,null);
  }

  public List<String> getAndReturnListOfPrimitives(List<String> request,
      RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("list-of-primitives")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<List<String>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public List<ObjectWithRequiredField> getAndReturnListOfObjects(
      List<ObjectWithRequiredField> request) {
    return getAndReturnListOfObjects(request,null);
  }

  public List<ObjectWithRequiredField> getAndReturnListOfObjects(
      List<ObjectWithRequiredField> request, RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("list-of-objects")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<List<ObjectWithRequiredField>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Set<String> getAndReturnSetOfPrimitives(Set<String> request) {
    return getAndReturnSetOfPrimitives(request,null);
  }

  public Set<String> getAndReturnSetOfPrimitives(Set<String> request,
      RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("set-of-primitives")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<Set<String>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Set<ObjectWithRequiredField> getAndReturnSetOfObjects(
      Set<ObjectWithRequiredField> request) {
    return getAndReturnSetOfObjects(request,null);
  }

  public Set<ObjectWithRequiredField> getAndReturnSetOfObjects(Set<ObjectWithRequiredField> request,
      RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("set-of-objects")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<Set<ObjectWithRequiredField>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Map<String, String> getAndReturnMapPrimToPrim(Map<String, String> request) {
    return getAndReturnMapPrimToPrim(request,null);
  }

  public Map<String, String> getAndReturnMapPrimToPrim(Map<String, String> request,
      RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("map-prim-to-prim")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<Map<String, String>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Map<String, ObjectWithRequiredField> getAndReturnMapOfPrimToObject(
      Map<String, ObjectWithRequiredField> request) {
    return getAndReturnMapOfPrimToObject(request,null);
  }

  public Map<String, ObjectWithRequiredField> getAndReturnMapOfPrimToObject(
      Map<String, ObjectWithRequiredField> request, RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("container")
      .addPathSegments("map-prim-to-object")
      .build();
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request _request = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json")
      .build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), new TypeReference<Map<String, ObjectWithRequiredField>>() {});
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
