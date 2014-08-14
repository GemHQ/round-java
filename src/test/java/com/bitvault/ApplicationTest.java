package com.bitvault;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import static com.bitvault.ClientTest.client;

public class ApplicationTest {
	
	@Test public void testGetWallets() throws IOException {
		WalletCollection wallets = client.application().wallets();
		
		Assert.assertNotNull(wallets.url);
	}
}