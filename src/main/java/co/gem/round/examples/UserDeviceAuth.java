package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.Client;


import java.io.IOException;

/**
 * Created by jled on 12/31/14.
 */

public class UserDeviceAuth {
    public static User init() throws IOException, Client.UnexpectedStatusCodeException {
        Round client = Round.client("https://api-sandbox.gem.co");
        User authUser = client.authenticateDevice(Utils.getApiToken(),
                Utils.getUserToken(),
                Utils.getDeviceId(),
                Utils.getUserEmail());

        System.out.println("\nuser token: " + authUser.userToken() +
                "\nuser url: " + authUser.userUrl() +
                "\nuser key: " + authUser.key());

        return authUser;
    }
}
