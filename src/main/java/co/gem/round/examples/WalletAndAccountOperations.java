package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.Client;
import java.security.*;
import java.security.spec.*;

import java.io.IOException;

/**
 * Created by jled on 12/31/14.
 */
public class WalletAndAccountOperations {
    public static void init(Boolean makeAddress) throws IOException, Client.UnexpectedStatusCodeException {
        User authUser = UserDeviceAuth.init();

        Wallet myWallet = authUser.wallets().get("default");
        System.out.println("\n" + myWallet.getBackupPublicSeed() + "\n" + myWallet.getCosignerPublicSeed());


        Account myAccount = myWallet.accounts().get("default");
        System.out.println("\nbalance: " + Long.toString(myAccount.balance()) +
        "\nPending Balance: " + Long.toString(myAccount.pendingBalance()));

        if(makeAddress) {
            Address address = myAccount.addresses().create();
            System.out.println(
                    "\nAddress: " + address.getAddressString());
        }

        //fund the account from a faucet and wait for 6 confirmations on a tx before attempting to send

        Payment payment = null;
        try {
            payment = myAccount.payToAddress("password", "mwhRqLNwRK7jsBLETP8ears5uSR7F7kEMN", 247644L);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        System.out.println(payment.getStatus());
    }
}
