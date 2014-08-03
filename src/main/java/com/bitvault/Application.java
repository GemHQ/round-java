package com.bitvault;

public class Application extends Resource{

	public static final String ACCEPT = "application/vnd.bitvault.application+json;version=1.0";
	
	public String name;
	public String api_token;
	public String callback_url;
	public String walletsUrl;
	
	private WalletsCollection walletsCollection;
	
	public Application(String url, Client client) {
		super(url, client);
	}
	
	public WalletsCollection getWallets() {
		if (this.walletsCollection == null) {
			this.walletsCollection = new WalletsCollection(this.walletsUrl, this.client);
		}
		
		return this.walletsCollection;
	}
}