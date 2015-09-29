package co.gem.round.crypto;

import co.gem.round.encoding.Hex;
import org.spongycastle.crypto.*;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.paddings.ZeroBytePadding;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;
import org.abstractj.kalium.crypto.SecretBox;

import javax.crypto.*;
import javax.crypto.Mac;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class PassphraseBox {

    public enum Mode { AES, SODIUM }

    private byte[] aesKey;
    private byte[] salt;
    private byte[] iv;
    private BufferedBlockCipher encryptCipher;
    private BufferedBlockCipher decryptCipher;
    private SecretKeySpec aesSecretKey;
    private SecretKeySpec hmacSecretKey;
    private int iterations;
    private SecureRandom random;
    private SecretBox box;
    private Mode mode;

    final int IVBYTES = 16;
    final int SALTBYTES = 16;
    final int KEYBYTES = 32;

    public static final String UTF_8 = "UTF-8";

    final int ITERATIONS = 100000;
    final int ITERATIONS_WINDOW = 20000;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public PassphraseBox(String passphrase, String salt, int iterations, Mode mode) throws
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, NoSuchProviderException {
        this.mode = mode;

        random = new SecureRandom();
        if (salt == null) {
            this.salt = new byte[SALTBYTES];
            random.nextBytes(this.salt);
        } else {
            this.salt = Hex.decode(salt);
        }

        if (iterations == 0) {
            int randomWindow = random.nextInt(ITERATIONS_WINDOW);
            this.iterations = ITERATIONS + randomWindow;
        } else {
            this.iterations = iterations;
        }

        if (this.mode == Mode.AES) {
            PBEParametersGenerator generator = new PKCS5S2ParametersGenerator();
            generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(passphrase.toCharArray()), this.salt, this.iterations);
            byte[] key = ((KeyParameter) generator.generateDerivedParameters(KEYBYTES * 2 * 8)).getKey();

            this.aesKey = Arrays.copyOfRange(key, 0, KEYBYTES);
            this.aesSecretKey = new SecretKeySpec(aesKey, "AES");
            this.hmacSecretKey = new SecretKeySpec(Arrays.copyOfRange(key, KEYBYTES, KEYBYTES * 2), "HmacSHA256");

            this.encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new ZeroBytePadding());
            this.decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new ZeroBytePadding());
        } else if (this.mode == Mode.SODIUM) {
            PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), this.salt, iterations, 32 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] key = skf.generateSecret(spec).getEncoded();
            this.box = new SecretBox(key);
        }
    }

    private byte[] cipherData(BufferedBlockCipher cipher, byte[] data) throws
            InvalidCipherTextException, UnsupportedEncodingException {
        byte[] outBuf = new byte[cipher.getOutputSize(data.length)];
        int length = cipher.processBytes(data, 0, data.length, outBuf, 0);
        length += cipher.doFinal(outBuf, length);
        byte[] out = new byte[length];
        System.arraycopy(outBuf, 0, out, 0, length);
        return out;
    }

    public String decrypt(String iv, String nonce, String ciphertext) throws
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException,
            NoSuchAlgorithmException {
        if (this.mode == Mode.AES) {
            return decryptAes(iv, ciphertext);
        } else if (this.mode == Mode.SODIUM) {
            return decryptSodium(nonce, ciphertext);
        } else {
            throw new NoSuchAlgorithmException();
        }
    }

    public String decryptAes(String iv, String ciphertext) throws
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidCipherTextException,
            NoSuchAlgorithmException {
        this.iv = Hex.decode(iv);
        byte[] ctext = Hex.decode(ciphertext);
        // This "ciphertext" that we import is constructed from actual_ciphertext + hmacsha1(iv + actual_ciphertext)
        byte[] ctextb = Arrays.copyOfRange(ctext, 0, ctext.length - 32);
        byte[] mac = Arrays.copyOfRange(ctext, ctext.length - 32, ctext.length);


        // Recreate the hmac and verify it matches.
        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(this.hmacSecretKey);
        if (!Arrays.areEqual(mac, hmac.doFinal(Arrays.concatenate(this.iv, ctextb)))) {
            throw new RuntimeException("Invalid authentication code: ciphertext may have been tampered with.");
        }

        // Decrypt the actual_ciphertext.
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(aesKey), this.iv);
        decryptCipher.init(false, ivAndKey);
        return new String(cipherData(decryptCipher, ctextb), UTF_8);
    }

    public String decryptSodium(String nonce, String ciphertext) {
        byte[] nonceBytes = Hex.decode(nonce);
        byte[] ciphertextBytes = Hex.decode(ciphertext);
        String message = new String(this.box.decrypt(nonceBytes, ciphertextBytes));
        return message;
    }

    public EncryptedMessage encrypt(String message) throws
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException,
            InvalidCipherTextException, NoSuchAlgorithmException {

        this.iv = new byte[IVBYTES];
        random.nextBytes(this.iv);
        CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(aesKey), this.iv);
        encryptCipher.init(true, ivAndKey);
        byte[] es = cipherData(encryptCipher, message.getBytes(UTF_8));

        Mac hmac = Mac.getInstance("HmacSHA256");
        hmac.init(this.hmacSecretKey);
        byte[] digest = hmac.doFinal(Arrays.concatenate(this.iv, es));
        byte[] ciphertext = Arrays.concatenate(es, digest);

        EncryptedMessage encrypted = new EncryptedMessage();
        encrypted.ciphertext = Hex.encode(ciphertext);
        encrypted.iv = Hex.encode(this.iv);
        encrypted.salt = Hex.encode(this.salt);
        encrypted.iterations = iterations;

        return encrypted;
    }

    public static String decrypt(String passphrase, EncryptedMessage encryptedMessage) throws
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException,
            IllegalBlockSizeException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
        Mode mode = null;
        if (encryptedMessage.iv != null)
            mode = Mode.AES;
        else if (encryptedMessage.nonce != null)
            mode = Mode.SODIUM;
        else
            throw new NoSuchAlgorithmException();
        PassphraseBox box = new PassphraseBox(passphrase, encryptedMessage.salt, encryptedMessage.iterations, mode);
        return box.decrypt(encryptedMessage.iv, encryptedMessage.nonce, encryptedMessage.ciphertext);
    }

    public static EncryptedMessage encrypt(String passphrase, String message) throws
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException,
            IllegalBlockSizeException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
        PassphraseBox box = new PassphraseBox(passphrase, null, 0, Mode.AES);
        return box.encrypt(message);
    }
}