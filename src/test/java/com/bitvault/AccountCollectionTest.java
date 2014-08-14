package com.bitvault;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.bitvault.ClientTest.client;

public class AccountCollectionTest {
	
	private static AccountCollection accounts = null;
	
	@Before
	public void setUp() throws IOException {
		accounts = client.application().wallets().get(0).accounts();
	}
	
	@Test 
	public void testCreateAccount() throws IOException {
		int count = accounts.size();
		
		String name = "Account-" + System.currentTimeMillis();
		accounts.create(name);
		
		Assert.assertEquals(count + 1, accounts.size());
	}
}
