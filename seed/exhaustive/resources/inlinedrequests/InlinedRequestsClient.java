package resources.inlinedrequests;

import core.ClientOptions;
import core.ObjectMappers;
import core.RequestOptions;
import java.lang.Exception;
import java.lang.Object;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import resources.inlinedrequests.requests.PostWithObjectBody;
import resources.types.object.types.ObjectWithOptionalField;

public class InlinedRequestsClient {
  protected final ClientOptions clientOptions;

  public InlinedRequestsClient(ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
  }

  public ObjectWithOptionalField postWithObjectBodyandResponse(PostWithObjectBody request) {
    return postWithObjectBodyandResponse(request,null);
  }

  public ObjectWithOptionalField postWithObjectBodyandResponse(PostWithObjectBody request,
      RequestOptions requestOptions) {
    HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl()).newBuilder()
      .addPathSegments("req-bodies")
      .addPathSegments("object")
      .build();
    Map<String, Object> _requestBodyProperties = new HashMap<>();
    _requestBodyProperties.put("string", request.getString());
    _requestBodyProperties.put("integer", request.getInteger());
    _requestBodyProperties.put("NestedObject", request.getNestedObject());
    RequestBody _requestBody;
    try {
      _requestBody = RequestBody.create(ObjectMappers.JSON_MAPPER.writeValueAsBytes(_requestBodyProperties), MediaType.parse("application/json"));
    }
    catch(Exception e) {
      throw new RuntimeException(e);
    }
    Request.Builder _requestBuilder = new Request.Builder()
      .url(_httpUrl)
      .method("POST", _requestBody)
      .headers(Headers.of(clientOptions.headers(requestOptions)))
      .addHeader("Content-Type", "application/json");
    Request _request = _requestBuilder.build();
    try {
      Response _response = clientOptions.httpClient().newCall(_request).execute();
      if (_response.isSuccessful()) {
        return ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), ObjectWithOptionalField.class);
      }
      throw new RuntimeException();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
