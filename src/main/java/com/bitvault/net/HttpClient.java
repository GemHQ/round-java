package com.bitvault.net;

import com.github.kevinsawicki.http.HttpRequest;

public class HttpClient {
  public void get(String url) {
    HttpRequest request = HttpRequest.get(url);
  }
}