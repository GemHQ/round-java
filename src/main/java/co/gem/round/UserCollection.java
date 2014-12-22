package co.gem.round;

import co.gem.round.coinop.MultiWallet;
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

  public User.Wrapper create(String email, String passphrase)
      throws Client.UnexpectedStatusCodeException, IOException,
      InvalidKeySpecException, NoSuchAlgorithmException{
    MultiWallet multiWallet = MultiWallet.generate(MultiWallet.Blockchain.TESTNET);
    String primaryPrivateSeed = multiWallet.serializedPrimaryPrivateSeed();
    EncryptedMessage encryptedPrivateSeed = PassphraseBox.encrypt(passphrase, primaryPrivateSeed);

    JsonObject wallet = new JsonObject();
    wallet.addProperty("network", "bitcoin_testnet");
    wallet.addProperty("backup_public_seed", multiWallet.serializedBackupPublicSeed());
    wallet.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicSeed());
    wallet.add("primary_private_seed", encryptedPrivateSeed.asJsonObject());

    JsonObject payload = new JsonObject();
    payload.addProperty("email", email);
    payload.add("wallet", wallet);

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
