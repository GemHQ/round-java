package co.gem.round;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import co.gem.round.multiwallet.MultiWallet;

import co.gem.round.Client.UnexpectedStatusCodeException;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Address;

public class Payment extends Resource{

	public static final String RESOURCE_NAME = "payment";
	
	public Payment(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}

	public Payment(String url, Client client)
            throws UnexpectedStatusCodeException, IOException {
		super(url, client, RESOURCE_NAME);
	}
	
	public Payment sign(MultiWallet wallet)
            throws IOException, UnexpectedStatusCodeException {
		Transaction transaction = this.getNativeTransaction();
		JsonArray signatures = this.getSignatures(wallet, transaction);
		
		JsonObject body = new JsonObject();
		body.addProperty("transaction_hash", transaction.getHashAsString());
		body.add("inputs", signatures);
		
		JsonElement response = this.client.performRequest(this.url, "unsigned_payment", "sign", body);
		
		return new Payment(response.getAsJsonObject(), this.client);
	}
	
	public JsonArray getSignatures(MultiWallet wallet, Transaction transaction) {
		JsonArray signatures = new JsonArray();
		int inputIndex = 0;
		for (JsonElement element : this.getInputsJson()) {
			JsonObject inputJson = element.getAsJsonObject().get("output").getAsJsonObject();
			String walletPath = inputJson.getAsJsonObject("metadata")
					.get("wallet_path").getAsString();
			
			Script redeemScript = wallet.redeemScriptForPath(walletPath);
			Sha256Hash sigHash = transaction.hashForSignature(inputIndex, redeemScript, org.bitcoinj.core.Transaction.SigHash.ALL, false);
			String base58Signature = wallet.base58SignatureForPath(walletPath, sigHash);
			JsonObject signatureJson = new JsonObject();
			signatureJson.addProperty("primary", base58Signature);
			signatures.add(signatureJson);
			
			inputIndex++;
		}
		return signatures;
	}
	
	public Transaction getNativeTransaction() {
		Transaction transaction = new Transaction(NetworkParameters.fromID(NetworkParameters.ID_TESTNET));
		for (TransactionInput input : this.getInputs(transaction)) {
			transaction.addInput(input);
		}
		
		this.setupOutputs(transaction);
		
		return transaction;
	}
	
	public String getHash() {
		return this.resource.get("hash").getAsString();
	}
	
	public JsonArray getInputsJson() {
		return this.resource.get("inputs").getAsJsonArray();
	}
	
	public JsonArray getOutputsJson() {
		return this.resource.get("outputs").getAsJsonArray();
	}
	
	public String getStatus() {
		return this.resource.get("status").getAsString();
	}
	
	public List<TransactionInput> getInputs(Transaction parent) {
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		for (JsonElement element : this.getInputsJson()) {
			JsonObject inputJson = element.getAsJsonObject();
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
			Script outputScript = ScriptBuilder.createOutputScript(address);
			long outputIndex = outputJson.get("index").getAsLong();
			Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
			NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
			TransactionInput input = new TransactionInput(networkParameters, parent, outputScript.getProgram(),
					new TransactionOutPoint(networkParameters, outputIndex, txHash), value);
			inputs.add(input);
		}
		return inputs;
	}
	
	public void setupOutputs(Transaction parent) {
		for (JsonElement element : this.getOutputsJson()) {
			JsonObject outputJson = element.getAsJsonObject();
			Coin value = Coin.valueOf(outputJson.get("value").getAsLong());
			Script script = this.parseScript(outputJson.get("script").getAsJsonObject()
					.get("string").getAsString());
			parent.addOutput(value, script);
		}
	}
	
	public Script parseScript(String scriptString) {
		StringTokenizer tokenizer = new StringTokenizer(scriptString);
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

			builder.data(BaseEncoding.base16().lowerCase().decode(token));
		}
		return builder.build();
	}
}
