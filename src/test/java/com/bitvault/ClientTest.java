package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

    private static final String baseUrl = "http://bitvault-api.dev";
	private static final String appKey = "Pu8mHhWtzbK2OT3jFvI_Cw";
	private static final String apiToken = "QPvLMWq-jD4WdWzzoCwyD0RCuX2sGe7gF2BDbkeYPUg";
    private static final String email = "test@gem.co";
    private static final String userToken = "krdJA1vA11bK-7PmyDoH8n6ayfj-7e4jSgndYUUAXMo";
    private static final String deviceId = "9f0db767-82a9-4563-89b4-20bff123f473";

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