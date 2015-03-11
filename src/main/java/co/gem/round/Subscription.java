package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;

/**
 *
 *
 * @author Julian Del Vergel de Dios (julian@gem.co) on 2/27/15.
 */
public class Subscription extends Base {
  public Subscription(Resource resource, Round round) {
    super(resource, round);
  }

  /**
   * Deletes a subscription
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   */
  public void delete() throws IOException, Client.UnexpectedStatusCodeException{
    resource.action("delete");
  }
}
