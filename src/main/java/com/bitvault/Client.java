package com.bitvault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.bitvault.net.HttpClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
 




public class Client {
  private String appUrl;
  private static String apiToken;
  private Application application;
 
  public static HttpClient httpClient;
  
  public static HttpClient getHttpClient() {
	  if (httpClient == null) {
		  httpClient = new HttpClient(apiToken);
	  }
	  return httpClient;
  }
  
  public Client(String appUrl, String apiToken) {
    this.appUrl = appUrl;
    Client.apiToken = apiToken;
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
    	application = new Application(this.appUrl);
    
    }
  
  return this.application;
  }
}