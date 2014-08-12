package com.bitvault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class AccountCollection extends ResourceCollection<Account> {
	public static final String RESOURCE_NAME = "accounts";
	
	private Wallet wallet;
	
	public AccountCollection(String url, Client client, Wallet wallet)  {
		super(url, client, RESOURCE_NAME);	
		
		this.wallet = wallet;
	}
	
	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Account account = new Account(resource, this.client);
			this.add(account);
		}
	}
	
	@Override
	public Account get(int index) {
		Account account = super.get(index);
		account.setWallet(this.wallet);
		return account;
	}

	public void create(String name) {
		
	}
}
