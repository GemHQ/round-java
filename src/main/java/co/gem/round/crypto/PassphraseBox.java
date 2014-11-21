package co.gem.round.crypto;

import co.gem.round.encoding.Hex;

import org.abstractj.kalium.crypto.SecretBox;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBox {

  private byte[] salt;
  private byte[] nonce;
  private byte[] ciphertext;
  private SecretBox box;

  final int KEYBYTES = 32;
  final int NONCEBYTES = 24;
  final int ZEROBYTES = 32;
  final int BOXZEROBYTES = 16;

  static final int DEFAULT_ITERATIONS = 100000;

  public PassphraseBox(String passphrase, String salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException {

    if (salt == null) {
      SecureRandom random = new SecureRandom();
      this.salt = new byte[16];
      random.nextBytes(this.salt);
    } else {
      this.salt = Hex.decode(salt);
    }

    PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), this.salt , iterations, 32 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    byte[] key = skf.generateSecret(spec).getEncoded();
    this.box = new SecretBox(key);
  }

  public String decrypt(String nonce, String ciphertext) {
    this.nonce = Hex.decode(nonce);
    this.ciphertext = Hex.decode(ciphertext);
    String message = new String(this.box.decrypt(this.nonce, this.ciphertext));
    return message;
  }

  public EncryptedMessage encrypt(String message) {
    byte[] messageBytes = message.getBytes();
    SecureRandom random = new SecureRandom();
    this.nonce = new byte[NONCEBYTES];
    random.nextBytes(this.nonce);

    this.ciphertext = this.box.encrypt(this.nonce, messageBytes);

    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = Hex.encode(this.ciphertext);
    encrypted.nonce = Hex.encode(this.nonce);
    encrypted.salt = Hex.encode(this.salt);
    encrypted.iterations = DEFAULT_ITERATIONS;

    return encrypted;
  }

  public static String decrypt(String passphrase, EncryptedMessage encryptedMessage) throws NoSuchAlgorithmException, InvalidKeySpecException {
    PassphraseBox box = new PassphraseBox(passphrase, encryptedMessage.salt, encryptedMessage.iterations);
    return box.decrypt(encryptedMessage.nonce, encryptedMessage.ciphertext);
  }

  public static EncryptedMessage encrypt(String passphrase, String message) throws NoSuchAlgorithmException, InvalidKeySpecException {
    PassphraseBox box = new PassphraseBox(passphrase, null, DEFAULT_ITERATIONS);
    return box.encrypt(message);
  }
}