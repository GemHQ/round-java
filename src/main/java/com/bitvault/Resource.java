package com.bitvault;

import java.io.IOException;

import com.google.gson.JsonObject;

public class Resource {

	private String url;
	private String key;
	protected JsonObject resource;
	protected Client client;
	protected String defaultAction = "get";
	
	public Resource(String url, Client client) {
		this.url = url;
		this.client = client;
		
		String resourceName = this.getClass().getSimpleName().toLowerCase();
		try {
			this.resource = this.client.performRequest(this.url, resourceName, this.defaultAction, null);
		} catch(Client.UnexpectedStatusCodeException exception) {
			System.out.println(exception.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
