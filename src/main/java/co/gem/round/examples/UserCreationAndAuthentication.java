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
        User.Wrapper newUser = null;
        try {
            newUser = client.users().create(Utils.getUserEmail(), "password", "testnet");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("Backup Seed: " + newUser.backupPrivateSeed); // userWrapper returns backup privKey only on user creation
        System.out.println("User url: " + newUser.user.url()
                + "\nUser Token: " + newUser.user.getAttribute("user_token").getAsString()
                + "\nUser email: " + newUser.user.email());

        //authenticate Device
        String key = client.beginDeviceAuth(Utils.getUserEmail(),
            Utils.getDeviceName(),
            Utils.getDeviceId(),
            Utils.getApiToken());

        System.out.println("OOB Secret: " + key);

        //get the OOB-OTP from email
        String otp = Utils.getUserInput("Enter OTP: ");

        User authUser = client.completeDeviceAuth(Utils.getUserEmail(),
            Utils.getDeviceName(),
            Utils.getDeviceId(),
            Utils.getApiToken(),
            key,
            otp);
    }
}