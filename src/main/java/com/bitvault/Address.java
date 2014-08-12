package com.bitvault;

import com.google.gson.JsonObject;

public class Address extends Resource {
	
	public static final String RESOURCE_NAME = "address";
	
	public Address(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}
	
	public Address(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}
}


