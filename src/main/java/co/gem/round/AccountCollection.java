package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;


public class AccountCollection extends BaseCollection<Account> {
  public static final String RESOURCE_NAME = "accounts";

  private Wallet wallet;

  public AccountCollection(String url, Round round, Wallet wallet) {
    super(url, round, RESOURCE_NAME);

    this.wallet = wallet;
    setWallets();
  }

  public AccountCollection(Resource resource, Round round, Wallet wallet) {
    super(resource, round);

    this.wallet = wallet;
    setWallets();
  }

  private void setWallets() {
    for (Account account : list) {
      account.setWallet(wallet);
    }
  }

  @Override
  public void populateCollection(Iterable<Resource> resources) {
    for (Resource resource : resources) {
      Account account = new Account(resource, round);
      account.setWallet(this.wallet);
      this.add(account.getString("name"), account);
    }
  }

  public Account create(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject body = new JsonObject();
    body.addProperty("name", name);

    Resource accountResource = resource.action("create", body);

    Account account = new Account(accountResource, this.round);
    this.add(account.key(), account);
    return account;
  }
}
