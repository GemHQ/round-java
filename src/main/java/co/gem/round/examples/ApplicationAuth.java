package co.gem.round.examples;

import co.gem.round.Application;
import co.gem.round.Round;
import co.gem.round.patchboard.Client;

import java.io.IOException;

/**
 * Created by julian on 3/2/15.
 */
public class ApplicationAuth {
  public static Application init() throws IOException, Client.UnexpectedStatusCodeException {
    Round client = Round.client("https://api-sandbox.gem.co");
    Application instance = client.authenticateApplication(Utils.getAppUrl(), Utils.getApiToken(), Utils.getAppInstance());

    return instance;
  }
}
