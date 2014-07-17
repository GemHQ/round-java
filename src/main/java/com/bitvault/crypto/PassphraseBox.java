package com.bitvault.crypto;
import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.abstractj.kalium.crypto.SecretBox;

import static org.abstractj.kalium.NaCl.sodium;

import org.abstractj.kalium.crypto.Random;

import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES;

public class PassphraseBox {
	
    String salt;
	byte[] nonce;
	int iterations;
	byte[] key;
	int iterat = 100000;
	String ciphertext;
	String passphrase;
	SecretBox  box;
	String plaintext;

	public PassphraseBox(String p, String s, int i)
	{
		this.passphrase=p;
		this.salt= s;
		//this.nonce = n;
		this.iterations =i;
        //this.ciphertext =c;
		
	}
	
	public PassphraseBox(String p)
	{
		this.passphrase = p;
	}
	
/*	private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }*/
	
	public void initialize (String passphrase, String salt, int iterations)throws NoSuchAlgorithmException , InvalidKeySpecException
	{
	
		Random r = new Random();
		
		PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt.getBytes(), iterations, (64 * 8));
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    this.key = skf.generateSecret(spec).getEncoded();
	     
		if(salt==null)
			this.salt=(r.randomBytes(16)).toString();
		else
			this.salt=salt;
		
		if(iterations ==0)
			this.iterations= iterat;
		else 
			this.iterations=iterations;
		
		box = new SecretBox(this.key);
	}
	
	public String decrypt(String passphrase, String salt, String nonce, int iterations, String ciphertext)
	{
		PassphraseBox box = new PassphraseBox(passphrase, salt, iterations);
		String s=box.decrypt(nonce, ciphertext);
		return s;
	}
	
	public String decrypt(String nonce, String ciphertext)
	{
		String s= (box.decrypt(nonce.getBytes(), ciphertext.getBytes())).toString();
		return s;
	}
	
	public void encrypt(String plaintext, String passphrase)
	{
		PassphraseBox box = new PassphraseBox(passphrase);
		box.encrypt(plaintext);
	}
	public void encrypt(String plaintext)
	{
		Random r = new Random();
		this.nonce = r.randomBytes(XSALSA20_POLY1305_SECRETBOX_NONCEBYTES);
		this.ciphertext = (box.encrypt(nonce, plaintext.getBytes())).toString();
		
		
	}
	
}
