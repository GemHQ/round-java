package co.gem.round.examples;

import co.gem.round.*;
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
                "\nPrimaryXPub:" + myWallet.getPrimaryPublicSeed() + "\n");

        Utils.print(myWallet.balance().toString());


        Account myAccount = myWallet.accounts().get("default");
        for(Account a : myWallet.accounts()) {
            System.out.println("Name: " + a.name() +
                            " | balance: " + Long.toString(a.balance()) +
                            " | Pending Balance: " + Long.toString(a.pendingBalance()));
        }

        Utils.print("");

        for(Transaction tx : myAccount.transactions()) {
            System.out.println(
                    "Tx status: " + tx.getStatus() +
                            " | Tx date: " + tx.getCreatedAt() +
                            " | Tx hash: " + tx.getTransactionHash());

//            if (tx.getStatus().equals("unconfirmed")){
//                tx.cancel();
//            }
        }

        if(makeAddress) {
            Address address = myAccount.addresses().create();
            System.out.println(
                    "\nAddress: " + address.getAddressString());
        }


//        List<Address> addys = myAccount.addresses().asList();
//        for(Address address : addys) {
//            System.out.println(address.getAddressPath());
//        }

//        fund the account from a faucet and wait for 6 confirmations on a tx before attempting to send
//        Payment payment = null;
//        try {
//            payment = myAccount.payToAddress("password", "2N3DdaZ8K9PmxXYXmwj9QZcPbcgqqPcs8hM", 40000L);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }

//        System.out.println(payment.getStatus());
    }
}
