package com.bitvault;

import java.io.IOException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.bitvault.ClientTest.client;

public class WalletCollectionTest {
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
