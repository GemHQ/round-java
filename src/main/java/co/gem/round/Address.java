package co.gem.round;

import com.google.gson.JsonObject;

public class Address extends Base {
  public static final String RESOURCE_NAME = "address";

  public Address(JsonObject resource, Round round) {
    super(resource, round, RESOURCE_NAME);
  }

  public String getAddressString() {
    return this.resource.get("string").getAsString();
  }
}


