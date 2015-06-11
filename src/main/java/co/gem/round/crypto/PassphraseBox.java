package co.gem.round.crypto;

import co.gem.round.encoding.Hex;
import org.spongycastle.asn1.pkcs.PBEParameter;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.crypto.*;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

import javax.crypto.*;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBox {

  private byte[] salt;
  private byte[] iv;
  private Cipher cipher;
  private BufferedBlockCipher encryptCipher;
  private BufferedBlockCipher decryptCipher;
  private SecretKeySpec aesSecretKey;
  private Mac mac;

  final int IVBYTES = 16;

  static final int DEFAULT_ITERATIONS = 100000;

  public PassphraseBox(String passphrase, String salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {

    if (salt == null) {
      SecureRandom random = new SecureRandom();
      this.salt = new byte[16];
      random.nextBytes(this.salt);
    } else {
      this.salt = Hex.decode(salt);
    }

    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), this.salt, iterations, 512);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1", "BC");

    SecureRandom random = new SecureRandom();
    this.iv = new byte[IVBYTES];
    random.nextBytes(this.iv);
    byte[] key = skf.generateSecret(spec).getEncoded();
    this.aesSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, 0, 32), "AES");
    SecretKeySpec hmacSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, 32, 64), "HmacSHA256");
    mac = Mac.getInstance("HmacSHA256");
    mac.init(hmacSecretKey);
    this.cipher = Cipher.getInstance("AES/CBC/NoPadding", "BC");

    // The following is using spongycastle.
    byte[] aesKey = Arrays.copyOfRange(key, 0, 32);
    byte[] hmacKey = Arrays.copyOfRange(key, 32, 64);
    encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
    decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
    CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(aesKey), this.iv);
    encryptCipher.init(true, ivAndKey);
    decryptCipher.init(false, ivAndKey);
  }

  private byte[] cipherData(BufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException, UnsupportedEncodingException {
    byte[] outBuf = new byte[cipher.getOutputSize(data.length)];
    int length = cipher.processBytes(data, 0, data.length, outBuf, 0);
    length += cipher.doFinal(outBuf, length);
    byte[] out = new byte[length];
    System.arraycopy(outBuf, 0, out, 0, length);
    return out;
  }

  public String decrypt(String iv, String ciphertext) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException {
    this.iv = Hex.decode(iv);
    byte[] ctext = Hex.decode(ciphertext);
    byte[] mac = Arrays.copyOfRange(ctext, ctext.length - 32, ctext.length);
    byte[] ctextb = Arrays.copyOfRange(ctext, 0, ctext.length - 32);
    if (!Arrays.areEqual(mac, (this.mac.doFinal(Arrays.concatenate(this.iv, ctextb))))) {
      throw new RuntimeException("Invalid authentication code: ciphertext may have been tampered with.");
    }
//    byte[] prepend = { 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10};
//    cipher.init(Cipher.DECRYPT_MODE, aesSecretKey, new IvParameterSpec(this.iv));
    return Hex.encode(cipherData(decryptCipher, ctext));
//    cipher.init(Cipher.DECRYPT_MODE, aesSecretKey, new IvParameterSpec(this.iv));
//    return Hex.encode(cipher.doFinal(ctext));
  }

  public EncryptedMessage encrypt(String message) throws InvalidAlgorithmParameterException, InvalidKeyException,
      BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException {
    byte[] es = cipherData(encryptCipher, message.getBytes(StandardCharsets.UTF_8));
//    cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey, new IvParameterSpec(this.iv));
//    byte[] es = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
    mac.update(Arrays.concatenate(iv, es));
    byte[] hmac = mac.doFinal();
    byte[] ciphertext = Arrays.concatenate(es, hmac);

    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = Hex.encode(ciphertext);
    encrypted.iv = Hex.encode(this.iv);
    encrypted.salt = Hex.encode(this.salt);
    encrypted.iterations = DEFAULT_ITERATIONS;

    return encrypted;
  }

  public static String decrypt(String passphrase, EncryptedMessage encryptedMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    PassphraseBox box = new PassphraseBox(passphrase, encryptedMessage.salt, encryptedMessage.iterations);
    return box.decrypt(encryptedMessage.iv, encryptedMessage.ciphertext);
  }

  public static EncryptedMessage encrypt(String passphrase, String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    PassphraseBox box = new PassphraseBox(passphrase, null, DEFAULT_ITERATIONS);
    return box.encrypt(message);
  }
}