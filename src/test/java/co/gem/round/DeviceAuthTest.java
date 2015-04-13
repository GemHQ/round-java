package co.gem.round;

import co.gem.round.examples.Utils;
import co.gem.round.patchboard.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class DeviceAuthTest {
  Round client;

  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
    client = Round.client("https://api-sandbox.gem.co");
  }

  @Test
  public void deviceAuthTest() throws NoSuchAlgorithmException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, IOException, InterruptedException {
    client.authenticateIdentify(Utils.getApiToken());
    int random = new Random().nextInt(1000000);
    String email = Utils.getRandomUserEmail();
    System.out.println(email);
    User user = client.users().create(email, "fname", "lname", "wat", "testnet", "daaaaname");
    System.out.println("This will sleep for 60 seconds while the user completes signup. Hurry!");
    Thread.sleep(60000);
    client.authenticateDevice(Utils.getApiToken(), user.userToken(), user.getString("device_id"), user.email());
    Assert.assertEquals(1, user.wallets().size());
    Wallet.Wrapper wrapper = user.wallets().create("name", "password", "testnet");
    Assert.assertEquals(2, user.wallets().size());
    Wallet wallet = wrapper.wallet;
    Assert.assertEquals(1, wallet.accounts().size());
    Account account = wallet.accounts().create("account");
    Assert.assertEquals(2, wallet.accounts().size());
    Assert.assertEquals(0, account.transactions().size());
  }
}
