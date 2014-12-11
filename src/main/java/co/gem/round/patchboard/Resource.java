package co.gem.round.patchboard;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by julian on 11/26/14.
 */
public class Resource {
  private String url;
  private Client client;
  private ResourceSpec spec;
  private JsonElement attributes;

  protected Resource(String url, ResourceSpec spec, Client client) {
    this.url = url;
    this.spec = spec;
    this.client = client;
  }

  public Resource subresource(String name) {
    return null;
  }

  public void action(String name) {

  }

  public void action(String name, JsonObject payload) {

  }

}
