package co.gem.round;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import co.gem.round.crypto.EncryptedMessage;
import co.gem.round.crypto.PassphraseBox;
import co.gem.round.multiwallet.MultiWallet;
import com.google.gson.JsonObject;

public class Wallet extends Resource {
	
	public static final String RESOURCE_NAME = "wallet";
	
	private AccountCollection accountsCollection;
	private EncryptedMessage encryptedSeed;
	private String accountsUrl;
	private String backupPublicSeed;
	private String cosignerPublicSeed;
	
	public Wallet(String url, Client client)
            throws Client.UnexpectedStatusCodeException, IOException{
		super(url, client, RESOURCE_NAME);
	}
	
	public Wallet(JsonObject resource, Client client) {
		super(resource, client, RESOURCE_NAME);
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

		MultiWallet wallet = new MultiWallet(decryptedSeed, this.getBackupPublicSeed(), this.getCosignerPublicSeed());
		callback.execute(wallet);
	}
	
	public AccountCollection accounts() throws Client.UnexpectedStatusCodeException, IOException {
		if (this.accountsCollection == null) {
			this.accountsCollection = new AccountCollection(this.getAccountsUrl(), this.client, this);
		}
		
		return this.accountsCollection;
	}
	
	public String getAccountsUrl() {
		if (this.accountsUrl == null) {
			this.accountsUrl = this.resource.getAsJsonObject("accounts")
					.get("url").getAsString();
		}
		
		return this.accountsUrl;
	}
	
	public EncryptedMessage getEncryptedSeed() {
		if (this.encryptedSeed ==  null) {
		    JsonObject seedObject = this.resource.getAsJsonObject("primary_private_seed");
		    
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
		if (this.backupPublicSeed == null) {
			this.backupPublicSeed = this.resource.get("backup_public_seed").getAsString();
		}
		
		return this.backupPublicSeed;
	}
	
	public String getCosignerPublicSeed() {
		if (this.cosignerPublicSeed == null) {
			this.cosignerPublicSeed = this.resource.get("cosigner_public_seed").getAsString();
		}
		
		return this.cosignerPublicSeed;
	}
}
