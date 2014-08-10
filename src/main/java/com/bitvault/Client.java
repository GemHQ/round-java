package com.bitvault;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Client {

	static final String API_HOST = "http://bitvault-api.dev/";
	static final String RESOURCES_KEY = "resources";
	static final String MAPPINGS_KEY = "mappings";
	static final String SCHEMAS_KEY = "schemas";
	
	private OkHttpClient httpClient;
	
	private String appKey;
	private String appUrl;
	private String apiToken;
	private Application application;
	
	private JsonObject mappings;
	private JsonObject resources;
	private JsonArray schemas;

	public Client(String appKey, String apiToken) {
		this.appKey = appKey;
		this.apiToken = apiToken;
		this.httpClient = new OkHttpClient();
		
		try {
			JsonObject discovery = this.performRequest(API_HOST, "application/json");
			this.parseDiscovery(discovery);
		} catch(Exception exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	public JsonObject performRequest(String url, 
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

	public JsonObject performRequest(String method, String url,
			String authorizationType, String accept, String contentType,
			JsonObject requestBody, int expectedStatus) 
					throws UnexpectedStatusCodeException, IOException {
		
		Request.Builder builder = new Request.Builder().url(url);
		
		RequestBody body = null;
		if (requestBody != null) {
			MediaType mediaType = MediaType.parse(contentType);
			body = RequestBody.create(mediaType, requestBody.getAsString());
		}
		
		builder.method(method, body);
		
		if (authorizationType != null)
			builder.header("Authorization", this.authorizationForType(authorizationType));
		if (accept != null)
			builder.header("Accept", accept);
		
		Request request = builder.build();
		Response response = this.httpClient.newCall(request).execute();
		
		int statusCode = response.code();
		String responseContent = response.body().string();
		if (statusCode != expectedStatus)
			throw new UnexpectedStatusCodeException(responseContent, statusCode);
		
		JsonElement element = new JsonParser().parse(responseContent);
		return element.getAsJsonObject();
	}
	
	public JsonObject performRequest(String url, String accept) 
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
	
	public Application getApplication() throws IOException {
		if (application == null) {
			application = new Application(this.getAppUrl(), this);
		}

		return this.application;
	}
	
	public String getAppUrl() {
		if (this.appUrl == null) {
			String template = this.mappings.get("application").getAsJsonObject()
					.get("template").getAsString();
			this.appUrl = template.replaceAll(":key", this.appKey);
		}
		return this.appUrl;
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