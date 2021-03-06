package co.gem.round;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.crypto.InvalidCipherTextException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.patchboard.Client;

public class ApplicationAuthTest {
    Round client;
    Application app;

    @Before
    public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
        client = Round.client(Utils.getApiUrl());
        client.authenticateIdentify(Utils.getApiToken());

        app = client.authenticateApplication(Utils.getApiToken(), Utils.getAdminToken());
        app.setTotpSecret(Utils.getTotpSecret());
    }

    @Test
    public void createWalletsTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        int random = new Random().nextInt(1000000);
        String walletName = "Wallet" + random;

        Wallet.Wrapper wrapper = app.wallets(false).create(walletName, "passphrase");
        Wallet wallet = wrapper.getWallet();
        try {
            wallet.unlock("wrong", new UnlockedWalletCallback() {
                @Override
                public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
                    Assert.fail();
                }
            });
        } catch (Exception ignore) {  }
        wallet.unlock("passphrase", new UnlockedWalletCallback() {
            @Override
            public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
                System.out.println("worked!");
            }
        });
        Assert.assertEquals(wallet.getPrimaryPublicSeed(), app.wallet(walletName).getPrimaryPublicSeed());
    }

    @Test
    public void viewUsersTest() throws IOException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        int size = app.users().size();
        int random = new Random().nextInt(1000000);
        String email = "email" + random + "@mailinator.com";

        String deviceToken = client.users().create(email, "fname", "lname", "password", "deviceName", "http://gem.co/user/");
        Assert.assertEquals(size + 1, app.users().size());
    }

    @Test
    public void differentNetworkAcountsTest() throws Client.UnexpectedStatusCodeException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        Wallet.Wrapper wrapper = app.wallets().create("name", "passphrase");
        Wallet wallet = wrapper.getWallet();

        Account testnetAccount = wallet.accounts().create("name", Round.Network.TESTNET);
        Account bitcoinAccount = wallet.accounts(false).create("name2", Round.Network.BITCOIN);
        Account litecoinAccount = wallet.accounts().create("name3", Round.Network.LITECOIN);
        Account dogecoinAccount = wallet.accounts(false).create("name4", Round.Network.DOGECOIN);

        Address testnetAddress = testnetAccount.addresses(false).create();
        Address bitcoinAddress = bitcoinAccount.addresses().create();
        Address litecoinAddress = litecoinAccount.addresses(false).create();
        Address dogecoinAddress = dogecoinAccount.addresses().create();

        System.out.println(testnetAddress.getAddressString());
        System.out.println(bitcoinAddress.getAddressString());
        System.out.println(litecoinAddress.getAddressString());
        System.out.println(dogecoinAddress.getAddressString());

        Assert.assertEquals(Round.Network.TESTNET.toString(), wallet.account("name").getString("network"));
        Assert.assertEquals(Round.Network.BITCOIN.toString(), wallet.account("name2").getString("network"));
        Assert.assertEquals(Round.Network.LITECOIN.toString(), wallet.account("name3").getString("network"));
        Assert.assertEquals(Round.Network.DOGECOIN.toString(), wallet.account("name4").getString("network"));

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
