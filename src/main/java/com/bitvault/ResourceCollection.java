package com.bitvault;

import java.util.ArrayList;

public class ResourceCollection extends Resource {

	protected String defaultAction = "list";
	protected ArrayList<Resource> collection = new ArrayList<Resource>();
	
	public ResourceCollection(String url, Client client) {
		super(url, client);
	}
	
	public Resource get(int index) {
		return this.collection.get(index);
	}
	
	public void add(Resource element) {
		this.collection.add(element);
	}
	
	public int size() {
		return this.collection.size();
	}
}