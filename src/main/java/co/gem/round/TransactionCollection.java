package co.gem.round;

import co.gem.round.patchboard.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class TransactionCollection extends BaseCollection<Transaction> {

  public static final String RESOURCE_NAME = "transactions";

  public TransactionCollection(String url, Round round)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, round, RESOURCE_NAME);
  }

  @Override
  public void populateCollection(JsonArray array) {
    for (JsonElement element : array) {
      JsonObject resource = element.getAsJsonObject();
      Transaction transaction = new Transaction(resource, this.round);
      this.add(transaction.getKey(), transaction);
    }
  }

}
