package com.bitvault.net;

import com.github.kevinsawicki.http.HttpRequest;

public class HttpClient {
  private String baseUrl;

  public HttpClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void get() {
    HttpRequest request = HttpRequest.get(this.baseUrl);
  }
}