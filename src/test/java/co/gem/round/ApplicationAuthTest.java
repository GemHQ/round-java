package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.Utils;
import co.gem.round.patchboard.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class ApplicationAuthTest {
  Round client;
  Application app;

  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
    client = Round.client("https://api-sandbox.gem.co/");
    app = client.authenticateApplication(Utils.getApiToken(), Utils.getAdminToken());
    app.setTotpSecret(Utils.getTotpSecret());
    // This is definitely a bug. Identify doesn't work if done before application auth
    client.authenticateIdentify(Utils.getApiToken());
  }

  @Test
  public void createWalletsTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException {
    Wallet.Wrapper wrapper = app.wallets().create("name", "passphrase", "testnet");
    Wallet wallet = wrapper.getWallet();
    try {
      wallet.unlock("wrong", new UnlockedWalletCallback() {
        @Override
        public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
          Assert.fail();
        }
      });
    } catch (Exception e) {  }
    wallet.unlock("passphrase", new UnlockedWalletCallback() {
      @Override
      public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
        System.out.println("worked!");
      }
    });
  }

  @Test
  public void viewUsersTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException {
    int size = app.users().size();
    int random = new Random().nextInt(1000000);
    String email = "email" + random + "@mailinator.com";
    String deviceToken = client.users().create(email, "fname", "lname", "wat", "testnet", "daaaaname");
    Assert.assertEquals(size + 1, app.users().size());
  }

  // The following will reset your API token and mess some stuff up.
//  @Test
//  public void resetTokensTest() throws IOException, Client.UnexpectedStatusCodeException {
//    app.setTotpSecret(Utils.getTotpSecret());
//    app.reset("api_token");
//    System.out.println("New API token: " + app.getString("api_token"));
//    app.fetch();
//  }
}
