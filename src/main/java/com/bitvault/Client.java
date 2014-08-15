package com.bitvault;

import java.io.IOException;

import com.google.bitcoin.crypto.DeterministicKey.NetworkMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Client {

	static final String API_HOST = "http://bitvault-api.dev";
	static final String RESOURCES_KEY = "resources";
	static final String MAPPINGS_KEY = "mappings";
	static final String SCHEMAS_KEY = "schemas";
	
	private OkHttpClient httpClient;
	
	private String baseUrl;
	private String appKey;
	private String apiToken;
	private String appUrl;
	private Application application;
	
	private String walletKey;
	private String walletUrl;
	private Wallet wallet;
	
	private JsonObject mappings;
	private JsonObject resources;
	private JsonArray schemas;
	
	public NetworkMode networkMode = NetworkMode.TESTNET;
	
	public Client(String baseUrl, String appKey, String apiToken, String walletKey) {
		this(baseUrl, appKey, apiToken);
		
		this.walletKey = walletKey;
	}
	
	public Client(String baseUrl, String appKey, String apiToken) {
		this(baseUrl);
		this.appKey = appKey;
		this.apiToken = apiToken;
	}
	
	public Client(String appKey, String appToken) {
		this(null, appKey, appToken);
	}
	
	public Client(String baseUrl) {
		this.baseUrl = baseUrl == null ? API_HOST : baseUrl;
		this.httpClient = new OkHttpClient();
		
		try {
			JsonObject discovery = this.performRequest(this.baseUrl, "application/json").getAsJsonObject();
			this.parseDiscovery(discovery);
		} catch(Exception exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	public JsonElement performRequest(String url, 
			String resourceName, String actionName, 
			JsonObject requestBody) 
					throws UnexpectedStatusCodeException, IOException {
		JsonObject resource = this.resources.get(resourceName).getAsJsonObject();
		JsonObject action = resource.get("actions").getAsJsonObject()
				.get(actionName).getAsJsonObject();
		
		JsonObject requestSpec = action.get("request").getAsJsonObject();
		JsonObject responseSpec = action.get("response").getAsJsonObject();
		
		String accept = responseSpec.get("type").getAsString();
		int expectedStatus = responseSpec.get("status").getAsInt();
		
		JsonElement contentTypeElement = requestSpec.get("type");
		String contentType = null;
		if (contentTypeElement != null)
			contentType = contentTypeElement.getAsString();
		JsonElement authorizationTypeElement = requestSpec.get("authorization");
		String authorizationType = null;
		if (authorizationTypeElement != null)
			authorizationType = authorizationTypeElement.getAsString();
		
		String method = action.get("method").getAsString();
		return this.performRequest(method, url, authorizationType,
				accept, contentType, requestBody, expectedStatus);
	}

	public JsonElement performRequest(String method, String url,
			String authorizationType, String accept, String contentType,
			JsonObject requestBody, int expectedStatus) 
					throws UnexpectedStatusCodeException, IOException {
		
		Request.Builder builder = new Request.Builder().url(url);
		
		RequestBody body = null;
		if (requestBody != null) {
			body = RequestBody.create(null, requestBody.toString());
		}
		
		builder.method(method, body);
		
		if (authorizationType != null)
			builder.header("Authorization", this.authorizationForType(authorizationType));
		if (accept != null)
			builder.header("Accept", accept);
		if (contentType != null)
			builder.header("Content-Type", contentType);
		
		Request request = builder.build();
		Response response = this.httpClient.newCall(request).execute();
		
		int statusCode = response.code();
		String responseContent = response.body().string();
		if (statusCode != expectedStatus)
			throw new UnexpectedStatusCodeException(responseContent, statusCode);
		
		return new JsonParser().parse(responseContent);
	}
	
	public JsonElement performRequest(String url, String accept) 
			throws UnexpectedStatusCodeException, IOException {
		return this.performRequest("GET", url, null, accept, null, null, 200);
	}
	
	private String authorizationForType(String type) {
		return type + " " + this.apiToken;
	}
	
	private void parseDiscovery(JsonObject discovery) {
		this.mappings = discovery.get(MAPPINGS_KEY).getAsJsonObject();
		this.resources = discovery.get(RESOURCES_KEY).getAsJsonObject();
		this.schemas = discovery.get(SCHEMAS_KEY).getAsJsonArray();
	}
	
	public Application application() throws IOException {
		if (application == null) {
			application = new Application(this.getAppUrl(), this);
		}

		return this.application;
	}
	
	public Wallet wallet() {
		if (this.wallet == null) {
			this.wallet = new Wallet(this.getWalletUrl(), this);
		}
		
		return this.wallet;
	}
	
	public String getAppUrl() {
		if (this.appUrl == null) {
			this.appUrl = this.urlTemplate("application", this.appKey);
		}
		return this.appUrl;
	}
	
	public void setWalletKey(String walletKey) {
		this.walletKey = walletKey;
	}
	
	public String getWalletUrl() {
		if (this.walletUrl == null) {
			this.walletUrl = this.urlTemplate("wallet", this.walletKey);
		}
		return this.walletUrl;
	}
	
	public String urlTemplate(String entity, String key) {
		String template = this.mappings.get(entity).getAsJsonObject()
				.get("template").getAsString();
		return template.replaceAll(":key", key);
	}

	public String getApiToken() {
		return this.apiToken;
	}

	public JsonObject getMappings() {
		return this.mappings;
	}
	
	public JsonObject getResources() {
		return this.resources;
	}
	
	public JsonArray getSchemas() {
		return this.schemas;
	}
	
	public class UnexpectedStatusCodeException extends Exception {
		private static final long serialVersionUID = 1L;
		private int statusCode;
		public UnexpectedStatusCodeException(String message, int statusCode) {
			super(message);
			this.statusCode = statusCode;
		}
		
		public String getMessage() {
			return "Unexpected status code: " 
					+ this.statusCode + "\n"
					+ super.getMessage();
		}
	}
}