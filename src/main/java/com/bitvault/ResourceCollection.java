package com.bitvault;

import java.util.ArrayList;

public class ResourceCollection extends Resource {

	private ArrayList<Resource> collection = new ArrayList<Resource>();
	
	// public abstract void parse(String json);
	
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
