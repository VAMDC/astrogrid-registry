package org.astrogrid.registry.webapp;

import java.io.InputStream;
import java.io.File;
import java.util.Iterator;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.codehaus.xfire.util.STAXUtils;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.*;
import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.admin.AdminFactory;

import org.astrogrid.registry.server.admin.AuthorityListManager;
import org.astrogrid.registry.server.admin.AuthorityList;
import java.util.Properties;

/**
 * Class: RegistryAdminTest
 * Description: Test for the admin ability of the registry which is mainly adding&updating xml resources to the
 * xml database (eXist).
 * @author Kevin Benson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RegistryAdminTest extends TestCase {
    
    IAdmin ras = null;
    IAdmin rasv1_0 = null;
    AuthorityListManager alm = null;
    
    /**
     * Our debug logger.
     *
     */
    //private static Log log = LogFactory.getLog(RegistryAdminTest.class);    
    
    public RegistryAdminTest() {
        
    }
    
    /**
     * Setup our test.
     *
    */ 
    public void setUp() throws Exception {
        super.setUp();
        //log.debug("doing setup log4j");
        //log.info("how about log4j info doing setup log4j");        
        File fi = new File("target/test-classes/conf.xml");
        Properties props = new Properties();
        props.setProperty("create-database", "true");
        props.setProperty("configuration",fi.getAbsolutePath());
        if(fi != null) {
          XMLDBManager.registerDB(props);
        }
        ras = AdminFactory.createAdminService("0.1");
        rasv1_0 = AdminFactory.createAdminService("1.0");
        alm = new AuthorityListManager();
    }

    
    public void testUpdateARegv1_0() throws Exception {
    	System.out.println("start testUpdateARegv1_0");    	
        Document doc = askQueryFromFile("ARegistryv1_0.xml");
        Document resultDoc = rasv1_0.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv1_0","1.0");
        System.out.println("hm tostring = " + hm.toString());
        assertTrue(hm.containsKey(new AuthorityList("registry.test","1.0")));
        assertTrue(hm.containsValue(new AuthorityList("registry.test","1.0","registry.test"))) ;       
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());        
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");   
    	System.out.println("start testUpdateARegv1_0");    	
    }
    
    public void testUpdateMultipleRecordsv1_0() throws Exception {
        Document doc = askQueryFromFile("Multiple_ResourceRecordsv1_0.xml");
        Document resultDoc = rasv1_0.updateInternal(doc);
        System.out.println("here is resultDoc in multUpdatev1_0 = " + DomHelper.DocumentToString(resultDoc));
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");        
    }  

    public void testUpdateInvalidv1_0NotManaged() throws Exception {
        Document doc = askQueryFromFile("InvalidEntryv1_0.xml");
        Document resultDoc = rasv1_0.updateInternal(doc);
        assertEquals(1,resultDoc.getElementsByTagNameNS("*","Fault").getLength());
    }
    
    public void testUpdateNewRegv1_0() throws Exception {
        Document doc = askQueryFromFile("NewRegistryv1_0.xml");
        Document resultDoc = rasv1_0.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv1_0","1.0");
        System.out.println("newreghm tostring = " + hm.toString());
        assertTrue(hm.containsKey(new AuthorityList("new.registry","1.0")));
        assertTrue(hm.containsValue(new AuthorityList("new.registry","1.0","new.registry")));
        assertTrue(hm.containsValue(new AuthorityList("new.registry.1","1.0","new.registry")));        
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());        
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");       
    }

    public void testUpdateAnotherNewRegInvalidv1_0() throws Exception {
        Document doc = askQueryFromFile("NewRegistryInvalidv1_0.xml");
        Document resultDoc = rasv1_0.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv1_0","1.0");
        assertTrue(hm.containsKey(new AuthorityList("new.registry","1.0")));
        assertTrue(hm.containsValue(new AuthorityList("new.registry","1.0","new.registry")));
        assertTrue(hm.containsValue(new AuthorityList("new.registry.1","1.0","new.registry")));        
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"Fault");       
    }
    
    public void testUpdateARegv0_10() throws Exception {
    	System.out.println("start testUpdateAregv0_10");
        Document doc = askQueryFromFile("ARegistryv0_10.xml");
        Document resultDoc = ras.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv0_10","0.10");
        System.out.println("hm.tostring = " + hm.toString());
        assertTrue(hm.containsKey(new AuthorityList("registry.test","0.10")));
        assertTrue(hm.containsValue(new AuthorityList("registry.test","0.10","registry.test")));
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());        
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");
    	System.out.println("done testUpdateAregv0_10");        
    }

    public void testUpdateAuthorityv0_10() throws Exception {
        Document doc = askQueryFromFile("AstrogridStandardAuthorityv0_10.xml");
        Document resultDoc = ras.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv0_10","0.10");
        assertTrue(hm.containsValue(new AuthorityList("astrogrid.org","0.10","registry.test")));        
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");        
    }
    

    public void testUpdateInvalidv0_10NotManaged() throws Exception {
        Document doc = askQueryFromFile("InvalidEntryv0_10.xml");
        Document resultDoc = ras.updateInternal(doc);
        assertEquals(1,resultDoc.getElementsByTagNameNS("*","Fault").getLength());
    }
    
    public void testUpdateNewReg0_10() throws Exception {
        Document doc = askQueryFromFile("Cambridge0_10_Reg.xml");
        Document resultDoc = ras.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv0_10","0.10");
        assertTrue(hm.containsKey(new AuthorityList("uk.ac.cam.ast","0.10")));
        assertTrue(hm.containsValue(new AuthorityList("uk.ac.cam.ast","0.10","uk.ac.cam.ast")));        
        assertTrue(!resultDoc.getDocumentElement().hasChildNodes());
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"UpdateResponse");        
    }
    
    public void testUpdateInvalidNewReg0_10() throws Exception {
        Document doc = askQueryFromFile("Cambridge0_10_RegInvalid.xml");
        Document resultDoc = ras.updateInternal(doc);
        HashMap hm = alm.getManagedAuthorities("astrogridv0_10","0.10");
        assertEquals(resultDoc.getDocumentElement().getLocalName(),"Fault");        
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