package com.bitvault.multiwallet;

import org.junit.Assert;
import org.junit.Test;

import com.google.bitcoin.crypto.DeterministicKey;

public class MultiWalletTest {
	private static final String serializedPrivateSeed = "tprv8ZgxMBicQKsPczvp9b9Pw2RstFcTvUhankrVkw7b2XPZ5kfrAFFvFeCuouqvYHJzVBP51AAE89t9k55jvUBSAoBN4MNtbMsAbDYNLUE7axP";
	private static final String serializedPublicSeed = "tpubD6NzVbkrYhZ4WTxc3EozLS5zTH8Q5otVN4TH3T9tSoBwvEvcne5WS8pmz2yW8S9LA17uygnSaAhhh1nniGD32FaNHX9dRWe9Nbc4sxgjZpg";
	
	private static final MultiWallet testWallet = new MultiWallet(serializedPrivateSeed);
	
	@Test
	public void testWalletDeserialize() {
		Assert.assertEquals(serializedPublicSeed, testWallet.serializedPrimaryPublicSeed());
	}
	
	@Test
	public void testWalletGeneration() {
		MultiWallet wallet = MultiWallet.generate();
		
		MultiWallet primary = new MultiWallet(wallet.serializedPrimaryPrivateSeed());
		MultiWallet backup = new MultiWallet(wallet.serializedBackupPrivateSeed());
		
		Assert.assertEquals(wallet.serializedPrimaryPublicSeed(), primary.serializedPrimaryPublicSeed());
		Assert.assertEquals(wallet.serializedBackupPublicSeed(), backup.serializedPrimaryPublicSeed());
	}
	
	private static final String depth1ChildKey = "tprv8c83tTQwk62ZEFHbabs9BfNhSpqyPKJERokhAKMeAi1KLiwEHhsBC1YY9C8r36AnDu7jTkqcu9PSUyLr3tKTi2tzRFdDkg4d9XQfQ5LaNzs";
	
	@Test
	public void testChildKeyDerivationDepth1() {
		DeterministicKey childKey = testWallet.childKeyFromPath("m/0");
		
		Assert.assertEquals(depth1ChildKey, childKey.serializePrivB58());
	}
	
	private static final String depth2ChildKey = "tprv8eYDSs689L665EDnMVm9zNQQdC4uunbAbhD6pQk1BFcTkFtNXyno64c29iYDhj5TTumkkwmyuopGFfSLmTHGczeaRf7v67CvyvmuQ87HWiG";
	
	@Test
	public void testChildKeyDerivationDepth2() {
		DeterministicKey childKey = testWallet.childKeyFromPath("m/1/1");
		
		Assert.assertEquals(depth2ChildKey, childKey.serializePrivB58());
	}
}
