package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.util.Network;
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

/**
 * WalletCollection provides access to a users collection of wallets as well as the create method to add additional
 * wallets to a user.
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class WalletCollection extends BaseCollection<Wallet> {
    private Application app;

    public WalletCollection(Resource resource, Round round, Application app) {
        super(resource, round);
        this.app = app;
    }
    public WalletCollection(Resource resource, Round round) {
        super(resource, round);
    }

    /**
     * Creates an additional wallet on the authenticated user.
     * @param name of the wallet
     * @param passphrase to encrypt the primary seed
     * @return Wallet.Wrapper were you can get the user object to initiate begin/complete device authentication.  This is
     * depricated and will be replaced with returning only a Wallet object.
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public Wallet.Wrapper create(String name, String passphrase)
            throws IOException, Client.UnexpectedStatusCodeException,
            InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, InvalidCipherTextException {

        MultiWallet multiWallet = MultiWallet.generate(Network.blockchainNetwork("bitcoin"));
        String primaryPrivateSeed = multiWallet.serializedPrimaryPrivateSeed();
        EncryptedMessage encryptedPrivateSeed = PassphraseBox.encrypt(passphrase, primaryPrivateSeed);

        JsonObject wallet = new JsonObject();
        wallet.addProperty("name", name);
        wallet.addProperty("backup_public_seed", multiWallet.serializedBackupPublicKey());
        wallet.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicKey());
        wallet.add("primary_private_seed", encryptedPrivateSeed.asJsonObject());

        Resource resource = this.resource.action("create", wallet);
        Wallet gemWallet = new Wallet(resource, round, app);

        Wallet.Wrapper wrapper = new Wallet.Wrapper(gemWallet, multiWallet.serializedBackupPrivateSeed());
        multiWallet.purgeSeeds();
        return wrapper;
    }

    @Override
    public void populateCollection(Iterable<Resource> collection) {
        for (Resource resource : collection) {
            Wallet wallet = new Wallet(resource, round, app);
            add(wallet.getString("name"), wallet);
        }
    }
}
