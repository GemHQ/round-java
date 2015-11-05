package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.crypto.EncryptedMessage;
import co.gem.round.crypto.PassphraseBox;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import com.google.common.base.Joiner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wallet is a gem wallet which is the HD Multi-Sig (2of3) wallet that is owned by a User.  Wallets provide access to
 * accounts in the wallet.  As well as info such as the balance of all accounts.  Gem wallets follow BIP32 and BIP44
 * but without hardened nodes.
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 * @see co.gem.round.Account
 */
public class Wallet extends Base {

    private Application app;
    private EncryptedMessage encryptedSeed;

    public Wallet(Resource resource, Round round, Application app) {
        super(resource, round);
        this.app = app;
    }

    public boolean hasApplication() {
        return app != null;
    }

    public void unlock(String passphrase, UnlockedWalletCallback callback)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, InvalidCipherTextException {

        String decryptedSeed = null;

        try {
            decryptedSeed = PassphraseBox.decrypt(passphrase, this.getEncryptedSeed());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return;
        }

        MultiWallet wallet = MultiWallet.importSeeds(decryptedSeed,
                this.getBackupPublicSeed(), this.getCosignerPublicSeed());
        wallet.purgeSeeds(); // Get rid of seeds, we have the keys after initialization.
        callback.execute(wallet);
    }

    /**
     * Gets default (first) account in a wallet
     * @return default Account
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @see co.gem.round.Account
     */
    public Account defaultAccount() throws Client.UnexpectedStatusCodeException, IOException {
        return accounts().get(0);
    }

    /**
     * Gets account in a wallet with name matching accountName parameter.
     * @param accountName Account name
     * @return Account
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @see co.gem.round.Account
     */
    public Account account(String accountName) throws Client.UnexpectedStatusCodeException, IOException {
        Map<String, String> query = new HashMap<>();
        query.put("name", accountName);
        Account account = new Account(resource.subresource("account_query", query), this.round);
        account.fetch();
        return account;
    }

    /**
     * Getter for accounts in a wallet. Returns populated AccountCollection object. To
     * retrieve reference without fetching accounts use 'accounts(false)'
     * @return AccountCollection of Accounts
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @see co.gem.round.AccountCollection
     */
    public AccountCollection accounts() throws Client.UnexpectedStatusCodeException, IOException {
        return accounts(true);
    }

    /**
     * Getter for AccountCollection object in a wallet.
     * @param fetch boolean used to determine whether to populate collection
     * @return AccountCollection object
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @see co.gem.round.AccountCollection
     */
    public AccountCollection accounts(boolean fetch) throws Client.UnexpectedStatusCodeException, IOException {
        AccountCollection accounts = new AccountCollection(resource.subresource("accounts"), this.round, this);
        if (fetch)
            accounts.fetch();
        return accounts;
    }

    /**
     * Getter for the elements of the encrypted primary seed.
     * @return EncryptedMessage containing ciphertext, salt, iv, iterations for decryption purposes
     */
    public EncryptedMessage getEncryptedSeed() {
        if (this.encryptedSeed == null) {
            JsonObject seedObject = getObject("primary_private_seed");

            EncryptedMessage encryptedMessage = new EncryptedMessage();
            encryptedMessage.ciphertext = seedObject.get("ciphertext").getAsString();
            encryptedMessage.salt = seedObject.get("salt").getAsString();
            encryptedMessage.iv = seedObject.get("iv").isJsonNull() ? null : seedObject.get("iv").getAsString();
            encryptedMessage.iterations = Integer.parseInt(seedObject.get("iterations").getAsString());
            this.encryptedSeed = encryptedMessage;
        }

        return this.encryptedSeed;
    }

    /**
     * Getter for the backup pubSeed
     * @return String
     */
    public String getBackupPublicSeed() {
        return getString("backup_public_seed");
    }

    /**
     * Getter for Gem API's cosigner pubSeed
     * @return String
     */
    public String getCosignerPublicSeed() {
        return getString("cosigner_public_seed");
    }

    /**
     * Getter for Primary pubSeed
     * @return String
     */
    public String getPrimaryPublicSeed() {
        return getString("primary_public_seed");
    }

    /**
     * Sum of all of the account balances within a wallet.  Balances are based off of the value of inputs with 1 or more
     * confirmations.
     * @return Long
     */
    public Long balance() {
        return getLong("balance");
    }

