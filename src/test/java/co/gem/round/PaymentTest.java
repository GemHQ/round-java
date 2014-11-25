package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static co.gem.round.ClientTest.client;

public class PaymentTest {

  public static Payment unsignedPayment;

  @Before
  public void setUp() throws URISyntaxException, IOException {
    URL url = this.getClass().getResource("/transaction.json");
    Assert.assertNotNull(url);

    String payload = null;
    BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      payload = sb.toString();
    } finally {
      br.close();
    }

    JsonObject resource = new JsonParser().parse(payload).getAsJsonObject();
    unsignedPayment = new Payment(resource, client);
  }

  private static final String primaryPrivSeed =
      "xprv9s21ZrQH143K2u1Ce1XfLCXFGXya1oXStg6uDns8tY4LVUH7TJuhwh9aTgidA4YmsgoEUpjY8rncY4CyUdiG5rPF1sG8o2x1JEavgWcVrmy";
  private static final String backupPubSeed =
      "xpub661MyMwAqRbcEwppdfPSA76Ean7apVUSuvYaXNMdoPLsBSZnRMj6A575qXHEDhPaiaqoNoADHXBcPxXBdfPgijQLFRr6PFb354rU5A7A6Ty";
  private static final String cosignerPubSeed =
      "xpub661MyMwAqRbcFbKuGUZ9Ju81FUff3FfyafeUqyjgDzVdZBwGfTfUETz6c6ivKJtrG4WDJV3XJV1wXY8Rc2J4v9k2bSpSLKV9rRkVsh14uZU";
  private static final MultiWallet wallet = MultiWallet.importSeeds(primaryPrivSeed, backupPubSeed, cosignerPubSeed);

  @Test
  public void testSign() throws IOException, Client.UnexpectedStatusCodeException {
    Payment signed = unsignedPayment.sign(wallet);

    Assert.assertEquals("unconfirmed", signed.getStatus());
  }

}
