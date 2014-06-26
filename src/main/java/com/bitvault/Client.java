package com.bitvault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
 




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
 
       
       // System.out.println(response.toString());


      // Parse JSON
       application = new Application();
       
       JsonElement jelement = new JsonParser().parse(response.toString());
       JsonObject  jobject = jelement.getAsJsonObject();
        
       String key = jobject.get("key").toString();
       String name = jobject.get("name").toString();
       String api_token = jobject.get("api_token").toString();
       String callback_url = jobject.get("callback_url").toString();
       String url = jobject.get("url").toString();
       
       /*System.out.println("key:"+key);       
       System.out.println("name:"+name);
       System.out.println("api_token:"+api_token);
       System.out.println("callback_url:"+callback_url);
       System.out.println("url:"+url);*/
        
        
        
       /* JsonObject jobject1 = jobject.getAsJsonObject("wallets");
        String keyw = jobject1.get("key").toString();
        String urlw=jobject1.get("url").toString();
        System.out.println("wallets");
        System.out.println("  key"+keyw) ; 
        System.out.println("  url"+urlw) ; 
        
        JsonObject jobject2 = jobject.getAsJsonObject("owner");
        String keyo = jobject2.get("key").toString();
        String urlo=jobject2.get("url").toString();
        System.out.println("owner");
        System.out.println("   key"+keyo) ; 
        System.out.println("   url"+urlo) ; */
        
    
      
      // Instantiate Application object
        
        application.key= key;
        application.name= name;
        application.api_token= api_token;
        application.callback_url= callback_url;
        application.url = url;
    
      
    
}
  
  return this.application;
  }
}