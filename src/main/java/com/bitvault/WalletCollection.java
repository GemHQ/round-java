package com.bitvault;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.bitvault.Client.UnexpectedStatusCodeException;
import com.bitvault.multiwallet.MultiWallet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neilalexander.jnacl.EncryptedMessage;
import com.neilalexander.jnacl.PassphraseBox;

public class WalletCollection extends ResourceCollection<Wallet>{
	
	public static final String RESOURCE_NAME = "wallets";
	
	public WalletCollection(String url, Client client)  {
		super(url, client, RESOURCE_NAME);
	}	

	public Wallet create(String name, String passphrase) {
		MultiWallet multiWallet = MultiWallet.generate(this.client.networkMode);
		
		EncryptedMessage primaryPrivateSeed = null;
		try {
			primaryPrivateSeed = PassphraseBox.encrypt(passphrase, multiWallet.serializedPrimaryPrivateSeed());
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeySpecException e) {
			return null;
		}
		
		JsonObject encryptedPrivateSeedJson = new JsonObject();
		encryptedPrivateSeedJson.addProperty("nonce", primaryPrivateSeed.nonce);
		encryptedPrivateSeedJson.addProperty("salt", primaryPrivateSeed.salt);
		encryptedPrivateSeedJson.addProperty("iterations", primaryPrivateSeed.iterations);
		encryptedPrivateSeedJson.addProperty("ciphertext", primaryPrivateSeed.ciphertext);
		
		JsonObject body = new JsonObject();
		body.addProperty("name", name);
		body.addProperty("network", "bitcoin_testnet");
		body.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicSeed());
		body.addProperty("backup_public_seed", multiWallet.serializedBackupPublicSeed());
		body.add("primary_private_seed", encryptedPrivateSeedJson);
		
		JsonObject resource = null;
		try {
			resource = this.client.performRequest(this.url, RESOURCE_NAME, "create", body).getAsJsonObject();
		} catch(IOException e) {
			return null;
		} catch(UnexpectedStatusCodeException e) {
			return null;
		}
		
		Wallet wallet = new Wallet(resource, this.client);
		this.add(wallet);
		return wallet;
	}

	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Wallet wallet = new Wallet(resource, this.client);
			this.add(wallet);
		}
	}
}
	

