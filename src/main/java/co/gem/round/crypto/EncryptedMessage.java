package co.gem.round.crypto;

import com.google.gson.JsonObject;

/**
 * Created by julian on 11/21/14.
 */
public class EncryptedMessage {
  public String salt;
  public String iv;
  public String ciphertext;
  public int iterations;

  public JsonObject asJsonObject() {
    JsonObject serialized = new JsonObject();
    serialized.addProperty("salt", salt);
    serialized.addProperty("iv", iv);
    serialized.addProperty("ciphertext", ciphertext);
    serialized.addProperty("iterations", iterations);
    return serialized;
  }
}
