package com.bitvault;

import java.io.IOException;

import com.bitvault.net.HttpClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Client {

	public static final String API_HOST = "http://bitvault-api.dev/";
	public static final String RESOURCES_KEY = "resources";
	public static final String MAPPINGS_KEY = "mappings";
	public static final String SCHEMAS_KEY = "schemas";
	
	private String appUrl;
	private String apiToken;
	private Application application;
	
	private HttpClient httpClient;
	
	private JsonObject mappings;
	private JsonObject resources;
	private JsonArray schemas;

	public Client(String appUrl, String apiToken) {
		this.appUrl = appUrl;
		this.apiToken = apiToken;
		
		String discovery = this.getHttpClient().get(API_HOST, "application/json");
		this.parseDiscovery(discovery);
	}
	
	public HttpClient getHttpClient() {
		if (this.httpClient == null) {
			this.httpClient = new HttpClient(this.apiToken);
		}
		return httpClient;
	}

	private void parseDiscovery(String discovery) {
		JsonElement element = new JsonParser().parse(discovery);
		JsonObject parsed = element.getAsJsonObject();
		this.mappings = parsed.get(MAPPINGS_KEY).getAsJsonObject();
		this.resources = parsed.get(RESOURCES_KEY).getAsJsonObject();
		this.schemas = parsed.get(SCHEMAS_KEY).getAsJsonArray();
	}
	
	public Application getApplication() throws IOException {
		if (application == null) {
			application = new Application(this.appUrl, this);
		}

		return this.application;
	}
	
	public String getAppUrl() {
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
	
	public String acceptHeaderForResource(String resourceName, String actionName) {
		this.getHttpClient();
		
		JsonObject resource = this.resources.get(resourceName).getAsJsonObject();
		JsonObject responseDefinition = resource.get("actions").getAsJsonObject()
				.get(actionName).getAsJsonObject()
				.get("response").getAsJsonObject();
		
		JsonElement typeElement = responseDefinition.get("type");
		if (typeElement == null) 
			return null;
		return responseDefinition.get("type").getAsString();
	}
	
	public String contentTypeHeaderForResource(String resourceName, String actionName) {
		this.getHttpClient();
		
		JsonObject resource = this.resources.get(resourceName).getAsJsonObject();
		JsonObject responseDefinition = resource.get("actions").getAsJsonObject()
				.get(actionName).getAsJsonObject()
				.get("request").getAsJsonObject();
		
		JsonElement typeElement = responseDefinition.get("type");
		if (typeElement == null) 
			return null;
		return typeElement.getAsString();
	}
}