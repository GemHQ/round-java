package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;
import java.util.*;

public abstract class BaseCollection<T> extends Base implements Iterable<T> {

  protected List<T> list = new ArrayList<T>();
  protected Map<String, T> map = new HashMap<String, T>();

  public static final String DEFAULT_ACTION = "list";

  public BaseCollection(String url, Round round, String resourceName) {
    super(url, round, resourceName);
  }

  public BaseCollection(Resource resource, Round round) {
    super(resource, round);
  }

  public T get(int index) {
    return this.list.get(index);
  }

  public T get(String key) {
    return this.map.get(key);
  }

  public void add(String key, T element) {
    list.add(element);
    map.put(key, element);
  }

  public void fetch() throws Client.UnexpectedStatusCodeException, IOException {
    resource = resource.action(DEFAULT_ACTION);
    list = new ArrayList<T>();
    map = new HashMap<String, T>();
    populateCollection(resource);
  }

  public int size() {
    return this.list.size();
  }

  public List<T> asList() {
    return list;
  }
  public Map<String, T> asMap() { return map; }

  public abstract void populateCollection(Iterable<Resource> collection);

  @Override
  public Iterator<T> iterator() {
    return list.iterator();
  }
}