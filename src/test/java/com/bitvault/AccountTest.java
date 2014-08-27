package com.bitvault;

import static com.bitvault.ClientTest.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {
	private static Account account = null;
	
	@Before
	public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
        account = client.wallet().accounts().get(0);
	}
	
	private static final String payAddress = "n3VispXfNCS7rgLpmXcYnUqT7WQKyavPXG";
	
	@Test
	public void testCreateUnsignedPayment() {
		List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(Recipient.recipientWithAddress(payAddress, 1000));
		Payment payment = account.createUnsignedPayment(recipients);
		
		Assert.assertNotNull(payment);
	}

    @Test
    public void testCreateAddress() {
        Address address = account.createAddress();
        Assert.assertNotNull(address);
    }
}
