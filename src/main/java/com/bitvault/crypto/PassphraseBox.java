package com.bitvault.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.abstractj.kalium.crypto.SecretBox;

import org.abstractj.kalium.crypto.Random;

public class PassphraseBox {
	
    byte[] salt;
	byte[] nonce;
	int iterations;
	byte[] key;
	int iterat = 100000;
	String ciphertext;
	String passphrase;
	NaCl box;
	String plaintext;

	public PassphraseBox(String p, String s, int i) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		this.passphrase= p;
		this.salt= hexStringToByteArray(s);
		//this.nonce = n;
		this.iterations =i;
        //this.ciphertext =c;
		this.initialize(p, this.salt, i);
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
	
	public void initialize (String passphrase, byte[] salt, int iterations)throws NoSuchAlgorithmException , InvalidKeySpecException
	{
	
		Random r = new Random();
		
		PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 32 * 8);
	    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	    
	    this.key = skf.generateSecret(spec).getEncoded();
	    
	   // NaCl obj = new NaCl(this.key);
	     
		if(salt==null)
			this.salt=r.randomBytes(16);
		else
			this.salt=salt;
		
		if(iterations ==0)
			this.iterations= iterat;
		else 
			this.iterations=iterations;
		
		//TODO Use NaCl to do this
		 this.box = new NaCl(this.key);
		
		//System.out.println(this.box.toString());
	}
	
	public static String decrypt(String passphrase, String salt, String nonce, int iterations, String ciphertext) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		PassphraseBox box = new PassphraseBox(passphrase, salt, iterations);
		String s=box.decrypt(nonce, ciphertext);
		
		return s;
	}
	
	public String decrypt(String nonce, String ciphertext)
	{
		byte[] nonceBytes = hexStringToByteArray(nonce);
		byte[] ciphertextBytes = hexStringToByteArray(ciphertext);
		String s= (this.box.decrypt(ciphertextBytes, nonceBytes)).toString();
	
		return s;
	}
	
	public void encrypt(String plaintext, String passphrase)
	{
		
	}
	public void encrypt(String plaintext)
	{

	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
