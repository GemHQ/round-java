package com.bitvault;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Resource {

	private String key;
	
	protected String url;
	protected JsonObject resource;
	protected Client client;
	protected String resourceName;
	
	public static final String DEFAULT_ACTION = "get";
	
	public Resource(String url, Client client, String resourceName)
            throws Client.UnexpectedStatusCodeException, IOException {
		this.url = url;
		this.client = client;
		this.resourceName = resourceName;

		this.resource = this.client.performRequest(this.url, this.resourceName, DEFAULT_ACTION, null).getAsJsonObject();
	}
	
	public Resource(JsonObject resource, Client client, String resourceName) {
		this.resource = resource;
		this.client = client;
		this.resourceName = resourceName;
		
		if (this.resource.has("url")) 
			this.url = this.resource.get("url").getAsString();
	}
	
	public String getKey() {
		if (this.key == null)
			this.key = this.resource.get("key").getAsString();
		
		return this.key;
	}
}
