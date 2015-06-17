package co.gem.round.crypto;

import co.gem.round.encoding.Hex;
/*import org.spongycastle.asn1.pkcs.PBEParameter;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.crypto.*;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;
*/
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

import javax.crypto.*;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class PassphraseBox {

  private byte[] aesKey;
  private byte[] salt;
  private byte[] iv;
  private BufferedBlockCipher encryptCipher;
  private BufferedBlockCipher decryptCipher;
  private SecretKeySpec aesSecretKey;
  private SecretKeySpec hmacSecretKey;

  final int IVBYTES = 16;

  static final int DEFAULT_ITERATIONS = 100000;

  public PassphraseBox(String passphrase, String salt, int iterations) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException {

    SecureRandom random = new SecureRandom();
    if (salt == null) {
      this.salt = new byte[16];
      random.nextBytes(this.salt);
    } else {
      this.salt = Hex.decode(salt);
    }

    this.iv = new byte[IVBYTES];
    random.nextBytes(this.iv);

    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
    generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(passphrase.toCharArray()), this.salt, iterations);
    byte[] key = ((KeyParameter)generator.generateDerivedParameters(512)).getKey();


    this.aesKey = Arrays.copyOfRange(key, 0, 32);
    this.aesSecretKey = new SecretKeySpec(aesKey, "AES");
    this.hmacSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, 32, 64), "HmacSHA256");

    this.encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
    this.decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
  }

  private byte[] cipherData(BufferedBlockCipher cipher, byte[] data) throws InvalidCipherTextException, UnsupportedEncodingException {
    byte[] outBuf = new byte[cipher.getOutputSize(data.length)];
    int length = cipher.processBytes(data, 0, data.length, outBuf, 0);
    length += cipher.doFinal(outBuf, length);
    byte[] out = new byte[length];
    System.arraycopy(outBuf, 0, out, 0, length);
    return out;
  }

  public String decrypt(String iv, String ciphertext) throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException, NoSuchAlgorithmException {
    byte[] encryptedIv = Hex.decode(iv);
    byte[] ctext = Hex.decode(ciphertext);
    // This "ciphertext" that we import is constructed from actual_ciphertext + hmacsha1(iv + actual_ciphertext)
    byte[] ctextb = Arrays.copyOfRange(ctext, 0, ctext.length - 32);
    byte[] mac = Arrays.copyOfRange(ctext, ctext.length - 32, ctext.length);


    // Recreate the hmac and verify it matches.
    Mac hmac = Mac.getInstance("HmacSHA256");
    hmac.init(this.hmacSecretKey);
    if (!Arrays.areEqual(mac, hmac.doFinal(Arrays.concatenate(encryptedIv, ctextb)))) {
      throw new RuntimeException("Invalid authentication code: ciphertext may have been tampered with.");
    }

    // Decrypt the actual_ciphertext.
    CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(aesKey), encryptedIv);
    decryptCipher.init(false, ivAndKey);
    return new String(cipherData(decryptCipher, ctextb), "UTF-8");
  }

  public EncryptedMessage encrypt(String message) throws InvalidAlgorithmParameterException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException, NoSuchAlgorithmException {

    CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(aesKey), this.iv);
    encryptCipher.init(true, ivAndKey);
    byte[] es = cipherData(encryptCipher, message.getBytes(StandardCharsets.UTF_8));

    Mac hmac = Mac.getInstance("HmacSHA256");
    hmac.init(this.hmacSecretKey);
    byte[] digest = hmac.doFinal(Arrays.concatenate(this.iv, es));
    byte[] ciphertext = Arrays.concatenate(es, digest);

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