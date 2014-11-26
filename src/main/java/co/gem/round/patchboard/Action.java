package co.gem.round.patchboard;

import com.google.gson.JsonObject;

/**
 * Created by julian on 11/25/14.
 */
public class Action {
  static final String METHOD = "method";
  static final String REQUEST = "request";
  static final String RESPONSE = "response";

  private String method;
  private Request request;
  private Response response;

  private Action(String method, Request request, Response response) {
    this.method = method;
    this.request = request;
    this.response = response;
  }

  public static Action parse(JsonObject actionJson) {
    String method = actionJson.get(METHOD).getAsString();
    Request request = Request.parse(actionJson.get(REQUEST).getAsJsonObject());
    Response response = Response.parse(actionJson.get(RESPONSE).getAsJsonObject());
    return new Action(method, request, response);
  }

  public String method() {
    return method;
  }

  public Request request() {
    return request;
  }

  public Response response() {
    return response;
  }
}
