package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;
import java.util.*;
/**
 * Base class for building Gem API collection objects.
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public abstract class BaseCollection<T> extends Base implements Iterable<T> {

  protected List<T> list = new ArrayList<T>();
  protected Map<String, T> map = new HashMap<String, T>();
  protected int page = 0;

  public static final String DEFAULT_ACTION = "list";
  public static final int PAGE_LIMIT = 100;

  public BaseCollection(String url, Round round, String resourceName, int page) {
    this.round = round;
    this.page = page;
    Map<String, String> pageQuery = new HashMap<String, String>();
    pageQuery.put("limit", Integer.toString(PAGE_LIMIT));
    pageQuery.put("offset", Integer.toString(page * PAGE_LIMIT));
    resource = this.round.patchboardClient().resources(resourceName, url, null, pageQuery);
  }

  public BaseCollection(String url, Round round, String resourceName) {
    this(url, round, resourceName, 0);
  }

  public BaseCollection(Resource resource, Round round) {
    super(resource, round);
  }

  public BaseCollection(Resource resource, Round round, int page) throws Client.UnexpectedStatusCodeException, IOException {
    this(resource, round);
    if (page > 0) { this.page(page); }
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

  public BaseCollection<T> page(int i) throws Client.UnexpectedStatusCodeException, IOException, IndexOutOfBoundsException {
    if (i < 0) { throw new IndexOutOfBoundsException(); }
    this.resource.setPage(i);
    fetch();
    if (this.size() <= 0 && i > 0) {
      this.resource.setPage(this.page);
      throw new IndexOutOfBoundsException();
    }
    this.page = i;
    return this;
  }

  public BaseCollection<T> nextPage() throws Client.UnexpectedStatusCodeException, IOException, IndexOutOfBoundsException {
    return page(this.page + 1);
  }

  public BaseCollection<T> previousPage() throws Client.UnexpectedStatusCodeException, IOException, IndexOutOfBoundsException {
    return page(this.page - 1);
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