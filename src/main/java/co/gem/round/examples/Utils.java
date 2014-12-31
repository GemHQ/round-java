package co.gem.round.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jled on 12/31/14.
 */
public class Utils {

    public static String getUserInput(String str) throws IOException {
        BufferedReader buff = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print(str);
        System.out.flush();
        return buff.readLine();
    }
}
