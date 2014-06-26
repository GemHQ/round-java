package com.bitvault;

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

  public Application getApplication() {
    if (application == null) {
      // GET application from API
      // Parse JSON
      // Instantiate Application object
    }
    return this.application;
  }
}