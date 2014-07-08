package com.bitvault;

import java.util.ArrayList;

public class BaseCollection {
	
	public  String url;
	public static String ACCEPT;
	
	private ArrayList<Base> collection = new ArrayList<Base>();
	
	// public abstract void parse(String json);
	
	public Base get(int index) {
		return this.collection.get(index);
	}
	
	public void add(Base element) {
		this.collection.add(element);
	}
}
