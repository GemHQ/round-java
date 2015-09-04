package co.gem.round.patchboard;

import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public interface AuthorizerInterface {
    public void authorize(String scheme, Map<String, String> params);
    public String getCredentials(String scheme);
    public boolean isAuthorized(String scheme);
    public void setOtpSecret(String otpSecret);
}
