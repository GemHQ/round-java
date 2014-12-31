package co.gem.round.example;

import co.gem.round.*;
import co.gem.round.patchboard.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Example {

  private static final String API_TOKEN = "DcBN1ZogZqFRJq2qcxNYk2fI_HOZoLiPhyWsVs8dtMQ";
  private static final String APP_URL = "https://api-sandbox.gem.co/apps/ppTz8QRMAZoUDBItjjVVzA";

  private static final String USER_TOKEN = "5tDB05iYH3jClS3q39TNxiX9JvKOxg6E-P3u3W8aIfc";
  private static final String USER_EMAIL = "joshua+uJava1@gem.co";
  private static final String DEVICE_ID = "12345abcd257v2212";


  public static void main(String[] args) {
    //createClient();
    //createUser();
    //authenticateDevice();

    //deviceAuthenticateUser();
    //doAccountWork();
    //makeAnAddress();
    //makePaymentFromAccount();

    //Console console = System.console();

    //create the API client pointing to one of our APIs
    // testnet sandbox:  https://api-sandbox.gem.co
    // mainnet:  https://api.gem.co
    Round client = null;
    try {
      client = Round.client("https://api-sandbox.gem.co");
    } catch (Client.UnexpectedStatusCodeException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(client.toString());
/*
        //user creation
        // testnet or mainnet
        // remember to to match what the client is set to
        User.Wrapper newUser = null;
        try {
            newUser = client.users().create("joshua+javau9@gem.co", "password", "testnet");
        } catch (Client.UnexpectedStatusCodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("Backup Seed: " + newUser.backupPrivateSeed); // userWrapper returns backup privKey only on user creation
        System.out.println("User url: " + newUser.user.url()
                + " \n User Token: " + newUser.user.getAttribute("user_token").getAsString()
                + "\n User email: " + newUser.user.email());

        //authenticate Device
        User myUser = newUser.user;
        String key = "";
        try {
            key = myUser.beginDeviceAuth(API_TOKEN, DEVICE_NAME, DEVICE_ID);
        } catch (Client.UnexpectedStatusCodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("OOB Secret: " + key);

        //get the OOB-OTP from email
        String otp = console.readLine("Enter OTP: ");

        User authUser = null;
        try {
            authUser = myUser.completeDeviceAuth(API_TOKEN, DEVICE_NAME, DEVICE_ID, key, otp);
            myUser = null;
        } catch (Client.UnexpectedStatusCodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

    User authUser = null;
    try{
      authUser = client.authenticateDevice(API_TOKEN, USER_TOKEN,DEVICE_ID, USER_EMAIL);
    } catch (IOException e){
      e.printStackTrace();
    } catch (Client.UnexpectedStatusCodeException e) {
      e.printStackTrace();
    }

    Wallet defaultWallet = null;
    try {
      defaultWallet = authUser.defaultWallet();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Client.UnexpectedStatusCodeException e) {
      e.printStackTrace();
    }

    //create a new account
    Account checking = null;
    try {
      checking = defaultWallet.accounts().create("checking");
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Client.UnexpectedStatusCodeException e) {
      e.printStackTrace();
    }
    try {
      AccountCollection accounts = defaultWallet.accounts();
      for (Account account : accounts) {

      }
    } catch (IOException e) {

    } catch (Client.UnexpectedStatusCodeException e) {

    }


    //get the default account
    Account defaultAccount = null;
    try {
      defaultAccount = defaultWallet.accounts().get("default");
    } catch (Client.UnexpectedStatusCodeException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for(int i = 0; i <= 10; i++){
      try {
        System.out.println(defaultAccount.createAddress().getAddressString());
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Client.UnexpectedStatusCodeException e) {
        e.printStackTrace();
      }
    }

    //make a payment
//    Payment payment = null;
//    try {
//      payment = defaultAccount.payToAddress("password", "1o234i9joiasdf98023489rs324j89", 586748596);
//    } catch (IOException e) {
//      e.printStackTrace();
//    } catch (Client.UnexpectedStatusCodeException e) {
//      e.printStackTrace();
//    } catch (NoSuchAlgorithmException e) {
//      e.printStackTrace();
//    } catch (InvalidKeySpecException e) {
//      e.printStackTrace();
//    }

//    System.out.println(payment.getAttribute("hash").getAsString());
  }


}