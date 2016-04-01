package org.astrogrid.registry.webapp;

import java.io.*;
import java.util.Iterator;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmldb.api.base.ResourceSet;

import org.codehaus.xfire.util.STAXUtils;


import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.query.ISearch;

import java.util.HashMap;

import junit.framework.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.astrogrid.config.Config;
import javax.xml.stream.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: RegistryQueryTest
 * Description: Tests out the various Query methods of the server side Registry.
 * @author Kevin Benson
 *
 */
public class RegistryQueryTest extends TestCase {
    
    /**
     * Our debug logger.
     *
     */
    //private static Log log = LogFactory.getLog(RegistryQueryTest.class);    
    
    public RegistryQueryTest() {
        
    }
    
    DocumentBuilder builder;
    ISearch rqs;
    ISearch rqsv1_0;
    /**
     * Setup our test.
     *
    */ 
    public void setUp() throws Exception {
        super.setUp();
        //log.debug("debug setup");
        //log.info("info setup");
        System.out.println("setup in registryquerytest");
        File fi = new File("target/test-classes/conf.xml");
        Properties props = new Properties();
        props.setProperty("create-database", "true");
        props.setProperty("configuration",fi.getAbsolutePath());      
        if(fi != null) {
          XMLDBManager.registerDB(props);
        }
        
        rqs = QueryFactory.createQueryService("0.1");
        rqsv1_0 = QueryFactory.createQueryService("1.0");
        
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    builder = dbf.newDocumentBuilder();        
    }
    
    /**
     * Method: testSearchByIdent
     * Description: test to perform an adql query for an identifier known in the registry.
     * @throws Exception standard junit exception to be thrown.
    */         
    public void testADQLSearchv0_10_1() throws Exception {
    	System.out.println("start testADQLSearchv0_10_1");
        Document adql = askQueryFromFile("QueryForIdentifier--adql-v0.7.4.xml");
        XMLStreamReader reader = rqs.Search(adql);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testADQLSearchv0_10_1");
    }
    
    /**
     * Method: testSearchByIdent
     * Description: test to perform an adql query for an identifier known in the registry.
     * @throws Exception standard junit exception to be thrown.
    */         
    public void testADQLSearchv1_0_1() throws Exception {
    	System.out.println("start testADQLSearchv1_0_1");
        Document adql = askQueryFromFile("QueryForIdentifier--adql-v0.7.4-1.0.xml");
        XMLStreamReader reader = rqsv1_0.Search(adql);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testADQLSearchv1_0_1");        
    }
    
    /**
     * Method: testSearchByLike
     * Description: test to perform an adql query for an identifier known in the registry.
     * @throws Exception standard junit exception to be thrown.
    */         
    public void testADQLSearchv1_0_2() throws Exception {
    	System.out.println("start testADQLSearchv1_0_2");
        Document adql = askQueryFromFile("QueryForLike2--adql-v1.0-1.0.xml");
        XMLStreamReader reader = rqsv1_0.Search(adql);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testADQLSearchv1_0_2");        
    }
    
    /**
     * Method: testSearchByLike
     * Description: test to perform an adql query for an identifier known in the registry.
     * @throws Exception standard junit exception to be thrown.
    */         
    public void testADQLSearchv1_0_3() throws Exception {
    	System.out.println("start testADQLSearchv1_0_2");
        Document adql = askQueryFromFile("QueryForLike--adql-v1.0-1.0.xml");
        XMLStreamReader reader = rqsv1_0.Search(adql);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testADQLSearchv1_0_2");        
    }
    
    

    /**
     * Method: testKeywordQueryService
     * Description: test to perform a keyword search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
     * */ 
    public void testKeywordQueryServicev0_10_1() throws Exception {
    	System.out.println("start testKeywordQueryServicev0_10_1");
        Document keywordDoc = DomHelper.newDocument("<KeywordSearch><keywords>Full</keywords></KeywordSearch>");
        XMLStreamReader reader = rqs.KeywordSearch(keywordDoc);
	    Document doc = STAXUtils.read(builder,reader,true);
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testKeywordQueryServicev0_10_1");        
    }
    
