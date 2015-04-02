package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.Address;
import co.gem.round.Transaction;
import co.gem.round.Wallet;
import co.gem.round.patchboard.Client;
import org.bitcoinj.core.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

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
                "\nPrimaryXPub:" + myWallet.getPrimaryPublicSeed() + "\n");

        Account myAccount = myWallet.accounts().get("default");
        for(Account a : myWallet.accounts()) {
            System.out.println("Name: " + a.name() +
                            " | balance: " + Long.toString(a.balance()) +
                            " | Pending Balance: " + Long.toString(a.pendingBalance()));
        }

        Utils.print("");

        for(Transaction tx : myAccount.transactions()) {
          if (tx.getType().equals("incoming")) {
            System.out.println(
                "Tx status: " + tx.getStatus() +
                    " | Tx conf: " + tx.getConfirmations() +
                    " | Tx val: " + tx.getValue() +
                    " | Tx type: " + tx.getType() +
                    " | Tx date: " + tx.getCreatedAt() +
                    " | Tx hash: " + tx.getTransactionHash());
          }
        }

        List<Address> addys = myAccount.addresses().asList();
        for(Address address : addys) {
          System.out.println(address.getAddressPath() + " | " + address.getAddressString());
        }

//        fund the account from a faucet and wait for 6 confirmations on a tx before attempting to send
        Transaction payment = null;
        try {
            payment = myAccount.payToAddress("password", "2N11qBXajB4DPNshMALBYNjDjgvjL7iGLZT", 450000L, 1);
            System.out.println(payment.resource().attributes().get("hash").getAsString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        if(makeAddress) {
          Address address = myAccount.addresses().create();
          System.out.println(
            "\n\nAddress: " + address.getAddressString());
        }
    }
}
