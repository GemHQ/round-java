package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

public class Account extends Base {

  private Wallet wallet;

  public Account(Resource resource, Round round) {
    super(resource, round);
  }

  public TransactionCollection transactions()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource transactionsResource = resource.subresource("transactions");
    TransactionCollection transactions = new TransactionCollection(transactionsResource, this.round);
    transactions.fetch();

    return transactions;
  }

  public AddressCollection addresses()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource addressesResource = resource.subresource("addresses");
    AddressCollection addresses = new AddressCollection(addressesResource, this.round);
    addresses.fetch();

    return addresses;
  }

  public String name() {
    return getString("name");
  }

  public long balance() {
    return getLong("balance");
  }

  public long pendingBalance() {
    return getLong("pending_balance");
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  public Payment payToEmail(String passphrase, String email, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    Recipient recipient = Recipient.recipientWithEmail(email, amount);
    return this.pay(passphrase, recipient);
  }

  public Payment payToAddress(String passphrase, String address, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.pay(passphrase, Recipient.recipientWithAddress(address, amount));
  }

  public Payment pay(String passphrase, Recipient recipient)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
    return this.pay(passphrase, recipients);
  }

  public Payment pay(String passphrase, List<Recipient> recipients)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    final Payment payment = this.createUnsignedPayment(recipients);
    this.wallet.unlock(passphrase, new UnlockedWalletCallback() {
      @Override
      public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
        payment.sign(wallet);
      }
    });
    return payment;
  }

  public SubscriptionCollection subscriptions()
      throws IOException, Client.UnexpectedStatusCodeException {
    SubscriptionCollection subscriptions = new SubscriptionCollection(resource.subresource("subscriptions"), round);
    subscriptions.fetch();
    return subscriptions;
  }

  public Payment createUnsignedPayment(List<Recipient> recipients)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonArray recipientsJson = new JsonArray();
    for (Recipient recipient : recipients) {
      JsonObject payeeJson = new JsonObject();
      if (recipient.email != null) {
        payeeJson.addProperty("email", recipient.email);
      } else if (recipient.address != null) {
        payeeJson.addProperty("address", recipient.address);
      }

      JsonObject recipientJson = new JsonObject();
      recipientJson.add("payee", payeeJson);
      recipientJson.addProperty("amount", recipient.amount);

      recipientsJson.add(recipientJson);
    }

    JsonObject body = new JsonObject();
    body.add("outputs", recipientsJson);

    Resource paymentResource = resource.subresource("payments").action("create", body);

    return new Payment(paymentResource, this.round);
  }

}
