package com.bitvault;

import com.bitvault.net.HttpClient;

public class Application {
  private String id;

  private static HttpClient client;

  public Application(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public static HttpClient client() {
    if (client == null)
      client = new HttpClient("http://bitvault.pandastrike.com");
    return client;
  }
}