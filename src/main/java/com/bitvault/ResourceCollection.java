package com.bitvault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;

public abstract class ResourceCollection<T> {

	protected String url;
	protected Client client;
	protected String resourceName;
	
	protected List<T> collection = new ArrayList<T>();
    protected Map<String, T> map = new HashMap<String, T>();
	
	public static final String DEFAULT_ACTION = "list";
	
	public ResourceCollection(String url, Client client, String resourceName)
            throws Client.UnexpectedStatusCodeException, IOException {
		this.url = url;
		this.client = client;
		this.resourceName = resourceName;
		
		JsonArray objects =
                this.client.performRequest(this.url, this.resourceName, DEFAULT_ACTION, null).getAsJsonArray();
		
		this.populateCollection(objects);
	}
	
	public T get(int index) {
		return this.collection.get(index);
	}

    public T get(String key) { return this.map.get(key); }
	
	public void add(String key, T element) {
        this.collection.add(element);
        this.map.put(key, element);
	}
	
	public int size() {
		return this.collection.size();
	}
	
	public List<T> asList() {
		return collection;
	}
	
	public abstract void populateCollection(JsonArray array);
}