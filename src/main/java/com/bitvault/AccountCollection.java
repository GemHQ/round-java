package com.bitvault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class AccountCollection extends ResourceCollection<Account> {
	public static final String RESOURCE_NAME = "accounts";
	
	public AccountCollection(String url, Client client)  {
		super(url, client, RESOURCE_NAME);	
	}
	
	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Account account = new Account(resource, this.client);
			this.add(account);
		}
	}
}
