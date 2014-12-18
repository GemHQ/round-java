package co.gem.round;

import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

public class Address extends Base {
  public static final String RESOURCE_NAME = "address";

  public Address(Resource resource, Round round) {
    super(resource, round);
  }

  public String getAddressString() {
    return getString("string");
  }
}


