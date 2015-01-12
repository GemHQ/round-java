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

  private EncryptedMessage encryptedSeed;

  public Wallet(Resource resource, Round round) {
    super(resource, round);
  }

  public void unlock(String passphrase, UnlockedWalletCallback callback)
      throws IOException, Client.UnexpectedStatusCodeException,
      NoSuchAlgorithmException, InvalidKeySpecException {

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
    AccountCollection accounts = new AccountCollection(resource.subresource("accounts"), this.round, this);
    accounts.fetch();
    return accounts;
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

  public String getPrimaryPublicSeed() {
    return getString("primary_public_seed");
  }
}
