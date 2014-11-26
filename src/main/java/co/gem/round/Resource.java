package co.gem.round;

import co.gem.round.patchboard.Client;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Resource {

  private String key;

  protected String url;
  protected JsonObject resource;
  protected Round round;
  protected String resourceName;

  public static final String DEFAULT_ACTION = "get";

  public Resource(String url, Round round, String resourceName)
      throws Client.UnexpectedStatusCodeException, IOException {
    this.url = url;
    this.round = round;
    this.resourceName = resourceName;

    refresh();
  }

  public Resource(JsonObject resource, Round round, String resourceName) {
    this.resource = resource;
    this.round = round;
    this.resourceName = resourceName;

    if (this.resource.has("url"))
      this.url = this.resource.get("url").getAsString();
  }

  public void refresh() throws Client.UnexpectedStatusCodeException, IOException {
    resource = this.round.performRequest(this.url, this.resourceName, DEFAULT_ACTION, null).getAsJsonObject();
  }

  public String getKey() {
    if (this.key == null)
      this.key = this.resource.get("key").getAsString();

    return this.key;
  }
}
