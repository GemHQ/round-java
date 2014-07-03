package com.bitvault;


import java.io.*;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Account {
	
	private AddressCollection addressCollection;
	private String urladd;
	
	
	public static final String CONTENT_TYPE = "application/vnd.bitvault.account+json;version=1.0";
	public static final String ACCEPT = "application/vnd.bitvault.account_create+json;version=1.0";
	
	
	
	
	public void parse(String json) {
		
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		String key = jobject.get("key").toString();
	    String name = jobject.get("name").toString();
	    String path = jobject.get("path").toString();
		 String url = jobject.get("url").toString();
		 float balance = jobject.get("balance").getAsFloat();
		   float pending_balance = jobject.get("pending_balance").getAsFloat();
		   
		   
		   System.out.println("-----------------------");
		    System.out.println("key:"+key); 
		    System.out.println("name:"+name);
		    System.out.println("url:"+url); 
		    System.out.println("path:"+path); 
		    System.out.println("balance:"+balance);
		    System.out.println("pending_balance:"+pending_balance);
		    
		    this.urladd = url;
		
	}
	
	public AddressCollection getAddress() {
		if (this.addressCollection == null) {
			this.addressCollection = new AddressCollection(this.urladd);
		}
		
		return this.addressCollection;
	}

}
