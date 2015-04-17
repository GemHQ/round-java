package co.gem.round;

import co.gem.round.examples.Utils;
import co.gem.round.patchboard.Client;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class IdentifyAuthTest {
  Round client;

  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
    client = Round.client("http://localhost:8999/");
    client.authenticateIdentify(Utils.getApiToken());
  }

  @Test
  public void creatingUser() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException,
      NoSuchAlgorithmException {
    client.authenticateIdentify(Utils.getApiToken());
    int random = new Random().nextInt(1000000);
    String email = "email" + random + "@mailinator.com";
    User user = client.users().create(email, "fname", "lname", "wat", "testnet", "daaaaname");
    Assert.assertEquals(email, user.email());
    Assert.assertEquals("fname", user.firstName());
    Assert.assertEquals("lname", user.lastName());
  }

  @Test
  public void userAccessingCollections() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException {
    client.authenticateIdentify(Utils.getApiToken());
    int random = new Random().nextInt(1000000);
    String email = "email" + random + "@mailinator.com";
    User user = client.users().create(email, "fname", "lname", "wat", "testnet", "daaaaname");
    Assert.assertEquals(0, user.devices().size());
    // WTF? Why does above work?
//    Assert.assertEquals(user.wallets().size(), 0);
  }
}
