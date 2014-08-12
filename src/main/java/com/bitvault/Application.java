package com.bitvault;

public class Application extends Resource{
	
	public static final String RESOURCE_NAME = "application";
	
	public String walletsUrl;
	
	private WalletCollection walletsCollection;
	
	public Application(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}
	
	public WalletCollection wallets() {
		if (this.walletsCollection == null) {
			this.walletsCollection = new WalletCollection(this.getWalletsUrl(), this.client);
		}
		
		return this.walletsCollection;
	}
	
	public String getWalletsUrl() {
		if (this.walletsUrl == null) {
			this.walletsUrl = this.resource.get("wallets").getAsJsonObject()
				.get("url").getAsString();
		}
		
		return this.walletsUrl;
	}
}