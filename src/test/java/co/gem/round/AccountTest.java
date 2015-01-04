package co.gem.round;

import co.gem.round.patchboard.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static co.gem.round.RoundTest.round;

public class AccountTest {
  private static Account account = null;

  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
//    account = round.wallet().accounts().getAttribute(0);
  }

  private static final String payAddress = "n3VispXfNCS7rgLpmXcYnUqT7WQKyavPXG";

  @Test
  public void testCreateUnsignedPayment() throws IOException, Client.UnexpectedStatusCodeException {
    List<Recipient> recipients = new ArrayList<Recipient>();
    recipients.add(Recipient.recipientWithAddress(payAddress, 1000));
    Payment payment = account.createUnsignedPayment(recipients);

    Assert.assertNotNull(payment);
  }

  @Test
  public void testCreateAddress() throws IOException, Client.UnexpectedStatusCodeException {
    Address address = account.addresses().create();
    Assert.assertNotNull(address);
  }
}
