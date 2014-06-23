package com.bitvault;

import org.junit.*;

public class ApplicationTest {

  @Test public void constructorTest() {
    String id = "abcdef123456";
    Application app = new Application(id);

    Assert.assertEquals(id, app.getId());
  }

}