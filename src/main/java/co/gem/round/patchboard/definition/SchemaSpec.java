package co.gem.round.patchboard.definition;

import com.google.gson.JsonObject;

/**
 * Created by julian on 12/11/14.
 */
public class SchemaSpec {
  static final String MEDIATYPE = "mediaType";
  static final String PROPERTIES = "properties";
  static final String TYPE = "type";
  static final String ITEMS = "items";
  static final String REF = "$ref";
  static final String ID = "id";

  private String id;
  private String type;
  private ResourceSpec arrayResource;
  private ResourceSpec resourceSpec;
  private String mediaType;
  private JsonObject properties;

  private SchemaSpec(String id, String type, String mediaType, ResourceSpec arrayResource, ResourceSpec resourceSpec, JsonObject properties) {
    this.id = id;
    this.type = type;
    this.arrayResource = arrayResource;
    this.mediaType = mediaType;
    this.resourceSpec = resourceSpec;
    this.properties = properties;
  }

  public static SchemaSpec parse(String name, JsonObject schemaJson, Definition definition) {
    ResourceSpec resourceSpec = definition.resource(name);
    String mediaType = schemaJson.get(MEDIATYPE).getAsString();
    String id = schemaJson.get(ID).getAsString();
    String type = null;
    if (schemaJson.has(TYPE))
      type = schemaJson.get(TYPE).getAsString();
    ResourceSpec arrayResource = null;
    if (schemaJson.has(ITEMS)) {
      JsonObject items = schemaJson.get(ITEMS).getAsJsonObject();
      String resourceName = items.get(REF).getAsString();
      arrayResource = definition.resource(resourceName);
    }
    JsonObject properties = null;
    if (schemaJson.has(PROPERTIES))
      properties = schemaJson.get(PROPERTIES).getAsJsonObject();

    return new SchemaSpec(id, type, mediaType, arrayResource, resourceSpec, properties);
  }

  public ResourceSpec resourceSpec() {
    return resourceSpec;
  }
  public ResourceSpec arrayResource() { return arrayResource; }
  public String mediaType() { return mediaType; }
  public String id() { return id; }
  public String type() { return type; }
  public JsonObject properties() { return properties; }

  public String associationSchemaId(String name) {
    if (properties() != null) {
      if (properties().has(name)) {
        JsonObject property = properties().get(name).getAsJsonObject();
        if (property.has(REF)) return property.get(REF).getAsString();
      }
    }
    return null;
  }
}
