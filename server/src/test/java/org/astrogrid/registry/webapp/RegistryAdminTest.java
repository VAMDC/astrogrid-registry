package org.astrogrid.registry.webapp;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;

import java.util.HashMap;

import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.admin.AdminFactory;

import org.astrogrid.registry.server.admin.AuthorityListManager;
import org.astrogrid.registry.server.admin.AuthorityList;
import java.util.Properties;
import org.astrogrid.registry.TestRegistry;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Class: RegistryAdminTest
 * Description: Test for the admin ability of the registry which is mainly adding&updating xml resources to the
 * xml database (eXist).
 * @author Kevin Benson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RegistryAdminTest {
    
    IAdmin sut = null;
    AuthorityListManager alm = null;
    
    /**
     * Our debug logger.
     *
     */
    //private static Log log = LogFactory.getLog(RegistryAdminTest.class);    
    
    private final static File DATABASE_LOCATION = new File("target", "exist");
    
    @BeforeClass
    public static void installRegistry() throws IOException {
      TestRegistry.installRegistry(DATABASE_LOCATION);
    }
    
    
    @Before
    public void setUp() throws Exception {  
        TestRegistry.emptyRegistry(DATABASE_LOCATION);
        Properties props = new Properties();
        props.setProperty("create-database", "true");
        props.setProperty("configuration", TestRegistry.getDatabaseConfiguration(DATABASE_LOCATION).getAbsolutePath());
        XMLDBManager.registerDB(props);
        sut = AdminFactory.createAdminService("1.0");
        alm = new AuthorityListManager();
    }

    @Test
    public void testUpdateARegv1_0() throws Exception {
    	System.out.println("start testUpdateARegv1_0");    	
        Document doc = askQueryFromFile("/xml/ARegistryv1_0.xml");
        Document resultDoc = sut.updateInternal(doc);
        RegistryDOMHelper.printDocument(resultDoc, System.out);
        HashMap hm = alm.getManagedAuthorities("astrogridv1_0","1.0");
        Assert.assertTrue(hm.containsKey(new AuthorityList("registry.test","1.0")));
        Assert.assertTrue(hm.containsValue(new AuthorityList("registry.test","1.0","registry.test"))) ;       
        Assert.assertTrue(!resultDoc.getDocumentElement().hasChildNodes());        
        Assert.assertEquals("UpdateResponse", resultDoc.getDocumentElement().getLocalName());  
    }
    
    @Test
    public void testUpdateMultipleRecordsv1_0() throws Exception {
        Document doc = askQueryFromFile("/xml/Multiple_ResourceRecordsv1_0.xml");
        Document resultDoc = sut.updateInternal(doc);
        System.out.println("here is resultDoc in multUpdatev1_0 = " + DomHelper.DocumentToString(resultDoc));
        System.out.println();
        Assert.assertEquals("Not a fault message", "UpdateResponse", resultDoc.getDocumentElement().getLocalName());  
        Assert.assertTrue("Response message is an empty element", !resultDoc.getDocumentElement().hasChildNodes());      
    }  

    @Test
    public void testUpdateInvalidv1_0NotManaged() throws Exception {
        Document doc = askQueryFromFile("/xml/InvalidEntryv1_0.xml");
        Document resultDoc = sut.updateInternal(doc);
        Assert.assertEquals(1,resultDoc.getElementsByTagNameNS("*","Fault").getLength());
    }
    
    /**
     * Test loading a registration for a registry with two managed authorities.
     * The authorities are new.registry (the primary authority) and new.registry.1.
     * The SUT should accept this update and we should then be able to find
     * both authorities associated with new.registry.
     * 
     * @throws Exception 
     */
    @Test
    public void testUpdateNewRegv1_0() throws Exception {
        Document doc = askQueryFromFile("/xml/NewRegistryv1_0.xml");
        Document resultDoc = sut.updateInternal(doc);
        DomHelper.DocumentToStream(doc, System.out);
        
        // Check that the update was accepted.
        Assert.assertEquals("UpdateResponse", resultDoc.getDocumentElement().getLocalName());        
        Assert.assertTrue(!resultDoc.getDocumentElement().hasChildNodes());          
        
        // Check that the expected authorities are visible in our registry.
        HashMap hm = alm.getManagedAuthorities("astrogridv1_0","1.0");
        System.out.println("newreghm tostring = " + hm.toString());    
        Assert.assertTrue(hm.containsKey(new AuthorityList("new.registry","1.0")));
        Assert.assertTrue(hm.containsValue(new AuthorityList("new.registry","1.0","new.registry")));
        Assert.assertTrue(hm.containsValue(new AuthorityList("new.registry.1","1.0","new.registry")));
    }

    @Test
    public void testUpdateAnotherNewRegInvalidv1_0() throws Exception {
      HashMap hm1 = alm.getManagedAuthorities("astrogridv1_0","1.0");
      
      Document queryDoc = askQueryFromFile("/xml/NewRegistryInvalidv1_0.xml");
      Document resultDoc = sut.updateInternal(queryDoc);    
      
      Assert.assertEquals("Invalid registration => SOAP fault", "Fault", resultDoc.getDocumentElement().getLocalName());       
        
      HashMap hm2 = alm.getManagedAuthorities("astrogridv1_0","1.0");
      
      Assert.assertEquals("Rejected update has not changed managed authorities", hm1.entrySet(), hm2.entrySet());
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
        
        return queryDoc;
    }    
}