package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 2/27/15.
 */
public class SubscriptionCollection extends BaseCollection<Subscription> {

  public SubscriptionCollection(Resource resource, Round round) {
    super(resource, round);
  }

  public Subscription create(String callbackUrl)
      throws Client.UnexpectedStatusCodeException, IOException {
    JsonObject payload = new JsonObject();
    payload.addProperty("subscribed_to", "address");
    payload.addProperty("callback_url", callbackUrl);
    Resource subscriptionResource = resource.action("create", payload);
    Subscription subscription = new Subscription(subscriptionResource, round);
    add(subscription.key(), subscription);
    return subscription;
  }

  @Override
  public void populateCollection(Iterable<Resource> collection) {
    for (Resource resource : collection) {
      Subscription subscription = new Subscription(resource, round);
      add(subscription.key(), subscription);
    }
  }
}
