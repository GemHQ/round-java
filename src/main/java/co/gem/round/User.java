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

  public String email() {
    return getString("email");
  }

  public String userToken() {
    return getString("user_token");
  }

  public String userUrl() {
    return getString("url");
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
