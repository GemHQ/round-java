package com.bitvault;

import java.io.IOException;

import com.bitvault.Client.UnexpectedStatusCodeException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class AddressCollection extends ResourceCollection<Address>{
	
	public static final String RESOURCE_NAME = "addresses";
	
	public AddressCollection(String url, Client client)  {
		super(url, client, RESOURCE_NAME);
	}
	
	public Address create() {
		JsonElement response = null;
		try {
			response = this.client.performRequest(this.url, RESOURCE_NAME, "create", null);
		} catch (UnexpectedStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Address(response.getAsJsonObject(), this.client);
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
