package co.gem.round;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by jled on 12/31/14.
 */
public class Utils {


  // Set these for testing.
  private static final String API_TOKEN = "kTvjcoCCreKAPrE9deST5lr3EUFO5MlymFBHt5jFh78";
  private static final String ADMIN_TOKEN = "-OOULkxd0CWbVkM6Z0J0H6BEtIXKhnEsBudoKoghVeo";
  private static final String TOTP_SECRET = "jgbijwin4qofzeab";

  private static final String APP_INSTANCE_ID = "DE73gGgLcJB07D0gW0G3VUN3aywgcd1T8F3bT-0eTy8";
  private static final String DEV_EMAIL = "joshua+devJava1@gem.co";

  private static final String USER_EMAIL = "james@gem.co";
  private static final String USER_TOKEN = "zqTjdwdIbMemJxp6KpLnDzFVYXWnYWjyb01-30Y9HLE";
  private static final String USER_URL = "https://api-sandbox.gem.co/users/D96qgNVcTekOjTcAk8ESMA";


  private static final String DEVICE_TOKEN = USER_EMAIL + "device7";
  private static final String DEVICE_NAME = USER_EMAIL + "mbp-java7";


  public static String getUserInput(String str) throws IOException {
    BufferedReader buff = new BufferedReader(
        new InputStreamReader(System.in));
    System.out.print(str);
    System.out.flush();
    return buff.readLine();
  }

  public static void print(String str) {
    System.out.println(str);
  }

  public static String getAdminToken() {
    return ADMIN_TOKEN;
  }

  public static String getTotpSecret() {
    return TOTP_SECRET;
  }

  public static String getApiToken() {
    return API_TOKEN;
  }

  public static String getDevEmail() {
    return DEV_EMAIL;
  }

  public static String getUserToken() {
    return USER_TOKEN;
  }

  public static String getUserEmail() {
    return USER_EMAIL;
  }

  public static String getRandomUserEmail() {
    String[] email = USER_EMAIL.split("@");
    Random rand = new Random();
    String first = email[0] + "+roundjavatest" + rand.nextInt(100000);
    return first + "@" + email[1];
  }

  public static String getDeviceToken() {
    return DEVICE_TOKEN;
  }

  public static String getDeviceName() {
    return DEVICE_NAME;
  }

  public static String getAppInstance() {
    return APP_INSTANCE_ID;
  }

  public static String getUserUrl() {
    return USER_URL;
  }
}



