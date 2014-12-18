package co.gem.round;

import co.gem.round.patchboard.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;


public class AccountCollection extends BaseCollection<Account> {
  public static final String RESOURCE_NAME = "accounts";

  private Wallet wallet;

  public AccountCollection(String url, Round round, Wallet wallet)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, round, RESOURCE_NAME);

    this.wallet = wallet;
    setWallets();
  }

  private void setWallets() {
    for (Account account : collection) {
      account.setWallet(wallet);
    }
  }

  @Override
  public void populateCollection(JsonArray array) {
    for (JsonElement element : array) {
      JsonObject resource = element.getAsJsonObject();
      Account account = new Account(resource, this.round);
      this.add(account.getKey(), account);
    }
  }

  public Account create(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject body = new JsonObject();
    body.addProperty("name", name);

    JsonObject resource = null;
//        this.round.performRequest(this.url, RESOURCE_NAME, "create", body).getAsJsonObject();

    Account account = new Account(resource, this.round);
    this.add(account.getKey(), account);
    return account;
  }
}
