package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.crypto.EncryptedMessage;
import co.gem.round.crypto.PassphraseBox;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        } catch (InvalidKeySpecException e) {
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
     * Getter for accounts in a wallet
     * @return AccountCollection of Accounts
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @see co.gem.round.AccountCollection
     */
    public AccountCollection accounts() throws Client.UnexpectedStatusCodeException, IOException {
        AccountCollection accounts = new AccountCollection(resource.subresource("accounts"), this.round, this);
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
