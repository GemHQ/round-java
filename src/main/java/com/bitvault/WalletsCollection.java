package com.bitvault;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class WalletsCollection {

	public static final String ACCEPT = "application/vnd.bitvault.wallet_list+json;version=1.0";
	private String url;
	
	public WalletsCollection(String url)  {
		this.url = url;
		
		
		// Fetch wallets resource
		String wallets= Client.getHttpClient().get(this.url, ACCEPT);
		
		System.out.println(wallets);
		this.parse(wallets);
		
	}
	
	// Parse wallets JSON
	private void parse(String wallets) {
		
		//JsonObject jobject1 = jobject.getAsJsonObject(0);
		JsonElement jelement = new JsonParser().parse(wallets);
	    JsonArray a = jelement.getAsJsonArray();
	    
	    for(int i=0; i<a.size();i++){
	    	
            JsonObject  jobject = a.get(i).getAsJsonObject();
        
		    String key = jobject.get("key").toString();
		    String name = jobject.get("name").toString();
		    String network = jobject.get("network").toString();
		    String backup_public_seed = jobject.get("backup_public_seed").toString();
		    String primary_public_seed = jobject.get("primary_public_seed").toString();
		    String cosigner_public_seed = jobject.get("cosigner_public_seed").toString();
		    String url = jobject.get("url").toString();
		    
		    
		    JsonObject jobject1 = jobject.getAsJsonObject("default_account");
		    String keyd = jobject1.get("key").toString();
		    String urld=jobject1.get("url").getAsString();
		    
		    JsonObject jobject2 = jobject.getAsJsonObject("primary_private_seed");
		    String salt = jobject2.get("salt").toString();
		    String iterations=jobject2.get("iterations").getAsString();
		    String nonce = jobject2.get("nonce").toString();
		    String ciphertext = jobject2.get("ciphertext").toString();
		    
		    
		    JsonObject jobject3 = jobject.getAsJsonObject("accounts");
		    String keya = jobject3.get("key").toString();
		    String urla=jobject3.get("url").getAsString();
		    
		    JsonObject jobject4 = jobject.getAsJsonObject("transfers");
		    String keyt = jobject4.get("key").toString();
		    String urlt=jobject4.get("url").getAsString();
		    
		    
		    // below is just to see whether the wallets collection is parsed correctly or not
		    // this below block can be commented out
		    
		    System.out.println("-----------------------");
		    System.out.println("key:"+key); 
		    System.out.println("name:"+name);
		    System.out.println("network:"+network);
            System.out.println("backup_public_seed:"+backup_public_seed);
            System.out.println("primary_public_seed:"+primary_public_seed);
            System.out.println("cosigner_public_seed:"+cosigner_public_seed);
            System.out.println("url:"+url); 
            System.out.println("default_account");
            System.out.println("  key:"+keyd); 
            System.out.println("  url:"+urld);
            System.out.println("primary_private_seed");
            System.out.println("  salt:"+salt); 
            System.out.println("  nonce:"+nonce);
            System.out.println("  iterations:"+iterations);
            System.out.println("  ciphertext:"+ciphertext);
            
            System.out.println("accounts");
            System.out.println("  key:"+keya); 
            System.out.println("  url:"+urla);
            System.out.println("transfers");
            System.out.println("  key:"+keyt); 
            System.out.println("  url:"+urlt);
	    }
	
		
		
		
		}
			
		
		
		
	}
	

