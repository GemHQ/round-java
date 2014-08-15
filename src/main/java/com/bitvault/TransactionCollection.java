package com.bitvault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class TransactionCollection extends ResourceCollection<Transaction> {

	public static final String RESOURCE_NAME = "transactions";
	
	public TransactionCollection(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}

	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Transaction transaction = new Transaction(resource, this.client);
			this.collection.add(transaction);
		}
	}

}
