package co.gem.round.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by julian on 12/18/14.
 */
public class Http {
    public static Map<String, String> extractParamsFromHeader(String header) {
        Map<String, String> params = new HashMap<String, String>();
        Pattern pattern = Pattern.compile("(\\S*)\\=\"(\\S*)\"");
        Matcher matcher = pattern.matcher(header);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            params.put(key, value);
        }
        return params;
    }
}
