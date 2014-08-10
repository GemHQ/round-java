package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

	private static final String appUrl = "http://bitvault-api.dev:8999/apps/h51G8o1ZNTOwkUh0waBryQ";
	private static final String apiToken = "PfyzNzRlv6AgV3P32a87MTXSMZhuCAG7dVIZBbZS0lc";
	private static final String appKey = "h51G8o1ZNTOwkUh0waBryQ";

	private static Client client = new Client(appKey, apiToken);

	@Test
	public void constructorTest() {
		Assert.assertEquals(appUrl, client.getAppUrl());
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
		Application app = client.getApplication();
		Assert.assertNotNull(app);
		Assert.assertEquals(appKey, app.getKey());
	}

}