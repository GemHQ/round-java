package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

    private static final String baseUrl = "http://bitvault-api.dev";
	private static final String appKey = "QYKoUjUoMg5nV9KXnr2rRw";
	private static final String apiToken = "Tx6DNDzAEZ1Fcwu0TjfkWqr3ntT9j8lP3cae9x_dnmA";
    private static final String email = "julian@gem.co";
    private static final String userToken = "QrkwA8nwpNwUqrEs795gS2l_dmmbA9Ae_3PnGM4D2l8";
    private static final String deviceId = "525f4b9c-2554-4862-8444-e76167dfcdd7";

	public static Client client;
    static {
        try {
            client = new Client(baseUrl, appKey, apiToken);
        } catch (Client.UnexpectedStatusCodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.setEmail(email);
        client.addDeviceAuthorization(deviceId);
        client.addAppAuthorization(userToken, deviceId);
    }

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
	public void testGetWallet() throws Client.UnexpectedStatusCodeException, IOException {
		Wallet wallet = client.wallet();
		Assert.assertNotNull(wallet);
	}

}