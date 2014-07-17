package com.bitvault.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.*;

public class PassphraseBoxTest {
	private static final String cipherText = "652f0af6af29e4d643b2fc26533c39591aa9a67cac961ade23330edde8ddec8b6124c2a43a658e8f5086c3df010668ec97534902da313b9d4f9a95bdd5162dd94faaaa48577d31c5911b22ef7ed06da5f2194804827b18a482ded1e754b1ec2886be311ddc1a388a5a15e605da980e4d22684b1433f256d0bb078f93d64226";
	private static final String nonce = "6f338e562e0defee6d74d87f199bad098f202fe788437f66";
	private static final String salt = "f554541cc823b3593a160c5ac269526f";
	private static final String passphrase = "passphrase";
	private static final String clearText = "tprv8ZgxMBicQKsPf4MukJZwUZSQhgrRP6SGXYh2Ds2hnAH5t6wKJtYq1ABc534anpFQSzYTY6j3C5PW8dngf7RLRPQsQLbCQcTv4LGUQZKmcUu";
	private static final int iterations = 100000;
	
	@Test public void decryptAddressTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
		PassphraseBox pass = new PassphraseBox(passphrase,salt,iterations);
	    pass.initialize(passphrase, salt, iterations);
		String s=pass.decrypt(passphrase, salt, nonce, iterations, cipherText);
		Assert.assertEquals(s, clearText);
		
	}
}
