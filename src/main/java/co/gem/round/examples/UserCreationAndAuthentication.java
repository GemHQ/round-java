package co.gem.round.examples;

import co.gem.round.*;
import co.gem.round.patchboard.Client;

import java.io.IOException;


public class UserCreationAndAuthentication {

    private static final String API_TOKEN = "DcBN1ZogZqFRJq2qcxNYk2fI_HOZoLiPhyWsVs8dtMQ";
    private static final String APP_URL = "https://api-sandbox.gem.co/apps/ppTz8QRMAZoUDBItjjVVzA";

    private static final String USER_TOKEN = "5tDB05iYH3jClS3q39TNxiX9JvKOxg6E-P3u3W8aIfc";
    private static final String USER_EMAIL = "joshua+uJava1@gem.co";
    private static final String DEVICE_ID = "12345abcd257v2212";

    public static void init() throws IOException, Client.UnexpectedStatusCodeException{
        //create the API client pointing to one of our APIs
        // testnet sandbox:  https://api-sandbox.gem.co
        // mainnet:  https://api.gem.co
        Round client = Round.client("https://api-sandbox.gem.co");
        System.out.println(client.toString());
    }
}