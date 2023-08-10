import core.ClientOptions;
import core.Environment;
import java.lang.String;

public final class SeedApiClientBuilder {
  private ClientOptions.Builder clientOptionsBuilder = ClientOptions.builder();

  private Environment environment;

  public SeedApiClientBuilder token(String token) {
    this.clientOptionsBuilder.addHeader("Authorization", "Bearer " + token);
    return this;
  }

  public SeedApiClientBuilder url(String url) {
    this.environment = Environment.custom(url);
    return this;
  }

  public SeedApiClient build() {
    clientOptionsBuilder.environment(this.environment);
    return new SeedApiClient(clientOptionsBuilder.build());
  }
}
