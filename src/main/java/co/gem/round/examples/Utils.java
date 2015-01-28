package co.gem.round.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jled on 12/31/14.
 */
public class Utils {


    private static final String API_TOKEN = "ssgPhVv-Pv-soqQtRM7pIFHzg7uGOGFzrAfSBVONqgo";
    private static final String APP_URL = "https://api-sandbox.gem.co/apps/oHgM6NrHq-C_K2-f1pfwIg";

    private static final String APP_INSTANCE_ID = "DE73gGgLcJB07D0gW0G3VUN3aywgcd1T8F3bT-0eTy8";
    private static final String DEV_EMAIL = "joshua+devJava1@gem.co";

    private static final String USER_EMAIL = "joshua+userJava07@gem.co";
    private static final String USER_TOKEN = "0x20mNTVcUWujHrtGENR9NjSF0_K6JYLlDfMTMUjh-Q";
    private static final String USER_URL = "https://api-sandbox.gem.co/users/1bIKvHKszboJhLBr2PfLLA";


    private static final String DEVICE_ID = USER_EMAIL + "device6";
    private static final String DEVICE_NAME = USER_EMAIL + "mbp-java1";


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
        return APP_INSTANCE_ID;
    }

    public static String getUserUrl() {
        return USER_URL;
    }
}


