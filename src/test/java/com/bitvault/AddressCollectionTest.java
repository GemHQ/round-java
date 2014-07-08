package com.bitvault;

import java.io.IOException;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class AddressCollectionTest {
	
	
	private static final String appUrl = "http://bitvault.pandastrike.com/apps/68qdgkyBFqXu_ixydaru0Q";
	private static final String apiToken = "GSku1WNZXF59TA1eSE21qoDpu3lOJghoK1eafOhixtA";
	
	private static Client client = new Client(appUrl, apiToken);
	
	@Test public void createAddressesTest() throws IOException {
		/*Wallet wallet = client.getApplication().getWallets().wallets.get(0);
		AccountCollection collection = wallet.accounts();*/
		
		Wallet wallet = (Wallet)client.getApplication().getWallets().get(0);
		Account acc = (Account)wallet.accounts().get(0);
		AddressCollection collection = acc.getAddress();
		
		int addressCount = collection.size();
		//int random = new Random().nextInt();
		collection.create();
		Assert.assertEquals(collection.size(), addressCount + 1);
	}

}
