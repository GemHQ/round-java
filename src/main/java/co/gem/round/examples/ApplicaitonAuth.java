package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by joshualederman on 12/31/14.
 */
public class ApplicaitonAuth {
    public static void init() throws IOException, Client.UnexpectedStatusCodeException {
        Round client = Round.client("https://api-sandbox.gem.co");
        Application myApp = client.authenticateApplication(
                Utils.getAppUrl(),
                Utils.getApiToken(),
                Utils.getAppInstance()
        );

        
        List<User> users = myApp.users().asList();
        for(User u : users){
            System.out.println(u.email());
        }
    }
}
