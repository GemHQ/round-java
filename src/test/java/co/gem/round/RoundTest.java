package co.gem.round;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class RoundTest {

  private static final String baseUrl = "http://bitvault-api.dev";
  private static final String appKey = "Pu8mHhWtzbK2OT3jFvI_Cw";
  private static final String apiToken = "QPvLMWq-jD4WdWzzoCwyD0RCuX2sGe7gF2BDbkeYPUg";
  private static final String email = "test@gem.co";
  private static final String userToken = "krdJA1vA11bK-7PmyDoH8n6ayfj-7e4jSgndYUUAXMo";
  private static final String deviceId = "9f0db767-82a9-4563-89b4-20bff123f473";

  public static Round round;

//  static {
//    try {
//      round = new Round(baseUrl, appKey, apiToken);
//    } catch (Round.UnexpectedStatusCodeException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    round.setEmail(email);
//    round.addDeviceAuthorization(deviceId);
//    round.addAppAuthorization(userToken, deviceId);
//  }
//
//  @Test
//  public void constructorTest() {
//    Assert.assertEquals(apiToken, round.getApiToken());
//  }
//
//  @Test
//  public void testDiscoveryParsing() {
//    Assert.assertNotNull(round.getMappings());
//    Assert.assertNotNull(round.getResources());
//    Assert.assertNotNull(round.getSchemas());
//  }
//
//  @Test
//  public void testGetWallet() throws Round.UnexpectedStatusCodeException, IOException {
//    Wallet wallet = round.wallet();
//    Assert.assertNotNull(wallet);
//  }

}