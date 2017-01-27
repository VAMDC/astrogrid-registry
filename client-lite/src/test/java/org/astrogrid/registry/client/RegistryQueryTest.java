package org.astrogrid.registry.client;


import org.apache.axis.utils.XMLUtils; 
import org.w3c.dom.Document;
import org.astrogrid.registry.client.query.v1_0.RegistryService;
import org.astrogrid.store.Ivorn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Class: RegistryQueryTest
 * Description: Unit tests to test out querying ability of the registry, since this is a unit test and is not
 * connected up to network/internet the tests will be conducted on a certain set of xml resources local to the
 * file system.  This also means we can only test out getResourceByIdentifer type methods *only*.  See 
 * integrationtests for connection to the web service endpoint to carry out adql and xquery type queries.
 * @author Kevin Benson
 *
 */
public class RegistryQueryTest { 

   /**
    * Switch for our debug statements.
    *
    */
   private static final boolean DEBUG_FLAG = true ;
   
   /**
    * Registry Service interface to all the query methods.
    */
   RegistryService rs = null;
   
   /**
    * base directory of stored sample files and where files will be stored to when adding/updating the
    * registry.
    */
   String regTestBase = null;

   /**
    * Setup our test.
    *
   * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception {
       if (DEBUG_FLAG) System.out.println("") ;
       if (DEBUG_FLAG) System.out.println("----\"----") ;
       if (DEBUG_FLAG) System.out.println("RegistryQueryJunit:setup()") ;
       //conf = org.astrogrid.config.SimpleConfig.getSingleton();

       regTestBase = System.getProperty("regtest.basedir");
       System.out.println("Property for regtest.basedir = " + regTestBase);
       
       RegistryDelegateFactory.conf.setProperty("registry.cache.dir",regTestBase);                    
       rs = RegistryDelegateFactory.createQueryv1_0();
       if (DEBUG_FLAG) System.out.println("----\"----");
   }
   
   /**
    * Method: testGetResource
    * Description: Query for a particular resource based on a String version of the identifier.
    * @throws Exception standard junit exception to be thrown.
    */
   @Test
   @Ignore
   public void testGetResource() throws Exception {
      if(DEBUG_FLAG) System.out.println("entered testGetResource");
      String ident = "ivo://org.test/org.astrogrid.registry.RegistryService";      
      Document doc = rs.getResourceByIdentifier(ident);
      Assert.assertNotNull(doc);
      if(DEBUG_FLAG) System.out.println("received in junit test = " + XMLUtils.DocumentToString(doc));
      System.out.println("exiting testGetResource");
   }   

   /**
    * Method: testGetResource2
    * Description: Query for a particular resource based on a Ivorn version of the identifier.
    * @throws Exception standard junit exception to be thrown.
    */   
   @Test
   @Ignore
   public void testGetResource2() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetResource2"); 
       Ivorn ivorn = new Ivorn("ivo://org.test/org.astrogrid.registry.RegistryService");
       Document doc = rs.getResourceByIdentifier(ivorn);
       Assert.assertNotNull(doc);
       if(DEBUG_FLAG) System.out.println("received in junit test = " + XMLUtils.DocumentToString(doc));
       System.out.println("exiting testGetResource2");
    }   

   /**
    * Method: testGetEndPoint
    * Description: Query for a particular resource which extracts a url as a string 
    * based on a given String identifier for the query.
    * @throws Exception standard junit exception to be thrown.
    */   
   @Test
   @Ignore
   public void testGetEndPoint() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetEndPoint");
       String ident = "ivo://org.test/org.astrogrid.registry.RegistryService"; 
       String url = rs.getEndPointByIdentifier(ident);
       Assert.assertNotNull(url);
       Assert.assertTrue(url.startsWith("http"));
       if(DEBUG_FLAG) System.out.println("url = " + url);
       System.out.println("exiting testGetEndPoint");
    }   

   /**
    * Method: testGetEndPoint
    * Description: Query for a particular resource which extracts a url as a string 
    * based on a given Ivorn identifier for the query.
    * @throws Exception standard junit exception to be thrown.
    */      
   @Test
   @Ignore
   public void testGetEndPoint2() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetEndPoint2");
       Ivorn ivorn = new Ivorn("ivo://org.test/org.astrogrid.registry.RegistryService");
       String url = rs.getEndPointByIdentifier(ivorn);
       Assert.assertNotNull(url);
       Assert.assertTrue(url.startsWith("http"));       
       if(DEBUG_FLAG) System.out.println("url = " + url);
       System.out.println("exiting testGetEndPoint2");
    }
   
}
