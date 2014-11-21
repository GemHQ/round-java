package co.gem.round;

import com.google.gson.JsonObject;

public class Address extends Resource {
	public static final String RESOURCE_NAME = "address";
	
	public Address(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}
	
	public String getAddressString() {
		return this.resource.get("string").getAsString();
	}
}


