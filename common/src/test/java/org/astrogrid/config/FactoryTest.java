package org.astrogrid.config;


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Abstract JUnit test case for ConfigurableTest
 * - extend to test implementations of Configurable.
 */

public class FactoryTest extends TestCase {

   /** Logger */
   private static Log log = LogFactory.getLog(FactoryTest.class);

   /**
    * Tests property creation
    */
   public void testPropertyMaker() throws IOException {
        log.trace("enter:testPropertyMaker");
      Config configA = ConfigFactory.getConfig("Arthur");
      log.debug("Got Arthur "+configA);
      assertNotNull(configA);
      
      Config configB = ConfigFactory.getConfig("Bernard");
      log.debug("Got Bernard "+ configB);
      assertNotNull(configB);
      assertNotSame(configA, configB);

      configA.setProperty("Fruit", "Banana");
      configB.setProperty("Fruit", "Apple");
      assertNotSame(configA.getProperty("Fruit"), configB.getProperty("Fruit"));
      
      Config configC = ConfigFactory.getConfig("Arthur");
      log.debug("Got Charlie "+configC);
      assertNotNull(configC);
      assertEquals(configA, configC);
   }


    /**
     * Assembles and returns a test suite made up of all the testXxxx() methods
      * of this class.
     */
    public static Test suite() {
        // Reflection is used here to add all the testXXX() methods to the suite.
        return new TestSuite(FactoryTest.class);
    }

    /**
     * Runs the test case.
     */
    public static void main(String args[])
    {
       junit.textui.TestRunner.run(suite());
    }
}
