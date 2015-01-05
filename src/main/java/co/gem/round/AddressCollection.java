package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;

/**
 * Created by julian on 1/4/15.
 */
public class AddressCollection extends BaseCollection<Address> {
  public AddressCollection(Resource resource, Round round) { super(resource, round); }

  public Address create()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource addressResource = resource.action("create");
    return new Address(addressResource, this.round);
  }

  @Override
  public void populateCollection(Iterable<Resource> resources) {
    for (Resource resource : resources) {
      Address address = new Address(resource, round);
      this.add(address.getString("string"), address);
    }
  }
}
