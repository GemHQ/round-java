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
public class WalletCollection extends BaseCollection<Wallet> {
  public WalletCollection(Resource resource, Round round) {
    super(resource, round);
  }

public Wallet.Wrapper create(String name, String passphrase, String blockchain)
        throws IOException, Client.UnexpectedStatusCodeException,
        InvalidKeySpecException, NoSuchAlgorithmException {

  MultiWallet multiWallet = MultiWallet.generate(Network.blockchainNetwork(blockchain));
  String primaryPrivateSeed = multiWallet.serializedPrimaryPrivateSeed();
  EncryptedMessage encryptedPrivateSeed = PassphraseBox.encrypt(passphrase, primaryPrivateSeed);

  String network = null;
  if (multiWallet.blockchain() == MultiWallet.Blockchain.MAINNET)
    network = "bitcoin";
  else
    network = "bitcoin_testnet";

  JsonObject wallet = new JsonObject();
  wallet.addProperty("name", name);
  wallet.addProperty("network", network);
  wallet.addProperty("backup_public_seed", multiWallet.serializedBackupPublicSeed());
  wallet.addProperty("primary_public_seed", multiWallet.serializedPrimaryPublicSeed());
  wallet.add("primary_private_seed", encryptedPrivateSeed.asJsonObject());

  Resource resource = this.resource.action("create", wallet);
  Wallet gemWallet = new Wallet(resource, round);

  Wallet.Wrapper wrapper = new Wallet.Wrapper(gemWallet, multiWallet.serializedBackupPrivateSeed());
  return wrapper;
}

  @Override
  public void populateCollection(Iterable<Resource> collection) {
    for (Resource resource : collection) {
      Wallet wallet = new Wallet(resource, round);
      add(wallet.getString("name"), wallet);
    }
  }
}
