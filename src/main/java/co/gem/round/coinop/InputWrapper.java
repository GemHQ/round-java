package co.gem.round.coinop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by julian on 11/24/14.
 */
public class InputWrapper {
    private TransactionInput input;
    private String walletPath;

    private InputWrapper(TransactionInput input, String walletPath) {
        this.input = input;
        this.walletPath = walletPath;
    }

    public static List<InputWrapper> parseInputs(JsonArray inputsJson, Transaction parent) {
        List<InputWrapper> inputs = new ArrayList<InputWrapper>();
        for (JsonElement element : inputsJson) {
            JsonObject inputJson = element.getAsJsonObject();
            InputWrapper input = parseInput(inputJson, parent);
            inputs.add(input);
            parent.addInput(input.input());
        }
        return inputs;
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

        String walletPath = outputJson.get("metadata").getAsJsonObject().get("wallet_path").getAsString();
        Script outputScript = ScriptBuilder.createOutputScript(address);
        long outputIndex = outputJson.get("index").getAsLong();
        Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
        NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
        TransactionInput input = new TransactionInput(networkParameters, parent, outputScript.getProgram(),
                new TransactionOutPoint(networkParameters, outputIndex, txHash), value);
        return new InputWrapper(input, walletPath);
    }

    public String walletPath() {
        return walletPath;
    }

    public TransactionInput input() {
        return input;
    }
}
