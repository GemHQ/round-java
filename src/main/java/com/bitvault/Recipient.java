package com.bitvault;

public class Recipient {
	public String address;
	public long amount;
	
	public static Recipient recipientWithAddress(String address, long amount) {
		Recipient recipient = new Recipient();
		recipient.address = address;
		recipient.amount = amount;
		return recipient;
	}
}
