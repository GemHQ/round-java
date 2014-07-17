package com.bitvault.crypto;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.abstractj.kalium.crypto.SecretBox;

import static org.abstractj.kalium.NaCl.sodium;

import org.abstractj.kalium.crypto.Random;

public class PassphraseBox {
	
    byte[] salt;
	String nonce;
	int iterations;
	byte[] key;
	int iterat = 100000;
	String ciphertext;

	/*public PassphraseBox(String s, String i, String n, String c)
	{
		this.salt= s;
		this.nonce = n;
		this.iterations =i;
        this.ciphertext =c;
		
	}*/
	
	public void initialize (String passphrase, byte[] salt, int iterations)throws NoSuchAlgorithmException , InvalidKeySpecException
	{
	
		Random r = new Random();
		
		PBEKeySpec spec = new PBEKeySpec(null, this.salt, this.iterations, 64 * 8);
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    this.key = skf.generateSecret(spec).getEncoded();
	     
		if(salt==null)
			this.salt=r.randomBytes(16);
		else
			this.salt=salt;
		
		if(iterations ==0)
			this.iterations= iterat;
		else 
			this.iterations=iterations;
		
		
	}
	
	public static void decrypt (String passphrase, String ciphertext)
	{
		PassphraseBox box = new PassphraseBox(passphrase, this.salt, this.iterations);
		box.decrypt(passphrase,ciphertext);
	}
	
	public void decrypt(String nonce, String ciphertext)
	{
		
	}
	
	
}
