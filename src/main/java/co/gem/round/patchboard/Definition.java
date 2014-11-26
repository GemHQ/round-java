package co.gem.round.patchboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public class Definition {
  static final String RESOURCES = "resources";
  static final String MAPPINGS = "mappings";
  static final String SCHEMAS = "schemas";

  private JsonObject mappingsJson;
  private JsonObject resourcesJson;
  private JsonArray schemasJson;

  private Map<String, Resource> resources = new HashMap<String, Resource>();

  private Definition(JsonObject mappingsJson, JsonObject resourcesJson, JsonArray schemasJson) {
    this.mappingsJson = mappingsJson;
    this.resourcesJson = resourcesJson;
    this.schemasJson = schemasJson;
  }

  public static Definition parse(JsonObject discovery) {
    return new Definition(
        discovery.get(MAPPINGS).getAsJsonObject(),
        discovery.get(RESOURCES).getAsJsonObject(),
        discovery.get(SCHEMAS).getAsJsonArray());
  }

  public Resource resource(String name) {
    Resource resource = resources.get(name);
    if (resource != null)
      return resource;

    resource = Resource.parse(resourcesJson.get(name).getAsJsonObject());
    resources.put(name, resource);
    return resource;
  }

}
