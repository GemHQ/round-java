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

  private static final String API_URL = "https://api.gem.co";

  private static final String API_TOKEN = "WQj_QQgOvhJXo4ISE0oryGUBwcmLW7c0CvO59qMZ-tE";
  private static final String ADMIN_TOKEN = "bm-UBRjQOFBCyod6MbdyGX0nVHvEb2gjoPkZYMpPlO8";
  private static final String TOTP_SECRET = "zh2l2ntslymi72on";


  private static final String USER_EMAIL = "joshua+usertest1@gem.co";


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

  public static String getApiUrl() { return API_URL; }

  public static String getAdminToken() {
    return ADMIN_TOKEN;
  }

  public static String getTotpSecret() {
    return TOTP_SECRET;
  }

  public static String getApiToken() {
    return API_TOKEN;
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
}



