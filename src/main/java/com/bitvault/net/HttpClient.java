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
  
  public String post(String url, String accept, String contentType, String body) {
    String response = HttpRequest.post(url)
    		.contentType(contentType)
    		.accept(accept)
    		.authorization(this.authorization())
    		.send(body)
    		.body();

    return response;
  }

  private String authorization() {
	  return "BitVault-Token " + this.apiToken;
  }
}