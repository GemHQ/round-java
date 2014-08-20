package com.bitvault;

import java.io.IOException;

import com.bitvault.Client.UnexpectedStatusCodeException;
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
			account.setWallet(wallet);
			this.add(account);
		}
	}
	
	@Override
	public Account get(int index) {
		Account account = super.get(index);
		return account;
	}

	public Account create(String name) {
		JsonObject body = new JsonObject();
		body.addProperty("name", name);
		
		JsonObject resource = null;
		try {
			resource = this.client.performRequest(this.url, RESOURCE_NAME, "create", body).getAsJsonObject();
		} catch(IOException e) {
			return null;
		} catch(UnexpectedStatusCodeException e) {
			return null;
		}
		
		Account account = new Account(resource, this.client);
		this.add(account);
		return account;
	}
}
