package com.bitvault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.spongycastle.util.encoders.Hex;

import com.bitvault.Client.UnexpectedStatusCodeException;
import com.bitvault.multiwallet.MultiWallet;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Coin;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutPoint;
import com.google.bitcoin.crypto.DeterministicKey.NetworkMode;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.script.ScriptOpCodes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Payment extends Resource{

	public static final String RESOURCE_NAME = "payment";
	private NetworkParameters networkParams;
	
	public Payment(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
		this.networkParams = this.client.networkMode == NetworkMode.TESTNET ?
				TestNet3Params.get() : MainNetParams.get();
	}

	public Payment(String url, Client client) {
		super(url, client, RESOURCE_NAME);
		this.networkParams = this.client.networkMode == NetworkMode.TESTNET ?
				TestNet3Params.get() : MainNetParams.get();
	}
	
	public Payment sign(MultiWallet wallet) {
		Transaction transaction = this.getNativeTransaction();
		JsonArray signatures = this.getSignatures(wallet, transaction);
		
		JsonObject body = new JsonObject();
		body.addProperty("transaction_hash", transaction.getHashAsString());
		body.add("inputs", signatures);
		
		JsonElement response = null;
		try {
			response = this.client.performRequest(this.url, "unsigned_payment", "sign", body);
		} catch (UnexpectedStatusCodeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
			Sha256Hash sigHash = transaction.hashForSignature(inputIndex, redeemScript, SigHash.ALL, false);
			String base58Signature = wallet.base58SignatureForPath(walletPath, sigHash);
			JsonObject signatureJson = new JsonObject();
			signatureJson.addProperty("primary", base58Signature);
			signatures.add(signatureJson);
			
			inputIndex++;
		}
		return signatures;
	}
	
	public Transaction getNativeTransaction() {
		Transaction transaction = new Transaction(this.networkParams);
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
			TransactionInput input = new TransactionInput(this.networkParams, parent, outputScript.getProgram(), 
					new TransactionOutPoint(this.networkParams, outputIndex, txHash), value);
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

			builder.data(Hex.decode(token));
		}
		return builder.build();
	}
}
