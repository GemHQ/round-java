package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

  private static final String appUrl = "http://bitvault.pandastrike.com/apps/1GSHP19iLGjnbpJpv03FVA";
  private static final String apiToken = "jQ4LsU2_jRZwEx6T51JfGcnKpNVOIoxwXKE7WAiqcUQ";

  private static Client client = new Client(appUrl, apiToken);

  @Test public void constructorTest() {
    Assert.assertEquals(appUrl, client.getAppUrl());
    Assert.assertEquals(apiToken, client.getApiToken());
  }

  @Test public void testGetApplication() throws IOException {
	  Assert.assertNotNull(client.getApplication());
	
  }
  
}