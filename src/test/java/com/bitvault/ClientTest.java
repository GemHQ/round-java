package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

//	private static final String apiToken = "WbqH1DmXp9NOUZM65BoO2VUyqtwOHwLVPOXXGP81bqo";
//	private static final String appKey = "gYjLBM7BZBV9_7VJ7u58ew";
	
	private static final String appKey = "sSV74p47VHhgkRbL2JlRxw";
	private static final String apiToken = "7gMwSOAQI6vW8TZP2PdsoIhB5sy540v8DVRg69pmL9Q";

	public static final Client client = new Client(appKey, apiToken);

	@Test
	public void constructorTest() {
		Assert.assertEquals(apiToken, client.getApiToken());
	}
	
	@Test
	public void testDiscoveryParsing() {
		Assert.assertNotNull(client.getMappings());
		Assert.assertNotNull(client.getResources());
		Assert.assertNotNull(client.getSchemas());
	}
	
	@Test
	public void testGetApplication() throws IOException {
		Application app = client.application();
		Assert.assertNotNull(app);
		Assert.assertEquals(appKey, app.getKey());
	}
	
//	@Test
//	public void testGetWallet() {
//		Wallet wallet = client.wallet();
//		Assert.assertNotNull(wallet);
//		AssertEquals()
//	}

}