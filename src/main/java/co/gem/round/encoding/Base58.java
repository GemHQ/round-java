package co.gem.round.encoding;

import org.bitcoinj.core.AddressFormatException;

/**
 * Created by julian on 11/24/14.
 */
public class Base58 {
  public static String encode(byte[] input) {
    return org.bitcoinj.core.Base58.encode(input);
  }

  public static byte[] decode(String input) throws AddressFormatException {
    return org.bitcoinj.core.Base58.decode(input);
  }
}
