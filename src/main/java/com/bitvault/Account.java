package com.bitvault;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.bitvault.Client.UnexpectedStatusCodeException;
import com.bitvault.multiwallet.MultiWallet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Account extends Resource{
	
	public static final String RESOURCE_NAME = "account";
	
	private Wallet wallet;
	private AddressCollection addresses;
	private TransactionCollection transactions;
	
	public Account(String url, Client client){
		super(url, client, RESOURCE_NAME);
	}
	
	public Account(JsonObject resource, Client client){
		super(resource, client, RESOURCE_NAME);
	}

	public AddressCollection addresses() {
		if (this.addresses == null) {
			this.addresses = new AddressCollection(this.getAccountsUrl(), this.client);
		}
		
		return this.addresses;
	}
	
	public TransactionCollection transactions() {
		if (this.transactions == null) {
			this.transactions = new TransactionCollection(this.getTransactionsUrl(), this.client);
		}
		
		return this.transactions;
	}
	
	public String getAccountsUrl() {
		return this.resource.get("addresses")
				.getAsJsonObject().get("url").getAsString();
	}
	
	public String getTransactionsUrl() {
		return this.resource.get("transactions")
				.getAsJsonObject().get("url").getAsString();
	}
	
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}
	
	public Payment pay(String passphrase, String address, long amount) {
		return this.pay(passphrase, Recipient.recipientWithAddress(address, amount));
	}
	
	public Payment pay(String passphrase, Recipient recipient) {
		List<Recipient> recipients = Arrays.asList(new Recipient[] {recipient});
		return this.pay(passphrase, recipients);
	}
	
	public Payment pay(String passphrase, List<Recipient> recipients) {
		final Payment payment = this.createUnsignedPayment(recipients);
		this.wallet.unlock(passphrase, new UnlockedWalletCallback() {
			@Override
			public void execute(MultiWallet wallet) {
				payment.sign(wallet);
			}
		});
		return payment;
	}
	
	public Payment createUnsignedPayment(List<Recipient> recipients) {
		JsonArray recipientsJson = new JsonArray();
		for (Recipient recipient : recipients) {
			JsonObject payeeJson = new JsonObject();
			payeeJson.addProperty("address", recipient.address);
			
			JsonObject recipientJson = new JsonObject();
			recipientJson.add("payee", payeeJson);
			recipientJson.addProperty("amount", recipient.amount);
			
			recipientsJson.add(recipientJson);
		}
		
		JsonObject body = new JsonObject();
		body.add("outputs", recipientsJson);
		
		String url = this.resource.getAsJsonObject("payments").get("url").getAsString();
		JsonObject resource = null;
		try {
			resource = this.client.performRequest(url, "payments", "create", body).getAsJsonObject();
		} catch(IOException e) {
			return null;
		} catch(UnexpectedStatusCodeException e) {
			return null;
		}
		
		return new Payment(resource, this.client);
	}
	
}
