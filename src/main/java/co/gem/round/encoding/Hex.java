package co.gem.round.encoding;

import com.google.common.io.BaseEncoding;

/**
 * Created by julian on 11/21/14.
 */
public class Hex {
    public static String encode(byte[] data) {
        return BaseEncoding.base16().lowerCase().encode(data);
    }

    public static byte[] decode(String data) {
        return BaseEncoding.base16().lowerCase().decode(data);
    }
}
