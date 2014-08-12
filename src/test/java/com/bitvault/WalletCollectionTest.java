package com.bitvault;

import java.io.IOException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WalletCollectionTest {
	private static final String apiToken = "PfyzNzRlv6AgV3P32a87MTXSMZhuCAG7dVIZBbZS0lc";
	private static final String appKey = "h51G8o1ZNTOwkUh0waBryQ";

	private static Client client = new Client(appKey, apiToken);
	private static WalletCollection wallets = null;
	
	@Before
	public void setUp() throws IOException {
		wallets = client.application().wallets();
	}
	
	@Test
	public void testCreateWallet() {
		int count = wallets.size();
		String name = UUID.randomUUID().toString();
		wallets.create(name, "passphrase");
		
		Assert.assertEquals(count + 1, wallets.size());
	}
}
