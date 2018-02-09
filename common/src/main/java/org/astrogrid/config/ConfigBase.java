/*
 * $Id: ConfigBase.java,v 1.2 2008/09/17 08:16:05 pah Exp $
 * 
 * Created on May 2, 2005 by Paul Harrison (pharriso@eso.org)
 * Copyright 2005 ESO. All rights reserved.
 *
 * This software is published under the terms of the ESO 
 * Software License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */

package org.astrogrid.config;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Base Class for all config stores. It is indended that each module creates a
 * class derived from this one that will list all of the configuration
 * properties that the module will need. This centralization is an attempt to
 * make adminsitration easier later. This provides a decorator on the standard
 * 
 * @link org.eso.vos.config.Config behavior that was inherited from AG. It is
 *       simpler and will point out programming errors sooner....
 * @author Paul Harrison (pharriso@eso.org) May 2, 2005
 * @version $Name:  $
 * @since initial Coding
 * @TODO - see if can use reflection to automatically test for all the staticly
 *       defined PropertyKeys - would make it easier to program with this class
 */
public abstract class ConfigBase implements KeyUse {
   static private Config config = ConfigFactory.getConfig("dummy");

   // underlying store is a treemap to make the unit tests easier - was a
   // treeset before, but that involved some strange stuff with creating temp
   // @link PropertyKey objects - the original idea was that a propertykey would
   // be an immutable object....
   private final Map keys = new TreeMap();

   /*
    * (non-Javadoc)
    * 
    * @see org.eso.vos.config.KeyUse#listkeys(java.io.PrintWriter)
    */
   public void listkeys(PrintWriter out)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException(
            "ConfigBase.listkeys() not implemented");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eso.vos.config.KeyUse#registerKey(org.eso.vos.config.Keys)
    */
   public void registerKey(PropertyKey key)
   {
      keys.put(key.getKey(), key);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eso.vos.config.KeyUse#getProperty(org.eso.vos.config.PropertyKey)
    */
   public String getProperty(PropertyKey key)
   {
      // REFACTORME could use containsValue here to be truer to the original
      // idea..?
      if (keys.containsKey(key.getKey())) {
         return (String) config.getProperty(key.getKey());
      }
      else {
         throw new KeyNotRegisteredException(key.getKey()
               + "has not been registered - programming error");
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eso.vos.config.KeyUse#setProperty(java.lang.String,
    *      java.lang.String)
    */
   public void setProperty(PropertyKey key, String val)
   {
      config.setProperty(key.getKey(), val);
   }

   protected abstract TestSuite validationTests();

   public TestSuite getInstallationTestSuite()
   {

      TestSuite theSuite = new TestSuite("Config tests");

      for (Iterator iter = keys.keySet().iterator(); iter.hasNext();) {
         String element = (String) iter.next();
         theSuite.addTest(new ParameterSetTest(element));
      }
      TestSuite additionalTests = validationTests();
      if (additionalTests != null) {
         theSuite.addTest(additionalTests);
      }
      return theSuite;

   }

   /**
    * @author Paul Harrison (pharriso@eso.org) 24-Aug-2005
    * @version $Name:  $
    * @since initial Coding
    */
   public final class ParameterSetTest extends TestCase {

      /**
       * @param arg0
       */
      public ParameterSetTest(String arg0) {
         super(arg0);

      }

      /**
       * Override the default so that the same method is always used. A little
       * hack in this class is that the name of the test is used to choose which
       * parameter to test against
       * 
       * @see junit.framework.TestCase#runTest()
       */
      protected void runTest() throws Throwable
      {
         testSet();
      }

      public void testSet()
      {

         // the following should never happen - but just in case ;-)
         assertTrue("key " + getName() + " does not exist",
               keys.containsKey(getName()));
         PropertyKey key = (PropertyKey) keys.get(getName());
         assertNotNull(key);
         // now do the real test
         String value = (String) config.getProperty(key.getKey());
         assertNotNull("the property is not set", value);
         assertTrue("property " + getName() + " has no content",
               value.length() > 0);

      }

   }

}

/*
 * $Log: ConfigBase.java,v $
 * Revision 1.2  2008/09/17 08:16:05  pah
 * result of merge of pah_community_1611 branch
 *
 * Revision 1.1.2.1  2008/05/17 20:55:13  pah
 * safety checkin before interop
 *
 * Revision 1.2  2005/08/24 15:44:09  pharriso
 * added some default tests for running as installation tests
 * Revision 1.1 2005/07/08 13:11:08 pharriso make
 * propertylookup keys have to be preregistered and provide a simpler interface
 * to properties through the KeyUse interface.
 * 
 * Revision 1.1 2005/05/13 11:52:20 pharriso started to separate out the
 * interface with view to making proper factory added some unit tests.
 * 
 */
