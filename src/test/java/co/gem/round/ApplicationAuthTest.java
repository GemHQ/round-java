package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.Utils;
import co.gem.round.patchboard.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
  public void createWalletsTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
    Wallet.Wrapper wrapper = app.wallets().create("name", "passphrase");
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
  public void viewUsersTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
    int size = app.users().size();
    int random = new Random().nextInt(1000000);
    String email = "email" + random + "@mailinator.com";
    String deviceToken = client.users().create(email, "fname", "lname", "wat", "testnet", "daaaaname");
    Assert.assertEquals(size + 1, app.users().size());
  }

  @Test
  public void differentNetworkAcountsTest() throws Client.UnexpectedStatusCodeException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
    Wallet.Wrapper wrapper = app.wallets().create("name", "passphrase");
    Wallet wallet = wrapper.getWallet();
    Account testnetAccount = wallet.accounts().create("name", Round.Network.TESTNET);
    Account bitcoinAccount = wallet.accounts().create("name2", Round.Network.BITCOIN);
    Account litecoinAccount = wallet.accounts().create("name3", Round.Network.LITECOIN);
    Account dogecoinAccount = wallet.accounts().create("name4", Round.Network.DOGECOIN);
    Address testnetAddress = testnetAccount.addresses().create();
    Address bitcoinAddress = bitcoinAccount.addresses().create();
    Address litecoinAddress = litecoinAccount.addresses().create();
    Address dogecoinAddress = dogecoinAccount.addresses().create();
    System.out.println(testnetAddress.getAddressString());
    System.out.println(bitcoinAddress.getAddressString());
    System.out.println(litecoinAddress.getAddressString());
    System.out.println(dogecoinAddress.getAddressString());
    Assert.assertEquals('2', testnetAddress.getAddressString().charAt(0));
    Assert.assertEquals('3', bitcoinAddress.getAddressString().charAt(0));
    Assert.assertEquals('3', litecoinAddress.getAddressString().charAt(0));
    boolean startsWithA = dogecoinAddress.getAddressString().charAt(0) == 'A';
    boolean startsWith9 = dogecoinAddress.getAddressString().charAt(0) == '9';
    Assert.assertTrue(startsWithA || startsWith9);
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
