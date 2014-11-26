package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.TransactionWrapper;
import co.gem.round.patchboard.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;


public class Payment extends Resource {

  public static final String RESOURCE_NAME = "payment";

  public Payment(JsonObject resource, Round round) {
    super(resource, round, RESOURCE_NAME);
  }

  public Payment(String url, Round round)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, round, RESOURCE_NAME);
  }

  public Payment sign(MultiWallet wallet)
      throws IOException, Client.UnexpectedStatusCodeException {
    TransactionWrapper transaction = TransactionWrapper.parseTransaction(this.resource, wallet.networkParameters());
    List<String> signatures = wallet.signaturesForTransaction(transaction);

    JsonArray signaturesJson = new JsonArray();
    for (String signature : signatures) {
      JsonObject signatureJson = new JsonObject();
      signatureJson.addProperty("primary", signature);
    }

    JsonObject body = new JsonObject();
    body.addProperty("transaction_hash", transaction.getHashAsString());
    body.add("inputs", signaturesJson);

    JsonElement response = this.round.performRequest(this.url, "unsigned_payment", "sign", body);

    return new Payment(response.getAsJsonObject(), this.round);
  }

  public String getStatus() {
    return this.resource.get("status").getAsString();
  }

}
