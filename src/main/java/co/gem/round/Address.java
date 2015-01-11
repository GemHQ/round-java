package co.gem.round;

import co.gem.round.patchboard.Resource;

public class Address extends Base {

  public Address(Resource resource, Round round) {
    super(resource, round);
  }

  public String getAddressString() {
    return getString("string");
  }

  public String getAddressPath() {
    return getString("path");
  }
}


