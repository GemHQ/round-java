package com.bitvault;

import junit.framework.TestCase;

public class ApplicationTest extends TestCase {
	public void testContructor() {
		String id = "abcdef123456";
		Application app = new Application(id);
		
		assertEquals(id, app.getId());
	}
}
