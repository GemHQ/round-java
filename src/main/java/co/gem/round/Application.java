package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 12/18/14.
 */
public class Application extends Base{
  public Application(Resource resource, Round round) {
    super(resource, round);
  }

  public UserCollection users()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource usersResource = resource.subresource("users");
    UserCollection users = new UserCollection(usersResource, round);
    users.fetch();
    return users;
  }

  public WalletCollection wallets()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource walletsResource = resource.subresource("wallets");
    WalletCollection wallets = new WalletCollection(walletsResource, round);
    wallets.fetch();
    return wallets;
  }
}
