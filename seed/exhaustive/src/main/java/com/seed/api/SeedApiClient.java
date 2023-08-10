package com.seed.api;

import com.seed.api.core.ClientOptions;
import com.seed.api.core.Suppliers;
import com.seed.api.resources.endpoints.EndpointsClient;
import com.seed.api.resources.inlinedrequests.InlinedRequestsClient;
import com.seed.api.resources.noauth.NoAuthClient;
import com.seed.api.resources.noreqbody.NoReqBodyClient;
import com.seed.api.resources.reqwithheaders.ReqWithHeadersClient;
import java.util.function.Supplier;

public class SeedApiClient {
    protected final ClientOptions clientOptions;

    protected final Supplier<EndpointsClient> endpointsClient;

    protected final Supplier<InlinedRequestsClient> inlinedRequestsClient;

    protected final Supplier<NoAuthClient> noAuthClient;

    protected final Supplier<NoReqBodyClient> noReqBodyClient;

    protected final Supplier<ReqWithHeadersClient> reqWithHeadersClient;

    public SeedApiClient(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.endpointsClient = Suppliers.memoize(() -> new EndpointsClient(clientOptions));
        this.inlinedRequestsClient = Suppliers.memoize(() -> new InlinedRequestsClient(clientOptions));
        this.noAuthClient = Suppliers.memoize(() -> new NoAuthClient(clientOptions));
        this.noReqBodyClient = Suppliers.memoize(() -> new NoReqBodyClient(clientOptions));
        this.reqWithHeadersClient = Suppliers.memoize(() -> new ReqWithHeadersClient(clientOptions));
    }

    public EndpointsClient endpoints() {
        return this.endpointsClient.get();
    }

    public InlinedRequestsClient inlinedRequests() {
        return this.inlinedRequestsClient.get();
    }

    public NoAuthClient noAuth() {
        return this.noAuthClient.get();
    }

    public NoReqBodyClient noReqBody() {
        return this.noReqBodyClient.get();
    }

    public ReqWithHeadersClient reqWithHeaders() {
        return this.reqWithHeadersClient.get();
    }

    public static SeedApiClientBuilder builder() {
        return new SeedApiClientBuilder();
    }
}
