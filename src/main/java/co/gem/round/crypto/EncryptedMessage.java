package co.gem.round.crypto;

import com.google.gson.JsonObject;

/**
 * Created by julian on 11/21/14.
 */
public class EncryptedMessage {
    public String salt;
    public String iv;
    public String nonce;
    public String ciphertext;
    public int iterations;

    public JsonObject asJsonObject() {
        JsonObject serialized = new JsonObject();
        serialized.addProperty("salt", salt);
        serialized.addProperty("ciphertext", ciphertext);
        serialized.addProperty("iterations", iterations);
        if (iv != null)
            serialized.addProperty("iv", iv);
        if (nonce != null)
            serialized.addProperty("nonce", iv);
        return serialized;
    }

    public static EncryptedMessage fromJson(JsonObject json) {
        EncryptedMessage message = new EncryptedMessage();
        if (json.has("salt"))
            message.salt = json.get("salt").getAsString();
        if (json.has("ciphertext"))
            message.ciphertext = json.get("ciphertext").getAsString();
        if (json.has("iterations"))
            message.iterations = json.get("iterations").getAsInt();
        if (json.has("iv"))
            message.iv = json.get("iv").getAsString();
        if (json.has("nonce"))
            message.nonce = json.get("nonce").getAsString();
        return message;
    }
}
