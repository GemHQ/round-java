package com.bitvault;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class WalletsCollection {
	
	public String url;
	
public String AccountsUrl;
	

	public static final String ACCEPT = "application/vnd.bitvault.wallet_list+json;version=1.0";
	//private String urll;
	
	public ArrayList<Wallet> wallets = new ArrayList<Wallet>();
	
	public WalletsCollection(String url)  {
		this.url = url;
		
		
		// Fetch wallets resource
		String wallets= Client.getHttpClient().get(this.url, ACCEPT);
		
		//System.out.println(wallets);
		
		this.parse(wallets);
		
	}
	
	// Parse wallets JSON
	private void parse(String wallets) {
		
		//JsonObject jobject1 = jobject.getAsJsonObject(0);
		JsonElement jelement = new JsonParser().parse(wallets);
	    JsonArray a = jelement.getAsJsonArray();
	    
	    for(int i=0; i<a.size();i++){
	    	
            JsonObject  jobject = a.get(i).getAsJsonObject();
        
            Wallet wallet = new Wallet();
            wallet.parse(jobject.toString());
		    
            this.wallets.add(wallet);
            
		    // below is just to see whether the wallets collection is parsed correctly or not
		    // this below block can be commented out
		    
		   /* System.out.println("-----------------------");
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
            System.out.println("  url:"+urlt);*/
	        
	        /*this.key= key;
	        this.name= name;
	        this.backup_public_seed= backup_public_seed;
	        this.cosigner_public_seed= cosigner_public_seed;
	        this.primary_public_seed= cosigner_public_seed;
	        this.network= network;
	        
	        this.url = url;*/
	    }
	
		
		
		
		}
			

		
		
	}
	

