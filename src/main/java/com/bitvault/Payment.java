package com.bitvault;

import java.util.ArrayList;
import java.util.List;

import org.spongycastle.util.encoders.Hex;

import com.bitvault.multiwallet.MultiWallet;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutPoint;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Payment extends Resource{

	public static final String RESOURCE_NAME = "payment";
	
	public Payment(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}

	public Payment(String url, Client client) {
		super(url, client, RESOURCE_NAME);
	}
	
	public Payment sign(MultiWallet wallet) {
		Transaction transaction = this.getNativeTransaction();
		
		JsonArray signatures = new JsonArray();
		int inputIndex = 0;
		for (JsonElement element : this.getInputsJson()) {
			JsonObject inputJson = element.getAsJsonObject();
			String walletPath = inputJson.getAsJsonObject("metadata")
					.get("wallet_path").getAsString();
			
			Script redeemScript = wallet.redeemScriptForPath(walletPath);
			Sha256Hash sigHash = transaction.hashForSignature(inputIndex, redeemScript, SigHash.ALL, false);
			String hexSignature = wallet.hexSignatureForPath(walletPath, sigHash);
			JsonObject signatureJson = new JsonObject();
			signatureJson.addProperty("primary", hexSignature);
			signatures.add(signatureJson);
			
			inputIndex++;
		}
		
		JsonObject body = new JsonObject();
		body.addProperty("transaction_hash", transaction.getHashAsString());
		body.add("inputs", signatures);
		
		return null;
	}
	

	
	public Transaction getNativeTransaction() {
		Transaction transaction = new Transaction(TestNet3Params.get());
		for (TransactionInput input : this.getInputs(transaction)) {
			transaction.addInput(input);
		}
		
		for (TransactionOutput output : this.getOutputs()) {
			transaction.addOutput(output);
		}
		
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
			TransactionInput input = new TransactionInput(TestNet3Params.get(), parent, outputScript.getProgram(), 
					new TransactionOutPoint(TestNet3Params.get(), outputIndex, txHash));
			inputs.add(input);
		}
		return inputs;
	}
	
	public List<TransactionOutput> getOutputs() {
		ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
		for (JsonElement element : this.getOutputsJson()) {
			JsonObject outputJson = element.getAsJsonObject();
			outputs.add(outputFromJson(outputJson));
		}
		
		return outputs;
	}
	
	private TransactionOutput outputFromJson(JsonObject json) {
		Coin value = Coin.valueOf(json.get("value").getAsLong());
		byte[] scriptBytes = json.get("script").getAsJsonObject().get("string").getAsString().getBytes();
		return new TransactionOutput(TestNet3Params.get(), null, value, scriptBytes);
	}
}
