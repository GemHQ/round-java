package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

/**
 * Base class for building Gem API singleton objects.  Simplifies interactions with the json resources
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
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

  public Base(Resource resource, Round round, JsonObject attributes) {
    this(resource, round);

    if (attributes != null) {
      for (Map.Entry<String, JsonElement> entry : attributes.entrySet()) {
        this.resource.attributes().add(entry.getKey(), entry.getValue());
      }
    }
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
