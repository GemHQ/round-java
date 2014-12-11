package co.gem.round;

import co.gem.round.patchboard.Client;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCollection<T> {

  protected String url;
  protected Round round;
  protected String resourceName;

  protected List<T> collection = new ArrayList<T>();
  protected Map<String, T> map = new HashMap<String, T>();

  public static final String DEFAULT_ACTION = "list";

  public BaseCollection(String url, Round round, String resourceName)
      throws Client.UnexpectedStatusCodeException, IOException {
    this.url = url;
    this.round = round;
    this.resourceName = resourceName;

    refresh();
  }

  public T get(int index) {
    return this.collection.get(index);
  }

  public T get(String key) {
    return this.map.get(key);
  }

  public void add(String key, T element) {
    this.collection.add(element);
    this.map.put(key, element);
  }

  public void refresh() throws Client.UnexpectedStatusCodeException, IOException {
    this.collection.clear();
    this.map.clear();

    JsonArray objects =
        this.round.performRequest(this.url, this.resourceName, DEFAULT_ACTION, null).getAsJsonArray();

    populateCollection(objects);
  }

  public int size() {
    return this.collection.size();
  }

  public List<T> asList() {
    return collection;
  }

  public abstract void populateCollection(JsonArray array);
}