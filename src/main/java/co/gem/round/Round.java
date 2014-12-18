package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Patchboard;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Round {

  static final String API_HOST = "http://bitvault-api.dev";

  private static Patchboard patchboard;

  private Client patchboardClient;

  private Round(Client patchboardClient) {
    this.patchboardClient = patchboardClient;
  }

  public static Round client(String url)
    throws Client.UnexpectedStatusCodeException, IOException {
    if (url == null)
      url = API_HOST;
    patchboard = Patchboard.discover(url);

    Client patchboardClient = patchboard.spawn(new Authorizer());
    return new Round(patchboardClient);
  }

  public Client patchboardClient() { return patchboardClient; }

}