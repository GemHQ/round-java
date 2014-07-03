package com.bitvault;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AddressCollection {

	public String key;
	public String url;
	
	public static final String ACCEPT = "application/vnd.bitvault.address_list+json;version=1.0";
	public ArrayList<Addresses> address = new ArrayList<Addresses>();
	
	
public AddressCollection(String url)  {
		
		this.url = url;
	
		// Fetch accounts resource
		String addr= Client.getHttpClient().get(this.url, ACCEPT);
	    System.out.println(address);
		this.parse(addr);
		
	}
	
	private void parse(String addr) {
		
		//JsonObject jobject1 = jobject.getAsJsonObject(0);
		JsonElement jelement = new JsonParser().parse(addr);
	    JsonArray a = jelement.getAsJsonArray();
	    
	    for(int i=0; i<a.size();i++){
	    	
	    	 JsonObject  jobject = a.get(i).getAsJsonObject();
	    	
	    	 //Create Account object
	    	Addresses address1 = new Addresses();
	    	
	    	//Call account.parse with string
            address1.parse(jobject.toString());
            
            
          //Add to accounts collection 
            this.address.add(address1);
	    	
	    	
	    	
	    }       
        
		   /* String key = jobject.get("key").toString();
			 String url = jobject.get("url").toString();
			 
			   System.out.println("-----------------------");
			    System.out.println("key:"+key); 
			    System.out.println("url:"+url); */
}


	public Addresses create(String url) {
		
		System.out.println("URL:"+this.url);
		System.out.println( Addresses.ACCEPT);
		
	
		String response = Client.getHttpClient().get(this.url, Addresses.ACCEPT);
		System.out.println(response);
		Addresses addr1 = new Addresses();
		addr1.parse(response);
		address.add(addr1);
		return addr1;
		
	}

	
}
