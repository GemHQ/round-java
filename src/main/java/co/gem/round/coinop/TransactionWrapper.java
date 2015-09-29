package co.gem.round.coinop;

import com.google.gson.JsonObject;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;

import java.util.List;

/**
 * Created by julian on 11/24/14.
 */
public class TransactionWrapper {
    private Transaction transaction;
    private List<InputWrapper> inputs;
    private List<OutputWrapper> outputs;

    private TransactionWrapper(Transaction transaction, List<InputWrapper> inputs, List<OutputWrapper> outputs) {
        this.transaction = transaction;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static TransactionWrapper parseTransaction(JsonObject transactionJson,
                                                      NetworkParameters networkParameters) {
        Transaction transaction = new Transaction(networkParameters);
        List<InputWrapper> inputs =
                InputWrapper.parseInputs(transactionJson.get("inputs").getAsJsonArray(), transaction);

        List<OutputWrapper> outputs =
                OutputWrapper.parseOutputs(transactionJson.get("outputs").getAsJsonArray(), transaction);

        return new TransactionWrapper(transaction, inputs, outputs);
    }

    public String getHashAsString() {
        return this.transaction.getHashAsString();
    }

    public List<InputWrapper> inputs() {
        return inputs;
    }

    public Transaction transaction() {
        return transaction;
    }

}
