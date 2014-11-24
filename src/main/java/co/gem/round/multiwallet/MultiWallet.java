package co.gem.round.multiwallet;



import co.gem.round.encoding.Base58;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import sun.nio.ch.Net;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

public class MultiWallet {

	public static enum Blockchain {
		TESTNET, MAINNET
	}

	private DeterministicKey primaryPrivateKey;
	private DeterministicKey backupPrivateKey;
	
	private DeterministicKey backupPublicKey;
	private DeterministicKey cosignerPublicKey;

	private NetworkParameters networkParameters;
	
	private MultiWallet(NetworkParameters networkParameters) {
		this.networkParameters = networkParameters;

		SecureRandom random1 = new SecureRandom();
		SecureRandom random2 = new SecureRandom();
		DeterministicSeed primarySeed = new DeterministicKeyChain(random1).getSeed();
		DeterministicSeed backupSeed = new DeterministicKeyChain(random2).getSeed();
		
		this.primaryPrivateKey = HDKeyDerivation.createMasterPrivateKey(primarySeed.getSeedBytes());
		this.backupPrivateKey = HDKeyDerivation.createMasterPrivateKey(backupSeed.getSeedBytes());
		this.backupPublicKey = this.backupPrivateKey.getPubOnly();
	}
	
	private MultiWallet(String primaryPrivateSeed, String backupPublicSeed, String cosignerPublicSeed) {
		byte[] decoded = new byte[0];
		try {
			decoded = Base58.decode(primaryPrivateSeed);
		} catch (AddressFormatException e) {
			e.printStackTrace();
		}
		ByteBuffer buffer = ByteBuffer.wrap(decoded);
		this.networkParameters = networkParametersFromHeaderBytes(buffer.getInt());

		this.primaryPrivateKey = DeterministicKey.deserializeB58(primaryPrivateSeed, networkParameters);
		if (backupPublicSeed != null)
			this.backupPublicKey = DeterministicKey.deserializeB58(backupPublicSeed, networkParameters);
		if (cosignerPublicSeed != null)
			this.cosignerPublicKey = DeterministicKey.deserializeB58(cosignerPublicSeed, networkParameters);
	}

	public static NetworkParameters networkParametersFromBlockchain(Blockchain blockchain) {
		switch(blockchain) {
			case MAINNET:
				return NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
			case TESTNET:
				return NetworkParameters.fromID(NetworkParameters.ID_MAINNET);
		}

		return NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
	}

	public static NetworkParameters networkParametersFromHeaderBytes(int headerBytes) {
		if (headerBytes == 0x043587CF || headerBytes == 0x04358394)
			return NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
		if (headerBytes == 0x0488B21E || headerBytes == 0x0488ADE4)
			return NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

		return NetworkParameters.fromID(NetworkParameters.ID_TESTNET);
	}

	public static MultiWallet generate(Blockchain blockchain) {
		NetworkParameters networkParameters = networkParametersFromBlockchain(blockchain);
		return new MultiWallet(networkParameters);
	}

	public static MultiWallet importSeeds(String primaryPrivateSeed, String backupPublicSeed, String cosignerPublicSeed) {
		return new MultiWallet(primaryPrivateSeed, backupPublicSeed, cosignerPublicSeed);
	}
	
	public String serializedPrimaryPrivateSeed() {
		return this.primaryPrivateKey.serializePrivB58(networkParameters);
	}
	
	public String serializedPrimaryPublicSeed() {
		return this.primaryPrivateKey.serializePubB58(networkParameters);
	}
	
	public String serializedBackupPrivateSeed() {
		return this.backupPrivateKey.serializePrivB58(networkParameters);
	}
	
	public String serializedBackupPublicSeed() {
		return this.backupPublicKey.serializePubB58(networkParameters);
	}
	
	public String serializedCosignerPublicSeed() {
		return this.cosignerPublicKey.serializePubB58(networkParameters);
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

	public NetworkParameters networkParameters() {
		return networkParameters;
	}
}
