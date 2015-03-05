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

/**
 * Account class is the primary class where most of the interactions for the wallet will occur.  From an account, you
 * have the ability to send transactions, get balance and pending balance of the account, generate addresses.
 *
 * @author Julian Del Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Account extends Base {

  private Wallet wallet;

  public Account(Resource resource, Round round) {
    super(resource, round);
  }

  /**
   * Getter for transactions on an account
   * @return TransactionCollection
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.TransactionCollection
   */
  public TransactionCollection transactions()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource transactionsResource = resource.subresource("transactions");
    TransactionCollection transactions = new TransactionCollection(transactionsResource, this.round);
    transactions.fetch();

    return transactions;
  }

  /**
   * Getter for addresses within an account
   * @return
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.AddressCollection
   */
  public AddressCollection addresses()
      throws IOException, Client.UnexpectedStatusCodeException {
    Resource addressesResource = resource.subresource("addresses");
    AddressCollection addresses = new AddressCollection(addressesResource, this.round);
    addresses.fetch();

    return addresses;
  }

  /**
   * Getter for the name of the account
   * @return String name
   */
  public String name() {
    return getString("name");
  }

  /**
   * Getter for the balance of the account.  This is 1 or more confirmations
   * @return Long balance
   */
  public long balance() {
    return getLong("balance");
  }

  /**
   * Getter for the pending balance on an account. This is 0 confirmations
   * @return Long pending balance
   */
  public long pendingBalance() {
    return getLong("pending_balance");
  }

  public void setWallet(Wallet wallet) {
    this.wallet = wallet;
  }

  @Deprecated
  private Payment payToEmail(String passphrase, String email, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    Recipient recipient = Recipient.recipientWithEmail(email, amount);
    return this.pay(passphrase, recipient);
  }

  /**
   * Make a payment to a specific bitcoin address.
   * @param passphrase String passphrase to the wallet
   * @param address String valid bitcoin address based on the network
   * @param amount Long amount in satoshis
   * @return Payment signed payment
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Payment
   */
  public Payment payToAddress(String passphrase, String address, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.pay(passphrase, Recipient.recipientWithAddress(address, amount));
  }

  /**
   * Make a payment to a Recipient object.
   * @param passphrase String
   * @param recipient
   * @return Payment signed broadcasted payment object (transaction)
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Recipient
   * @see co.gem.round.Payment
   */
  public Payment pay(String passphrase, Recipient recipient)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
    return this.pay(passphrase, recipients);
  }

  /**
   * Make payment to a list of recipients.  This is a transaction with multiple To: addresses and amounts
   * @param passphrase String
   * @param recipients List of recipients
   * @return Signed broadcasted payment
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Recipient
   * @see co.gem.round.Payment
   */
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

  /**
   * Requests a payment object to be created by the Gem API.  This will lock UTXOs while you inspect the unsigned
   * payment for things like the suggested fee.
   * @param recipients List of recipients
   * @return Payment - unsigned and not broadcasted
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.Payment
   */
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
