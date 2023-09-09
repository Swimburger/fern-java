package com.seed.trace.resources.admin;

import com.seed.trace.core.ApiError;
import com.seed.trace.core.ClientOptions;
import com.seed.trace.core.ObjectMappers;
import com.seed.trace.core.RequestOptions;
import com.seed.trace.resources.admin.requests.StoreTracedTestCaseRequest;
import com.seed.trace.resources.admin.requests.StoreTracedWorkspaceRequest;
import com.seed.trace.resources.submission.types.TestSubmissionStatus;
import com.seed.trace.resources.submission.types.TestSubmissionUpdate;
import com.seed.trace.resources.submission.types.TraceResponseV2;
import com.seed.trace.resources.submission.types.WorkspaceSubmissionStatus;
import com.seed.trace.resources.submission.types.WorkspaceSubmissionUpdate;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminClient {
    protected final ClientOptions clientOptions;

    public AdminClient(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    public void updateTestSubmissionStatus(UUID submissionId, TestSubmissionStatus request) {
        updateTestSubmissionStatus(submissionId, request, null);
    }

    public void updateTestSubmissionStatus(
            UUID submissionId, TestSubmissionStatus request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-test-submission-status")
                .addPathSegment(submissionId.toString())
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTestSubmissionUpdate(UUID submissionId, TestSubmissionUpdate request) {
        sendTestSubmissionUpdate(submissionId, request, null);
    }

    public void sendTestSubmissionUpdate(
            UUID submissionId, TestSubmissionUpdate request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-test-submission-status-v2")
                .addPathSegment(submissionId.toString())
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateWorkspaceSubmissionStatus(UUID submissionId, WorkspaceSubmissionStatus request) {
        updateWorkspaceSubmissionStatus(submissionId, request, null);
    }

    public void updateWorkspaceSubmissionStatus(
            UUID submissionId, WorkspaceSubmissionStatus request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-workspace-submission-status")
                .addPathSegment(submissionId.toString())
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendWorkspaceSubmissionUpdate(UUID submissionId, WorkspaceSubmissionUpdate request) {
        sendWorkspaceSubmissionUpdate(submissionId, request, null);
    }

    public void sendWorkspaceSubmissionUpdate(
            UUID submissionId, WorkspaceSubmissionUpdate request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-workspace-submission-status-v2")
                .addPathSegment(submissionId.toString())
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTracedTestCase(UUID submissionId, String testCaseId, StoreTracedTestCaseRequest request) {
        storeTracedTestCase(submissionId, testCaseId, request, null);
    }

    public void storeTracedTestCase(
            UUID submissionId, String testCaseId, StoreTracedTestCaseRequest request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-test-trace/submission")
                .addPathSegment(submissionId.toString())
                .addPathSegments("testCase")
                .addPathSegment(testCaseId)
                .build();
        Map<String, Object> _requestBodyProperties = new HashMap<>();
        _requestBodyProperties.put("result", request.getResult());
        _requestBodyProperties.put("traceResponses", request.getTraceResponses());
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(_requestBodyProperties),
                    MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTracedTestCaseV2(UUID submissionId, String testCaseId, List<TraceResponseV2> request) {
        storeTracedTestCaseV2(submissionId, testCaseId, request, null);
    }

    public void storeTracedTestCaseV2(
            UUID submissionId, String testCaseId, List<TraceResponseV2> request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-test-trace-v2/submission")
                .addPathSegment(submissionId.toString())
                .addPathSegments("testCase")
                .addPathSegment(testCaseId)
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTracedWorkspace(UUID submissionId, StoreTracedWorkspaceRequest request) {
        storeTracedWorkspace(submissionId, request, null);
    }

    public void storeTracedWorkspace(
            UUID submissionId, StoreTracedWorkspaceRequest request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-workspace-trace/submission")
                .addPathSegment(submissionId.toString())
                .build();
        Map<String, Object> _requestBodyProperties = new HashMap<>();
        _requestBodyProperties.put("workspaceRunDetails", request.getWorkspaceRunDetails());
        _requestBodyProperties.put("traceResponses", request.getTraceResponses());
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(_requestBodyProperties),
                    MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeTracedWorkspaceV2(UUID submissionId, List<TraceResponseV2> request) {
        storeTracedWorkspaceV2(submissionId, request, null);
    }

    public void storeTracedWorkspaceV2(
            UUID submissionId, List<TraceResponseV2> request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("admin")
                .addPathSegments("store-workspace-trace-v2/submission")
                .addPathSegment(submissionId.toString())
                .build();
        RequestBody _requestBody;
        try {
            _requestBody = RequestBody.create(
                    ObjectMappers.JSON_MAPPER.writeValueAsBytes(request), MediaType.parse("application/json"));
        } catch (Exception e) {
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
                return;
            }
            throw new ApiError(
                    _response.code(),
                    ObjectMappers.JSON_MAPPER.readValue(_response.body().string(), Object.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}