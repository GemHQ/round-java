package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 4/1/15.
 */
public class Devices extends Base {
  public Devices(Resource resource, Round round) {
    super(resource, round);
  }

  public AuthRequest create(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject body = new JsonObject();
    body.addProperty("name", name);

    Resource authResource = resource.action("create", body);
    String deviceToken = authResource.attributes().get("metadata").getAsJsonObject().get("device_token").getAsString();
    String mfaUri authResource.attributes().get("mfa_uri");
    return device;
  }
}
