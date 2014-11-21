package co.gem.round;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Julian on 8/27/14.
 */
public class Util {
    public static String join(List<String> list, String delimiter) {
        String result = null;
        Iterator<String> iterator = list.iterator();
        while(iterator.hasNext()) {
            if (result == null)
                result = iterator.next();
            else
                result = result + iterator.next();
            if (iterator.hasNext())
                result = result + delimiter;
        }
        return result;
    }
}
