package co.gem.round.coinop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julian on 11/24/14.
 */
public class TransactionWrapper {
  private Transaction nativeTransaction;
  private List<InputWrapper> inputs;
  private List<OutputWrapper> outputs;

  private TransactionWrapper(Transaction nativeTransaction, List<InputWrapper> inputs, List<OutputWrapper> outputs) {
    this.nativeTransaction = nativeTransaction;
    this.inputs = inputs;
    this.outputs = outputs;
  }

  public static TransactionWrapper parseTransaction(JsonObject transactionJson, NetworkParameters networkParameters) {
    Transaction transaction = new Transaction(networkParameters);
    JsonArray inputsJson = transactionJson.get("inputs").getAsJsonArray();
    List<InputWrapper> inputs = new ArrayList<InputWrapper>();
    for (JsonElement element : inputsJson) {
      JsonObject inputJson = element.getAsJsonObject();
      InputWrapper input = InputWrapper.parseInput(inputJson, transaction);
      inputs.add(input);
      transaction.addInput(input.input());
    }

    List<OutputWrapper> outputs = new ArrayList<OutputWrapper>();
    JsonArray ouputsJson = transactionJson.get("outputs").getAsJsonArray();
    for (JsonElement element : ouputsJson) {
      JsonObject outputJson = element.getAsJsonObject();
      OutputWrapper output = OutputWrapper.parseOutput(outputJson, transaction);
      outputs.add(output);
      transaction.addOutput(output.output());
    }

    return new TransactionWrapper(transaction, inputs, outputs);
  }

  public String getHashAsString() {
    return this.nativeTransaction.getHashAsString();
  }

  public List<InputWrapper> inputs() {
    return inputs;
  }

  public Transaction transaction() {
    return nativeTransaction;
  }

}
