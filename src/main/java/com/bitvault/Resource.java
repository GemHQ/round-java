package com.bitvault;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Resource {

	private String url;
	private String key;
	protected JsonObject resource;
	protected Client client;
	
	public Resource(String url, Client client) {
		this.url = url;
		this.client = client;
		String response = this.client.getHttpClient().get(this.url, null);
		this.parse(response);
	}
	
	private void parse(String response) {
		JsonElement element = new JsonParser().parse(response);
	    this.resource = element.getAsJsonObject();
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getKey() {
		if (this.key == null)
			this.key = this.resource.get("key").getAsString();
		
		return this.key;
	}
}
