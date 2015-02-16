package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.util.Network;
import co.gem.round.crypto.EncryptedMessage;
import co.gem.round.crypto.PassphraseBox;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by julian on 12/18/14.
 */
public class UserCollection extends BaseCollection<User> {
  public UserCollection(Resource resource, Round round) { super(resource, round); }

  public User.Wrapper create(String email, String passphrase, String blockchain)
      throws Client.UnexpectedStatusCodeException, IOException,
      InvalidKeySpecException, NoSuchAlgorithmException{
    MultiWallet multiWallet = MultiWallet.generate(Network.blockchainNetwork(blockchain));
    String primaryPrivateSeed = multiWallet.serializedPrimaryPrivateSeed();
    EncryptedMessage encryptedPrivateSeed = PassphraseBox.encrypt(passphrase, primaryPrivateSeed);

    String network = null;
    if (multiWallet.blockchain() == MultiWallet.Blockchain.MAINNET)
      network = "bitcoin";
    else
      network = "bitcoin_testnet";

    JsonObject wallet = new JsonObject();
    wallet.addProperty("name", "default");
    wallet.addProperty("network", network);
    wallet.addProperty("backup_public_seed", multiWallet.serializedBackupPublicSeed());
    wallet.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicSeed());
    wallet.add("primary_private_seed", encryptedPrivateSeed.asJsonObject());

    JsonObject payload = new JsonObject();
    payload.addProperty("email", email);
    payload.add("default_wallet", wallet);

    Resource resource = this.resource.action("create", payload);
    User user = new User(resource, round);
    User.Wrapper wrapper = new User.Wrapper(user, multiWallet.serializedBackupPrivateSeed());
    return wrapper;
  }

  @Override
  public void populateCollection(Iterable<Resource> collection) {
    for (Resource resource : collection) {
      User user = new User(resource, round);
      add(user.key(), user);
    }
  }
}
