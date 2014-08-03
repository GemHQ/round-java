package com.bitvault;



public class Account extends Resource{
	
	public static final String CONTENT_TYPE = "application/vnd.bitvault.account+json;version=1.0";
	
	public Account(String url, Client client){
		super(url, client);
	}

}
