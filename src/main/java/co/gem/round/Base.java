package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Base {
  protected Resource resource;
  protected Round round;

  public static final String DEFAULT_ACTION = "get";

  public Base() {}

  public Base(String url, Round round, String resourceName) {
    this.round = round;
    resource = this.round.patchboardClient().resources(resourceName, url);
  }

  public Base(Resource resource, Round round) {
    this.resource = resource;
    this.round = round;
  }

  public void fetch() throws Client.UnexpectedStatusCodeException, IOException {
    resource = resource.action(DEFAULT_ACTION);
  }

  public String key() {
    return getString("key");
  }
  public String url() { return resource.url(); }

  public Resource resource() {
    return resource;
  }

  public JsonElement getAttribute(String key) {
    if (resource.attributes().has(key))
      return resource.attributes().get(key);
    return null;
  }

  public String getString(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsString();
  }

  public Integer getInt(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsInt();
  }

  public Double getDouble(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsDouble();
  }

  public Float getFloat(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsFloat();
  }

  public Long getLong(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsLong();
  }

  public JsonObject getObject(String key) {
    if (getAttribute(key) == null) return null;
    return getAttribute(key).getAsJsonObject();
  }
}
