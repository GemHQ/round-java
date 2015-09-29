package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    public TransactionCollection transactions(List<Transaction.Status> status) throws IOException, Client.UnexpectedStatusCodeException {
        return transactions(null, status);
    }

    public TransactionCollection transactions(Transaction.Type type) throws IOException, Client.UnexpectedStatusCodeException {
        return transactions(type, null);
    }

    public TransactionCollection transactions() throws IOException, Client.UnexpectedStatusCodeException {
        return transactions(null, null);
    }

    public Wallet getWallet() {
        return wallet;
    }

    /**
     * Getter for transactions on an account
     * @return TransactionCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.TransactionCollection
     */
    public TransactionCollection transactions(Transaction.Type type, List<Transaction.Status> status)
            throws IOException, Client.UnexpectedStatusCodeException {
        Map<String, String> query = new HashMap<>();
        if (type != null) {
            query.put("type", type.toString());
        }
        if (status != null) {
            query.put("status", Joiner.on(',').join(status));
        }
        Resource transactionsResource = resource.subresource("transactions", query);
        TransactionCollection transactions = new TransactionCollection(transactionsResource, this.round);
        transactions.fetch();

        return transactions;
    }

    /**
     * Getter for addresses within an account
     * @return AddressCollection
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
            NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidAlgorithmParameterException, InvalidCipherTextException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        return this.payToEmail(passphrase, email, amount, 6);
    }
    @Deprecated
    public Transaction payToEmail(String passphrase, String email, long amount, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
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
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchProviderException, InvalidCipherTextException {
        return this.payToAddress(passphrase, address, amount, null, 6);
    }

    /**
     * Make a payment to a specific bitcoin address.
     * @param passphrase String passphrase to the wallet
     * @param address String valid bitcoin address based on the network
     * @param amount Long amount in satoshis
     * @param redirectUri String used to override default mfa uri
     * @return Payment signed payment
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public Transaction payToAddress(String passphrase, String address, long amount,  String redirectUri)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchProviderException, InvalidCipherTextException {
        return this.payToAddress(passphrase, address, amount, redirectUri, 6);
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
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        return this.pay(passphrase, Recipient.recipientWithAddress(address, amount), confirmations);
    }

    /**
     *
     * @param passphrase String passphrase to the wallet
     * @param address String valid bitcoin address based on the network
     * @param amount Long amount in satoshis
     * @param redirectUri String used to override default mfa uri
     * @param confirmations Int number of confirmations UTXOs must have to be used in the payment
     * @return Payment signed payment
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public Transaction payToAddress(String passphrase, String address, long amount, String redirectUri, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        return this.pay(passphrase, Recipient.recipientWithAddress(address, amount), redirectUri, confirmations);
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
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
        return this.pay(passphrase, recipients, 6);
    }

    /**
     *
     * @param passphrase String passphrase to the wallet
     * @param recipient Recipient
     * @param redirectUri String used to override default mfa uri
     * @return Payment signed broadcasted payment object (transaction)
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @see co.gem.round.Recipient
     */
    public Transaction pay(String passphrase, Recipient recipient, String redirectUri)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
        return this.pay(passphrase, recipients, redirectUri, 6);
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
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
        return this.pay(passphrase, recipients, confirmations);
    }

    /**
     *
     * @param passphrase String passphrase to the wallet
     * @param recipient Recipient
     * @param redirectUri String used to override default mfa uri
     * @param confirmations Int number of confirmations UTXOs must have to be used in the payment
     * @return Payment signed broadcasted payment object (transaction)
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @see co.gem.round.Recipient
     */
    public Transaction pay(String passphrase, Recipient recipient, String redirectUri, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        List<Recipient> recipients = Arrays.asList(new Recipient[]{recipient});
        return this.pay(passphrase, recipients, redirectUri, confirmations);
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
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        return this.pay(passphrase, recipients, 6);
    }

    /**
     *
     * @param passphrase String passphrase to the wallet
     * @param recipients List of recipients
     * @param redirectUri String used to override default mfa uri
     * @return Signed broadcasted payment
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @see co.gem.round.Recipient
     */
    public Transaction pay(String passphrase, List<Recipient> recipients, String redirectUri)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, InvalidCipherTextException {
        return this.pay(passphrase, recipients, redirectUri, 6);
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
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        return this.pay(passphrase,recipients,null,confirmations);
    }

    /**
     *
     * @param passphrase String passphrase to the wallet
     * @param recipients List of recipients
     * @param redirectUri String used to override default mfa uri
     * @param confirmations Int number of confirmations UTXOs must have for selection in the transaction
     * @return Signed broadcasted payment
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @see co.gem.round.Recipient
     */
    public Transaction pay(String passphrase, List<Recipient> recipients, String redirectUri, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        final Transaction payment = this.transactions().create(recipients, confirmations);
        this.wallet.unlock(passphrase, new UnlockedWalletCallback() {
            @Override
            public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
                payment.sign(wallet);
            }
        });
        if (redirectUri != null) {
            payment.setRedirectUri(redirectUri);
        }
        if (wallet.hasApplication()) {
            payment.approve();
        }
        return payment;
    }

}
