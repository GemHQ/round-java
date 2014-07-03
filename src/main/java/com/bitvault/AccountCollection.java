package com.bitvault;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AccountCollection {
	
	public String key;
	public String name;
	public String path;
	public String balance;
	public String pending_balance;
	public String url;
	
	public static final String ACCEPT = "application/vnd.bitvault.account_list+json;version=1.0";
	public ArrayList<Account> accounts = new ArrayList<Account>();
	
	public AccountCollection(String url)  {
		
		this.url = url;
	
		// Fetch accounts resource
		String account= Client.getHttpClient().get(this.url, ACCEPT);
	    System.out.println(account);
		this.parse(account);
		
	}
	
	private void parse(String account) {
		
		//JsonObject jobject1 = jobject.getAsJsonObject(0);
		JsonElement jelement = new JsonParser().parse(account);
	    JsonArray a = jelement.getAsJsonArray();
	    
	    for(int i=0; i<a.size();i++){
	    	
	    	 JsonObject  jobject = a.get(i).getAsJsonObject();
	    	
	    	 //Create Account object
	    	Account accountt = new Account();
	    	
	    	//Call account.parse with string
            accountt.parse(jobject.toString());
            
            
          //Add to accounts collection 
            this.accounts.add(accountt);
	    	
	    	
	    	
	    }       
        
		   /* String key = jobject.get("key").toString();
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
			    System.out.println("pending_balance:"+pending_balance);*/
			    
			
	   /* this.key= key;
        this.name= name;
        this.path= path;
        this.balance=balance;
        this.pending_balance= pending_balance;
        this.url = url;*/
			}


	public Account create(String name) {
		
		System.out.println("URL:"+this.url);
		System.out.println(name);
		System.out.println( Account.CONTENT_TYPE);
		System.out.println( Account.ACCEPT);
		
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		String response = Client.getHttpClient().post(this.url, Account.CONTENT_TYPE, Account.ACCEPT, object.toString());
		System.out.println(response);
		Account account = new Account();
		account.parse(response);
		accounts.add(account);
		return account;
		
	}


}
