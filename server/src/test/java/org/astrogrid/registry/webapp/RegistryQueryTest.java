package org.astrogrid.registry.webapp;

import java.io.File;
import java.io.InputStream;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;
import org.codehaus.xfire.util.STAXUtils;
import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.query.ISearch;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.astrogrid.registry.TestRegistry;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.admin.AdminFactory;
import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;

/**
 * Class: RegistryQueryTest Description: Tests out the various Query methods of
 * the server side Registry.
 *
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
    System.out.println("setup in registryquerytest");

    // Make an empty registry.
    File target = new File("target");
    TestRegistry.installRegistry(target);
    TestRegistry.emptyRegistry(target);
    File fi = TestRegistry.getDatabaseConfiguration(target);
    Properties props = new Properties();
    props.setProperty("create-database", "true");
    props.setProperty("configuration", fi.getAbsolutePath());
    XMLDBManager.registerDB(props);

    // Add a self-registration.
    IAdmin rasv1_0 = AdminFactory.createAdminService("1.0");
    Document resultDoc1 = rasv1_0.updateInternal(RegistryDOMHelper.documentFromResource("/xml/ARegistryv1_0.xml"));
    Assert.assertEquals("UpdateResponse", resultDoc1.getDocumentElement().getLocalName());

    // Add a resource to support the keyword-query test
    Document adilRegistration = RegistryDOMHelper.documentFromResource("/xml/cdms.xml");
    rasv1_0.updateInternal(adilRegistration);

    // Check that the resource went in and is accessible.
    XMLDBRegistry xdbRegistry = new XMLDBRegistry();
    ResourceSet rs = xdbRegistry.query("/", "astrogridv1_0");
    Assert.assertNotNull(rs);

    rqsv1_0 = QueryFactory.createQueryService("1.0");

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    builder = dbf.newDocumentBuilder();
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

  /**
   * Method: testKeywordQueryService Description: test to perform a keyword
   * search on the registry via the use of the common web service interface
   * method.
   *
   * @throws Exception standard junit exception to be thrown.
   *
   */
  @Test
  public void testKeywordQueryServicev1_0_1() throws Exception {
    System.out.println("start testKeywordQueryServicev1_0_1");
    String xql = "<KeywordSearch><keywords>spectroscopy</keywords></KeywordSearch>";
    Document queryDoc = RegistryDOMHelper.documentFromString(xql);
    RegistryDOMHelper.printDocument(queryDoc, System.out);

    XMLStreamReader reader = rqsv1_0.KeywordSearch(queryDoc);

    Document resultDoc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(resultDoc);
    RegistryDOMHelper.printDocument(resultDoc, System.out);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());

    XPathExpression q1 = xpath.compile("count(/rw:SearchResponse)");
    Assert.assertEquals("1", q1.evaluate(resultDoc));

    XPathExpression q2 = xpath.compile("count(/rw:SearchResponse/ri:VOResources)");
    Assert.assertEquals("1", q2.evaluate(resultDoc));

    XPathExpression q3 = xpath.compile("count(/rw:SearchResponse/ri:VOResources/ri:Resource)");
    Assert.assertEquals("1", q3.evaluate(resultDoc));
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_1() throws Exception {
    System.out.println("start testXQuerySearchv1_0_1");

    String xql
        = "<XQuerySearch><XQuery>declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0';"
        + " for $x in //RootResource where $x/identifier = 'ivo://registry.test/cdms' "
        + " return $x</XQuery></XQuerySearch>";

    //String xql = "<XQuerySearch><XQuery>/</XQuery></XQuerySearch>";
    Document queryDoc = DomHelper.newDocument(xql);
    Assert.assertNotNull(queryDoc);
    RegistryDOMHelper.printDocument(queryDoc, System.out);

    XMLStreamReader reader = rqsv1_0.XQuerySearch(queryDoc);
    Document resultDoc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(resultDoc);
    RegistryDOMHelper.printDocument(resultDoc, System.out);

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());

    XPathExpression q1 = xpath.compile("count(/rw:XQuerySearchResponse)");
    Assert.assertEquals("1", q1.evaluate(resultDoc));

    XPathExpression q2 = xpath.compile("count(/rw:XQuerySearchResponse/ri:Resource)");
    Assert.assertEquals("1", q2.evaluate(resultDoc));
    
    XPathExpression q3 = xpath.compile("/rw:XQuerySearchResponse/ri:Resource/identifier");
    Assert.assertEquals("ivo://registry.test/cdms", q3.evaluate(resultDoc));
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
    Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource[identifier = 'ivo://registry.test/registry']</XQuery></XQuerySearch>");
    XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(doc);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() > 0));
    System.out.println("done testXQuerySearchv1_0_2");
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_3() throws Exception {
    System.out.println("start testXQuerySearchv1_0_3");
    Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource</XQuery></XQuerySearch>");
    XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(doc);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() > 0));
    System.out.println("done testXQuerySearchv1_0_3");
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
    Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; "
        + "//RootResource[(matches(title,'Spectroscopy','i') or matches(identifier,'spectroscopy','i') or matches(shortName,'Imaging','i') or matches(content/subject,'spectroscopy','i') or matches(content/description,'Imaging','i')) and @status='active']" + "</XQuery></XQuerySearch>");
    XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    RegistryDOMHelper.printDocument(doc, System.out);
    
    Assert.assertNotNull(doc);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() > 0));
    System.out.println("done testXQuerySearchv1_0_4");
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   * 
   * This search should find no resources, but should still return the 
   * normal XML-wrapper.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_5() throws Exception {
    System.out.println("start testXQuerySearchv1_0_5");
    Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\"; "
        + "//RootResource[(match-all(title,'Imaging') or match-all(identifier,'Imaging') or match-all(shortName,'Imaging') or match-all(content/subject,'Imaging') or match-all(content/description,'Imaging')) and @status='active']" + "</XQuery></XQuerySearch>");
    XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
    Document resultDoc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(resultDoc);
    
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    xpath.setNamespaceContext(new RegistryNamespaceContext());

    XPathExpression q1 = xpath.compile("count(/rw:XQuerySearchResponse)");
    Assert.assertEquals("1", q1.evaluate(resultDoc));

    XPathExpression q2 = xpath.compile("count(/rw:XQuerySearchResponse/ri:Resource)");
    Assert.assertEquals("0", q2.evaluate(resultDoc));
    
    System.out.println("done testXQuerySearchv1_0_5");
  }

  /**
   * Method: testXQuerySearch Description: test to perform a xquery search on
   * the registry via the use of the common web service interface method.
   *
   * @throws Exception standard junit exception to be thrown.
   */
  @Test
  public void testXQuerySearchv1_0_6() throws Exception {
    System.out.println("start testXQuerySearchv1_0_6");
    Document xqueryDoc = DomHelper.newDocument("<XQuerySearch><XQuery>declare namespace ri = \"http://www.ivoa.net/xml/RegistryInterface/v1.0\";  //RootResource[identifier = 'ivo://nottobefound']</XQuery></XQuerySearch>");
    XMLStreamReader reader = rqsv1_0.XQuerySearch(xqueryDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(doc);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() == 0));
    Assert.assertTrue(doc.getDocumentElement().getLocalName().equals("XQuerySearchResponse"));
    System.out.println("done testXQuerySearchv1_0_6");
  }

  /**
   * Method: testGetResourceByIdentifier Description: test to perform a query
   * for one particular identifier in the registry not based on adql.
   *
   * @throws Exception standard junit exception to be thrown.
   *
   */
  @Test
  public void testGetResourcev1_0_1() throws Exception {
    System.out.println("begin testGetResourcev1_0_1");
    Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://registry.test/registry</identifier></GetResource>");
    XMLStreamReader reader = rqsv1_0.GetResource(inputDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(doc);
    RegistryDOMHelper.printDocument(doc, System.out);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() == 1));
    System.out.println("done testGetResourcev1_0_1");
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
    Document inputDoc = DomHelper.newDocument("<GetResource><identifier>ivo://nothing/helloworld</identifier></GetResource>");
    XMLStreamReader reader = rqsv1_0.GetResource(inputDoc);
    Document doc = STAXUtils.read(builder, reader, false);
    Assert.assertNotNull(doc);
    Assert.assertTrue((doc.getElementsByTagNameNS("*", "Resource").getLength() == 0));
    Assert.assertEquals("Fault", doc.getDocumentElement().getLocalName());
    System.out.println("done testGetResourceNotFoundv1_0_1");
  }

  /**
   * Method: askQueryFromFile Description: Obtains a File on the local system as
   * a inputstream and feeds it into a Document DOM object to be returned.
   *
   * @param queryFile - File name of a xml resource.
   * @return Document DOM object of a XML file.
   * @throws Exception Any IO Exceptions or DOM Parsing exceptions could be
   * thrown.
   */
  protected Document askQueryFromFile(String queryFile) throws Exception {
    Assert.assertNotNull(queryFile);
    InputStream is = this.getClass().getResourceAsStream(queryFile);

    Assert.assertNotNull("Could not open query file :" + queryFile, is);
    Document queryDoc = DomHelper.newDocument(is);

    //Document queryDoc = DomHelper.newDocument(new File(queryFile));
    return queryDoc;
  }
  
  /**
   * Extracts the text associated with an element node. The text is assumed
   * to be distributed across one or more text nodes that are children of
   * the given node.
   * <p>
   * This is essentially org.w3c.dom.Element.getTextContent(), which is missing
   * from the eXist implementation of the Element class.
   * 
   * @param parent The element.
   * @return The text (null if the node is not an element) 
   */
  private String textContent(Node parent) {
    if (parent.getNodeType() != Node.ELEMENT_NODE) {
      return null;
    }
    else {
      StringBuilder sb = new StringBuilder();
      NodeList nl = parent.getChildNodes();
      for (int i = 0; i < nl.getLength(); i++) {
        Node n = nl.item(i);
        if (n.getNodeType() == Node.TEXT_NODE) {
          sb.append(n.getNodeValue());
        }
      }
      return sb.toString();
    }
  }
  
}
