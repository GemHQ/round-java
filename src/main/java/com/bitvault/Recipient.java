package com.bitvault;

public class Recipient {
	public String address;
	public int amount;
	
	public static Recipient recipientWithAddress(String address, int amount) {
		Recipient recipient = new Recipient();
		recipient.address = address;
		recipient.amount = amount;
		return recipient;
	}
}
