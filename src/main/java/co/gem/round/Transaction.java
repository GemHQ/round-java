package co.gem.round;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction extends Resource {

  public static final String RESOURCE_NAME = "transaction";
  public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

  public Transaction(JsonObject resource, Client client) {
    super(resource, client, RESOURCE_NAME);
  }

  public Transaction(String url, Client client)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, client, RESOURCE_NAME);
  }

  public String getType() {
    return resource.get("type").getAsString();
  }

  public JsonObject getBitcoinTransaction() {
    return resource.getAsJsonObject("data");
  }

  public String getTransactionHash() {
    return getBitcoinTransaction().get("hash").getAsString();
  }

  public long getValue() {
    return getBitcoinTransaction().get("value").getAsLong();
  }

  public String getStatus() {
    return getBitcoinTransaction().get("status").getAsString();
  }

  public String getActor() {
    JsonObject bitcoinTransaction = getBitcoinTransaction();

    if (getType().equals("incoming")) {
      JsonObject input = bitcoinTransaction.getAsJsonArray("inputs")
          .get(0).getAsJsonObject()
          .get("output").getAsJsonObject();
      return input.get("address").getAsString();
    }

    if (getType().equals("outgoing")) {
      for (JsonElement element : bitcoinTransaction.getAsJsonArray("outputs")) {
        JsonObject output = element.getAsJsonObject();
        JsonObject metadata = output.getAsJsonObject("metadata");
        if (Math.abs(output.get("value").getAsLong()) != Math.abs(getValue()))
          continue;

        String address = output.get("address").getAsString();
        if (metadata.has("pay_to_email"))
          return metadata.get("pay_to_email").getAsString();
        return address;
      }
    }

    return null;
  }

  public Date getCreatedAt() {
    String dateString = getBitcoinTransaction().get("created_at").getAsString();

    Date createdAt = null;
    try {
      createdAt = DATE_FORMATTER.parse(dateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return createdAt;
  }
}
