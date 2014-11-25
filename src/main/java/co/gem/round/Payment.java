package co.gem.round;

import co.gem.round.Client.UnexpectedStatusCodeException;
import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.TransactionWrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;


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
		TransactionWrapper transaction = TransactionWrapper.parseTransaction(this.resource, wallet.networkParameters());
		List<String> signatures = wallet.signaturesForTransaction(transaction);

		JsonArray signaturesJson = new JsonArray();
		for (String signature : signatures) {
			JsonObject signatureJson = new JsonObject();
			signatureJson.addProperty("primary", signature);
		}

		JsonObject body = new JsonObject();
		body.addProperty("transaction_hash", transaction.getHashAsString());
		body.add("inputs", signaturesJson);
		
		JsonElement response = this.client.performRequest(this.url, "unsigned_payment", "sign", body);
		
		return new Payment(response.getAsJsonObject(), this.client);
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

}
