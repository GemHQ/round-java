package co.gem.round.coinop;

import com.google.gson.JsonObject;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

/**
 * Created by julian on 11/24/14.
 */
public class OutputWrapper {
  private TransactionOutput nativeOutput;

  private OutputWrapper(TransactionOutput nativeOutput) {
    this.nativeOutput = nativeOutput;
  }

  public static OutputWrapper parseOutput(JsonObject outputJson, Transaction parent) {
    Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
    Script script = ScriptWrapper.parseScript(outputJson.get("script").getAsJsonObject()
        .get("string").getAsString());
    TransactionOutput output = new TransactionOutput(parent.getParams(), parent, value, script.getProgram());
    return new OutputWrapper(output);
  }

  public TransactionOutput output() {
    return nativeOutput;
  }
}
