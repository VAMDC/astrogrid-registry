package org.astrogrid.registry.webapp;

import java.io.*;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;

import org.codehaus.xfire.util.STAXUtils;


import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.query.ISearch;

import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.stream.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Class: RegistryQueryTest
 * Description: Tests out the various Query methods of the server side Registry.
 * @author Kevin Benson
 *
 */
public class RegistryQueryTest {
    
    public RegistryQueryTest() {
    }
    
    DocumentBuilder builder;
    ISearch rqsv1_0;
    
    /**
     * Setup our test.
     *
   * @throws java.lang.Exception
    */ 
    @Before
    public void setUp() throws Exception {
        //log.debug("debug setup");
        //log.info("info setup");
        System.out.println("setup in registryquerytest");
        File fi = new File("src/test/resources/conf.xml");
        Properties props = new Properties();
        props.setProperty("create-database", "true");
        props.setProperty("configuration",fi.getAbsolutePath());      
        XMLDBManager.registerDB(props);
        
        rqsv1_0 = QueryFactory.createQueryService("1.0");
        
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    builder = dbf.newDocumentBuilder();        
    }
    
    
    /**
     * Method: testKeywordQueryService
     * Description: test to perform a keyword search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
     * */ 
    @Test
    public void testKeywordQueryServicev1_0_1() throws Exception {
    	System.out.println("start testKeywordQueryServicev1_0_1");
        Document keywordDoc = DomHelper.newDocument("<KeywordSearch><keywords>Imaging</keywords></KeywordSearch>");
        XMLStreamReader reader = rqsv1_0.KeywordSearch(keywordDoc);
	    Document doc = STAXUtils.read(builder,reader,true);
        Assert.assertNotNull(doc);
        Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
        System.out.println("done testKeywordQueryServicev1_0_1");        
    }
    
    
    /**
     * Method: testXQuerySearch
     * Description: test to perform a xquery search on the registry via the use of the common 
     * web service interface method.
     * @throws Exception standard junit exception to be thrown.
    */
    @Test
    public void testXQuerySearchv1_0_1() throws Exception {
    	System.out.println("start testXQuerySearchv1_0_1");
        Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  for $x in //RootResource where $x/identifier = 'ivo://registry.test/registry' return $x</XQuery></XQuerySearch>");
        XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
	    Document doc = STAXUtils.read(builder,reader,true);        
        Assert.assertNotNull(doc);
        Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
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
        Assert.assertNotNull(doc);
        Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
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
        Assert.assertNotNull(doc);
        Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
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
      Assert.assertNotNull(doc);
      Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
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
      Assert.assertNotNull(doc);
      Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() > 0));
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
      Assert.assertNotNull(doc);
      Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
      Assert.assertTrue(doc.getDocumentElement().getLocalName().equals("XQuerySearchResponse"));        
      System.out.println("done testXQuerySearchv1_0_6");
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
      Assert.assertNotNull(doc);
      printDocument(doc);
      Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 1));
      System.out.println("done testGetResourcev1_0_1");         
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
       Assert.assertNotNull(doc);
       Assert.assertTrue((doc.getElementsByTagNameNS("*","Resource").getLength() == 0));
       Assert.assertEquals(doc.getDocumentElement().getLocalName(),"Fault");
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
        Assert.assertNotNull(queryFile);
        InputStream is = this.getClass().getResourceAsStream(queryFile);
        
        Assert.assertNotNull("Could not open query file :" + queryFile,is);
        Document queryDoc = DomHelper.newDocument(is);
        
        //Document queryDoc = DomHelper.newDocument(new File(queryFile));
        return queryDoc;
    }    
    
  private void printDocument(Document doc) throws IOException {
    OutputFormat format = new OutputFormat(doc);
    format.setIndenting(true);
    XMLSerializer serializer = new XMLSerializer(System.out, format);
    serializer.serialize(doc);
  }
}