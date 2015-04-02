package co.gem.round;

import co.gem.round.patchboard.AuthorizerInterface;
import co.gem.round.util.Strings;
import org.jboss.aerogear.security.otp.Totp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julian on 8/27/14.
 */
public class Authorizer implements AuthorizerInterface {
  private Map<String, Map<String, String>> schemes = new HashMap<>();
  private Totp totp = null;

  public void setOtpSecret(String otpSecret) {
    totp = new Totp(otpSecret);
  }

  @Override
  public void authorize(String scheme, Map<String, String> params) {
    schemes.put(scheme, params);
  }

  @Override
  public String getCredentials(String scheme) {
    Map<String, String> params = schemes.get(scheme);

    List<String> credentials = new ArrayList<String>();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      credentials.add(entry.getKey() + "=" + entry.getValue());
    }
    if (totp != null) {
      credentials.add("mfa_token=" + totp.now());
    }
    
    String credentialString = Strings.join(", ", credentials);

    if (credentialString == null) credentialString = "data=none";

    return scheme + " " + credentialString;
  }

  @Override
  public boolean isAuthorized(String scheme) {
    return schemes.containsKey(scheme);
  }
}
