package co.gem.round.coinop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import co.gem.round.Payment;
import com.google.common.io.BaseEncoding;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MultiWalletTest {
	private static final String serializedPrivateSeed =
			"tprv8ZgxMBicQKsPczvp9b9Pw2RstFcTvUhankrVkw7b2XPZ5kfrAFFvFeCuouqvYHJzVBP51AAE89t9k55jvUBSAoBN4MNtbMsAbDYNLUE7axP";
	private static final String serializedPublicSeed =
			"tpubD6NzVbkrYhZ4WTxc3EozLS5zTH8Q5otVN4TH3T9tSoBwvEvcne5WS8pmz2yW8S9LA17uygnSaAhhh1nniGD32FaNHX9dRWe9Nbc4sxgjZpg";

	private static final MultiWallet testWallet = MultiWallet.importSeeds(serializedPrivateSeed, null, null);
	
	public static JsonObject transactionJson;
	
	@Before
	public void setUp() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/transaction.json");
		Assert.assertNotNull(url);
		String payload = null;
		BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        payload = sb.toString();
	    } finally {
	        br.close();
	    }
		
		transactionJson = new JsonParser().parse(payload).getAsJsonObject();
	}
	
	@Test
	public void testWalletDeserialize() {
		Assert.assertEquals(serializedPublicSeed, testWallet.serializedPrimaryPublicSeed());
	}
	
	@Test
	public void testWalletGeneration() {
		MultiWallet wallet = MultiWallet.generate(MultiWallet.Blockchain.MAINNET);
		
		MultiWallet primary = MultiWallet.importSeeds(wallet.serializedPrimaryPrivateSeed(), null, null);
		MultiWallet backup = MultiWallet.importSeeds(wallet.serializedBackupPrivateSeed(), null, null);
		
		Assert.assertEquals(wallet.serializedPrimaryPublicSeed(), primary.serializedPrimaryPublicSeed());
		Assert.assertEquals(wallet.serializedBackupPublicSeed(), backup.serializedPrimaryPublicSeed());
	}
	
	private static final String depth1ChildKey =
			"tprv8c83tTQwk62ZEFHbabs9BfNhSpqyPKJERokhAKMeAi1KLiwEHhsBC1YY9C8r36AnDu7jTkqcu9PSUyLr3tKTi2tzRFdDkg4d9XQfQ5LaNzs";
	
	@Test
	public void testChildKeyDerivationDepth1() {
		DeterministicKey childKey = testWallet.childPrimaryPrivateKeyFromPath("m/0");
		
		Assert.assertEquals(depth1ChildKey, childKey.serializePrivB58(testWallet.networkParameters()));
	}
	
	private static final String depth2ChildKey =
			"tprv8eYDSs689L665EDnMVm9zNQQdC4uunbAbhD6pQk1BFcTkFtNXyno64c29iYDhj5TTumkkwmyuopGFfSLmTHGczeaRf7v67CvyvmuQ87HWiG";
	
	@Test
	public void testChildKeyDerivationDepth2() {
		DeterministicKey childKey = testWallet.childPrimaryPrivateKeyFromPath("m/1/1");
		
		Assert.assertEquals(depth2ChildKey, childKey.serializePrivB58(testWallet.networkParameters()));
	}
	
	private static final String serializedBackupPublicSeed =
			"tpubD6NzVbkrYhZ4XcCJ7djVayRpnwAyXvHFgFgeqAfZXRVvUiY3vHbXM8jimqTaSXeVshQN7aH5jWZ7YW3L4o5757bZjTLWLnVonG8YC9ZLx8q";
	private static final String serializedCosignerPublicSeed =
			"tpubD6NzVbkrYhZ4XT9seAjKNepKycdtzwoNmUGcnGKhqHyGPXdjuyH5FB6oeLJVen9K3ZDiDrMKbsup1oMRb6U27273cE7pwW5oLL6br8D8eYu";
	
	@Test
	public void testMultiWalletConstructor() {
		MultiWallet wallet =
				MultiWallet.importSeeds(serializedPrivateSeed, serializedBackupPublicSeed, serializedCosignerPublicSeed);
		Assert.assertEquals(serializedBackupPublicSeed, wallet.serializedBackupPublicSeed());
		Assert.assertEquals(serializedCosignerPublicSeed, wallet.serializedCosignerPublicSeed());
	}
	
	private static final String redeemScriptPrivateSeed =
			"xprv9s21ZrQH143K2u1Ce1XfLCXFGXya1oXStg6uDns8tY4LVUH7TJuhwh9aTgidA4YmsgoEUpjY8rncY4CyUdiG5rPF1sG8o2x1JEavgWcVrmy";
	private static final String redeemScriptBackupSeed =
			"xpub661MyMwAqRbcEwppdfPSA76Ean7apVUSuvYaXNMdoPLsBSZnRMj6A575qXHEDhPaiaqoNoADHXBcPxXBdfPgijQLFRr6PFb354rU5A7A6Ty";
	private static final String redeemScriptCosignerSeed =
			"xpub661MyMwAqRbcFbKuGUZ9Ju81FUff3FfyafeUqyjgDzVdZBwGfTfUETz6c6ivKJtrG4WDJV3XJV1wXY8Rc2J4v9k2bSpSLKV9rRkVsh14uZU";
	private static final String redeemScriptPath = "m/0/0/0";
	
	private static final MultiWallet redeemScriptMultiWallet =
			MultiWallet.importSeeds(redeemScriptPrivateSeed, redeemScriptBackupSeed, redeemScriptCosignerSeed);
	
	private static final String expectedChildCosignerPubKey =
			"xpub6CTXuBd3wgYr8T1JR9a3wpQ136bwatoNQ7vFzAxmjHFkhhYVQYBaqHtXsQbUXCq4foTBP8vHxN1pY3ZDinwpFXYwF9SJyUCirjDCCadKrvv";
	private static final String expectedChildBackupPubKey =
			"xpub6DKH4AwsBzZ1dj1dMjAWcTbX5d11bwSor1fhN77z9SbS8394fiUQnqn2SqvywxiVe7ALHBen4hbNR8WsNYfqPbkEmeGKX2CyxtK74qsuUqV";
	private static final String expectedChildPrimaryPubKey =
			"xpub6CqEyZGzCknTx2ApKrpAoK2GWtb3iJMCh3ow28UPDguCpKajGZ7uGqcrkLGi2MijL2vbt9si9K6vLW1BeDwR9PpzCq2Rd7fbrCqH2H98zox";
	
	@Test
	public void testChildPubKeyDerivation() {
		DeterministicKey childCosignerPubKey = redeemScriptMultiWallet.childCosignerPublicKeyFromPath(redeemScriptPath);
		DeterministicKey childBackupPubKey = redeemScriptMultiWallet.childBackupPublicKeyFromPath(redeemScriptPath);
		DeterministicKey childPrimaryPubKey = redeemScriptMultiWallet.childPrimaryPublicKeyFromPath(redeemScriptPath);
		Assert.assertEquals(expectedChildCosignerPubKey,
				childCosignerPubKey.serializePubB58(redeemScriptMultiWallet.networkParameters()));
		Assert.assertEquals(expectedChildBackupPubKey,
				childBackupPubKey.serializePubB58(redeemScriptMultiWallet.networkParameters()));
		Assert.assertEquals(expectedChildPrimaryPubKey,
				childPrimaryPubKey.serializePubB58(redeemScriptMultiWallet.networkParameters()));
	}
	
	private static final String expectedScriptHex = "522103b98fd29fddb45e4675c7c60ee6a9fcb4f0e440ba" +
			"bf2c11d2860173af3b24079f21021f4d0a2c833741690b53c57609a63a6f7c95b437a87cb3be0eb864d5d84e7c" +
			"8721028506d2b42803ee2c4f02e854b4cad7cce0dec1c43c42bc7ce9e8089576ffc6e853ae";
	
	@Test
	public void testRedeemScriptForPath() {
		Script redeemScript = redeemScriptMultiWallet.redeemScriptForPath(redeemScriptPath);
		
		Assert.assertEquals(expectedScriptHex, BaseEncoding.base16().lowerCase().encode(redeemScript.getProgram()));
	}
	
	@Test
	public void testHexSignatureForPath() {
		TransactionWrapper transactionWrapper =
				TransactionWrapper.parseTransaction(transactionJson,
						redeemScriptMultiWallet.networkParameters());
		Transaction transaction = transactionWrapper.transaction();
		Script redeemScript = redeemScriptMultiWallet.redeemScriptForPath(redeemScriptPath);
		Sha256Hash sigHash = transaction.hashForSignature(0, redeemScript, Transaction.SigHash.ALL, false);
		
		String expectedSigHash = transactionJson.getAsJsonArray("inputs").get(0)
				.getAsJsonObject().get("sig_hash").getAsString();
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
