package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class UserCreationAndAuthentication {

    private static final String API_TOKEN = "ssgPhVv-Pv-soqQtRM7pIFHzg7uGOGFzrAfSBVONqgo";
    private static final String APP_URL = "https://api-sandbox.gem.co/apps/oHgM6NrHq-C_K2-f1pfwIg";
    private static final String DEV_EMAIL = "joshua+devJava1@gem.co";

    private static final String USER_TOKEN = "OZyLrmlIUO6jT3HgG0o6SAFKeSXhdiMFiV_6YngXGfQ";
    private static final String USER_EMAIL = "joshua+userJava1@gem.co";
    private static final String DEVICE_ID = "12345abcd257v2212";
    private static final String DEVICE_NAME = "mbp-java";

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
            newUser = client.users().create(USER_EMAIL, "password", "testnet");
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
                String key = newUser.user.beginDeviceAuth(API_TOKEN, DEVICE_NAME, DEVICE_ID);

        System.out.println("OOB Secret: " + key);

        //get the OOB-OTP from email
        String otp = Utils.getUserInput("Enter OTP: ");

        User authUser = newUser.user.completeDeviceAuth(API_TOKEN, DEVICE_NAME, DEVICE_ID, key, otp);
    }
}