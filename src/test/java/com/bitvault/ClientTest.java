package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

	private static final String appUrl = "http://bitvault.pandastrike.com/apps/68qdgkyBFqXu_ixydaru0Q";
	private static final String apiToken = "GSku1WNZXF59TA1eSE21qoDpu3lOJghoK1eafOhixtA";

	private static Client client = new Client(appUrl, apiToken);

	@Test
	public void constructorTest() {
		Assert.assertEquals(appUrl, client.getAppUrl());
		Assert.assertEquals(apiToken, client.getApiToken());
	}

	@Test
	public void testGetHttpClient() throws IOException {
		Assert.assertNotNull(client.getHttpClient());
	}
	
	@Test
	public void testDiscoveryParsing() {
		Assert.assertNotNull(client.getMappings());
		Assert.assertNotNull(client.getResources());
		Assert.assertNotNull(client.getSchemas());
	}
	
	@Test 
	public void testAcceptHeaderForResourceAndAction() {
		String accept = client.acceptHeaderForResource("application", "get");
		Assert.assertEquals("application/vnd.bitvault.application+json;version=1.0", accept);
	}
	
	@Test
	public void testContentTypeHeaderForResourceAndAction() {
		String contentType = client.contentTypeHeaderForResource("applications", "create");
		Assert.assertEquals("application/vnd.bitvault.application+json;version=1.0", contentType);
	}
	
	@Test
	public void testNoContentTypeHeader() {
		String contentType = client.contentTypeHeaderForResource("applications", "list");
		Assert.assertNull(contentType);
	}

}