    /**
     * Return the confirmed, claimed (reserved for a pending, unsigned transaction), and available
     * balances, where the threshold for confirmed is the value of `utxo_confirmations`.
     * @param confirmations the # of confirmations to use when computing balances.
     * @param network Type of cryptocurrency.  Can be one of, 'BITCOIN', 'TESTNET', 'LITECOIN', 'DOGECOIN'
     * @return A map of form { 'available_balance': 0,
     *                         'claimed_balance': 0,
     *                         'confirmed_balance': 0,
     *                         'utxo_confirmations': 0 }
     */
    public Map<String, Long> balances_at(int confirmations, Round.Network network) {
        Map<String, String> query = new HashMap<>();

        query.put("utxo_confirmations", ""+confirmations);
        query.put("network", network.toString());

        try {
            Resource balanceResource = resource.subresource("balance", query);
            Map<String, Long> balances = new HashMap<>();

            if (balanceResource.attributes().has("available_balance")) {
                balances.put("available_balance",
                        balanceResource.attributes().get("available_balance").getAsLong());
            } else {
                balances.put("available_balance", (long) 0);
            }
            if (balanceResource.attributes().has("claimed_balance")) {
                balances.put("claimed_balance",
                        balanceResource.attributes().get("claimed_balance").getAsLong());
            } else {
                balances.put("claimed_balance", (long) 0);
            }
            if (balanceResource.attributes().has("confirmed_balance")) {
                balances.put("confirmed_balance",
                        balanceResource.attributes().get("confirmed_balance").getAsLong());
            } else {
                balances.put("confirmed_balance", (long) 0);
            }
            if (balanceResource.attributes().has("utxo_confirmations")) {
                balances.put("utxo_confirmations",
                        balanceResource.attributes().get("utxo_confirmations").getAsLong());
            } else {
                balances.put("utxo_confirmations", (long) 0);
            }
            return balances;

        } catch (IOException | Client.UnexpectedStatusCodeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Getter for transactions on an account. Returns populated TransactionCollection object. To
     * retrieve reference without fetching transactions use 'transactions(false)'
     * @return TransactionCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.TransactionCollection
     */
    public TransactionCollection transactions() throws IOException, Client.UnexpectedStatusCodeException {
        return transactions(null, null, true);
    }

    /**
     * Getter for TransactionCollection object
     * @param fetch boolean used to determine whether to populate collection
     * @return TransactionCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.TransactionCollection
     */
    public TransactionCollection transactions(boolean fetch) throws IOException, Client.UnexpectedStatusCodeException {
        return transactions(null, null, fetch);
    }

    /**
     * Getter for transactions on an account.
     * @param type desired transaction type
     * @param status list of desired transaction status
     * @param fetch boolean used to determine whether to populate collection
     * @return TransactionCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.TransactionCollection
     */
    public TransactionCollection transactions(Transaction.Type type, List<Transaction.Status> status, boolean fetch)
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
        if (fetch) {
            transactions.fetch();
        }
        return transactions;
    }

    /**
     * Create, verify, and sign a new Transaction.
     *
     * This method is distinct from Account.pay in that it exposes the `payers`
     * parameter which allows you to create transactions using inputs from
     * multiple Accounts (which must ALL belong to this Wallet) by specifying
     * a dict with account names or Account objects and amounts to be deducted.
     * Note that a change output will be created for each account if necessary
     * to deduct the precise amount specified. Fees will be drawn from the
     * `remainder_account`, as well as any difference between the sum of `payee`
     * outputs and the `payers` inputs. (Which is why `remainder_account` is
     * mandatory and `payers` is optional.)
     * If this Wallet is owned by a User object, the user must be redirected to
     * a URL (`mfa_uri`) returned by this call to input their MFA token. After
     * they complete that step, the Transaction will be approved and published
     * to the bitcoin network. If a `redirect_uri` is provided in this call, the
     * user will be redirected to that uri after they complete the MFA challenge
     * so it's a good idea to have an endpoint in your app (or custom scheme on
     * mobile) that can provide the user a seamless flow for returning to your
     * application. If they have not configured a TOTP MFA application (e.g.
     * Google Authenticator), then an SMS will be sent to their phone number
     * with their token.
     * If this Wallet is owned by an Application, the `mfa_token` can be
     * included in this call and the Transaction will be automatically approved
     * and published to the blockchain.
     * @param passphrase
     * @param recipients
     * @param payers
     * @param remainderAccount an Account to handle the difference between payer and
     *                         payee sums as well as tx fees (if set to None, a
     *                         transaction will be created that potentiallyuses UTXOs
     *                         from every Account belonging to this wallet)
     * @param changeAccountKey if supplied, this account will be used to generate a
     *                         change address in the event that a change output is
     *                         required. Note that this does not replace the change
     *                         outputs that will be generated to ensure that each Account
     *                         in `payers` (if supplied) is deducted the precise amounts
     *                         specified.
     * @param network Type of cryptocurrency.  Can be one of, 'bitcoin', 'bitcoin_testnet',
     *                'litecoin', 'dogecoin'.
     * @param redirectUri URI to redirect a user to after they input an mfa token on the
     *                    page referenced by the `mfa_uri` returned by this function.
     * @param confirmations Required confirmations for UTXO selection ( > 0)
     * @return
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     * @throws InvalidCipherTextException
     */
    public Transaction pay(String passphrase, List<Recipient> recipients, List<Payer> payers,
                           Account remainderAccount, String changeAccountKey, String network,
                           String redirectUri, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException,
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {

        String remainderAccountKey = null;
        if (remainderAccount == null) {
            if (payers != null) {
                // error?
                return null;
            } else if (network == null) {
                // error?
                return null;
            }
        } else {
            remainderAccountKey = remainderAccount.name();
            if (network == null) {
                network = remainderAccount.network();
            }
        }

        final Transaction payment = this.transactions(false)
                .create(recipients, payers, remainderAccountKey, changeAccountKey, network, confirmations);


        this.unlock(passphrase, new UnlockedWalletCallback() {
            @Override
            public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException {
                payment.sign(wallet);
            }
        });
        if (redirectUri != null) {
            payment.setRedirectUri(redirectUri);
        }
        if (hasApplication()) {
            payment.approve();
        }
        return payment;
    }

    public static class Wrapper {
        private Wallet wallet;
        private String backupPrivateSeed;

        public Wrapper(Wallet wallet, String backupPrivateSeed) {
            this.wallet = wallet;
            this.backupPrivateSeed = backupPrivateSeed;
        }

        /**
         * Getter for the wallet
         * @return String
         */
        public Wallet getWallet() {
            return wallet;
        }

        /**
         * Getter for the wallet's backup private seed
         * @return String
         */
        public String getBackupPrivateSeed() {
            return backupPrivateSeed;
        }
    }
}
