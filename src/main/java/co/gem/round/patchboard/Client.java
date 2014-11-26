package co.gem.round.patchboard;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by julian on 11/25/14.
 */
public class Client {
  static final String AUTHORIZATION_HEADER = "Authorization";
  static final String ACCEPT_HEADER = "Accept";
  static final String CONTENT_TYPE_HEADER = "Content-Type";

  private Patchboard patchboard;
  private OkHttpClient httpClient;
  private AuthorizerInterface authorizer;

  public Client(Patchboard patchboard, OkHttpClient httpClient, AuthorizerInterface authorizer) {
    this.patchboard = patchboard;
    this.httpClient = httpClient;
    this.authorizer = authorizer;
  }

  public JsonElement performRequest(String url, String resourceName, String actionName, JsonObject requestBody)
      throws IOException, UnexpectedStatusCodeException {
    Resource resource = patchboard.definition().resource(resourceName);
    Action action = resource.action(actionName);

    com.squareup.okhttp.Request.Builder builder = new Request.Builder().url(url);

    RequestBody body = null;
    if (requestBody != null)
      body = RequestBody.create(null, requestBody.toString());

    builder.method(action.method(), body);

    String authorization = null;
    for (String scheme : action.request().authorizations()) {
      if (authorizer.isAuthorized(scheme)) {
        authorization = authorizer.getCredentials(scheme);
        break;
      }
    }

    if (authorization != null)
      builder.header(AUTHORIZATION_HEADER, authorization);
    if (action.response().type() != null)
      builder.header(ACCEPT_HEADER, action.response().type());
    if (action.request().type() != null)
      builder.header(CONTENT_TYPE_HEADER, action.request().type());

    Request request = builder.build();
    Response response = httpClient.newCall(request).execute();

    int statusCode = response.code();
    String responseContent = response.body().string();
    if (statusCode != action.response().status())
      throw new UnexpectedStatusCodeException(responseContent, statusCode, response);

    return new JsonParser().parse(responseContent);

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
