package org.astrogrid.registry.client;


import java.net.URL; 
import java.util.Vector; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException; 
import org.apache.axis.utils.XMLUtils; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList;
import org.w3c.dom.Element; 
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import junit.framework.*;
import java.io.File;
import java.util.Date;
import org.astrogrid.oldquery.sql.Sql2Adql;
import org.astrogrid.registry.client.RegistryDelegateFactory;
import org.astrogrid.registry.client.query.v1_0.RegistryService;
import java.util.*;
import org.astrogrid.registry.RegistryException;
import org.astrogrid.config.Config;
import org.astrogrid.store.Ivorn;


/**
 * Class: RegistryQueryTest
 * Description: Unit tests to test out querying ability of the registry, since this is a unit test and is not
 * connected up to network/internet the tests will be conducted on a certain set of xml resources local to the
 * file system.  This also means we can only test out getResourceByIdentifer type methods *only*.  See 
 * integrationtests for connection to the web service endpoint to carry out adql and xquery type queries.
 * @author Kevin Benson
 *
 */
public class RegistryQueryTest extends TestCase { 

   /**
    * Switch for our debug statements.
    *
    */
   private static boolean DEBUG_FLAG = true ;
   
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
    */
   public void setUp() throws Exception {
       super.setUp() ;
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
   
   public void testconvertSQLToADQL() throws Exception {
       System.out.println("enter testSADQLOne");
       String sql = "Select * from Registry where ((vr:title like 'sextractor' or vr:description " +      
       " like 'sextractor' or vr:identifier like 'sextractor' or vr:shortName like " + 
       " 'sextractor' or vr:subject like 'sextractor')) and  ( ((@xsi:type like " + 
       " '%CeaApplicationType'  or @xsi:type like '%CeaHttpApplicationType'  ) and " + 
       " @status = 'active')) ";   
       String adqlString = Sql2Adql.translateToAdql074(sql);
       System.out.println("the adql string = " + adqlString);
       
       System.out.println("exit testSADQLOne");
   }
   
   
   /**
    * Method: testGetResource
    * Description: Query for a particular resource based on a String version of the identifier.
    * @throws Exception standard junit exception to be thrown.
    */
   public void testGetResource() throws Exception {
      if(DEBUG_FLAG) System.out.println("entered testGetResource");
      String ident = "ivo://org.test/org.astrogrid.registry.RegistryService";      
      Document doc = rs.getResourceByIdentifier(ident);
      assertNotNull(doc);
      if(DEBUG_FLAG) System.out.println("received in junit test = " + XMLUtils.DocumentToString(doc));
      System.out.println("exiting testGetResource");
   }   

   /**
    * Method: testGetResource2
    * Description: Query for a particular resource based on a Ivorn version of the identifier.
    * @throws Exception standard junit exception to be thrown.
    */   
   public void testGetResource2() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetResource2"); 
       Ivorn ivorn = new Ivorn("ivo://org.test/org.astrogrid.registry.RegistryService");
       Document doc = rs.getResourceByIdentifier(ivorn);
       assertNotNull(doc);
       if(DEBUG_FLAG) System.out.println("received in junit test = " + XMLUtils.DocumentToString(doc));
       System.out.println("exiting testGetResource2");
    }   

   /**
    * Method: testGetEndPoint
    * Description: Query for a particular resource which extracts a url as a string 
    * based on a given String identifier for the query.
    * @throws Exception standard junit exception to be thrown.
    */   
   public void testGetEndPoint() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetEndPoint");
       String ident = "ivo://org.test/org.astrogrid.registry.RegistryService"; 
       String url = rs.getEndPointByIdentifier(ident);
       assertNotNull(url);
       assertTrue(url.startsWith("http"));
       if(DEBUG_FLAG) System.out.println("url = " + url);
       System.out.println("exiting testGetEndPoint");
    }   

   /**
    * Method: testGetEndPoint
    * Description: Query for a particular resource which extracts a url as a string 
    * based on a given Ivorn identifier for the query.
    * @throws Exception standard junit exception to be thrown.
    */      
   public void testGetEndPoint2() throws Exception {
       if(DEBUG_FLAG) System.out.println("entered testGetEndPoint2");
       Ivorn ivorn = new Ivorn("ivo://org.test/org.astrogrid.registry.RegistryService");
       String url = rs.getEndPointByIdentifier(ivorn);
       assertNotNull(url);
       assertTrue(url.startsWith("http"));       
       if(DEBUG_FLAG) System.out.println("url = " + url);
       System.out.println("exiting testGetEndPoint2");
    }
   
}
