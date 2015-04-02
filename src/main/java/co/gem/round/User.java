package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;

/**
 * Gem users are the objects that interact with wallets.  When creating a user, the user will have a default wallet
 * but users can also have multiple wallets over time.  Users also have their own tokens which are used for various
 * authentication schemes.
 * @author Julian Del Vergel de Dios (julian@gem.co) on 12/18/14.
 * @see co.gem.round.Round#authenticateDevice(String, String, String, String)
 */
public class User extends Base {
  public User(Resource resource, Round round) {
    super(resource, round);
  }

  /**
   * Getter all the wallets belonging to a user
   * @return WalletCollection
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.WalletCollection
   */
  public WalletCollection wallets() throws
    IOException, Client.UnexpectedStatusCodeException {
    Resource resource = this.resource.subresource("wallets");
    WalletCollection wallets = new WalletCollection(resource, round);
    wallets.fetch();
    return wallets;
  }

  public DeviceCollection devices() throws
      IOException, Client.UnexpectedStatusCodeException {
    Resource resource = this.resource.subresource("devices");
    DeviceCollection devices = new DeviceCollection(resource, round);
    return devices;
  }

  /**
   * Getter for the default wallet of a user
   * @return Wallet
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   */
  public Wallet defaultWallet()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource resource = this.resource.subresource("default_wallet");
    Wallet defaultWallet = new Wallet(resource, round);
    defaultWallet.fetch();
    return defaultWallet;
  }

  /**
   * Getter for email for a user
   * @return String email address
   */
  public String email() {
    return getString("email");
  }

  /**
   * Getter for the user token which is used in various authentication schemes
   * @return String user token
   */
  public String userToken() {
    return getString("user_token");
  }

  /**
   * Getter for the Gem API url of a user
   * @return String
   */
  public String userUrl() {
    return getString("url");
  }

  public String redirectUri() {
    return getString("redirect_uri");
  }
}
