package com.bitvault;

import com.google.gson.JsonObject;

public class Transaction extends Resource {
	
	public static final String RESOURCE_NAME = "transaction";

	public Transaction(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}

	public Transaction(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}
	
}
