package co.gem.round.multiwallet;



import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

public class MultiWallet {

	private DeterministicKey primaryPrivateKey;
	private DeterministicKey backupPrivateKey;
	
	private DeterministicKey backupPublicKey;
	private DeterministicKey cosignerPublicKey;
	
	public MultiWallet() {
		SecureRandom random1 = new SecureRandom();
		SecureRandom random2 = new SecureRandom();
		DeterministicSeed primarySeed = new DeterministicKeyChain(random1).getSeed();
		DeterministicSeed backupSeed = new DeterministicKeyChain(random2).getSeed();
		
		this.primaryPrivateKey = HDKeyDerivation.createMasterPrivateKey(primarySeed.getSeedBytes());
		this.backupPrivateKey = HDKeyDerivation.createMasterPrivateKey(backupSeed.getSeedBytes());
		this.backupPublicKey = this.backupPrivateKey.getPubOnly();
	}
	
	public MultiWallet(String primaryPrivateSeed, String backupPublicSeed, String cosignerPublicSeed) {
		this.primaryPrivateKey = DeterministicKey.deserializeB58(null, primaryPrivateSeed);
		if (backupPublicSeed != null)
			this.backupPublicKey = DeterministicKey.deserializeB58(null, backupPublicSeed);
		if (cosignerPublicSeed != null)
			this.cosignerPublicKey = DeterministicKey.deserializeB58(null, cosignerPublicSeed);
	}
	
	public static MultiWallet generate() {
		return new MultiWallet();
	}
	
	public String serializedPrimaryPrivateSeed() {
		return this.primaryPrivateKey.serializePrivB58();
	}
	
	public String serializedPrimaryPublicSeed() {
		return this.primaryPrivateKey.serializePubB58();
	}
	
	public String serializedBackupPrivateSeed() {
		return this.backupPrivateKey.serializePrivB58();
	}
	
	public String serializedBackupPublicSeed() {
		return this.backupPublicKey.serializePubB58();
	}
	
	public String serializedCosignerPublicSeed() {
		return this.cosignerPublicKey.serializePubB58();
	}
	
	public DeterministicKey childPrimaryPrivateKeyFromPath(String path) {
		return this.childKeyFromPath(path, this.primaryPrivateKey);
	}
	
	public DeterministicKey childPrimaryPublicKeyFromPath(String path) {
		return this.childKeyFromPath(path, this.primaryPrivateKey.getPubOnly());
	}
	
	public DeterministicKey childBackupPublicKeyFromPath(String path) {
		return this.childKeyFromPath(path, this.backupPublicKey);
	}
	
	public DeterministicKey childCosignerPublicKeyFromPath(String path) {
		return this.childKeyFromPath(path, this.cosignerPublicKey);
	}
	
	public DeterministicKey childKeyFromPath(String path, DeterministicKey parentKey) {
		String[] segments = path.split("/");
		DeterministicKey currentKey = parentKey;	
		for (int i = 1; i < segments.length; i++) {
			int childNumber = Integer.parseInt(segments[i]);
			currentKey = HDKeyDerivation.deriveChildKey(currentKey, childNumber);
		}
		return currentKey;
	}
	
	public Script redeemScriptForPath(String path) {
		DeterministicKey primaryPublicKey = this.childPrimaryPublicKeyFromPath(path);
		DeterministicKey backupPublicKey = this.childBackupPublicKeyFromPath(path);
		DeterministicKey cosignerPublicKey = this.childCosignerPublicKeyFromPath(path);
		
		List<ECKey> pubKeys = Arrays.asList(new ECKey[] {
				backupPublicKey, cosignerPublicKey, primaryPublicKey });

		return ScriptBuilder.createMultiSigOutputScript(2, pubKeys);
	}
	
	public String base58SignatureForPath(String walletPath, Sha256Hash sigHash) {
		DeterministicKey primaryPrivateKey = this.childPrimaryPrivateKeyFromPath(walletPath);
		TransactionSignature signature = new TransactionSignature(primaryPrivateKey.sign(sigHash), Transaction.SigHash.ALL, false);
		return Base58.encode(signature.encodeToBitcoin());
	}
}
