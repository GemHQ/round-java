package com.bitvault;
import java.io.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Addresses extends Base {
	
	public Addresses() {
		
	this.ACCEPT = "application/vnd.bitvault.address+json;version=1.0";	
	
	}
	 //ACCEPT = "application/vnd.bitvault.address+json;version=1.0";
	
	public void parse(String json) {
		
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		
		/*String key = jobject.get("key").toString();
		 String url = jobject.get("url").toString();*/
		   
		   String path =jobject.get("path").toString();
		   String s =jobject.get("string").toString();
		   
		  System.out.println("-----------------------");
		  /*  System.out.println("key:"+key); 
		    System.out.println("url:"+url); */
		   System.out.println("path:"+path);
		   System.out.println("string:"+s);
		   
		  
		
	}

}


