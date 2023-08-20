package com.seed.fileUpload.resources.service;

import com.seed.fileUpload.core.ApiError;
import com.seed.fileUpload.core.ClientOptions;
import com.seed.fileUpload.core.ObjectMappers;
import com.seed.fileUpload.core.RequestOptions;
import com.seed.fileUpload.resources.service.requests.JustFileRequet;
import com.seed.fileUpload.resources.service.requests.MyRequest;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceClient {
    protected final ClientOptions clientOptions;

    public ServiceClient(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    public void post(File file, Optional<File> maybeFile, MyRequest request) {
        post(file, maybeFile, request, null);
    }

    public void post(File file, Optional<File> maybeFile, MyRequest request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .build();
        MultipartBody.Builder _multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        try {
        if (request.getMaybeString().isPresent()) {
            _multipartBody.addFormDataPart("maybeString", request.getMaybeString());
        }
        _multipartBody.addFormDataPart("integer", request.getInteger());
        _multipartBody.addFormDataPart("file", null, RequestBody.create(null, file));
        if (maybeFile.isPresent()) {
            _multipartBody.addFormDataPart("maybeFile", null, RequestBody.create(null, maybeFile.get()));
        }
        if (request.getMaybeInteger().isPresent()) {
            _multipartBody.addFormDataPart("maybeInteger", request.getMaybeInteger());
        }
        _multipartBody.addFormDataPart("listOfStrings", request.getListOfStrings());
        _multipartBody.addFormDataPart("setOfStrings", request.getSetOfStrings());
        if (request.getOptionalListOfStrings().isPresent()) {
            _multipartBody.addFormDataPart("optionalListOfStrings", request.getOptionalListOfStrings());
        }
        if (request.getOptionalSetOfStrings().isPresent()) {
            _multipartBody.addFormDataPart("optionalSetOfStrings", request.getOptionalSetOfStrings());
        }
        _multipartBody.addFormDataPart("maybeList", request.getMaybeList());
        if (request.getOptionalMaybeList().isPresent()) {
            _multipartBody.addFormDataPart("optionalMaybeList", request.getOptionalMaybeList());
        }
        _multipartBody.addFormDataPart("maybeListOrSet", request.getMaybeListOrSet());
        if (request.getOptionalMaybeListOrSet().isPresent()) {
            _multipartBody.addFormDataPart("optionalMaybeListOrSet", request.getOptionalMaybeListOrSet());
        }
        _multipartBody.addFormDataPart("listOfObjects", request.getListOfObjects());
        RequestBody _requestBody = _multipartBody.build();
        Request.Builder _requestBuilder = new Request.Builder()
                .url(_httpUrl)
                .method("POST", _requestBody)
                .headers(Headers.of(clientOptions.headers(requestOptions)));
        Request _request = _requestBuilder.build();
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

    public void justFile(File file, JustFileRequet request) {
        justFile(file, request, null);
    }

    public void justFile(File file, JustFileRequet request, RequestOptions requestOptions) {
        HttpUrl _httpUrl = HttpUrl.parse(this.clientOptions.environment().getUrl())
                .newBuilder()
                .addPathSegments("just-file")
                .build();
        MultipartBody.Builder _multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        _multipartBody.addFormDataPart("file", null, RequestBody.create(null, file));
        RequestBody _requestBody = _multipartBody.build();
        Request.Builder _requestBuilder = new Request.Builder()
                .url(_httpUrl)
                .method("POST", _requestBody)
                .headers(Headers.of(clientOptions.headers(requestOptions)));
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
}
