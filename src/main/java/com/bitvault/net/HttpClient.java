package com.bitvault.net;

import com.github.kevinsawicki.http.HttpRequest;

public class HttpClient {

	private String apiToken;
	
	public HttpClient(String apiToken) {
		this.apiToken = apiToken;
	}
	
  public String get(String url, String accept) {
	 
    String response = HttpRequest.get(url)
    		.accept(accept)
    		.authorization(this.authorization())
    		.body();
    return response;
  }
  
  private String authorization() {
	  return "BitVault-Token " + this.apiToken;
  }
}