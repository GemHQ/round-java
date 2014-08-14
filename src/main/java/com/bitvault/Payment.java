package com.bitvault;

import java.util.ArrayList;
import java.util.List;

import com.bitvault.multiwallet.MultiWallet;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.params.TestNet3Params;
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
		
		
		return null;
	}
	
	public Transaction getNativeTransaction() {
		Transaction transaction = new Transaction(TestNet3Params.get());
		for (TransactionOutput input : this.getInputs()) {
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
	
	public List<TransactionOutput> getInputs() {
		JsonArray inputsJson = this.resource.get("inputs").getAsJsonArray();
		ArrayList<TransactionOutput> inputs = new ArrayList<TransactionOutput>();
		for (JsonElement element : inputsJson) {
			JsonObject inputJson = element.getAsJsonObject();
			JsonObject outputJson = inputJson.get("output").getAsJsonObject();
			inputs.add(outputFromJson(outputJson));
		}
		
		return inputs;
	}
	
	public List<TransactionOutput> getOutputs() {
		JsonArray outputsJson = this.resource.get("outputs").getAsJsonArray();
		ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
		for (JsonElement element : outputsJson) {
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
