package org.astrogrid.config;


import java.io.IOException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests SimpleConfig class.  As this is a static singleton wrapped around
 * PropertyConfig, it also exercises PropertyConfig
 */

public class SimpleTest extends TestCase
{

   private static final String testPropertyFile = "test.properties";
   private static final String nonExistentFile = "testNotHere.properties";

   
   /**
    * Some keys are valid, some are not - this tests that the assertions for
    * invalid keys are working
    */
   public void testPropertyKeys()
   {
      try {
         SimpleConfig.getProperty("wibble:wibble");
         fail("Should have thrown an assertion error given a key with a colon in ");
      }
      catch (AssertionError ae) {}
         
      try {
         SimpleConfig.getProperty("wibble wibble");
         fail("Should have thrown an assertion error given a key with a space in ");
      }
      catch (AssertionError ae) {}
         
      try {
         SimpleConfig.getProperty("wibble=wibble");
         fail("Should have thrown an assertion error given a key with an equals in ");
      }
      catch (AssertionError ae) {}
         
   }
   
 /**
 * These tests require assertions to be enabled.
 * java must be run with the -ea option.
 */
   public void testAssertionsEnabled() {
      try{
         assert false;
         fail("Assertions must be enabled for these tests.  Run java with the -ea option.");
      } catch (AssertionError ae) {
         //expected
         return;
      }
   }

   public void testConfiguration() throws IOException {

      //check it's loaded a value
      String fruit = SimpleConfig.getProperty("TEST.FRUIT");
      assertEquals("APPLE", fruit );

      //check default is NOT returned if there IS a value
      fruit = SimpleConfig.getProperty("TEST.FRUIT", "Banana");
      assertEquals("Should return APPLE as it's defined", fruit, "APPLE");

      //check default IS returned if there is NOT a value
      fruit = SimpleConfig.getProperty("TEST.CRASH.DUMMY", "Hatstand");
      assertEquals("Should return default HATSTAND", fruit, "Hatstand");

      //check we can set a property
      SimpleConfig.setProperty("TMP.TESTSET", "Hovercraft");
      fruit = SimpleConfig.getProperty("TMP.TESTSET");
      assertEquals("set property didn't", fruit, "Hovercraft");

   }
}
