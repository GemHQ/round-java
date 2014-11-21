package co.gem.round;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julian on 8/27/14.
 */
public class AuthorizerTest {
    public static final Authorizer authorizer = new Authorizer();

    @Test
    public void testNotAuthorized() {
        String credential = authorizer.getCredentials("Foo");
        Assert.assertEquals("Foo data=none", credential);
    }

    @Test
    public void testRegisteredScheme() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("foo", "bar");
        authorizer.authorize("Foo", params);
        String credential = authorizer.getCredentials("Foo");

        Assert.assertEquals("Foo foo=bar", credential);
    }

    @Test
    public void testAuthorizeBasic() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", "julian@gem.co");
        params.put("password", "password");
        authorizer.authorize("Basic", params);

        String credential = authorizer.getCredentials("Basic");
        Assert.assertEquals("Basic anVsaWFuQGdlbS5jbzpwYXNzd29yZA==", credential);
    }
}
