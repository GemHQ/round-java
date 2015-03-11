package co.gem.round;

import co.gem.round.patchboard.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Transaction associated with an account.  Contains hash, type, status and the bitcoin transaction json.
 *
 * @author Julian Del Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Transaction extends Base {

  public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

  public Transaction(Resource resource, Round round) {
    super(resource, round);
  }

  /**
   * Getter for the transaction type.  "incoming" and "outgoing" are the two types of values to be returned
   * @return String type either "incoming" or "outgoing"
   */
  public String getType() {
    return this.resource().attributes().get("type").getAsString();
  }

  /**
   * Getter for the bitcoin transaction hash
   * @return String bitcoin transaction hash
   */
  public String getTransactionHash() {
    return this.resource().attributes().get("hash").getAsString();
  }

  /**
   * Getter for the value in the bitcoin transaction.
   * @return Long value
   */
  public long getValue() {
    return this.resource().attributes().get("value").getAsLong();
  }

  /**
   * Getter for the status of a bitcoin transaction.  Will return confirmed, unconfirmed, canceled, unsigned
   * @return String status: confirmed, unconfirmed, canceled, unsigned
   */
  public String getStatus() {
    return this.resource().attributes().get("status").getAsString();
  }

  /**
   * Getter for the number of confirmations of a transaction
   * @return int number of confirmations
   */
  public int getConfirmations() {
    return this.resource().attributes().get("confirmations").getAsInt();
  }

  /**
   * Getter for the fee in the transaction.  Value - Fee = amount sent
   * @return
   */
  public long getFee() {
    return this.resource().attributes().get("fee").getAsLong();
  }

//  public String getActor() {
//    JsonObject bitcoinTransaction = getBitcoinTransaction();
//
//    if (getType().equals("incoming")) {
//      JsonObject input = bitcoinTransaction.getAsJsonArray("inputs")
//          .get(0).getAsJsonObject()
//          .get("output").getAsJsonObject();
//      return input.get("address").getAsString();
//    }
//
//    if (getType().equals("outgoing")) {
//      for (JsonElement element : bitcoinTransaction.getAsJsonArray("outputs")) {
//        JsonObject output = element.getAsJsonObject();
//        JsonObject metadata = output.getAsJsonObject("metadata");
//        if (Math.abs(output.get("value").getAsLong()) != Math.abs(getValue()))
//          continue;
//
//        String address = output.get("address").getAsString();
//        if (metadata.has("pay_to_email"))
//          return metadata.get("pay_to_email").getAsString();
//        return address;
//      }
//    }
//
//    return null;
//  }

  /**
   * Getter for the date created for a bitcoin transaction yyyy-MM-dd HH:mm:ss Z
   * @return Date created at
   */
  public Date getCreatedAt() {
    String dateString = this.resource().attributes().get("created_at").getAsString();

    Date createdAt = null;
    try {
      createdAt = DATE_FORMATTER.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return createdAt;
  }
}
