package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 12/18/14.
 */
public class Application extends Base{
  public Application(Resource resource, Round round) {
    super(resource, round);
  }

  public UserCollection users()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource usersResource = resource.subresource("users");
    UserCollection users = new UserCollection(usersResource, round);
    users.fetch();
    return new UserCollection(usersResource, round);
  }

  public ApplicationInstance authorizeInstance(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject payload = new JsonObject();
    payload.addProperty("name", name);
    Resource applicationInstanceResource = resource.action("authorize_instance", payload);
    return new ApplicationInstance(applicationInstanceResource, round);
  }
}
