package com.bitvault;
import java.io.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Addresses {
	
	
	public static final String ACCEPT = "application/vnd.bitvault.address+json;version=1.0";
	
	public void parse(String json) {
		
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		String key = jobject.get("key").toString();
		 String url = jobject.get("url").toString();
		   
		   
		   System.out.println("-----------------------");
		    System.out.println("key:"+key); 
		    System.out.println("url:"+url); 
		  
		
	}

}


