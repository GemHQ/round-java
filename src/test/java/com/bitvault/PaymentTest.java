package com.bitvault;

import static com.bitvault.ClientTest.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PaymentTest {
	
	public static Payment unsigned;
	
	@Before
	public void setUp() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/transaction.json");
		Assert.assertNotNull(url);
		Path path = Paths.get(url.toURI());
		
		String payload = new String(Files.readAllBytes(path));
		JsonObject resource = new JsonParser().parse(payload).getAsJsonObject();
		unsigned = new Payment(resource, client);
	}
	
	@Test
	public void testGetNativeTransaction() {
		Transaction transaction = unsigned.getNativeTransaction();
		Assert.assertEquals(unsigned.getHash(), transaction.getHashAsString());
	}
	
	@Test
	public void testGetOutputs() {
		List<TransactionOutput> outputs = unsigned.getOutputs();
		Assert.assertEquals(2, outputs.size());
	}
	
	@Test
	public void testHexSignatureForWalletPath() {
		
	}
}
