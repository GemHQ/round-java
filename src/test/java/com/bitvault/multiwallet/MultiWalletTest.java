package com.bitvault.multiwallet;

import org.junit.Assert;
import org.junit.Test;

import com.google.bitcoin.crypto.DeterministicKey;

public class MultiWalletTest {
	private static final String serializedPrivateSeed = "tprv8ZgxMBicQKsPczvp9b9Pw2RstFcTvUhankrVkw7b2XPZ5kfrAFFvFeCuouqvYHJzVBP51AAE89t9k55jvUBSAoBN4MNtbMsAbDYNLUE7axP";
	private static final String serializedPublicSeed = "tpubD6NzVbkrYhZ4WTxc3EozLS5zTH8Q5otVN4TH3T9tSoBwvEvcne5WS8pmz2yW8S9LA17uygnSaAhhh1nniGD32FaNHX9dRWe9Nbc4sxgjZpg";
	
	private static final MultiWallet testWallet = new MultiWallet(serializedPrivateSeed, null, null);
	
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
}
