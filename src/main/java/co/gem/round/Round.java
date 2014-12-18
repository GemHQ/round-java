package co.gem.round;

import co.gem.round.patchboard.Client;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Round {

  private Client patchboardClient;

  static final String API_HOST = "http://bitvault-api.dev";

//  public JsonElement performRequest(String url,
//                                    String resourceName, String actionName,
//                                    JsonObject requestBody)
//      throws Client.UnexpectedStatusCodeException, IOException {
//
//    return this.patchboardClient.performRequest(url, resourceName, actionName, requestBody);
//  }

//  public Wallet wallet() throws Client.UnexpectedStatusCodeException, IOException {
//    if (wallet == null) {
//      Map<String, String> params = new HashMap<String, String>();
//      params.put("email", email);
//      String url = getUrl("wallet_query", params);
//
//      JsonElement walletResource = performRequest(url, "wallet_query", "get", null);
//      wallet = new Wallet(walletResource.getAsJsonObject(), this);
//    }
//
//    return wallet;
//  }

//  public String getUrl(String entity, Map<String, String> params) {
//    JsonObject urlSpec = this.mappings.get(entity).getAsJsonObject();
//    String url = urlSpec.get("url").getAsString();
//
//    if (params != null) {
//      List<String> paramsList = new ArrayList<String>();
//      for (Map.Entry<String, String> entry : params.entrySet()) {
//        paramsList.add(entry.getKey() + "=" + entry.getValue());
//      }
//      String paramsString = Strings.join("&", paramsList);
//      url = url + "?" + paramsString;
//    }
//
//    return url;
//  }

}