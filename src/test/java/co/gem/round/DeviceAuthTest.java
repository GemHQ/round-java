package co.gem.round;

import co.gem.round.coinop.MultiWallet;
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
    user = client.authenticateDevice(Utils.getApiToken(), user.userToken(), user.getString("device_id"), user.email());

    // Make sure the default wallet is in the collection
    user.defaultWallet();
    Assert.assertEquals(1, user.wallets().size());
    // Create a new wallet
    Wallet.Wrapper wrapper = user.wallets().create("name", "password", "testnet");
    // Make sure the wallet count increases
    Assert.assertEquals(2, user.wallets().size());
    Wallet wallet = wrapper.wallet;
    // Unlock the wallet
    wallet.unlock("password", new UnlockedWalletCallback() {
      @Override
      public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
        System.out.println("I'm handling this unlock!");
      }
    });
    // Wallet balance
    Assert.assertEquals(new Long(0), wallet.balance());

    // Default account exists?
    wallet.defaultAccount();
    Assert.assertEquals(1, wallet.accounts().size());
    // Create a new account
    Account account = wallet.accounts().create("account");
    // Make sure account count increases
    Assert.assertEquals(2, wallet.accounts().size());

    // Make sure we can call transactions
    Assert.assertEquals(0, account.transactions().size());
  }
}
