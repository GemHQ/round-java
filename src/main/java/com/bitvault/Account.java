package com.bitvault;


import java.io.*;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Account {
	public static final String ACCEPT = "application/vnd.bitvault.account+json;version=1.0";
	public static final String CONTENT_TYPE = "application/vnd.bitvault.account_create+json;version=1.0";
	
	public void parse(String json) {
		
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		
		String id = jobject.get("id").toString();
		
		
		JsonObject jobject1 = jobject.getAsJsonObject("properties");
		JsonObject jobject2 = jobject1.getAsJsonObject("url");
		
		String type = jobject2.get("type").toString();
		String format = jobject2.get("format").toString();
		Boolean readonly = jobject2.get("readonly").getAsBoolean();
		
		System.out.println(id);
		System.out.println(type);
		System.out.println(format);
		System.out.println(readonly);
		
	}

}
