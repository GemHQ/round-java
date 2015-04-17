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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Account class is the primary class where most of the interactions for the wallet will occur.  From an account, you
 * have the ability to send transactions, get balance and pending balance of the account, generate addresses.
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Account extends Base {

  private Wallet wallet;

  public Account(Resource resource, Round round) {
    super(resource, round);
  }

  public TransactionCollection transactions(Transaction.Status status) throws IOException, Client.UnexpectedStatusCodeException {
    return transactions(null, status);
  }

  public TransactionCollection transactions(Transaction.Type type) throws IOException, Client.UnexpectedStatusCodeException {
    return transactions(type, null);
  }

  public TransactionCollection transactions() throws IOException, Client.UnexpectedStatusCodeException {
    return transactions(null, null);
  }

  /**
   * Getter for transactions on an account
   * @return TransactionCollection
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @see co.gem.round.TransactionCollection
   */
  public TransactionCollection transactions(Transaction.Type type, Transaction.Status status)
      throws IOException, Client.UnexpectedStatusCodeException {
    Map<String, String> query = new HashMap<>();
    if (type != null) {
      query.put("type", type.toString());
    }
    if (status != null) {
      query.put("status", status.toString());
    }
    Resource transactionsResource = resource.subresource("transactions", query);
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
  private Transaction payToEmail(String passphrase, String email, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.payToEmail(passphrase, email, 6);
  }
  @Deprecated
  public Transaction payToEmail(String passphrase, String email, long amount, int confirmations)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    Recipient recipient = Recipient.recipientWithEmail(email, amount);
    return this.pay(passphrase, recipient, confirmations);
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
   */
  public Transaction payToAddress(String passphrase, String address, long amount)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.payToAddress(passphrase, address, amount, 6);
  }

  /**
   * Make a payment to a specific bitcoin address.
   * @param passphrase String passphrase to the wallet
   * @param address String valid bitcoin address based on the network
   * @param amount Long amount in satoshis
   * @param confirmations Int number of confirmations UTXOs must have to be used in the payment
   * @return Payment signed payment
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public Transaction payToAddress(String passphrase, String address, long amount, int confirmations)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.pay(passphrase, Recipient.recipientWithAddress(address, amount), confirmations);
  }

  /**
   * Make a payment to a Recipient object with a default of 6 confirmations for UTXO selection
   * @param passphrase String
   * @param recipient Recipient
   * @return Payment signed broadcasted payment object (transaction)
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Recipient
   */
  public Transaction pay(String passphrase, Recipient recipient)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
    return this.pay(passphrase, recipients, 6);
  }

  /**
   * Make a payment to a Recipient object with an overrided number of confirmations for UTXO selection
   * @param passphrase String
   * @param recipient Recipient
   * @param confirmations Int number of confirmations UTXOs must have for selection in the transaction
   * @return Payment signed broadcasted payment object (transaction)
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Recipient
   */
  public Transaction pay(String passphrase, Recipient recipient, int confirmations)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
    return this.pay(passphrase, recipients, confirmations);
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
   */
  public Transaction pay(String passphrase, List<Recipient> recipients)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    return this.pay(passphrase, recipients, 6);
  }

  /**
   * Make payment to a list of recipients.  This is a transaction with multiple To: addresses and amounts
   * @param passphrase String
   * @param recipients List of recipients
   * @param confirmations Int number of confirmations UTXOs must have for selection in the transaction
   * @return Signed broadcasted payment
   * @throws IOException
   * @throws Client.UnexpectedStatusCodeException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @see co.gem.round.Recipient
   */
  public Transaction pay(String passphrase, List<Recipient> recipients, int confirmations)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {
    final Transaction payment = this.transactions().create(recipients, confirmations);
    this.wallet.unlock(passphrase, new UnlockedWalletCallback() {
      @Override
      public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
          payment.sign(wallet);
      }
    });
    return payment;
  }

}
