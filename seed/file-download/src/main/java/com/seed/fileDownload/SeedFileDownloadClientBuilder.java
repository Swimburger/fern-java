package com.seed.fileDownload;

import com.seed.fileDownload.core.ClientOptions;
import com.seed.fileDownload.core.Environment;

public final class SeedFileDownloadClientBuilder {
    private ClientOptions.Builder clientOptionsBuilder = ClientOptions.builder();

    private Environment environment;

    public SeedFileDownloadClientBuilder url(String url) {
        this.environment = Environment.custom(url);
        return this;
    }

    public SeedFileDownloadClient build() {
        clientOptionsBuilder.environment(this.environment);
        return new SeedFileDownloadClient(clientOptionsBuilder.build());
    }
}
