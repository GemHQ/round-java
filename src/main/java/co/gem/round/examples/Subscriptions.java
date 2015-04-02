package co.gem.round.examples;

import co.gem.round.Application;
import co.gem.round.Subscription;
import co.gem.round.patchboard.Client;

import java.io.IOException;

/**
 * Created by julian on 3/2/15.
 */
public class Subscriptions {
  public static void init() throws IOException, Client.UnexpectedStatusCodeException {
    Application app = ApplicationAuth.init();
    Subscription sub = app.subscriptions().create("https://someapp.com/url");

    System.out.println(sub.resource().attributes());
  }
}
