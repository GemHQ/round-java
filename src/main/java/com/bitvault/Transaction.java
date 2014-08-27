package com.bitvault;

import com.google.gson.JsonObject;

import java.io.IOException;

public class Transaction extends Resource {
	
	public static final String RESOURCE_NAME = "transaction";

	public Transaction(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}

	public Transaction(String url, Client client)
            throws Client.UnexpectedStatusCodeException, IOException {
		super(url, client, RESOURCE_NAME);
	}
	
	public String getType() {
		return this.resource.get("type").getAsString();
	}
	
	public String getTransactionHash() {
		JsonObject bitcoinTransaction = this.resource.getAsJsonObject("data");
		return bitcoinTransaction.get("hash").getAsString();
	}
}
