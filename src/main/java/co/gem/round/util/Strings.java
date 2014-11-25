package co.gem.round.util;

import com.google.common.base.Joiner;
import java.util.List;

/**
 * Created by julian on 11/25/14.
 */
public class Strings {
  public static String join(String delimiter, List<String> list) {
    Joiner joiner = Joiner.on(delimiter).skipNulls();
    return joiner.join(list);
  }

  public static String join(String delimiter, String... list) {
    Joiner joiner = Joiner.on(delimiter).skipNulls();
    return joiner.join(list);
  }
}
