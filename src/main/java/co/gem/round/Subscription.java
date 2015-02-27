package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;

/**
 * Created by julian on 2/27/15.
 */
public class Subscription extends Base {
  public Subscription(Resource resource, Round round) {
    super(resource, round);
  }

  public void delete() throws IOException, Client.UnexpectedStatusCodeException{
    resource.action("delete");
  }
}
