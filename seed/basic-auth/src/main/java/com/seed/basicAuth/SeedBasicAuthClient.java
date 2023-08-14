package com.seed.basicAuth;

import com.seed.basicAuth.core.ClientOptions;
import com.seed.basicAuth.core.Suppliers;
import com.seed.basicAuth.resources.basicauth.BasicAuthClient;
import java.util.function.Supplier;

public class SeedBasicAuthClient {
    protected final ClientOptions clientOptions;

    protected final Supplier<BasicAuthClient> basicAuthClient;

    public SeedBasicAuthClient(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.basicAuthClient = Suppliers.memoize(() -> new BasicAuthClient(clientOptions));
    }

    public BasicAuthClient basicAuth() {
        return this.basicAuthClient.get();
    }

    public static SeedBasicAuthClientBuilder builder() {
        return new SeedBasicAuthClientBuilder();
    }
}
