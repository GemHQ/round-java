package co.gem.round.patchboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julian on 11/25/14.
 */
public class Request {
  static final String TYPE = "type";
  static final String AUTHORIZATION = "authorization";

  private String type;
  private List<String> authorizations;

  private Request(String type, List<String> authorizations) {
    this.type = type;
    this.authorizations = authorizations;
  }

  public static Request parse(JsonObject requestJson) {
    String type = requestJson.get(TYPE).getAsString();
    JsonElement authorizationsElement = requestJson.get(AUTHORIZATION);
    List<String> authorizations = new ArrayList<String>();
    if (authorizationsElement != null) {
      if(authorizationsElement.isJsonArray()) {
        JsonArray authorizationsJson = authorizationsElement.getAsJsonArray();
        for (JsonElement authorizationJson : authorizationsJson) {
          String authorization = authorizationJson.getAsString();
          authorizations.add(authorization);
        }
      } else {
        String authorization = authorizationsElement.getAsString();
        authorizations.add(authorization);
      }
    }
    return new Request(type, authorizations);
  }

  public String type() {
    return type;
  }

  public List<String> authorizations() {
    return authorizations;
  }
}