    /**
     * Method: testKeywordQueryService
     * Description: test to perform a keyword search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
     * */ 
    public void testKeywordQueryServicev1_0_1() throws Exception {
    	System.out.println("start testKeywordQueryServicev1_0_1");
        Document keywordDoc = DomHelper.newDocument("<KeywordSearch><keywords>Imaging</keywords></KeywordSearch>");
        XMLStreamReader reader = rqsv1_0.KeywordSearch(keywordDoc);
	    Document doc = STAXUtils.read(builder,reader,true);
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testKeywordQueryServicev1_0_1");        
    }
    
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_1() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_1");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  for $x in //RootResource where $x/identifier = 'ivo://registry.test/registry' return $x</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv1_0_1");
    }
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_2() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_2");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource[identifier = 'ivo://registry.test/registry']</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv1_0_2");
    }  
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_3() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_3");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv1_0_3");
    }
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_4() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_4");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; " +
        "//RootResource[(matches(title,'Imaging','i') or matches(identifier,'Imaging','i') or matches(shortName,'Imaging','i') or matches(content/subject,'Imaging','i') or matches(content/description,'Imaging','i')) and @status='active']" +		"</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);
	    if(doc == null) {
	    	System.out.println("yes it was null why");
	    }
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv1_0_4");
    }     
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_5() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_5");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; " +
        "//RootResource[(match-all(title,'Imaging') or match-all(identifier,'Imaging') or match-all(shortName,'Imaging') or match-all(content/subject,'Imaging') or match-all(content/description,'Imaging')) and @status='active']" +		"</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv1_0_5");
    } 
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv1_0_6() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_6");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource[identifier = 'ivo://nottobefound']</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
        assertTrue(doc.getDocumentElement().getLocalName().equals("XQuerySearchResponse"));        
        System.out.println("done testXQuerySearchv1_0_6");
    }      
    
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv0_10_1() throws Exception {
    	System.out.println("start testXQuerySearchv0_10_1");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\"; declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\"; for $x in //RootResource where $x/vr:identifier = 'ivo://registry.test/org.astrogrid.registry.RegistryService' return $x</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqs.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv0_10_1");
    }
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv0_10_2() throws Exception {
        System.out.println("start testXQuerySearchv0_10_2");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\"; declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\"; for $x in //RootResource return $x</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqs.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv0_10_2");
    }    

    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv0_10_3() throws Exception {
    	System.out.println("start testXQuerySearchv0_10_3");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\"; declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\";" +
        "//RootResource[(matches(vr:title,'main','i') or matches(vr:identifier,'main','i') or matches(vr:shortName,'main','i') or matches(vr:content/vr:subject,'main','i') or matches(vr:content/vr:description,'main','i')) and @status='active']" +		"</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqs.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv0_10_3");
    }     
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv0_10_4() throws Exception {
        System.out.println("start testXQuerySearchv0_10_4");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\"; declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\"; for $x in //RootResource where $x/vr:identifier = 'nothingto be found' return $x</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqs.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
        assertTrue(doc.getDocumentElement().getLocalName().equals("XQuerySearchResponse"));        
        System.out.println("done testXQuerySearchv0_10_4");
    }    

    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */       
    public void testXQuerySearchv0_10_5() throws Exception {
    	System.out.println("start testXQuerySearchv0_10_5");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace vr = \"http://www.ivoa.net/xml/VOResource/v0.10\"; declare namespace vor=\"http://www.ivoa.net/xml/RegistryInterface/v0.1\";" +
        "//RootResource[(match-all(vr:title,'main') or match-all(vr:identifier,'main') or match-all(vr:shortName,'main') or match-all(vr:content/vr:subject,'main') or match-all(vr:content/vr:description,'main')) and @status='active']" +		"</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqs.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        assertNotNull(doc);
        assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testXQuerySearchv0_10_5");
    }

    
    /**
     * Method: testGetResourceByIdentifier
     * Description: test to perform a query for one particular identifier in the registry not based on adql.
     * @throws Exception standard junit exception to be thrown.
     * 
     */
    public void testGetResourcev0_10_1() throws Exception {        
         System.out.println("begin testGetResourcev0_10_1");
         Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://registry.test/org.astrogrid.registry.RegistryService</identifier></GetResource>");
         XMLStreamReader reader = rqs.GetResource(inputDoc);
 	     Document doc = STAXUtils.read(builder,reader,true);         
         assertNotNull(doc);
         assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 1));
         System.out.println("done testGetResourcev0_10_1");         
     }
    
    /**
     * Method: testGetResourceByIdentifier
     * Description: test to perform a query for one particular identifier in the registry not based on adql.
     * @throws Exception standard junit exception to be thrown.
     * 
     */
    public void testGetResourcev1_0_1() throws Exception {        
         System.out.println("begin testGetResourcev1_0_1");
         Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://registry.test/registry</identifier></GetResource>");
         XMLStreamReader reader = rqsv1_0.GetResource(inputDoc);
 	     Document doc = STAXUtils.read(builder,reader,true);         
         assertNotNull(doc);
         assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 1));
         System.out.println("done testGetResourcev1_0_1");         
     }
    
    
    /**
     * Method: testGetResourceByIdentifier
     * Description: test to perform a query for one particular identifier in the registry not based on adql.
     * @throws Exception standard junit exception to be thrown.
     */     
    public void testGetResourceNotFoundv0_10_1() throws Exception {
         System.out.println("begin testGetResourceNotFoundv0_10_1");
         Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://nothing/helloworld</identifier></GetResource>");
         XMLStreamReader reader = rqs.GetResource(inputDoc);
 	     Document doc = STAXUtils.read(builder,reader,true);         
         assertNotNull(doc);
         assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
         assertEquals(doc.getDocumentElement().getLocalName(),"Fault");
         System.out.println("done testGetResourceNotFoundv1_0_1");         
     }
    
    /**
     * Method: testGetResourceByIdentifier
     * Description: test to perform a query for one particular identifier in the registry not based on adql.
     * @throws Exception standard junit exception to be thrown.
     */     
    public void testGetResourceNotFoundv1_0_1() throws Exception {
         System.out.println("begin testGetResourceNotFoundv1_0_1");
         Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://nothing/helloworld</identifier></GetResource>");
         XMLStreamReader reader = rqsv1_0.GetResource(inputDoc);
 	     Document doc = STAXUtils.read(builder,reader,true);         
         assertNotNull(doc);
         assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
         assertEquals(doc.getDocumentElement().getLocalName(),"Fault");
         System.out.println("done testGetResourceNotFoundv1_0_1");         
     }    
    
         
    /**
     * Method: askQueryFromFile
     * Description: Obtains a File on the local system as a inputstream and feeds it into a Document DOM object to be
     * returned.
     * @param queryFile - File name of a xml resource.
     * @return Document DOM object of a XML file.
     * @throws Exception  Any IO Exceptions or DOM Parsing exceptions could be thrown.
     */       
    protected Document askQueryFromFile(String queryFile) throws Exception {
        assertNotNull(queryFile);
        InputStream is = this.getClass().getResourceAsStream(queryFile);
        
        assertNotNull("Could not open query file :" + queryFile,is);
        Document queryDoc = DomHelper.newDocument(is);
        
        //Document queryDoc = DomHelper.newDocument(new File(queryFile));
        return queryDoc;
    }    
}