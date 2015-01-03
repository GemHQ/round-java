package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.AccountCollection;
import co.gem.round.patchboard.Client;

import java.io.IOException;
import java.util.List;

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
    }
}
