package org.astrogrid.registry.server.query;

import java.io.File;
import java.util.List;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.astrogrid.registry.TestRegistry;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.admin.v1_0.RegistryAdminService;
import org.astrogrid.registry.server.query.v1_0.RegistryQueryService;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.registry.RegistryNamespaceContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;

/**
 * Class: RegistryQueryTest Description: Tests out the various Query methods of
 * the server side Registry.
 *
 * @author Kevin Benson
 *
 */
public class RegistryQueryTest {
  
  private static final String COLLECTION_NAME = "astrogridv1_0";

  public RegistryQueryTest() {
  }

  DocumentBuilder builder;
  RegistryQueryService rqsv1_0;

  /**
   * Setup our test.
   *
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    System.out.println("setup in registryquerytest");

    // Make an empty registry.
    File target = new File("target");
    TestRegistry.installRegistry(target);
    TestRegistry.emptyRegistry(target);
    TestRegistry.configure(target);

    // Add a self-registration.
    RegistryAdminService rasv1_0 = new RegistryAdminService();
    Document resultDoc1 = rasv1_0.updateInternal(RegistryDOMHelper.documentFromResource("/xml/ARegistryv1_0.xml"));
    Assert.assertEquals("UpdateResponse", resultDoc1.getDocumentElement().getLocalName());

    // Add a resource to support the keyword-query test
    Document adilRegistration = RegistryDOMHelper.documentFromResource("/xml/cdms.xml");
    Document resultDoc2 = rasv1_0.updateInternal(adilRegistration);
    Assert.assertEquals("UpdateResponse", resultDoc2.getDocumentElement().getLocalName());

    // Check that the resource went in and is accessible.
    XMLDBRegistry xdbRegistry = new XMLDBRegistry();
    ResourceSet rs = xdbRegistry.query("/", COLLECTION_NAME);
    Assert.assertNotNull(rs);
    
    Resource r = xdbRegistry.getResource("registry_test_cdms", COLLECTION_NAME);
    Assert.assertNotNull(r);

    rqsv1_0 = new RegistryQueryService();

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    builder = dbf.newDocumentBuilder();
  }
  
  @After
  public void shutDownDatabase() throws Exception {
    TestRegistry.shutDown();
  }
  

  @Test
  public void testKeywordQueryLowLevel() throws Exception {
    //String xql = "<KeywordSearch><keywords>spectroscopy</keywords></KeywordSearch>";
    String xql = "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance';"
               + " declare namespace agr='urn:astrogrid:schema:RegistryStoreResource:v1';"
               + " declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0';"
               + " let $hits := (//ri:Resource[(@status = 'active') and "
               + " ( ( near(identifier,'spectroscopy') or  near(content/description,'spectroscopy') "
               + " or  near(title,'spectroscopy') or  near(content/subject,'spectroscopy')) )] ) "
               + " return subsequence($hits,1,100)";
    System.out.println("Querying at low level");
    System.out.println(xql);
    XMLDBRegistry xdbRegistry = new XMLDBRegistry();
    ResourceSet rs = xdbRegistry.query(xql, "astrogridv1_0");
    System.out.println("Number of resources in result: " + rs.getSize());
    Assert.assertEquals(1, rs.getSize());
    
    // Having established that there is one resource, extract it as a DOM
    // for interrogation by XPath. This means parsing it as XML text and parsing
    // that text; the getContentAsDOM() method is no good as eXist's DOM
    // implementation is broken.
    Document resultDoc = RegistryDOMHelper.documentFromString((String) rs.getResource(0).getContent());
    System.out.println("Result document:");
    RegistryDOMHelper.printDocument(resultDoc, System.out);
    
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());
    
    XPathExpression q1 = xpath.compile("count(/ri:Resource)");
    Assert.assertEquals("1", q1.evaluate(resultDoc));
    
    XPathExpression q2 = xpath.compile("/ri:Resource/identifier");
    Assert.assertEquals("ivo://registry.test/cdms", q2.evaluate(resultDoc));
  }
  
  @Test
  public void testXqueryLowLevel() throws Exception {
    String xql = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0';"
               + " //ri:Resource[identifier='ivo://registry.test/cdms']";
    System.out.println("Querying at low level");
    System.out.println(xql);
    XMLDBRegistry xdbRegistry = new XMLDBRegistry();
    ResourceSet rs = xdbRegistry.query(xql, "astrogridv1_0");
    System.out.println("Number of resources in result: " + rs.getSize());
    Assert.assertEquals(1, rs.getSize());
    
    // Having established that there is one resource, extract it as a DOM
    // for interrogation by XPath. This means parsing it as XML text and parsing
    // that text; the getContentAsDOM() method is no good as eXist's DOM
    // implementation is broken.
    Document resultDoc = RegistryDOMHelper.documentFromString((String) rs.getResource(0).getContent());
    System.out.println("Result document:");
    RegistryDOMHelper.printDocument(resultDoc, System.out);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());

    XPathExpression q1 = xpath.compile("count(/ri:Resource)");
    Assert.assertEquals("1", q1.evaluate(resultDoc));

    XPathExpression q2 = xpath.compile("/ri:Resource/identifier");
    Assert.assertEquals("ivo://registry.test/cdms", q2.evaluate(resultDoc));
  }

  @Test
  public void testXQuerySearchv1_0_1() throws Exception {
    System.out.println("start testXQuerySearchv1_0_1");

    String xql
        = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';"
        + " for $x in //RootResource where $x/identifier = 'ivo://registry.test/cdms' "
        + " return $x";
    System.out.println(xql);

    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(1, docs.size());
    
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());

    XPathExpression q2 = xpath.compile("count(//ri:Resource)");
    Assert.assertEquals("1", q2.evaluate(docs.get(0)));
    
    XPathExpression q3 = xpath.compile("//ri:Resource/identifier");
    Assert.assertEquals("ivo://registry.test/cdms", q3.evaluate(docs.get(0)));
    System.out.println("done testXQuerySearchv1_0_1");
  }
  
  @Test
  public void testXQuerySearchv1_0_1a() throws Exception {
    System.out.println("start testXQuerySearchv1_0_1a");

    String xql
        = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';"
        + " for $x in //RootResource where $x/identifier = 'ivo://registry.test/cdms' "
        + " return $x";
    System.out.println(xql);

    String xml = rqsv1_0.xQueryToText(xql);
    Assert.assertNotNull(xml);
    System.out.println("done testXQuerySearchv1_0_1a");
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_2() throws Exception {
    System.out.println("start testXQuerySearchv1_0_2");
    String xql = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource[identifier = 'ivo://registry.test/registry']";
    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(1, docs.size());
    RegistryDOMHelper.printDocument(docs.get(0), System.out);
    Assert.assertTrue((docs.get(0).getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testXQuerySearchv1_0_2");
  }

  @Test
  public void testXQuerySearchv1_0_3() throws Exception {
    System.out.println("start testXQuerySearchv1_0_3");
    String xql = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource";
    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(2, docs.size());
    Assert.assertTrue((docs.get(0).getElementsByTagNameNS("*", "Resource").getLength() == 1));
    Assert.assertTrue((docs.get(1).getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testXQuerySearchv1_0_3");
  }
  
  @Test
  public void testXQuerySearchv1_0_3a() throws Exception {
    System.out.println("start testXQuerySearchv1_0_3");
    String xql = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource";
    String xml = rqsv1_0.xQueryToText(xql);
    Assert.assertNotNull(xml);
    System.out.println("done testXQuerySearchv1_0_3a");
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_4() throws Exception {
    System.out.println("start testXQuerySearchv1_0_4");
    String xql ="declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource[(matches(title,'Spectroscopy','i') or matches(identifier,'spectroscopy','i') or matches(shortName,'Imaging','i') or matches(content/subject,'spectroscopy','i') or matches(content/description,'Imaging','i')) and @status='active']";
    
    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(1, docs.size());
    
    Assert.assertTrue((docs.get(0).getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testXQuerySearchv1_0_4");
  }

  @Test
  public void testXQuerySearchv1_0_5() throws Exception {
    System.out.println("start testXQuerySearchv1_0_5");
    String xql ="declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; "
        + "//RootResource[(match-all(title,'Bogus') or match-all(identifier,'Bogus') or match-all(shortName,'Bogus') or match-all(content/subject,'Bogus') or match-all(content/description,'Bogus')) and @status='active']";
    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(0, docs.size());
    System.out.println("done testXQuerySearchv1_0_5");
  }
  
  @Test
  public void testXQuerySearchv1_0_5a() throws Exception {
    System.out.println("start testXQuerySearchv1_0_5");
    String xql ="declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; "
        + "//RootResource[(match-all(title,'Bogus') or match-all(identifier,'Bogus') or match-all(shortName,'Bogus') or match-all(content/subject,'Bogus') or match-all(content/description,'Bogus')) and @status='active']";
    String xml = rqsv1_0.xQueryToText(xql);
    Assert.assertNotNull(xml);
    Assert.assertTrue(xml.isEmpty());
    System.out.println("done testXQuerySearchv1_0_5a");
  }

  @Test
  public void testXQuerySearchv1_0_6() throws Exception {
    System.out.println("start testXQuerySearchv1_0_6");
    String xql = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource[identifier = 'ivo://nottobefound']";
    List<Document> docs = rqsv1_0.xQueryToDocuments(xql);
    Assert.assertEquals(0, docs.size());
    System.out.println("done testXQuerySearchv1_0_6");
  }
  
   @Test
  public void testXQuerySearchv1_0_6a() throws Exception {
    System.out.println("start testXQuerySearchv1_0_6");
    String xql = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; "
        + "//RootResource[identifier = 'ivo://nottobefound']";
    String xml = rqsv1_0.xQueryToText(xql);
    Assert.assertNotNull(xml);
    Assert.assertTrue(xml.isEmpty());
    System.out.println("done testXQuerySearchv1_0_6");
  }

  @Test
  public void testGetResourcev1_0_1() throws Exception {
    System.out.println("begin testGetResourcev1_0_1");
    Document doc = rqsv1_0.getResourceToDocument("ivo://registry.test/registry");
    Assert.assertNotNull(doc);
    RegistryDOMHelper.printDocument(doc, System.out);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testGetResourcev1_0_1");
  }
  
  @Test
  public void testGetResourcev1_0_1a() throws Exception {
    System.out.println("begin testGetResourcev1_0_1a");
    Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://registry.test/registry</identifier></GetResource>");
    String xml = rqsv1_0.getResourceToText("ivo://registry.test/registry");
    Assert.assertNotNull(xml);
    System.out.println("done testGetResourcev1_0_1a");
  }
  
  @Test
  public void testGetResourcev1_0_2() throws Exception {
    System.out.println("begin testGetResourcev1_0_2");
    Document doc = rqsv1_0.getResourceToDocument("ivo://registry.test/cdms");
    Assert.assertNotNull(doc);
    RegistryDOMHelper.printDocument(doc, System.out);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testGetResourcev1_0_2");
  }

  /**
   * Method: testGetResourceByIdentifier Description: test to perform a query
   * for one particular identifier in the registry not based on adql.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testGetResourceNotFoundv1_0_1() throws Exception {
    System.out.println("begin testGetResourceNotFoundv1_0_1");
    Document doc = rqsv1_0.getResourceToDocument("ivo://nothing/helloworld");
    Assert.assertNull(doc);
    System.out.println("done testGetResourceNotFoundv1_0_1");
  }
  
  /**
   * Method: testGetResourceByIdentifier Description: test to perform a query
   * for one particular identifier in the registry not based on adql.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testGetResourceNotFoundv1_0_1a() throws Exception {
    System.out.println("begin testGetResourceNotFoundv1_0_1a");
    String xml = rqsv1_0.getResourceToText("ivo://nothing/helloworld");
    Assert.assertNull(xml);
    System.out.println("done testGetResourceNotFoundv1_0_1a");
  }
  
}
