/*
 * $Id: FailbackTest.java,v 1.6 2004/06/17 17:34:08 jdt Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid
 * Software License version 1.2, a copy of which has been included
 * with this distribution in the LICENSE.txt file.
 *
 */

package org.astrogrid.config;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import org.junit.Test;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.Before;

/**
 * Tests config class.
 */

public class FailbackTest
{
   private static final String testPropertyFile = "test.properties";
   private static final String nonExistentFile = "testNotHere.properties";
   
   

   @Test
   public void testLoad() throws IOException
   {
      FailbackConfig config = new FailbackConfig();
      
      //force initialisation - and it will barf if this key does not exist
      try {
         config.getProperty("TEST.FRUIT");
      }
      catch (ConfigException ioe) {
         Assert.fail("Should have been able to find TEST.FRUIT");
      }
      
      try {
         config.setConfigFilename(testPropertyFile);
         
         Assert.fail("Should not be able to set config filename after initialisation");
      }
      catch (ConfigException ioe) {} //fine do nothing

   }

   /**
    * Some keys are valid, some are not - this tests that the assertions for
    * invalid keys are working
    */
   @Test
   public void testPropertyKeysValid()
   {
      FailbackConfig config = new FailbackConfig();
         
      try {
         config.getProperty("wibble:wibble");
         Assert.fail("Should have thrown an assertion error given a key with a colon in ");
      }
      catch (AssertionError ae) {}
         
      try {
         config.getProperty("wibble wibble");
         Assert.fail("Should have thrown an assertion error given a key with a space in ");
      }
      catch (AssertionError ae) {}
         
      try {
         config.getProperty("wibble=wibble");
         Assert.fail("Should have thrown an assertion error given a key with an equals in ");
      }
      catch (AssertionError ae) {}
         
   }

   /**
    * Gets all the keys
    */
   @Test
   public void testKeySet() {
      FailbackConfig config = new FailbackConfig();
      
      Set keys = config.keySet();
   }
   
   /**
    * These tests require assertions to be enabled.
    * java must be run with the -ea option.
    */
   @Test
   public void testAssertionsEnabled() {
      try {
         assert false;
         Assert.fail("Assertions must be enabled for these tests.  Run java with the -ea option.");
      } catch (AssertionError ae) {
         //expected
         return;
      }
   }

   /**
    * test that properties are got and sot propertly...
    */
   @Test
   public void testStore() throws IOException
   {
      FailbackConfig config = new FailbackConfig();
         
      //check it's loaded a value
      String fruit = config.getProperty("TEST.FRUIT").toString();
      Assert.assertEquals("Didn't get right result from '"+testPropertyFile+"'", fruit, "APPLE");

      //check default is NOT returned if there IS a value
      fruit = config.getProperty("TEST.FRUIT", "Banana").toString();
      Assert.assertEquals("Should return APPLE as it's defined", fruit, "APPLE");

      //check default IS returned if there is NOT a value
      fruit = config.getProperty("TEST.CRASH.DUMMY", "Hatstand").toString();
      Assert.assertEquals("Should return default HATSTAND", fruit, "Hatstand");

      //check we can set a property
      config.setProperty("TMP.TESTSET", "Hovercraft");
      fruit = config.getProperty("TMP.TESTSET").toString();
      Assert.assertEquals("set property didn't", fruit, "Hovercraft");

      //check if we get a nonexistent property it throws exception
      try {
         fruit = config.getString("TEST.CRASH.DUMMY");
         Assert.fail("Did not throw exception for missing property");
      }
      catch (PropertyNotFoundException pnfe) {} //good, supposed to
      
   }

   /**
    * test property types
    */
   @Test
   public void testTypes() throws IOException
   {
      FailbackConfig config = new FailbackConfig();
         
      String okUrl = "http://www.google.com";
      
      //set some properties so we *know* the types are correct
      config.setProperty("TEST.URL.OK", okUrl);
      config.setProperty("TEST.URL.BAD", "bad:/very.bad");
      
      config.setProperty("TEST.INT", "12  "); //includes spaces as this should not cause problems

      //check URL gets expected
      Assert.assertEquals("Unexpected value", okUrl, config.getUrl("TEST.URL.OK").toString());
      
      //check bad urls throw exception
      try {
         config.getUrl("TEST.URL.BAD");
         Assert.fail("Did not throw exception for bad url");
      }
      catch (ConfigException ce) {
         if (!(ce.getCause() instanceof MalformedURLException)) {
            Assert.fail("Unexpected failure reading bad url");
         }
      }
      
      //check int gets expected
      Assert.assertEquals("Unexpected value", 12, config.getInt("TEST.INT"));
      
      //check bad int throws exception
      try {
         config.getInt("TEST.URL.BAD");
         Assert.fail("Did not throw exception for bad int");
      }
      catch (ConfigException ce) {
         if (!(ce.getCause() instanceof NumberFormatException)) {
            Assert.fail("Unexpected failure reading bad int");
         }
      }

      //check strings work OK...
      Assert.assertEquals("Unexpected value", "APPLE", config.getString("TEST.FRUIT"));

      //..even for non-strings
      config.setProperty("TEST.URL.INTOBJ", new Integer(12));
      Assert.assertEquals("Unexpected value", "12", config.getString("TEST.URL.INTOBJ"));
      
      //check returns default OK
      Assert.assertEquals(5, config.getInt("TEST.NOT.THERE", 5));
      
      //check returns default OK
      Assert.assertEquals(new URL("http://something/path"), config.getUrl("TEST.NOT.THERE", new URL("http://something/path")));
      
      //check returns default OK
      Assert.assertEquals("Hi there", config.getString("TEST.NOT.THERE", "Hi there"));
      
   }

}


