package co.gem.round.coinop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julian on 11/24/14.
 */
public class OutputWrapper {
  private TransactionOutput output;

  private OutputWrapper(TransactionOutput output) {
    this.output = output;
  }

  public static OutputWrapper parseOutput(JsonObject outputJson, Transaction parent) {
    Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
    Script script = ScriptWrapper.parseScript(outputJson.get("script").getAsJsonObject()
        .get("string").getAsString());
    TransactionOutput output = new TransactionOutput(parent.getParams(), parent, value, script.getProgram());
    return new OutputWrapper(output);
  }

  public static List<OutputWrapper> parseOutputs(JsonArray outputsJson, Transaction parent) {
    List<OutputWrapper> outputs = new ArrayList<OutputWrapper>();
    for (JsonElement element : outputsJson) {
      JsonObject outputJson = element.getAsJsonObject();
      OutputWrapper output = parseOutput(outputJson, parent);
      outputs.add(output);
      parent.addOutput(output.output());
    }

    return outputs;
  }

  public TransactionOutput output() {
    return output;
  }
}
