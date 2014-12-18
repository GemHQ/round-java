package co.gem.round.patchboard.definition;

import com.google.gson.JsonObject;

/**
 * Created by julian on 11/25/14.
 */
public class ResponseSpec {
  static final String TYPE = "type";
  static final String STATUS = "status";

  private String type;
  private int status;

  private ResponseSpec(String type, int status) {
    this.type = type;
    this.status = status;
  }

  public static ResponseSpec parse(JsonObject responseJson) {
    String type = null;
    if (responseJson.has(TYPE))
      type = responseJson.get(TYPE).getAsString();
    int status = responseJson.get(STATUS).getAsInt();
    return new ResponseSpec(type, status);
  }

  public String type() {
    return type;
  }

  public int status() {
    return status;
  }
}
