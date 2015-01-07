package co.gem.round.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jled on 12/31/14.
 */
public class Utils {


    private static final String API_TOKEN = "";
    private static final String APP_URL = "";
    private static final String APP_INSTANCE = "";
    private static final String DEV_EMAIL = "";

    private static final String USER_EMAIL = "";
    private static final String USER_TOKEN = "";
    private static final String USER_URL = "";

    private static final String DEVICE_ID = "";
    private static final String DEVICE_NAME = "";

    public static String getUserInput(String str) throws IOException {
        BufferedReader buff = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print(str);
        System.out.flush();
        return buff.readLine();
    }

    public static String getApiToken() {
        return API_TOKEN;
    }

    public static String getAppUrl() {
        return APP_URL;
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

    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static String getDeviceName() {
        return DEVICE_NAME;
    }

    public static String getAppInstance() {
        return APP_INSTANCE;
    }

    public static String getUserUrl() {
        return USER_URL;
    }
}


