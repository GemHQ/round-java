package co.gem.round.patchboard;

import co.gem.round.patchboard.definition.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    return resources(name, null, null);
  }

  public Resource resources(String name, JsonObject query) {
    return resources(name, null, query);
  }

  public Resource resources(String name, String url) {
    return resources(name, url, null);
  }

  public Resource resources(String name, String url, JsonObject query) {
    MappingSpec mappingSpec = patchboard.definition().mapping(name);
    if (url == null)
      url = mappingSpec.url();

    return new Resource(url, mappingSpec.resourceSpec(), this);
  }

  public String performRawRequest(String url, ActionSpec actionSpec, JsonObject requestBody)
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

  public JsonElement performRequest(String url, ActionSpec actionSpec, JsonObject requestBody)
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
}
