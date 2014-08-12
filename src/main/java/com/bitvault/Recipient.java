package com.bitvault;

public class Recipient {
	public String address;
	public String email;
	public int amount;
	
	public static Recipient recipientWithEmail(String email, int amount) {
		Recipient recipient = new Recipient();
		recipient.email = email;
		recipient.amount = amount;
		return recipient;
	}
	
	public static Recipient recipientWithAddress(String address, int amount) {
		Recipient recipient = new Recipient();
		recipient.address = address;
		recipient.amount = amount;
		return recipient;
	}
}
