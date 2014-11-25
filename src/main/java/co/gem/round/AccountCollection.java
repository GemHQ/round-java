package co.gem.round;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;


public class AccountCollection extends ResourceCollection<Account> {
  public static final String RESOURCE_NAME = "accounts";

  private Wallet wallet;

  public AccountCollection(String url, Client client, Wallet wallet)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, client, RESOURCE_NAME);

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
      Account account = new Account(resource, this.client);
      this.add(account.getKey(), account);
    }
  }

  public Account create(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject body = new JsonObject();
    body.addProperty("name", name);

    JsonObject resource =
        this.client.performRequest(this.url, RESOURCE_NAME, "create", body).getAsJsonObject();

    Account account = new Account(resource, this.client);
    this.add(account.getKey(), account);
    return account;
  }
}
