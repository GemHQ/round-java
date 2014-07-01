package com.bitvault;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Application {

	public static final String ACCEPT = "application/vnd.bitvault.application+json;version=1.0";
	
	public String key;
	public String name;
	public String api_token;
	public String callback_url;
	public String url;
	
	public String walletsUrl;
	
	private WalletsCollection walletsCollection;
	
	public Application(String url) {
		this.url = url;
		
		String response = Client.getHttpClient().get(this.url, ACCEPT);
		this.parse(response);
	}
	
	private void parse(String response) {
		
		
		JsonElement jelement = new JsonParser().parse(response.toString());
	       JsonObject  jobject = jelement.getAsJsonObject();
	        
	       String key = jobject.get("key").toString();
	       String name = jobject.get("name").toString();
	       String api_token = jobject.get("api_token").toString();
	       String callback_url = jobject.get("callback_url").toString();
	       String url = jobject.get("url").toString();
	       
	      /* System.out.println("key:"+key);       
	       System.out.println("name:"+name);
	       System.out.println("api_token:"+api_token);
	       System.out.println("callback_url:"+callback_url);
	       System.out.println("url:"+url);*/
	        
	        
	        
	       JsonObject jobject1 = jobject.getAsJsonObject("wallets");
	        String keyw = jobject1.get("key").toString();
	        String urlw=jobject1.get("url").getAsString();
	       // System.out.println("wallets");
	       // System.out.println("  key"+keyw) ; 
	        //System.out.println(" url"+urlw) ;
	        
	       
	        
	        this.walletsUrl = urlw;
	        walletsCollection = new WalletsCollection(this.walletsUrl);
	        
	        /*
	        JsonObject jobject2 = jobject.getAsJsonObject("owner");
	        String keyo = jobject2.get("key").toString();
	        String urlo=jobject2.get("url").toString();
	        System.out.println("owner");
	        System.out.println("   key"+keyo) ; 
	        System.out.println("   url"+urlo) ; */
	        
	    
	      
	      // Instantiate Application object
	        
	        this.key= key;
	        this.name= name;
	        this.api_token= api_token;
	        this.callback_url= callback_url;
	        this.url = url;
	}
	
	public WalletsCollection getWallets() {
		if (this.walletsCollection == null) {
			this.walletsCollection = new WalletsCollection(this.walletsUrl);
		}
		
		return this.walletsCollection;
	}
}