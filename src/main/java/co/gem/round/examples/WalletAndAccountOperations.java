package co.gem.round.examples;

import co.gem.round.Account;
import co.gem.round.Address;
import co.gem.round.User;
import co.gem.round.Wallet;
import co.gem.round.patchboard.Client;

import java.io.IOException;

/**
 * Created by jled on 12/31/14.
 */
public class WalletAndAccountOperations {
    public static void init() throws IOException, Client.UnexpectedStatusCodeException {
        User authUser = UserDeviceAuth.init();

        System.out.println("\nuser token: " + authUser.userToken() +
                "\nuser url: " + authUser.userUrl() +
                "\nuser key: " + authUser.key());

        Wallet myWallet = authUser.wallets().get("default");
        System.out.println("\n" + myWallet.getBackupPublicSeed() + "\n" + myWallet.getCosignerPublicSeed());


        Account myAccount = myWallet.accounts().get("default");
        System.out.println(Long.toString(myAccount.balance()));

        Address myAddress = myAccount.addresses().create();
        System.out.println(myAddress.getAddressString());

        //fund the account from a faucet and wait for 6 confirmations on a tx before attempting to send

//        Payment payment = null;
//        try {
//            payment = myAccount.payToAddress("password", "1lJPij0u4209430jsdf", 84059540L);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(payment.getStatus());
    }
}
