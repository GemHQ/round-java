package com.bitvault;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.JsonArray;

public abstract class ResourceCollection<T> {

	protected String url;
	protected Client client;
	protected String resourceName;
	
	protected ArrayList<T> collection = new ArrayList<T>();
	
	public static final String DEFAULT_ACTION = "list";
	
	public ResourceCollection(String url, Client client, String resourceName) {
		this.url = url;
		this.client = client;
		this.resourceName = resourceName;
		
		JsonArray objects = null;
		try {
			objects = this.client.performRequest(this.url, this.resourceName, DEFAULT_ACTION, null).getAsJsonArray();
		} catch(Client.UnexpectedStatusCodeException exception) {
			System.out.println(exception.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.populateCollection(objects);
	}
	
	public T get(int index) {
		return this.collection.get(index);
	}
	
	public void add(T element) {
		this.collection.add(element);
	}
	
	public int size() {
		return this.collection.size();
	}
	
	public abstract void populateCollection(JsonArray array);
}