package co.gem.round;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

public class TransactionCollection extends ResourceCollection<Transaction> {

	public static final String RESOURCE_NAME = "transactions";
	
	public TransactionCollection(String url, Client client)
            throws Client.UnexpectedStatusCodeException, IOException {
		super(url, client, RESOURCE_NAME);
	}

	@Override
	public void populateCollection(JsonArray array) {
		for (JsonElement element : array) {
			JsonObject resource = element.getAsJsonObject();
			Transaction transaction = new Transaction(resource, this.client);
			this.add(transaction.getKey(), transaction);
		}
	}

}
