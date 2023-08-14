package com.seed.customAuth.core;

import java.util.HashMap;
import java.util.Map;

public final class RequestOptions {
    private final String customAuthScheme;

    private RequestOptions(String customAuthScheme) {
        this.customAuthScheme = customAuthScheme;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (this.customAuthScheme != null) {
            headers.put("X-API-KEY", this.customAuthScheme);
        }
        return headers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String customAuthScheme = null;

        public Builder customAuthScheme(String customAuthScheme) {
            this.customAuthScheme = customAuthScheme;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(customAuthScheme);
        }
    }
}
