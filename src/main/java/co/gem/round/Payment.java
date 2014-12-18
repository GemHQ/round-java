package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.TransactionWrapper;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;


public class Payment extends Base {

  public static final String RESOURCE_NAME = "payment";

  public Payment(Resource resource, Round round) {
    super(resource, round);
  }

  public Payment(String url, Round round)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, round, RESOURCE_NAME);
  }

  public Payment sign(MultiWallet wallet)
      throws IOException, Client.UnexpectedStatusCodeException {
    TransactionWrapper transaction = TransactionWrapper.parseTransaction(resource.attributes(), wallet.networkParameters());
    List<String> signatures = wallet.signaturesForTransaction(transaction);

    JsonArray signaturesJson = new JsonArray();
    for (String signature : signatures) {
      JsonObject signatureJson = new JsonObject();
      signatureJson.addProperty("primary", signature);
    }

    JsonObject body = new JsonObject();
    body.addProperty("transaction_hash", transaction.getHashAsString());
    body.add("inputs", signaturesJson);

    Resource signedPayment = resource.action("sign", body);

    return new Payment(signedPayment, this.round);
  }

  public String getStatus() {
    return resource.attributes().get("status").getAsString();
  }

}
