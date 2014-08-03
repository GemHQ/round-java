package com.bitvault;

import java.io.IOException;

import com.bitvault.net.HttpClient;

public class Client {
  private String appUrl;
  private String apiToken;
  private Application application;
 
  private HttpClient httpClient;
  
  public HttpClient getHttpClient() {
	  if (this.httpClient == null) {
		  this.httpClient = new HttpClient(this.apiToken);
	  }
	  return httpClient;
  }
  
  public Client(String appUrl, String apiToken) {
    this.appUrl = appUrl;
    this.apiToken = apiToken;
  }

  public String getAppUrl() {
    return this.appUrl;
  }

  public String getApiToken() {
    return this.apiToken;
  }

  public Application getApplication() throws IOException {
    if (application == null) {
    	
      // GET application from API
    	application = new Application(this.appUrl, this);
    
    }
  
    return this.application;
  }
}