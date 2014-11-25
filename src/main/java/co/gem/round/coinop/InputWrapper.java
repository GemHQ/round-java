package co.gem.round.coinop;

import com.google.gson.JsonObject;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

/**
 * Created by julian on 11/24/14.
 */
public class InputWrapper {
  private TransactionInput nativeInput;
  private String walletPath;

  private InputWrapper(TransactionInput nativeInput, String walletPath) {
    this.nativeInput = nativeInput;
    this.walletPath = walletPath;
  }

  public static InputWrapper parseInput(JsonObject inputJson, Transaction parent) {
    JsonObject outputJson = inputJson.get("output").getAsJsonObject();
    Sha256Hash txHash = new Sha256Hash(outputJson.get("transaction_hash").getAsString());
    Address address = null;
    try {
      address = new Address(null, outputJson.get("address").getAsString());
    } catch (AddressFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
    // TODO parse the wallet path
    Script outputScript = ScriptBuilder.createOutputScript(address);
    long outputIndex = outputJson.get("index").getAsLong();
    Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
    NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
    TransactionInput input = new TransactionInput(networkParameters, parent, outputScript.getProgram(),
        new TransactionOutPoint(networkParameters, outputIndex, txHash), value);
    return new InputWrapper(input, null);
  }

  public String walletPath() {
    return walletPath;
  }

  public TransactionInput input() {
    return nativeInput;
  }
}
