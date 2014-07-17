package com.bitvault;


import com.bitvault.crypto.PassphraseBox;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Wallet extends Base {
	
	private AccountCollection accountsCollection;
	//private String accountsUrl;
	  //private PassphraseBox pb;
	
	public Wallet() {
		
	}
	
	public void parse(String json) {
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		
		
	    String key = jobject.get("key").toString();
	    String name = jobject.get("name").toString();
	    String network = jobject.get("network").toString();
	    String backup_public_seed = jobject.get("backup_public_seed").toString();
	    String primary_public_seed = jobject.get("primary_public_seed").toString();
	    String cosigner_public_seed = jobject.get("cosigner_public_seed").toString();
	    String urll = jobject.get("url").toString();
	    
	    
	    JsonObject jobject1 = jobject.getAsJsonObject("default_account");
	    String keyd = jobject1.get("key").toString();
	    String urld=jobject1.get("url").getAsString();
	    
	    JsonObject jobject2 = jobject.getAsJsonObject("primary_private_seed");
	    String salt = jobject2.get("salt").toString();
	    String iterations=jobject2.get("iterations").toString();
	    String nonce = jobject2.get("nonce").toString();
	    String ciphertext = jobject2.get("ciphertext").toString();
	    
	    
	    JsonObject jobject3 = jobject.getAsJsonObject("accounts");
	    String keya = jobject3.get("key").toString();
	    String urla=jobject3.get("url").getAsString();
	    
	    JsonObject jobject4 = jobject.getAsJsonObject("transfers");
	    String keyt = jobject4.get("key").toString();
	    String urlt=jobject4.get("url").getAsString();
	    
	    
	    this.url = urla;
	  // pb = new PassphraseBox (salt, iterations, nonce, ciphertext);
	}
	
	public AccountCollection accounts() {
		if (this.accountsCollection == null) {
			this.accountsCollection = new AccountCollection(this.url);
		}
		
		return this.accountsCollection;
	}
	
}
