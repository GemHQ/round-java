package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Patchboard;
import co.gem.round.patchboard.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Round {

  static final String API_HOST = "http://localhost:8999";

  private static Patchboard patchboard;

  private Client patchboardClient;

  private Round(Client patchboardClient) {
    this.patchboardClient = patchboardClient;
  }

  public static Round client(String url)
    throws Client.UnexpectedStatusCodeException, IOException {
    if (url == null)
      url = API_HOST;
    patchboard = Patchboard.discover(url);

    Client patchboardClient = patchboard.spawn(new Authorizer());
    return new Round(patchboardClient);
  }

  public User authenticateDevice(String apiToken, String userToken, String deviceId, String email) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    params.put("user_token", userToken);
    params.put("device_id", deviceId);
    patchboardClient.authorizer().authorize(AuthScheme.DEVICE, params);

    return user(email);
  }

  public Application authenticateApplication(String url, String apiToken, String instanceId) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    params.put("instance_id", instanceId);
    patchboardClient.authorizer().authorize(AuthScheme.APPLICATION, params);

    return application(url);
  }

  public void authenticateOtp(String apiToken, String key, String secret) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    if (key != null) params.put("key", key);
    if (secret != null) params.put("secret", secret);
    patchboardClient.authorizer().authorize(AuthScheme.OTP, params);
  }

  public User user(String email) {
    Map<String, String> query = new HashMap<String, String>();
    query.put("email", email);
    Resource resource = patchboardClient.resources("user_query", query);
    return new User(resource, this);
  }

  public Application application(String url) {
    Resource resource = patchboardClient.resources("application", url);
    return new Application(resource, this);
  }

  public UserCollection users() {
    Resource resource = patchboardClient.resources("users");
    return new UserCollection(resource, this);
  }

  public Client patchboardClient() { return patchboardClient; }

  static class AuthScheme {
    static final String DEVICE = "Gem-Device";
    static final String APPLICATION = "Gem-Application";
    static final String DEVELOPER = "Gem-Developer";
    static final String DEVELOPER_SESSION = "Gem-Developer-Session";
    static final String USER = "Gem-User";
    static final String OTP = "Gem-OOB-OTP";
  }
}