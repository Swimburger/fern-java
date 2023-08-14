package com.seed.exhaustive;

import com.seed.exhaustive.core.ClientOptions;
import com.seed.exhaustive.core.Environment;

public final class SeedExhaustiveClientBuilder {
    private ClientOptions.Builder clientOptionsBuilder = ClientOptions.builder();

    private Environment environment;

    public SeedExhaustiveClientBuilder token(String token) {
        this.clientOptionsBuilder.addHeader("Authorization", "Bearer " + token);
        return this;
    }

    public SeedExhaustiveClientBuilder url(String url) {
        this.environment = Environment.custom(url);
        return this;
    }

    public SeedExhaustiveClient build() {
        clientOptionsBuilder.environment(this.environment);
        return new SeedExhaustiveClient(clientOptionsBuilder.build());
    }
}
