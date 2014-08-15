package com.bitvault;

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

import com.bitvault.multiwallet.MultiWallet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PaymentTest {
	
	public static Payment unsignedPayment;
	
	@Before
	public void setUp() throws URISyntaxException, IOException {
		URL url = this.getClass().getResource("/transaction.json");
		Assert.assertNotNull(url);
		Path path = Paths.get(url.toURI());
		
		String payload = new String(Files.readAllBytes(path));
		JsonObject resource = new JsonParser().parse(payload).getAsJsonObject();
		unsignedPayment = new Payment(resource, client);
	}
	
	private static final String primaryPrivSeed = "xprv9s21ZrQH143K2u1Ce1XfLCXFGXya1oXStg6uDns8tY4LVUH7TJuhwh9aTgidA4YmsgoEUpjY8rncY4CyUdiG5rPF1sG8o2x1JEavgWcVrmy";
	private static final String backupPubSeed = "xpub661MyMwAqRbcEwppdfPSA76Ean7apVUSuvYaXNMdoPLsBSZnRMj6A575qXHEDhPaiaqoNoADHXBcPxXBdfPgijQLFRr6PFb354rU5A7A6Ty";
	private static final String cosignerPubSeed = "tpubD6NzVbkrYhZ4WktZmRfVsUF1ZqPLirFiuaTom2W6dp9W6DuPzefqHR83ALqyRBNVSoScLwwnHQjj95xzTsqxJP2RuopfVmp7Yd7aSyTZoZp";
	private static final MultiWallet wallet = new MultiWallet(primaryPrivSeed, backupPubSeed, cosignerPubSeed);
	
	@Test
	public void testSign() {
		Payment signed = unsignedPayment.sign(wallet);
		
		Assert.assertEquals("unconfirmed", signed.getStatus());
	}
	
}
