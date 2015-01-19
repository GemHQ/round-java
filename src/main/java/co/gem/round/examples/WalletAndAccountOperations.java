package co.gem.round.examples;

import co.gem.round.Account;
import co.gem.round.Address;
import co.gem.round.User;
import co.gem.round.Wallet;
import co.gem.round.patchboard.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by jled on 12/31/14.
 */
public class WalletAndAccountOperations {
    public static void init(Boolean makeAddress) throws
            IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException {

        User authUser = UserDeviceAuth.init();

        //Wallet.Wrapper newWallet = authUser.wallets().create("newWallet", "password", "testnet");
//        System.out.println("backupXpriv: " + newWallet.backupPrivateSeed +
//                            "\nWalletPrimaryXpub: " + newWallet.wallet.getPrimaryPublicSeed());


        Wallet myWallet = authUser.wallets().get("default");
        System.out.println("\nBackupXPub: " + myWallet.getBackupPublicSeed() +
                "\nCosignerXPub: " + myWallet.getCosignerPublicSeed() +
                "\nPrimaryXPub:" + myWallet.getPrimaryPublicSeed());


        Account myAccount = myWallet.accounts().get("default");
        System.out.println("\nbalance: " + Long.toString(myAccount.balance()) +
        "\nPending Balance: " + Long.toString(myAccount.pendingBalance()) + "\n");

        if(makeAddress) {
            Address address = myAccount.addresses().create();
            System.out.println(
                    "\nAddress: " + address.getAddressString());
        }

//        Map<String, Address> addys = myAccount.addresses().asMap();
//        List<Address> addys = myAccount.addresses().asList();
//        for(Address address : addys) {
//            System.out.println(address.getAddressPath());
//        }

        //fund the account from a faucet and wait for 6 confirmations on a tx before attempting to send
//        Payment payment = null;
//        try {
//            payment = myAccount.payToAddress("password", "mwhRqLNwRK7jsBLETP8ears5uSR7F7kEMN", 227579L);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(payment.getStatus());
    }
}
