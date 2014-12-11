package co.gem.round.patchboard;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public class ResourceSpec {
  static final String ACTIONS = "actions";
  static final String URL = "url";
  static final String TEMPLATE = "template";
  static final String QUERY = "query";
  static final String PROPERTIES = "properties";

  private JsonObject actionsJson;
  private JsonObject schemaJson;
  private Map<String, Action> actions = new HashMap<String, Action>();
  private String url;
  private String template;

  private ResourceSpec(String url, String template, JsonObject actionsJson, JsonObject schemaJson) {
    this.url = url;
    this.template = template;
    this.actionsJson = actionsJson;
    this.schemaJson = schemaJson;
  }

  public static ResourceSpec parse(JsonObject mappingJson, JsonObject resourceJson, JsonObject schemaJson) {
    JsonObject actionsJson = resourceJson.get(ACTIONS).getAsJsonObject();
    String url = null;
    if (mappingJson.has(URL))
      url = mappingJson.get(URL).getAsString();
    String template = null;
    if (mappingJson.has(TEMPLATE))
      template = mappingJson.get(TEMPLATE).getAsString();
    return new ResourceSpec(url, template, actionsJson, schemaJson);
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
