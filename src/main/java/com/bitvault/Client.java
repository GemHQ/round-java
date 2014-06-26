package com.bitvault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 




public class Client {
  private String appUrl;
  private String apiToken;
  private Application application;
 
  
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
    	
    
    	URL obj = new URL(getAppUrl());
    
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Authorization","BitVault-Token "+ getApiToken());
        con.setRequestProperty("Accept","application/vnd.bitvault.application+json;version=1.0");
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
 
       
        System.out.println(response.toString());

System.out.println("hello");
      // Parse JSON
      // Instantiate Application object
    
    
}
  
  return this.application;
  }
}