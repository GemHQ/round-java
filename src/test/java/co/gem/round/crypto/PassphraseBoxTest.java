package co.gem.round.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBoxTest {
  private static String passphrase = "passphrase";
  private static String clearText = "0123456789abcdef";

  @Test
  public void testEncryptAndDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    EncryptedMessage encrypted = PassphraseBox.encrypt(passphrase, clearText);
    String decrypted = PassphraseBox.decrypt(passphrase, encrypted);

    Assert.assertEquals(clearText, decrypted);
  }

  @Test
  public void testWalletDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = "718877e7aed7ef43c8aefbfce3a856b4a85a092cafc88a85a22a14c7ce632ac3f83beef10ac0797441209039ebd947c2";
    encrypted.iv = "680546fd230d044778c9e7da09712946";
    encrypted.salt = "e713895c90c226cb4d46c4ba4ac60371";
    encrypted.iterations = 99307;

    String result = PassphraseBox.decrypt("veryveryveryveryverylongpassphrase", encrypted);
    System.out.println(result);
    Assert.assertEquals("hellohellohello!", result);
  }

}