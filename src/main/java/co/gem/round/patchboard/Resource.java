package co.gem.round.patchboard;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public class Resource {
  static final String ACTIONS = "actions";

  private JsonObject actionsJson;
  private Map<String, Action> actions = new HashMap<String, Action>();

  private Resource(JsonObject actionsJson) {
    this.actionsJson = actionsJson;
  }

  public static Resource parse(JsonObject resourceJson) {
    JsonObject actionsJson = resourceJson.get(ACTIONS).getAsJsonObject();
    return new Resource(actionsJson);
  }

  public Action action(String name) {
    Action action = actions.get(name);
    if (action != null)
      return action;

    action = Action.parse(actionsJson.get(name).getAsJsonObject());
    actions.put(name, action);
    return action;
  }
}
