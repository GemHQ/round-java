package com.bitvault;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class Transaction extends Resource {
	
	public static final String RESOURCE_NAME = "transaction";

	public Transaction(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
	}

	public Transaction(String url, Client client)
            throws Client.UnexpectedStatusCodeException, IOException {
		super(url, client, RESOURCE_NAME);
	}
	
	public String getType() {
		return resource.get("type").getAsString();
	}

    public JsonObject getBitcoinTransaction() {
        return resource.getAsJsonObject("data");
    }

	public String getTransactionHash() {
		return getBitcoinTransaction().get("hash").getAsString();
	}

    public long getValue() {
        return getBitcoinTransaction().get("value").getAsLong();
    }

    public String getStatus() {
        return getBitcoinTransaction().get("status").getAsString();
    }

    public String getActor() {
        JsonObject bitcoinTransaction = getBitcoinTransaction();

        if (getType().equals("incoming")) {
            JsonObject input = bitcoinTransaction.getAsJsonArray("inputs")
                    .get(0).getAsJsonObject()
                    .get("output").getAsJsonObject();
            return input.get("address").getAsString();
        }

        if (getType().equals("outgoing")) {
            String address = null;
            for (JsonElement element : bitcoinTransaction.getAsJsonArray("outputs")) {
                JsonObject output = element.getAsJsonObject();
                JsonObject metadata = output.getAsJsonObject("metadata");
                address = output.get("address").getAsString();
                if (metadata.has("pay_to_email"))
                    return metadata.get("pay_to_email").getAsString();
            }

            return address;
        }

        return null;
    }
}
