package com.seed.trace.core;

import java.util.HashMap;
import java.util.Map;

public final class RequestOptions {
    private final String token;

    private final String xRandomHeader;

    private RequestOptions(String token, String xRandomHeader) {
        this.token = token;
        this.xRandomHeader = xRandomHeader;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (this.token != null) {
            headers.put("Authorization", "Bearer " + this.token);
        }
        if (this.xRandomHeader != null) {
            headers.put("X-Random-Header", this.xRandomHeader);
        }
        return headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String token = null;

        private String xRandomHeader = null;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder xRandomHeader(String xRandomHeader) {
            this.xRandomHeader = xRandomHeader;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(token, xRandomHeader);
        }
    }
}
