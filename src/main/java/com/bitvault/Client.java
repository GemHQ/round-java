package com.bitvault;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Client {

	static final String API_HOST = "http://bitvault-api.dev/";
	static final String RESOURCES_KEY = "resources";
	static final String MAPPINGS_KEY = "mappings";
	static final String SCHEMAS_KEY = "schemas";
	
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new GsonFactory();
	
	private String appUrl;
	private String apiToken;
	private Application application;
	
	private JsonObject mappings;
	private JsonObject resources;
	private JsonArray schemas;

	public Client(String appUrl, String apiToken) {
		this.appUrl = appUrl;
		this.apiToken = apiToken;
		
		try {
			JsonObject discovery = this.performRequest(API_HOST, "application/json");
			this.parseDiscovery(discovery);
		} catch(Exception exception) {
			System.out.println(exception.getMessage());
		}
	}
	
	public JsonObject performRequest(String urlString, 
			String resourceName, String actionName, 
			JsonObject requestBody) throws UnexpectedStatusCodeException, IOException {
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
		GenericUrl url = new GenericUrl(urlString);
		return this.performRequest(method, url, authorizationType,
				accept, contentType, requestBody, expectedStatus);
	}

	public JsonObject performRequest(String method, GenericUrl url,
			String authorizationType, String accept, String contentType,
			JsonObject requestBody, int expectedStatus) throws UnexpectedStatusCodeException, IOException {
		HttpRequestFactory requestFactory = 
				HTTP_TRANSPORT.createRequestFactory();
		
		HttpRequest request = requestFactory.buildRequest(method, url, null);
		
		HttpHeaders headers = new HttpHeaders();
		if (authorizationType != null)
			headers.setAuthorization(this.authorizationForType(authorizationType));
		if (accept != null)
			headers.setAccept(accept);
		if (contentType != null)
			headers.setContentType(contentType);
		if (requestBody != null)
			request.setContent(new JsonHttpContent(new GsonFactory(), requestBody));
		request.setHeaders(headers);
		
		HttpResponse response = request.execute();
		int statusCode = response.getStatusCode();
		String responseContent = response.parseAsString();
		if (statusCode != expectedStatus)
			throw new UnexpectedStatusCodeException(responseContent, statusCode);
		
		JsonElement element = new JsonParser().parse(responseContent);
		return element.getAsJsonObject();
	}
	
	public JsonObject performRequest(String urlString, String accept) throws UnexpectedStatusCodeException, IOException {
		GenericUrl url = new GenericUrl(urlString);
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