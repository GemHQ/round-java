package co.gem.round.patchboard;

import co.gem.round.patchboard.definition.ActionSpec;
import co.gem.round.patchboard.definition.ResourceSpec;
import co.gem.round.patchboard.definition.SchemaSpec;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by julian on 11/26/14.
 */
public class Resource implements Iterable<Resource>{
  static final String URL = "url";

  private String url;
  private Client client;
  private ResourceSpec spec;
  private JsonObject attributes;
  private List<Resource> resourceList;

  protected Resource(String url, ResourceSpec spec, Client client) {
    this.url = url;
    this.spec = spec;
    this.client = client;
  }

  protected Resource(JsonObject attributes, ResourceSpec spec, Client client) {
    this(attributes.get(URL).getAsString(), spec, client);
    this.attributes = attributes;
  }

  protected Resource(JsonObject attributes, List<Resource> resourceList, ResourceSpec spec, Client client) {
    this(attributes, spec, client);
    this.resourceList = resourceList;
  }

  public Resource subresource(String name) throws
    IOException, Client.UnexpectedStatusCodeException {
    String url = attributes.get(name).getAsJsonObject()
        .get(URL).getAsString();
    return client.resources(name, url);
  }

  public Resource action(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    return action(name, null);
  }

  public Resource action(String name, JsonObject payload)
      throws IOException, Client.UnexpectedStatusCodeException {
    ActionSpec actionSpec = spec.action(name);

    JsonElement response = client.performRequest(url, actionSpec, payload);
    String responseMediaType = actionSpec.response().type();
    SchemaSpec responseSchema = client.definition().schemaByMediaType(responseMediaType);
    if (responseSchema.type() == "array") {
      resourceList = new ArrayList<Resource>();
      JsonArray jsonArray = response.getAsJsonArray();
      for (JsonElement element : jsonArray) {
        JsonObject attributes = element.getAsJsonObject();
        Resource resource = new Resource(attributes, responseSchema.arrayResource(), client);
        resourceList.add(resource);
      }
      return this;
    } else {
      return new Resource(response.getAsJsonObject(), responseSchema.resourceSpec(), client);
    }

  }

  public JsonObject attributes() {
    return attributes;
  }
  public String url() {
    return url;
  }

  @Override
  public Iterator<Resource> iterator() {
    return resourceList.iterator();
  }
}
