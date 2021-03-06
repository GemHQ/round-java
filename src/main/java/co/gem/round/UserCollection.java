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
 * UserCollection provides functionality to create users and generate a collection of users.
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class UserCollection extends BaseCollection<User> {
    public UserCollection(Resource resource, Round round) { super(resource, round); }


    /**
     * Create a User on the Gem platform.  At the time of user creation a default HD multi-sig wallet is created
     * labeled "default".  The wallet requires a passphrase to encrypt the primary key and the network for address
     * creation.
     * @param email of the user
     * @param firstName of the user
     * @param lastName of the user
     * @param passphrase to encrypt the primary seed
     * @return String deviceToken, used to authenticate device
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public String create(String email, String firstName, String lastName, String passphrase,
                         String deviceName) throws NoSuchAlgorithmException, Client.UnexpectedStatusCodeException,
            InvalidKeySpecException, IOException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException, InvalidCipherTextException {
        return create(email, firstName, lastName, passphrase, deviceName, null);
    }

    /**
     * Create a User on the Gem platform.  At the time of user creation a default HD multi-sig wallet is created
     * labeled "default".  The wallet requires a passphrase to encrypt the primary key and the network for address
     * creation.
     * @param email of the user
     * @param firstName of the user
     * @param lastName of the user
     * @param passphrase to encrypt the primary seed
     * @param redirectUri the user is sent to after confirming his/her email
     * @return String deviceToken, used to authenticate device
     * @throws Client.UnexpectedStatusCodeException
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public String create(String email, String firstName, String lastName, String passphrase,
                         String deviceName, String redirectUri)
            throws Client.UnexpectedStatusCodeException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, InvalidCipherTextException {
        MultiWallet multiWallet = MultiWallet.generate(Network.blockchainNetwork("bitcoin"));
        String primaryPrivateSeed = multiWallet.serializedPrimaryPrivateSeed();
        EncryptedMessage encryptedPrivateSeed = PassphraseBox.encrypt(passphrase, primaryPrivateSeed);
        JsonObject wallet = new JsonObject();
        wallet.addProperty("name", "default");
        wallet.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicKey());
        wallet.add("primary_private_seed", encryptedPrivateSeed.asJsonObject());

        JsonObject payload = new JsonObject();
        if (redirectUri != null) {
//      payload.addProperty("redirect_uri", redirectUri);
        }
        payload.addProperty("email", email);
        payload.addProperty("device_name", deviceName);
        payload.add("default_wallet", wallet);
        payload.addProperty("first_name", firstName);
        payload.addProperty("last_name", lastName);

        Resource resource = this.resource.action("create", payload);
        return resource.attributes().get("metadata").getAsJsonObject().get("device_token").getAsString();
    }

    @Override
    public void populateCollection(Iterable<Resource> collection) {
        for (Resource resource : collection) {
            User user = new User(resource, round);
            add(user.email(), user);
        }
    }
}
