package com.bitvault;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.bitvault.multiwallet.MultiWallet;
import com.google.gson.JsonObject;
import com.neilalexander.jnacl.EncryptedMessage;
import com.neilalexander.jnacl.PassphraseBox;

public class Wallet extends Resource {
	
	public static final String RESOURCE_NAME = "wallet";
	
	private AccountCollection accountsCollection;
	private EncryptedMessage encryptedSeed;
	private String accountsUrl;
	
	public Wallet(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}
	
	public Wallet(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}
	
	public void unlock(String passphrase, UnlockedWalletCallback callback) {
		
		String decryptedSeed = null;
		try {
			decryptedSeed = PassphraseBox.decrypt(passphrase, this.encryptedSeed);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			return;
		}
		
		MultiWallet wallet = new MultiWallet(decryptedSeed);
		callback.execute(wallet);
	}
	
	public AccountCollection accounts() {
		if (this.accountsCollection == null) {
			this.accountsCollection = new AccountCollection(this.getAccountsUrl(), this.client, this);
		}
		
		return this.accountsCollection;
	}
	
	public String getAccountsUrl() {
		if (this.accountsUrl == null) {
			this.accountsUrl = this.resource.getAsJsonObject("accounts")
					.get("url").getAsString();
		}
		
		return this.accountsUrl;
	}
	
	public EncryptedMessage getEncryptedSeed() {
		if (this.encryptedSeed ==  null) {
		    JsonObject seedObject = this.resource.getAsJsonObject("primary_private_seed");
		    
		    EncryptedMessage encryptedMessage = new EncryptedMessage();
		    encryptedMessage.ciphertext = seedObject.get("ciphertext").toString();
		    encryptedMessage.salt = seedObject.get("salt").toString();
		    encryptedMessage.nonce = seedObject.get("nonce").toString();
		    encryptedMessage.iterations = Integer.parseInt(seedObject.get("iterations").toString());
		    this.encryptedSeed = encryptedMessage;
		}
		
		return this.encryptedSeed;
	}
}
