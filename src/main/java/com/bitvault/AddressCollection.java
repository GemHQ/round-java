package com.bitvault;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AddressCollection extends ResourceCollection<Address>{
	
	public static final String RESOURCE_NAME = "addresses";
	
	public AddressCollection(String url, Client client)  {
		super(url, client, RESOURCE_NAME);
	}
	
	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Address address = new Address(resource, this.client);
			this.add(address);
		}
	}
}
