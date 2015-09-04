package co.gem.round.patchboard;

import co.gem.round.patchboard.definition.*;
import co.gem.round.util.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public class Client {
    static final String AUTHORIZATION_HEADER = "Authorization";
    static final String ACCEPT_HEADER = "Accept";
    static final String CONTENT_TYPE_HEADER = "Content-Type";

    private OkHttpClient httpClient;
    private AuthorizerInterface authorizer;
    private Patchboard patchboard;

    public Client(Patchboard patchboard, OkHttpClient httpClient, AuthorizerInterface authorizer) {
        this.patchboard = patchboard;
        this.httpClient = httpClient;
        this.authorizer = authorizer;
    }

    public Resource resources(String name) {
        return resources(name, null, null, null);
    }

    public Resource resources(String name, Map<String, String> query) {
        return resources(name, null, null, query);
    }

    public Resource resources(String name, String url) {
        return resources(name, url, null, null);
    }

    public Resource resources(String name, String url, String schemaId) {
        return resources(name, url, schemaId, null);
    }

    public Resource resources(String name, String url, String schemaId, Map<String, String> query) {
        SchemaSpec schemaSpec = null;
        if (schemaId != null) {
            schemaSpec = patchboard.definition().schemaById(schemaId);
        }

        MappingSpec mappingSpec = patchboard.definition().mapping(name);

        ResourceSpec resourceSpec = null;
        if (schemaSpec != null) {
            resourceSpec = schemaSpec.resourceSpec();
        } else {
            resourceSpec = mappingSpec.resourceSpec();
        }

        if (url == null)
            url = mappingSpec.url();

        if (query != null)
            url = url + "?" + queryStringFromObject(query);

        return new Resource(url, resourceSpec, this);
    }

    public String queryStringFromObject(Map<String, String> query) {
        List<String> params = new ArrayList<String>();
        for(Map.Entry<String, String> entry : query.entrySet()) {
            String param = Strings.urlEncode(entry.getKey()) + "="
                    + Strings.urlEncode(entry.getValue());
            params.add(param);
        }

        return Strings.join("&", params);
    }

    public String performRawRequest(String url, ActionSpec actionSpec, JsonElement requestBody)
            throws IOException, UnexpectedStatusCodeException {

        com.squareup.okhttp.Request.Builder builder = new Request.Builder().url(url);

        RequestBody body = null;
        if (requestBody != null)
            body = RequestBody.create(null, requestBody.toString());

        builder.method(actionSpec.method(), body);

        String authorization = null;
        for (String scheme : actionSpec.request().authorizations()) {
            if (authorizer.isAuthorized(scheme)) {
                authorization = authorizer.getCredentials(scheme);
                break;
            }
        }

        if (authorization != null)
            builder.header(AUTHORIZATION_HEADER, authorization);
        if (actionSpec.response().type() != null)
            builder.header(ACCEPT_HEADER, actionSpec.response().type());
        if (actionSpec.request().type() != null)
            builder.header(CONTENT_TYPE_HEADER, actionSpec.request().type());

        Request request = builder.build();
        Response response = httpClient.newCall(request).execute();

        int statusCode = response.code();
        String responseContent = response.body().string();
        if (statusCode != actionSpec.response().status())
            throw new UnexpectedStatusCodeException(responseContent, statusCode, response);

        return responseContent;
    }

    public JsonElement performRequest(String url, ActionSpec actionSpec, JsonElement requestBody)
            throws IOException, UnexpectedStatusCodeException {
        String responseContent = performRawRequest(url, actionSpec, requestBody);
        JsonElement attributes = new JsonParser().parse(responseContent);

        return attributes;
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

    public Definition definition() { return patchboard.definition(); }
    public AuthorizerInterface authorizer() { return authorizer; }
}
