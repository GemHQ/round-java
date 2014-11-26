package co.gem.round;

import co.gem.round.patchboard.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static co.gem.round.RoundTest.round;

public class AccountCollectionTest {

  private static AccountCollection accounts = null;

  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
//    accounts = round.wallet().accounts();
  }

  @Test
  public void testCreateAccount() throws IOException, Client.UnexpectedStatusCodeException {
    int count = accounts.size();

    String name = "Account-" + System.currentTimeMillis();
    accounts.create(name);

    Assert.assertEquals(count + 1, accounts.size());
  }
}
