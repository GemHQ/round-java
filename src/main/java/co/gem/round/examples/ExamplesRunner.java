package co.gem.round.examples;

import co.gem.round.patchboard.Client;

import java.io.IOException;

/**
 * Created by jled on 12/31/14.
 */
public class ExamplesRunner {
    public static void main(String[] args) throws IOException, Client.UnexpectedStatusCodeException {

        //UserCreationAndAuthentication.init();
        //UserDeviceAuth.init();
        WalletAndAccountOperations.init(false);
        //ApplicaitonAuth.init();
    }
}
