package com.bitvault.net;

import com.github.kevinsawicki.http.HttpRequest;

public class HttpClient {

	private String apiToken;

	public HttpClient(String apiToken) {
		this.apiToken = apiToken;
	}

	public String get(String url, String accept) {

		String response = HttpRequest.get(url).accept(accept)
				.authorization(this.authorization()).body();
		return response;
	}

	public String post(String url, String accept, String contentType,
			String body) {
		HttpRequest request = HttpRequest.post(url).accept(accept)
				.authorization(this.authorization());

		if (contentType != null)
			request.contentType(contentType);

		if (body != null)
			request.send(body);

		return request.body();
	}

	private String authorization() {
		return "BitVault-Token " + this.apiToken;
	}
}