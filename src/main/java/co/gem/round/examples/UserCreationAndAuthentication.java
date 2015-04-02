package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class UserCreationAndAuthentication {

    public static void init() throws IOException, Client.UnexpectedStatusCodeException{
        //create the API client pointing to one of our APIs
        // testnet sandbox:  https://api-sandbox.gem.co
        // mainnet:  https://api.gem.co
        Round client = Round.client("https://api-sandbox.gem.co");
        System.out.println(client.toString());

        //user creation
        // testnet or mainnet
        // remember to to match what the client is set to
        User newUser = null;
        try {
            newUser = client.users().create(Utils.getUserEmail(), "password", "testnet");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        System.out.println("User url: " + newUser.url()
            + "\nUser Token: " + newUser.getAttribute("user_token").getAsString()
            + "\nUser email: " + newUser.email());

        //authenticate Device
        Device device = newUser.devices().create(Utils.getDeviceName());

        System.out.println("Redirect URI: " + device.getRedirectURI());
        System.out.println("authed user: " + newUser.email() + " | " + newUser.toString());
    }
}