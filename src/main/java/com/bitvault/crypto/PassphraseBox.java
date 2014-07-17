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
	byte[] nonce;
	int iterations;
	byte[] key;
	int iterat = 100000;
	byte[] ciphertext;
	String passphrase;
	SecretBox  box;

	public PassphraseBox(String p, byte[] s, int i)
	{
		this.passphrase=p;
		this.salt= s;
		//this.nonce = n;
		this.iterations =i;
        //this.ciphertext =c;
		
	}
	
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
		
		box = new SecretBox(key);
	}
	
	public void decrypt(String passphrase, byte[] salt, byte[] nonce, int iterations, byte[] ciphertext)
	{
		PassphraseBox box = new PassphraseBox(passphrase, salt, iterations);
		box.decrypt(nonce, ciphertext);
	}
	
	public void decrypt(byte[] nonce, byte[] ciphertext)
	{
		box.decrypt(nonce, ciphertext);
	}
	
	
}
