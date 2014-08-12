package com.bitvault;

import com.google.gson.JsonObject;

public class Account extends Resource{
	
	public static final String RESOURCE_NAME = "account";
	
	private Wallet wallet;
	
	public Account(String url, Client client){
		super(url, client, RESOURCE_NAME);
	}
	
	public Account(JsonObject resource, Client client){
		super(resource, client, RESOURCE_NAME);
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
}
