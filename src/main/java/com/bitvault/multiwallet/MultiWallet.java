package com.bitvault.multiwallet;

import java.security.SecureRandom;

import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.HDKeyDerivation;
import com.google.bitcoin.wallet.DeterministicKeyChain;
import com.google.bitcoin.wallet.DeterministicSeed;

public class MultiWallet {

	private DeterministicKey primaryKey;
	private DeterministicKey backupKey;
	
	public MultiWallet() {
		SecureRandom random1 = new SecureRandom();
		SecureRandom random2 = new SecureRandom();
		DeterministicSeed primarySeed = new DeterministicKeyChain(random1).getSeed();
		DeterministicSeed backupSeed = new DeterministicKeyChain(random2).getSeed();
		
		this.primaryKey = HDKeyDerivation.createMasterPrivateKey(primarySeed.getSeedBytes());
		this.backupKey = HDKeyDerivation.createMasterPrivateKey(backupSeed.getSeedBytes());
	}
	
	public MultiWallet(String primarySeed) {
		this.primaryKey = DeterministicKey.deserializeB58(null, primarySeed);
	}
	
	public static MultiWallet generate() {
		return new MultiWallet();
	}
	
	public String serializedPrimaryPrivateSeed() {
		return this.primaryKey.serializePrivB58();
	}
	
	public String serializedPrimaryPublicSeed() {
		return this.primaryKey.serializePubB58();
	}
	
	public String serializedBackupPrivateSeed() {
		return this.backupKey.serializePrivB58();
	}
	
	public String serializedBackupPublicSeed() {
		return this.backupKey.serializePubB58();
	}
	
	public DeterministicKey childKeyFromPath(String path) {
		String[] segments = path.split("/");
		DeterministicKey currentKey = this.primaryKey;	
		for (int i = 1; i < segments.length; i++) {
			int childNumber = Integer.parseInt(segments[i]);
			currentKey = HDKeyDerivation.deriveChildKey(currentKey, childNumber);
		}
		
		return currentKey;
	}
}
