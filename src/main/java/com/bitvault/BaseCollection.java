package com.bitvault;

import java.util.ArrayList;

public abstract class BaseCollection {
	
	private ArrayList<Base> collection;
	
	public abstract void parse(String json);
	
}
