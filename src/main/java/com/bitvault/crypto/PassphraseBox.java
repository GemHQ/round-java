package com.bitvault.crypto;


import javax.crypto.SecretKeyFactory;

import org.abstractj.kalium.crypto.SecretBox;


public class PassphraseBox {
	String salt;
	String nonce;
	String iterations;
	String key;
	
	String ciphertext;
	
	PassphraseBox(String s, String i, String n, String k,String c)
	{
		this.salt= s;
		this.nonce = n;
		this.iterations =i;
		this.key=k;
		
		this.ciphertext =c;
		
		
			
	}
	
	void decrypt (String passphrase, String ciphertext)
	{
		box = new (passphrase, this.salt, this.iterations);
		box.decrypt(passphrase,ciphertext);
	}
	
	
}
