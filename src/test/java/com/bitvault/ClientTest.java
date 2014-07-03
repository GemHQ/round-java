package com.bitvault;

import java.io.IOException;

import org.junit.*;

public class ClientTest {

  private static final String appUrl = "http://bitvault.pandastrike.com/apps/68qdgkyBFqXu_ixydaru0Q";
  private static final String apiToken = "GSku1WNZXF59TA1eSE21qoDpu3lOJghoK1eafOhixtA";

  private static Client client = new Client(appUrl, apiToken);

  @Test public void constructorTest() {
    Assert.assertEquals(appUrl, client.getAppUrl());
    Assert.assertEquals(apiToken, client.getApiToken());
  }

  @Test public void testGetApplication() throws IOException {
	  Assert.assertNotNull(client.getApplication());
	
  }
  
}