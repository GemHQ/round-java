package co.gem.round;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

/**
 * Created by jled on 12/31/14.
 */
public class Utils {


  // Set these for testing.

  private static final String API_URL = "http://localhost:8999";

  private static final String API_TOKEN = "gLtx5wivoHyOJlTQSQBp8nqv_u7Ung8vZzVwYKrfoXs";
  private static final String ADMIN_TOKEN = "P_EXnT9RjKCZhpSOyvqv0ER6AGPRotJFuszz90kKwWY";
  private static final String TOTP_SECRET = "oi3skxjunerfarqt";


  private static final String USER_EMAIL = "poema@gem.co";


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


  public static JsonObject loadJsonResource(String path) throws
      URISyntaxException, FileNotFoundException, IOException {
    URL url = Utils.class.getResource("/wallet_ciphertexts.json");
    Assert.assertNotNull(url);

    String payload = null;
    BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      payload = sb.toString();
    } finally {
      br.close();
    }

    return new JsonParser().parse(payload).getAsJsonObject();
  }
}



