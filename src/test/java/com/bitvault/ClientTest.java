package com.bitvault;

import org.junit.*;

public class ClientTest {

  private static final String appUrl = "http://bitvault.pandastrike.com/apps/1GSHP19iLGjnbpJpv03FVA";
  private static final String apiToken = "VFS--Qv47juqT0soSuig1KaRTqVh78BRR4Ws8bvl2g";

  private static Client client = new Client(appUrl, apiToken);

  @Test public void constructorTest() {
    Assert.assertEquals(appUrl, client.getAppUrl());
    Assert.assertEquals(apiToken, client.getApiToken());
  }

  
}