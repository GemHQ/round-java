package co.gem.round.coinop;

import co.gem.round.encoding.Hex;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;

import java.util.StringTokenizer;

/**
 * Created by julian on 11/24/14.
 */
public class ScriptWrapper {
  public static Script parseScript(String script) {
    StringTokenizer tokenizer = new StringTokenizer(script);
    ScriptBuilder builder = new ScriptBuilder();
    while(tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      token = token.replace("OP_", "");
      Integer opCode = ScriptOpCodes.getOpCode(token);
      if(opCode != ScriptOpCodes.OP_INVALIDOPCODE) {
        builder.op(opCode);
        continue;
      }
      Integer smallNum = null;
      try {
        smallNum = Integer.parseInt(token);
        if (smallNum <= 16) {
          builder.smallNum(smallNum);
          continue;
        }
      } catch(NumberFormatException e) {

      }

      builder.data(Hex.decode(token));
    }
    return builder.build();
  }
}
