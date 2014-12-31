package co.gem.round.examples;

import co.gem.round.Round;
import co.gem.round.User;
import co.gem.round.Wallet;
import co.gem.round.patchboard.Client;

import java.io.IOException;

/**
 * Created by jled on 12/31/14.
 */
public class WalletAndAccountOperations {

    private static final String API_TOKEN = "ssgPhVv-Pv-soqQtRM7pIFHzg7uGOGFzrAfSBVONqgo";
    private static final String APP_URL = "https://api-sandbox.gem.co/apps/oHgM6NrHq-C_K2-f1pfwIg";
    private static final String DEV_EMAIL = "joshua+devJava1@gem.co";

    private static final String USER_TOKEN = "OZyLrmlIUO6jT3HgG0o6SAFKeSXhdiMFiV_6YngXGfQ";
    private static final String USER_EMAIL = "joshua+userJava1@gem.co";
    private static final String DEVICE_ID = "12345abcd257v2212";
    private static final String DEVICE_NAME = "mbp-java";

    public static void init() throws IOException, Client.UnexpectedStatusCodeException {

        Round client = Round.client("https://api-sandbox.gem.co");

        User authUser = client.authenticateDevice(API_TOKEN, USER_TOKEN,DEVICE_ID, USER_EMAIL);

        System.out.println("\nuser token: " + authUser.userToken() +
                "\nuser url: " + authUser.userUrl() +
                "\nuser key: " + authUser.key());

        Wallet myWallet = authUser.wallets().get("default");
    }
}
