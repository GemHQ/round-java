package co.gem.round.patchboard.definition;

import com.google.gson.JsonObject;

/**
 * Created by julian on 12/11/14.
 */
public class MappingSpec {
  static final String URL = "url";
  static final String QUERY = "query";
  static final String RESOURCE = "resource";

  private String url;
  private JsonObject query;
  private ResourceSpec resourceSpec;

  private MappingSpec(String url, ResourceSpec resourceSpec, JsonObject query) {
    this.url = url;
    this.query = query;
    this.resourceSpec = resourceSpec;
  }

  public static MappingSpec parse(JsonObject mappingJson, Definition definition) {
    String url = null;
    if (mappingJson.has(URL))
      url = mappingJson.get(URL).getAsString();
    String resource = mappingJson.get(RESOURCE).getAsString();
    ResourceSpec resourceSpec = definition.resource(resource);
    JsonObject query = null;
    if (mappingJson.has(QUERY))
      query = mappingJson.get(QUERY).getAsJsonObject();

    return new MappingSpec(url, resourceSpec, query);
  }

  public ResourceSpec resourceSpec() {
    return resourceSpec;
  }

  public String url() {
    return url;
  }
}
