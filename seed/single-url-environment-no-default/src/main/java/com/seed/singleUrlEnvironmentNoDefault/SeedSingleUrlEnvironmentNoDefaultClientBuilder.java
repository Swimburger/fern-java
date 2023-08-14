package com.seed.singleUrlEnvironmentNoDefault;

import com.seed.singleUrlEnvironmentNoDefault.core.ClientOptions;
import com.seed.singleUrlEnvironmentNoDefault.core.Environment;

public final class SeedSingleUrlEnvironmentNoDefaultClientBuilder {
    private ClientOptions.Builder clientOptionsBuilder = ClientOptions.builder();

    private Environment environment;

    public SeedSingleUrlEnvironmentNoDefaultClientBuilder token(String token) {
        this.clientOptionsBuilder.addHeader("Authorization", "Bearer " + token);
        return this;
    }

    public SeedSingleUrlEnvironmentNoDefaultClientBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public SeedSingleUrlEnvironmentNoDefaultClientBuilder url(String url) {
        this.environment = Environment.custom(url);
        return this;
    }

    public SeedSingleUrlEnvironmentNoDefaultClient build() {
        clientOptionsBuilder.environment(this.environment);
        return new SeedSingleUrlEnvironmentNoDefaultClient(clientOptionsBuilder.build());
    }
}
