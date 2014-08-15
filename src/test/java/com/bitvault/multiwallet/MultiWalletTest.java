package com.bitvault.multiwallet;

import static com.bitvault.ClientTest.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import com.bitvault.Payment;
import com.google.bitcoin.core.Base58;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Transaction.SigHash;
import com.google.bitcoin.crypto.DeterministicKey;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.script.Script;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MultiWalletTest {
	private static final String serializedPrivateSeed = "tprv8ZgxMBicQKsPczvp9b9Pw2RstFcTvUhankrVkw7b2XPZ5kfrAFFvFeCuouqvYHJzVBP51AAE89t9k55jvUBSAoBN4MNtbMsAbDYNLUE7axP";
	private static final String serializedPublicSeed = "tpubD6NzVbkrYhZ4WTxc3EozLS5zTH8Q5otVN4TH3T9tSoBwvEvcne5WS8pmz2yW8S9LA17uygnSaAhhh1nniGD32FaNHX9dRWe9Nbc4sxgjZpg";
	
	private static final MultiWallet testWallet = new MultiWallet(serializedPrivateSeed, null, null);
	
	public static JsonObject transactionJson;
	
	@Before
	public void setUp() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/transaction.json");
		Assert.assertNotNull(url);
		Path path = Paths.get(url.toURI());
		
		String payload = new String(Files.readAllBytes(path));
		transactionJson = new JsonParser().parse(payload).getAsJsonObject();
	}
	
	@Test
	public void testWalletDeserialize() {
		Assert.assertEquals(serializedPublicSeed, testWallet.serializedPrimaryPublicSeed());
	}
	
	@Test
	public void testWalletGeneration() {
		MultiWallet wallet = MultiWallet.generate();
		
		MultiWallet primary = new MultiWallet(wallet.serializedPrimaryPrivateSeed(), null, null);
		MultiWallet backup = new MultiWallet(wallet.serializedBackupPrivateSeed(), null, null);
		
		Assert.assertEquals(wallet.serializedPrimaryPublicSeed(), primary.serializedPrimaryPublicSeed());
		Assert.assertEquals(wallet.serializedBackupPublicSeed(), backup.serializedPrimaryPublicSeed());
	}
	
	private static final String depth1ChildKey = "tprv8c83tTQwk62ZEFHbabs9BfNhSpqyPKJERokhAKMeAi1KLiwEHhsBC1YY9C8r36AnDu7jTkqcu9PSUyLr3tKTi2tzRFdDkg4d9XQfQ5LaNzs";
	
	@Test
	public void testChildKeyDerivationDepth1() {
		DeterministicKey childKey = testWallet.childPrimaryPrivateKeyFromPath("m/0");
		
		Assert.assertEquals(depth1ChildKey, childKey.serializePrivB58());
	}
	
	private static final String depth2ChildKey = "tprv8eYDSs689L665EDnMVm9zNQQdC4uunbAbhD6pQk1BFcTkFtNXyno64c29iYDhj5TTumkkwmyuopGFfSLmTHGczeaRf7v67CvyvmuQ87HWiG";
	
	@Test
	public void testChildKeyDerivationDepth2() {
		DeterministicKey childKey = testWallet.childPrimaryPrivateKeyFromPath("m/1/1");
		
		Assert.assertEquals(depth2ChildKey, childKey.serializePrivB58());
	}
	
	private static final String serializedBackupPublicSeed = "tpubD6NzVbkrYhZ4XcCJ7djVayRpnwAyXvHFgFgeqAfZXRVvUiY3vHbXM8jimqTaSXeVshQN7aH5jWZ7YW3L4o5757bZjTLWLnVonG8YC9ZLx8q";
	private static final String serializedCosignerPublicSeed = "tpubD6NzVbkrYhZ4XT9seAjKNepKycdtzwoNmUGcnGKhqHyGPXdjuyH5FB6oeLJVen9K3ZDiDrMKbsup1oMRb6U27273cE7pwW5oLL6br8D8eYu";
	
	@Test
	public void testMultiWalletConstructor() {
		MultiWallet wallet = new MultiWallet(serializedPrivateSeed, serializedBackupPublicSeed, serializedCosignerPublicSeed);
		Assert.assertEquals(serializedBackupPublicSeed, wallet.serializedBackupPublicSeed());
		Assert.assertEquals(serializedCosignerPublicSeed, wallet.serializedCosignerPublicSeed());
	}
	
	private static final String redeemScriptPrivateSeed = "xprv9s21ZrQH143K2u1Ce1XfLCXFGXya1oXStg6uDns8tY4LVUH7TJuhwh9aTgidA4YmsgoEUpjY8rncY4CyUdiG5rPF1sG8o2x1JEavgWcVrmy";
	private static final String redeemScriptBackupSeed = "xpub661MyMwAqRbcEwppdfPSA76Ean7apVUSuvYaXNMdoPLsBSZnRMj6A575qXHEDhPaiaqoNoADHXBcPxXBdfPgijQLFRr6PFb354rU5A7A6Ty";
	private static final String redeemScriptCosignerSeed = "tpubD6NzVbkrYhZ4WktZmRfVsUF1ZqPLirFiuaTom2W6dp9W6DuPzefqHR83ALqyRBNVSoScLwwnHQjj95xzTsqxJP2RuopfVmp7Yd7aSyTZoZp";
	private static final String redeemScriptPath = "m/0/0/0";
	
	private static final MultiWallet redeemScriptMultiWallet = new MultiWallet(redeemScriptPrivateSeed, redeemScriptBackupSeed, redeemScriptCosignerSeed);
	
	private static final String expectedChildCosignerPubKey = "tpubDCumBBiuwagnoZUQC6D5m2LgcoP87UL9W7xHkLVbgoodh14CwjPGpdCx9CHnGWciLZ1u5xaPiC6t8YctrvUztgr6C1Bb5qeiBk1BXnqxKsC";
	private static final String expectedChildBackupPubKey = "xpub6DKH4AwsBzZ1dj1dMjAWcTbX5d11bwSor1fhN77z9SbS8394fiUQnqn2SqvywxiVe7ALHBen4hbNR8WsNYfqPbkEmeGKX2CyxtK74qsuUqV";
	private static final String expectedChildPrimaryPubKey = "xpub6CqEyZGzCknTx2ApKrpAoK2GWtb3iJMCh3ow28UPDguCpKajGZ7uGqcrkLGi2MijL2vbt9si9K6vLW1BeDwR9PpzCq2Rd7fbrCqH2H98zox";
	
	@Test
	public void testChildPubKeyDerivation() {
		DeterministicKey childCosignerPubKey = redeemScriptMultiWallet.childCosignerPublicKeyFromPath(redeemScriptPath);
		DeterministicKey childBackupPubKey = redeemScriptMultiWallet.childBackupPublicKeyFromPath(redeemScriptPath);
		DeterministicKey childPrimaryPubKey = redeemScriptMultiWallet.childPrimaryPublicKeyFromPath(redeemScriptPath);
		Assert.assertEquals(expectedChildCosignerPubKey, childCosignerPubKey.serializePubB58());
		Assert.assertEquals(expectedChildBackupPubKey, childBackupPubKey.serializePubB58());
		Assert.assertEquals(expectedChildPrimaryPubKey, childPrimaryPubKey.serializePubB58());
	}
	
	private static final String expectedScriptHex = "522103b98fd29fddb45e4675c7c60ee6a9fcb4f0e440bab" 
			+ "f2c11d2860173af3b24079f21034ce115b6ecd1f05c9a83c00d082d8a0a2d8a3a688ef7fdeacbe892a5ecbc9"
			+ "4bf21028506d2b42803ee2c4f02e854b4cad7cce0dec1c43c42bc7ce9e8089576ffc6e853ae";
	
	@Test
	public void testRedeemScriptForPath() {
		Script redeemScript = redeemScriptMultiWallet.redeemScriptForPath(redeemScriptPath);
		
		Assert.assertEquals(expectedScriptHex, Hex.toHexString(redeemScript.getProgram()));
	}
	
	@Test
	public void testHexSignatureForPath() {
		Payment payment = new Payment(transactionJson, client);
		Transaction transaction = payment.getNativeTransaction();
		Script redeemScript = redeemScriptMultiWallet.redeemScriptForPath(redeemScriptPath);
		Sha256Hash sigHash = transaction.hashForSignature(0, redeemScript, SigHash.ALL, false);
		
		String expectedSigHash = transactionJson.getAsJsonArray("inputs").get(0).getAsJsonObject().get("sig_hash").getAsString();
		Assert.assertEquals(expectedSigHash, sigHash.toString());
		
		String hexSignature = redeemScriptMultiWallet.base58SignatureForPath(redeemScriptPath, sigHash);
		
		DeterministicKey primaryPubKey = redeemScriptMultiWallet.childPrimaryPublicKeyFromPath(redeemScriptPath);
		TransactionSignature signature = null;
		try {
			signature = TransactionSignature.decodeFromBitcoin(Base58.decode(hexSignature), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertTrue(primaryPubKey.verify(sigHash, signature));
	}
}
