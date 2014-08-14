package com.bitvault.multiwallet;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.spongycastle.util.encoders.Hex;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.HDKeyDerivation;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.wallet.DeterministicKeyChain;
import com.google.bitcoin.wallet.DeterministicSeed;

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
	
	public String hexSignatureForPath(String walletPath, Sha256Hash sigHash) {
		DeterministicKey primaryPrivateKey = this.childPrimaryPrivateKeyFromPath(walletPath);
		TransactionSignature signature = new TransactionSignature(primaryPrivateKey.sign(sigHash), SigHash.ALL, false);
		return Hex.toHexString(signature.encodeToBitcoin());
	}
}
