package co.gem.round.patchboard;

import co.gem.round.patchboard.definition.Definition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by julian on 11/25/14.
 */
public class Patchboard {
  private static OkHttpClient httpClient = new OkHttpClient();

  private Definition definition;

  private Patchboard(Definition definition) {
    this.definition = definition;
  }

  public static Patchboard discover(String url) throws IOException {
    Request.Builder builder = new Request.Builder().url(url);
    builder.header("Accept", "application/json");
    Request request = builder.build();
    Response response = httpClient.newCall(request).execute();
    JsonObject definitionJson = new JsonParser().parse(response.body().string()).getAsJsonObject();
    Definition definition = Definition.parse(definitionJson);
    return new Patchboard(definition);
  }

  public Client spawn(AuthorizerInterface authorizer) {
    return new Client(this, httpClient, authorizer);
  }

  public Definition definition() {
    return definition;
  }
}
