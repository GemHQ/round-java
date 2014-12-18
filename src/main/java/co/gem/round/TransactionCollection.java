package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class TransactionCollection extends BaseCollection<Transaction> {

  public static final String RESOURCE_NAME = "transactions";

  public TransactionCollection(String url, Round round) {
    super(url, round, RESOURCE_NAME);
  }

  public TransactionCollection(Resource resource, Round round) {
    super(resource, round);
  }

  @Override
  public void populateCollection(Iterable<Resource> resources) {
    for (Resource resource : resources) {
      Transaction transaction = new Transaction(resource, this.round);
      this.add(transaction.key(), transaction);
    }
  }

}
