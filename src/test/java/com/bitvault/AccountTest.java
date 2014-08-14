package com.bitvault;

import static com.bitvault.ClientTest.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {
	private static Account account = null;
	
	@Before
	public void setUp() throws IOException {
		account = client.application().wallets().get(0).accounts().get(0);
	}
	
	private static final String payAddress = "n3VispXfNCS7rgLpmXcYnUqT7WQKyavPXG";
	
	@Test
	public void testCreateUnsignedPayment() {
		List<Recipient> recipients = 
				Arrays.asList(new Recipient[] {Recipient.recipientWithAddress(payAddress, 1000)});
		Payment payment = account.createUnsignedPayment(recipients);
		
		Assert.assertNotNull(payment);
	}
}
