package co.gem.round.crypto;

import co.gem.round.encoding.Hex;
import org.spongycastle.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBox {

  private byte[] salt;
  private byte[] iv;
  private byte[] ciphertext;
  private Cipher cipher;
  private SecretKeySpec aesSecretKey;
  private Mac mac;

  final int IVBYTES = 16;

  static final int DEFAULT_ITERATIONS = 100000;

  public PassphraseBox(String passphrase, String salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {

    if (salt == null) {
      SecureRandom random = new SecureRandom();
      this.salt = new byte[16];
      random.nextBytes(this.salt);
    } else {
      this.salt = Hex.decode(salt);
    }

    PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), this.salt, iterations, 512);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    SecureRandom random = new SecureRandom();
    this.iv = new byte[IVBYTES];
    random.nextBytes(this.iv);
    byte[] key = skf.generateSecret(spec).getEncoded();
    this.aesSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, 0, 255), "AES");
    SecretKeySpec hmacSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, 256, 512), "HmacSHA256");
    mac = Mac.getInstance("HmacSHA256");
    mac.init(hmacSecretKey);
    this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
  }

  public String decrypt(String iv, String ciphertext) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    this.iv = Hex.decode(iv);
    this.ciphertext = Hex.decode(ciphertext);
    byte[] mac = Arrays.copyOfRange(this.ciphertext, -64, 0);
    this.ciphertext = Arrays.copyOfRange(this.ciphertext, 0, -64);
    this.mac.update(Arrays.concatenate(this.iv, this.ciphertext));
    if (!mac.equals(this.mac.doFinal())) {
      throw new RuntimeException("Invalid authentication code: ciphertext may have been tampered with.");
    }
    cipher.init(Cipher.DECRYPT_MODE, aesSecretKey, new IvParameterSpec(this.iv));
    return Hex.encode(cipher.doFinal(this.ciphertext));
  }

  public EncryptedMessage encrypt(String message) throws InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException {
    cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey, new IvParameterSpec(this.iv));
    byte[] es = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
    mac.update(Arrays.concatenate(iv, es));
    byte[] hmac = mac.doFinal();
    this.ciphertext = Arrays.concatenate(es, hmac);

    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = Hex.encode(this.ciphertext);
    encrypted.iv = Hex.encode(this.iv);
    encrypted.salt = Hex.encode(this.salt);
    encrypted.iterations = DEFAULT_ITERATIONS;

    return encrypted;
  }

  public static String decrypt(String passphrase, EncryptedMessage encryptedMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
    PassphraseBox box = new PassphraseBox(passphrase, encryptedMessage.salt, encryptedMessage.iterations);
    return box.decrypt(encryptedMessage.iv, encryptedMessage.ciphertext);
  }

  public static EncryptedMessage encrypt(String passphrase, String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
    PassphraseBox box = new PassphraseBox(passphrase, null, DEFAULT_ITERATIONS);
    return box.encrypt(message);
  }
}