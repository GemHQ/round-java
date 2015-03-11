package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Patchboard;
import co.gem.round.patchboard.Resource;
import co.gem.round.util.Http;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Round is the Gem API client class.  Most importantly it is where you access the authentication methods for the api
 * @author Julian Del Vergel de Dios (julian@gem.co) on 1/4/15.
 */
public class Round {

  static final String API_HOST = "https://api-sandbox.gem.co";

  private static Patchboard patchboard;

  private Client patchboardClient;

  private Round(Client patchboardClient) {
    this.patchboardClient = patchboardClient;
  }

  /**
   * Returns an API client on the appropriate network.  The client defaults to https://api-sandbox.gem.co the
   * Gem Testnet environment.  To set to Mainnet pass string "https://api.gem.co" as the param.
   * @param  url of the API in use.  Default is testnet https://api-sandbox.gem.co, mainnet https://api.gem.co
   * @return Round
   * @throws Client.UnexpectedStatusCodeException
   * @throws IOException
   */
  public static Round client(String url)
    throws Client.UnexpectedStatusCodeException, IOException {
    if (url == null)
      url = API_HOST;
    patchboard = Patchboard.discover(url);

    Client patchboardClient = patchboard.spawn(new Authorizer());
    return new Round(patchboardClient);
  }

  /**
   * Authentication method to get a user with device auth of an authenticated user (see begin/finish device
   * authentication).  This authenticated user has the ability to perform transactions on a wallet.
   * @param apiToken - the application API token
   * @param userToken - the user token retreived after the initial device authentication pattern
   * @param deviceId -  unique device id provided at initial device authentication
   * @param email - email address of the user
   * @return User object with Gem-Device-Authentication level
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.Round#beginDeviceAuth(String, String, String, String)
   * @see co.gem.round.Round#completeDeviceAuth(String, String, String, String, String, String)
   */
  public User authenticateDevice(String apiToken, String userToken, String deviceId, String email)
    throws IOException, Client.UnexpectedStatusCodeException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    params.put("user_token", userToken);
    params.put("device_id", deviceId);
    patchboardClient.authorizer().authorize(AuthScheme.DEVICE, params);

    User user = user(email);
    user.fetch();
    return user;
  }

  /**
   * Method to authenticate an application instance with read only access to information about the application and
   * user base.
   * @param url of the application
   * @param apiToken api token of the application
   * @param instanceId unique instance id
   * @return Application Round Application
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.Application#authorizeInstance(String)
   */
  public Application authenticateApplication(String url, String apiToken, String instanceId)
    throws IOException, Client.UnexpectedStatusCodeException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    params.put("instance_id", instanceId);
    patchboardClient.authorizer().authorize(AuthScheme.APPLICATION, params);

    Application app = application(url);
    app.fetch();
    return app;
  }


  private void authenticateOtp(String apiToken, String key, String secret) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    if (key != null) params.put("key", key);
    if (secret != null) params.put("secret", secret);
    patchboardClient.authorizer().authorize(AuthScheme.OTP, params);
  }

  /**
   * Use when associating a user to an application for the first time.  An OTP key string will be returned and a
   * secret key will be emailed to the user's email address OOB.  The user will then provide the secret at which point
   * you can call completeDeviceAuth.  There is a 24hr TTL on the secret that is sent to the user.
   * @param email of the user
   * @param deviceName device name associated with the user
   * @param deviceId unique deviceID associated with the User/Application.
   * @param apiToken Api token of the application
   * @return String OTP key
   * @throws Client.UnexpectedStatusCodeException
   * @throws IOException
   */
  public String beginDeviceAuth(String email, String deviceName, String deviceId, String apiToken)
      throws Client.UnexpectedStatusCodeException, IOException {
    this.authenticateOtp(apiToken, null, null);
    JsonObject payload = new JsonObject();
    payload.addProperty("name", deviceName);
    payload.addProperty("device_id", deviceId);
    User user = this.user(email);
    try {
      user.resource().action("authorize_device", payload);
    } catch(Client.UnexpectedStatusCodeException e) {
      if (e.statusCode != 401) throw e;
      String authHeader = e.response.header("Www-Authenticate");
      return Http.extractParamsFromHeader(authHeader).get("key");
    }
    return null;
  }

  /**
   * Use for the second part to authenticating a user-device on an application for the first time.  Only a user-deviceid
   * can be used once within an application.  If the secret:key do not match, an error will be thrown which will contain
   * a new otp.key and a new secret will be sent to the user.
   * @param email
   * @param deviceName
   * @param deviceId
   * @param apiToken
   * @param key
   * @param secret
   * @return User Round User
   * @throws Client.UnexpectedStatusCodeException
   * @throws IOException
   */
  public User completeDeviceAuth(String email, String deviceName, String deviceId, String apiToken, String key, String secret)
      throws Client.UnexpectedStatusCodeException, IOException {
    this.authenticateOtp(apiToken, key, secret);
    JsonObject payload = new JsonObject();
    payload.addProperty("name", deviceName);
    payload.addProperty("device_id", deviceId);
    User user = this.user(email);
    Resource userResource = user.resource().action("authorize_device", payload);

    return new User(userResource, this);
  }

  /**
   * Returns a user object
   * @param email
   * @return User Round User
   */
  public User user(String email) {
    Map<String, String> query = new HashMap<String, String>();
    query.put("email", email);
    Resource resource = patchboardClient.resources("user_query", query);
    return new User(resource, this);
  }

  /**
   *
   * @param url
   * @return Application Round Application
   */
  public Application application(String url) {
    Resource resource = patchboardClient.resources("application", url);
    return new Application(resource, this);
  }

  /**
   *
   * @return UserCollection Round UserCollection
   */
  public UserCollection users() {
    Resource resource = patchboardClient.resources("users");
    return new UserCollection(resource, this);
  }

  public Client patchboardClient() { return patchboardClient; }


  static class AuthScheme {
    static final String DEVICE = "Gem-Device";
    static final String APPLICATION = "Gem-Application";
    static final String DEVELOPER = "Gem-Developer";
    static final String DEVELOPER_SESSION = "Gem-Developer-Session";
    static final String USER = "Gem-User";
    static final String OTP = "Gem-OOB-OTP";
  }
}