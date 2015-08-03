package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.patchboard.Client;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.spongycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;

public class PaginationTest {
  @Rule
  public ExpectedException thrown= ExpectedException.none();

  Round client;
  User user;
  Wallet wallet;
  Account account;

  Map<String, String> testCreds = ImmutableMap.of(
          "api_url", "https://api-develop.gem.co",
          "api_token", "93WsPkG5Xmzq-tGdGoih42nlgQSgxmRNzhrD5W7TnrU",
          "device_token", "ROLmRhTD1-THq26xmJRxqRUK23NEa1CSBTqkTcRgbxA",
          "email", "matt+007@gem.co",
          "password", "asdfasdf"
  );
  @Before
  public void setUp() throws Client.UnexpectedStatusCodeException, IOException {
    client = Round.client(testCreds.get("api_url"));
    user = client.authenticateDevice(
            testCreds.get("api_token"),
            testCreds.get("device_token"),
            testCreds.get("email"));

    wallet = user.wallet();

    // This is primed with 150 addresses
    account = wallet.accounts().get("default");
  }

  @Test
  public void paginationTest() throws NoSuchAlgorithmException, Client.UnexpectedStatusCodeException, InvalidKeySpecException, IOException, InterruptedException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
    AddressCollection addresses = account.addresses();

    // With more than 100 elements, returns 100
    Assert.assertEquals(100, addresses.size());

    // Next page returns the remainder (if lte 100)
    Assert.assertEquals(50, addresses.nextPage().size());
    Assert.assertEquals(100, addresses.previousPage().size());
  }

    @Test
    public void negativeOutOfBoundsTest() throws IOException, Client.UnexpectedStatusCodeException {
        thrown.expect(java.lang.IndexOutOfBoundsException.class);
        account.addresses().page(-1);
    }

    @Test
    public void tooHighOutOfBoundsTest() throws IOException, Client.UnexpectedStatusCodeException {
        thrown.expect(java.lang.IndexOutOfBoundsException.class);
        account.addresses().page(3);
    }
}
