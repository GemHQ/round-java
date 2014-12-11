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

  private Map<String, ResourceSpec> resources = new HashMap<String, ResourceSpec>();

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

  public ResourceSpec resource(String name) {
    ResourceSpec resource = resources.get(name);
    if (resource != null)
      return resource;

    JsonObject mappingJson = mappingsJson.get(name).getAsJsonObject();
    JsonObject resourceJson = resourcesJson.get(name).getAsJsonObject();
    JsonObject temp = schemasJson.get(1).getAsJsonObject();
    JsonObject schemaJson = temp.get(name).getAsJsonObject();
    resource = ResourceSpec.parse(mappingJson, resourceJson, schemaJson);
    resources.put(name, resource);
    return resource;
  }

}
