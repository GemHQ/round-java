package com.bitvault;

import java.io.IOException;
import java.util.Random;

import org.junit.*;

public class AccountCollectionTest {
	
	private static final String appUrl = "http://bitvault.pandastrike.com/apps/68qdgkyBFqXu_ixydaru0Q";
	private static final String apiToken = "GSku1WNZXF59TA1eSE21qoDpu3lOJghoK1eafOhixtA";
	
	private static Client client = new Client(appUrl, apiToken);
	
	@Test public void createAccountTest() throws IOException {
		
		Wallet wallet = (Wallet)client.getApplication().getWallets().get(0);
		
		AccountCollection collection = wallet.accounts();
		
		int accountsCount = collection.size();
		int random = new Random().nextInt();
		collection.create("spending" + random);
		Assert.assertEquals(collection.size(), accountsCount + 1);
	}
}
