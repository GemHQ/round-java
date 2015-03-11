package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.TransactionWrapper;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

/**
 * Payment objects
 *
 * @author Julian Del Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Payment extends Base {

  public Payment(Resource resource, Round round) {
    super(resource, round);
  }

  /**
   * Sign a Gem API unsigned payment to send back to Gem for rules verification, co-signing and publishing to the
   * blockchain
   * @param wallet coinop.MultiWallet
   * @return Payment signed
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.coinop.MultiWallet
   */
  public Payment sign(MultiWallet wallet)
      throws IOException, Client.UnexpectedStatusCodeException {
    TransactionWrapper transaction = TransactionWrapper.parseTransaction(resource.attributes(), wallet.networkParameters());
    List<String> signatures = wallet.signaturesForTransaction(transaction);

    JsonArray signaturesJson = new JsonArray();
    for (String signature : signatures) {
      JsonObject signatureJson = new JsonObject();
      signatureJson.addProperty("primary", signature);
      signaturesJson.add(signatureJson);
    }

    JsonObject body = new JsonObject();
    body.addProperty("transaction_hash", transaction.getHashAsString());
    body.add("inputs", signaturesJson);

    Resource signedPayment = resource.action("sign", body);
    resource = signedPayment;
    return this;
  }

  /**
   * cancels an unsigned payment
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   */
  public void cancel()
      throws IOException, Client.UnexpectedStatusCodeException {
    resource.action("cancel");
  }

  /**
   * Getter for the status of a payment
   * @return String status
   */
  public String getStatus() {
    return resource.attributes().get("status").getAsString();
  }

}
