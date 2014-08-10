package com.bitvault;

public class Application extends Resource{
	
	public String walletsUrl;
	
	private WalletsCollection walletsCollection;
	
	public Application(String url, Client client) {
		super(url, client);
	}
	
	public WalletsCollection getWallets() {
		if (this.walletsCollection == null) {
			this.walletsCollection = new WalletsCollection(this.getWalletsUrl(), this.client);
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