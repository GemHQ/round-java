package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import co.gem.round.util.Http;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 12/18/14.
 */
public class User extends Base {
  public User(Resource resource, Round round) {
    super(resource, round);
  }

  public WalletCollection wallets() throws
    IOException, Client.UnexpectedStatusCodeException {
    Resource resource = this.resource.subresource("wallets");
    WalletCollection wallets = new WalletCollection(resource, round);
    wallets.fetch();
    return wallets;
  }

  public Wallet defaultWallet()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource resource = this.resource.subresource("default_wallet");
    Wallet defaultWallet = new Wallet(resource, round);
    defaultWallet.fetch();
    return defaultWallet;
  }

  public String beginDeviceAuth(String apiToken, String deviceName, String deviceId)
      throws Client.UnexpectedStatusCodeException, IOException {
    round.authenticateOtp(apiToken, null, null);
    JsonObject payload = new JsonObject();
    payload.addProperty("name", deviceName);
    payload.addProperty("device_id", deviceId);
    try {
      resource.action("authorize_device", payload);
    } catch(Client.UnexpectedStatusCodeException e) {
      if (e.statusCode != 401) throw e;
      String authHeader = e.response.header("Www-Authenticate");
      return Http.extractParamsFromHeader(authHeader).get("key");
    }
    return null;
  }

  public User completeDeviceAuth(String apiToken, String deviceName, String deviceId, String key, String secret)
      throws Client.UnexpectedStatusCodeException, IOException {
    round.authenticateOtp(apiToken, key, secret);
    JsonObject payload = new JsonObject();
    payload.addProperty("name", deviceName);
    payload.addProperty("device_id", deviceId);
    Resource userResource = resource.action("authorize_device", payload);

    return new User(userResource, round);
  }

  public String email() {
    return getString("email");
  }

  public static class Wrapper {
    public User user;
    public String backupPrivateSeed;

    public Wrapper(User user, String backupPrivateSeed) {
      this.user = user;
      this.backupPrivateSeed = backupPrivateSeed;
    }
  }
}
