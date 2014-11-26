package co.gem.round.patchboard;

import com.google.gson.JsonObject;

/**
 * Created by julian on 11/25/14.
 */
public class Response {
  static final String TYPE = "type";
  static final String STATUS = "status";

  private String type;
  private int status;

  private Response(String type, int status) {
    this.type = type;
    this.status = status;
  }

  public static Response parse(JsonObject responseJson) {
    String type = responseJson.get(TYPE).getAsString();
    int status = responseJson.get(STATUS).getAsInt();
    return new Response(type, status);
  }

  public String type() {
    return type;
  }

  public int status() {
    return status;
  }
}
