package com.bitvault;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountCollectionTest {
	
	private static final String apiToken = "PfyzNzRlv6AgV3P32a87MTXSMZhuCAG7dVIZBbZS0lc";
	private static final String appKey = "h51G8o1ZNTOwkUh0waBryQ";

	private static Client client = new Client(appKey, apiToken);
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
