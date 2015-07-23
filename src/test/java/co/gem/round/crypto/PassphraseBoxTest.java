package co.gem.round.crypto;

import co.gem.round.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.spongycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian on 7/30/14.
 */
@RunWith(Theories.class)
public class PassphraseBoxTest {

  private static class TestCyphertext {
    public String passphrase;
    public String cleartext;
    public EncryptedMessage encrypted;
  }

  @DataPoints
  public static List<TestCyphertext> cipherTexts() throws
      URISyntaxException, FileNotFoundException, IOException {
    JsonObject json = Utils.loadJsonResource("/wallet_ciphertexts.json");
    JsonArray jsonCiphertexts = json.getAsJsonArray("ciphertexts");
    ArrayList<TestCyphertext> ciphertexts = new ArrayList<>();
    for (JsonElement e : jsonCiphertexts) {
      JsonObject obj = e.getAsJsonObject();
      TestCyphertext ciphertext = new TestCyphertext();
      ciphertext.passphrase = obj.get("passphrase").getAsString();
      ciphertext.cleartext = obj.get("cleartext").getAsString();
      ciphertext.encrypted = EncryptedMessage.fromJson(obj.get("encrypted").getAsJsonObject());
      ciphertexts.add(ciphertext);
    }

    return ciphertexts;
  }

  @Theory
  public void testWalletDecrypt(TestCyphertext ciphertext) throws
      NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException,
      InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException,
      InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    EncryptedMessage encrypted = PassphraseBox.encrypt(ciphertext.passphrase, ciphertext.cleartext);
    String decrypted = PassphraseBox.decrypt(ciphertext.passphrase, encrypted);
    String originalDecrypted = PassphraseBox.decrypt(ciphertext.passphrase, ciphertext.encrypted);
    Assert.assertEquals(ciphertext.cleartext, decrypted);
    Assert.assertEquals(originalDecrypted, decrypted);
  }

}