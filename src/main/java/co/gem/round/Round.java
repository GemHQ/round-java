package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Patchboard;
import co.gem.round.patchboard.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Round is the Gem API client class.  Most importantly it is where you access the authentication methods for the api
 * @author Julian Vergel de Dios (julian@gem.co) on 1/4/15.
 */
public class Round {

  static final String API_HOST = "https://api-sandbox.gem.co";

  private static Patchboard patchboard;

  private Client patchboardClient;
  private Authorizer authorizer;

  private Round(Client patchboardClient, Authorizer authorizer) {
    this.patchboardClient = patchboardClient;
    this.authorizer = authorizer;
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

    Authorizer authorizer = new Authorizer();
    Client patchboardClient = patchboard.spawn(authorizer);
    return new Round(patchboardClient, authorizer);
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
   */
  public User authenticateDevice(String apiToken, String userToken, String deviceId, String email)
    throws IOException, Client.UnexpectedStatusCodeException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("api_token", apiToken);
    params.put("user_token", userToken);
    params.put("device_id", deviceId);
    patchboardClient.authorizer().authorize(AuthScheme.DEVICE, params);
    Map<String, String> identifyParams = new HashMap<>();
    params.put("api_token", apiToken);
    patchboardClient.authorizer().authorize(AuthScheme.IDENTIFY, identifyParams);


    User user = user(email);
    user.fetch();
    return user;
  }

  /**
   * Method to authenticate an application instance with read only access to information about the application and
   * user base.
   * @param url of the application
   * @param apiToken api token of the application
   * @param instanceToken unique instance id
   * @param otpSecret TOTP secret for authorizing transactions
   * @return Application Round Application
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   */
  public Application authenticateApplication(String url, String apiToken, String instanceToken, String otpSecret)
    throws IOException, Client.UnexpectedStatusCodeException {
    authorizer.setOtpSecret(otpSecret);
    return authenticateApplication(url, apiToken, instanceToken);
  }

  public Application authenticateApplication(String url, String apiToken, String instanceToken)
      throws IOException, Client.UnexpectedStatusCodeException {
    Map<String, String> params = new HashMap<>();
    params.put("api_token", apiToken);
    params.put("instance_id", instanceToken);
    patchboardClient.authorizer().authorize(AuthScheme.APPLICATION, params);
    Map<String, String> identifyParams = new HashMap<>();
    params.put("api_token", apiToken);
    patchboardClient.authorizer().authorize(AuthScheme.IDENTIFY, identifyParams);

    Application app = application(url);
    app.fetch();
    return app;
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
    static final String IDENTIFY = "Gem-Identify";
  }
}