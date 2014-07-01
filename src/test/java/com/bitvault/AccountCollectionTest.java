package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class AccountCollectionTest {
	
	private static final String appUrl = "http://bitvault.pandastrike.com/apps/1GSHP19iLGjnbpJpv03FVA";
	private static final String apiToken = "qVFS--Qv47juqT0soSuig1KaRTqVh78BRR4Ws8bvl2g";
	
	private static Client client = new Client(appUrl, apiToken);
	
	@Test public void createAccountTest() throws IOException {
		Wallet wallet = client.getApplication().getWallets().wallets.get(0);
		AccountCollection collection = wallet.accounts();
		
		int accountsCount = collection.accounts.size();
		collection.create("spending");
		Assert.assertEquals(collection.accounts.size(), accountsCount + 1);
	}
}
