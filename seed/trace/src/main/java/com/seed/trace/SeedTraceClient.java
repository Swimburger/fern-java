package com.seed.trace;

import com.seed.trace.core.ClientOptions;
import com.seed.trace.core.Suppliers;
import com.seed.trace.resources.admin.AdminClient;
import com.seed.trace.resources.homepage.HomepageClient;
import com.seed.trace.resources.migration.MigrationClient;
import com.seed.trace.resources.playlist.PlaylistClient;
import com.seed.trace.resources.problem.ProblemClient;
import com.seed.trace.resources.submission.SubmissionClient;
import com.seed.trace.resources.sysprop.SyspropClient;
import com.seed.trace.resources.v2.V2Client;
import java.util.function.Supplier;

public class SeedTraceClient {
    protected final ClientOptions clientOptions;

    protected final Supplier<V2Client> v2Client;

    protected final Supplier<AdminClient> adminClient;

    protected final Supplier<HomepageClient> homepageClient;

    protected final Supplier<MigrationClient> migrationClient;

    protected final Supplier<PlaylistClient> playlistClient;

    protected final Supplier<ProblemClient> problemClient;

    protected final Supplier<SubmissionClient> submissionClient;

    protected final Supplier<SyspropClient> syspropClient;

    public SeedTraceClient(ClientOptions clientOptions) {
        this.clientOptions = clientOptions;
        this.v2Client = Suppliers.memoize(() -> new V2Client(clientOptions));
        this.adminClient = Suppliers.memoize(() -> new AdminClient(clientOptions));
        this.homepageClient = Suppliers.memoize(() -> new HomepageClient(clientOptions));
        this.migrationClient = Suppliers.memoize(() -> new MigrationClient(clientOptions));
        this.playlistClient = Suppliers.memoize(() -> new PlaylistClient(clientOptions));
        this.problemClient = Suppliers.memoize(() -> new ProblemClient(clientOptions));
        this.submissionClient = Suppliers.memoize(() -> new SubmissionClient(clientOptions));
        this.syspropClient = Suppliers.memoize(() -> new SyspropClient(clientOptions));
    }

    public V2Client v2() {
        return this.v2Client.get();
    }

    public AdminClient admin() {
        return this.adminClient.get();
    }

    public HomepageClient homepage() {
        return this.homepageClient.get();
    }

    public MigrationClient migration() {
        return this.migrationClient.get();
    }

    public PlaylistClient playlist() {
        return this.playlistClient.get();
    }

    public ProblemClient problem() {
        return this.problemClient.get();
    }

    public SubmissionClient submission() {
        return this.submissionClient.get();
    }

    public SyspropClient sysprop() {
        return this.syspropClient.get();
    }

    public static SeedTraceClientBuilder builder() {
        return new SeedTraceClientBuilder();
    }
}
