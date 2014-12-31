package co.gem.round.patchboard.definition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
  static final String DEFINITIONS = "definitions";
  static final String MEDIA_TYPE = "mediaType";

  private Map<String, ResourceSpec> resources;
  private Map<String, MappingSpec> mappings;
  private Map<String, SchemaSpec> schemasByMediaType;
  private Map<String, SchemaSpec> schemasByResourceName;
  private Map<String, SchemaSpec> schemasById;

  private Definition(JsonObject mappingsJson, JsonObject resourcesJson, JsonArray schemasJson) {
    parseResources(resourcesJson);
    parseMappings(mappingsJson);
    parseSchemas(schemasJson);
    for (Map.Entry<String, ResourceSpec> entry : resources.entrySet()) {
      SchemaSpec schemaSpec = schemasByResourceName.get(entry.getKey());
      entry.getValue().setSchemaSpec(schemaSpec);
    }
  }

  public static Definition parse(JsonObject discovery) {
    return new Definition(
        discovery.get(MAPPINGS).getAsJsonObject(),
        discovery.get(RESOURCES).getAsJsonObject(),
        discovery.get(SCHEMAS).getAsJsonArray());
  }

  public ResourceSpec resource(String name) {
    return resources.get(name);
  }

  public MappingSpec mapping(String name) {
    return mappings.get(name);
  }

  public SchemaSpec schemaByName(String name) {
    return schemasByResourceName.get(name);
  }

  public SchemaSpec schemaByMediaType(String mediaType) {
    return schemasByMediaType.get(mediaType);
  }

  public SchemaSpec schemaById(String id) {
    return schemasById.get(id);
  }

  private void parseResources(JsonObject resourcesJson) {
    resources = new HashMap<String, ResourceSpec>();
    for (Map.Entry<String, JsonElement> entry : resourcesJson.entrySet()) {
      JsonObject resourceJson = entry.getValue().getAsJsonObject();
      ResourceSpec resourceSpec = ResourceSpec.parse(resourceJson);
      resources.put(entry.getKey(), resourceSpec);
    }
  }

  private void parseMappings(JsonObject mappingsJson) {
    mappings = new HashMap<String, MappingSpec>();
    for (Map.Entry<String, JsonElement> entry : mappingsJson.entrySet()) {
      JsonObject mappingJson = entry.getValue().getAsJsonObject();
      MappingSpec mappingSpec = MappingSpec.parse(mappingJson, this);
      mappings.put(entry.getKey(), mappingSpec);
    }
  }

  private void parseSchemas(JsonArray schemasJson) {
    schemasByMediaType = new HashMap<String, SchemaSpec>();
    schemasByResourceName = new HashMap<String, SchemaSpec>();
    schemasById = new HashMap<String, SchemaSpec>();
    for (JsonElement element : schemasJson) {
      JsonObject namespace = element.getAsJsonObject();
      JsonObject definitions = namespace.get(DEFINITIONS).getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : definitions.entrySet()) {
        JsonObject schemaJson = entry.getValue().getAsJsonObject();
        if (!schemaJson.has(MEDIA_TYPE))
          continue;
        String mediaType = schemaJson.get(MEDIA_TYPE).getAsString();
        if (mediaType != null) {
          String name = entry.getKey();
          SchemaSpec schemaSpec = SchemaSpec.parse(name, schemaJson, this);
          if (schemaSpec != null) {
            schemasByResourceName.put(name, schemaSpec);
            schemasByMediaType.put(schemaSpec.mediaType(), schemaSpec);
            schemasById.put(schemaSpec.id(), schemaSpec);
          }
        }
      }
    }
  }

}
