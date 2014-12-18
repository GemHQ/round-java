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

public class Wallet extends Base {

  public static final String RESOURCE_NAME = "wallet";

  private AccountCollection accountsCollection;
  private EncryptedMessage encryptedSeed;

  public Wallet(String url, Round round)
      throws Client.UnexpectedStatusCodeException, IOException {
    super(url, round, RESOURCE_NAME);
  }

  public Wallet(Resource resource, Round round) {
    super(resource, round);
  }

  public void unlock(String passphrase, UnlockedWalletCallback callback)
      throws IOException, Client.UnexpectedStatusCodeException {

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
    callback.execute(wallet);
  }

  public AccountCollection accounts() throws Client.UnexpectedStatusCodeException, IOException {
    if (this.accountsCollection == null) {
      this.accountsCollection = new AccountCollection(resource.subresource("accounts"), this.round, this);
    }

    return this.accountsCollection;
  }

  public EncryptedMessage getEncryptedSeed() {
    if (this.encryptedSeed == null) {
      JsonObject seedObject = getObject("primary_private_seed");

      EncryptedMessage encryptedMessage = new EncryptedMessage();
      encryptedMessage.ciphertext = seedObject.get("ciphertext").getAsString();
      encryptedMessage.salt = seedObject.get("salt").getAsString();
      encryptedMessage.nonce = seedObject.get("nonce").getAsString();
      encryptedMessage.iterations = Integer.parseInt(seedObject.get("iterations").getAsString());
      this.encryptedSeed = encryptedMessage;
    }

    return this.encryptedSeed;
  }

  public String getBackupPublicSeed() {
    return getString("backup_public_seed");
  }

  public String getCosignerPublicSeed() {
    return getString("cosigner_public_seed");
  }
}
