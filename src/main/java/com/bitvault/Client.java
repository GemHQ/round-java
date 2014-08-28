package com.bitvault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
    private String email;
	private Wallet wallet;

    private Authorizer authorizer;
	
	private JsonObject mappings;
	private JsonObject resources;
	private JsonArray schemas;
	
	public NetworkMode networkMode = NetworkMode.TESTNET;
	
	public Client(String baseUrl, String appKey, String apiToken)
            throws UnexpectedStatusCodeException, IOException {
		this(baseUrl);
		this.appKey = appKey;
		this.apiToken = apiToken;
	}
	
	public Client(String appKey, String appToken)
            throws UnexpectedStatusCodeException, IOException {
		this(null, appKey, appToken);
	}
	
	public Client(String baseUrl)
            throws UnexpectedStatusCodeException, IOException {
		this.baseUrl = baseUrl == null ? API_HOST : baseUrl;
		httpClient = new OkHttpClient();
        authorizer = new Authorizer();
		

		JsonObject discovery = this.performRequest(this.baseUrl, "application/json").getAsJsonObject();
		this.parseDiscovery(discovery);
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
		if (authorizationTypeElement != null) {
            if (authorizationTypeElement.isJsonArray()) {
                JsonArray authorizationTypes = authorizationTypeElement.getAsJsonArray();
                String scheme = null;
                for (JsonElement element : authorizationTypes) {
                    scheme = element.getAsString();
                    if (authorizer.isAuthorized(scheme)) {
                        authorizationType = scheme;
                        break;
                    }
                }
                if (authorizationType == null) {
                    authorizationType = scheme;
                }
            } else {
                authorizationType = authorizationTypeElement.getAsString();
            }
        }

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
			builder.header("Authorization", authorizer.getCredentials(authorizationType));
		if (accept != null)
			builder.header("Accept", accept);
		if (contentType != null)
			builder.header("Content-Type", contentType);
		
		Request request = builder.build();
		Response response = this.httpClient.newCall(request).execute();
		
		int statusCode = response.code();
		String responseContent = response.body().string();
		if (statusCode != expectedStatus)
			throw new UnexpectedStatusCodeException(responseContent, statusCode, response);
		
		return new JsonParser().parse(responseContent);
	}
	
	public JsonElement performRequest(String url, String accept) 
			throws UnexpectedStatusCodeException, IOException {
		return this.performRequest("GET", url, null, accept, null, null, 200);
	}

    public String authorizeDevice(String name, String deviceId)
            throws IOException, UnexpectedStatusCodeException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        String authUrl = getUrl("user_query", params);

        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("device_id", deviceId);

        JsonObject response = performRequest(authUrl, "user", "authorize_device", body).getAsJsonObject();

        return response.get("auth_token").getAsString();
    }

    public void addAppAuthorization(String userToken, String deviceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_token", userToken);
        params.put("device_id", deviceId);
        params.put("api_token", getApiToken());

        authorizer.authorize("Gem-Application", params);
    }

    public void addDeviceAuthorization(String deviceId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("device_id", deviceId);
        params.put("api_token", getApiToken());

        authorizer.authorize("Gem-Device", params);
    }

    public void addOTPAuthorization(String key, String secret) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", key);
        params.put("secret", secret);
        params.put("api_token", getApiToken());

        authorizer.authorize("Gem-OOB-OTP", params);
    }

    public void setEmail(String email) {
        this.email = email;
    }

	private void parseDiscovery(JsonObject discovery) {
		this.mappings = discovery.get(MAPPINGS_KEY).getAsJsonObject();
		this.resources = discovery.get(RESOURCES_KEY).getAsJsonObject();
		this.schemas = discovery.get(SCHEMAS_KEY).getAsJsonArray();
	}
	
	public Wallet wallet() throws UnexpectedStatusCodeException, IOException {
        if (wallet == null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("email", email);
            String url = getUrl("wallet_query", params);

            JsonElement walletResource = performRequest(url, "wallet_query", "get", null);
            wallet = new Wallet(walletResource.getAsJsonObject(), this);
        }

        return wallet;
    }

	public String getUrl(String entity, Map<String, String> params) {
        JsonObject urlSpec = this.mappings.get(entity).getAsJsonObject();
		String url = urlSpec.get("url").getAsString();

        if (params != null) {
            List<String> paramsList = new ArrayList<String>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsList.add(entry.getKey() + "=" + entry.getValue());
            }
            String paramsString = Util.join(paramsList, "&");
            url = url + "?" + paramsString;
        }

		return url;
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
		public int statusCode;
        public Response response;
		public UnexpectedStatusCodeException(String message, int statusCode, Response response) {
			super(message);
			this.statusCode = statusCode;
            this.response = response;
		}
		
		public String getMessage() {
			return "Unexpected status code: " 
					+ this.statusCode + "\n"
					+ super.getMessage();
		}
	}
}