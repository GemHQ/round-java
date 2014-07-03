package com.bitvault;

import java.io.IOException;
import java.util.Random;

import org.junit.*;

public class AccountCollectionTest {
	
	private static final String appUrl = "http://bitvault.pandastrike.com/apps/1GSHP19iLGjnbpJpv03FVA";
	private static final String apiToken = "jQ4LsU2_jRZwEx6T51JfGcnKpNVOIoxwXKE7WAiqcUQ";
	
	private static Client client = new Client(appUrl, apiToken);
	
	@Test public void createAccountTest() throws IOException {
		Wallet wallet = client.getApplication().getWallets().wallets.get(0);
		AccountCollection collection = wallet.accounts();
		
		int accountsCount = collection.accounts.size();
		int random = new Random().nextInt();
		collection.create("spending" + random);
		Assert.assertEquals(collection.accounts.size(), accountsCount + 1);
	}
}
