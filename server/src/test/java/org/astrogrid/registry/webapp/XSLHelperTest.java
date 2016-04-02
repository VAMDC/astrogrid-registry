package org.astrogrid.registry.webapp;

import java.io.InputStream;
import java.io.File;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;

import junit.framework.*;

import org.astrogrid.registry.server.XSLHelper;
import java.util.Properties;

/**
 * Class: XSLHelperTest
 * Description: Unit tests for testing out the XSL that happens on the server side of a registry.  Typically these
 * methods are already called from updates and queries on the registry.  But this tests verifies the xsl's are 
 * happening and receiving results.
 * @author Kevin Benson
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XSLHelperTest extends TestCase {
    
    
    public XSLHelperTest() {
        
    }
    
    /**
     * Setup our test.
     *
   * @throws java.lang.Exception
    */ 
    @Override
    public void setUp() throws Exception {
        super.setUp();
        File fi = new File("src/test/resources/conf.xml");
        Properties props = new Properties();
        props.setProperty("create-database", "true");
        props.setProperty("configuration",fi.getAbsolutePath());
        
        XMLDBManager.registerDB(props);
    }
    

    
    /**
     * Method: testXSLADQLToXQLTest
     * Description: test to transform ADQL to XQL.
     * @throws Exception standard junit exception to be thrown.
     */   
    public void testXSLADQLToXQL4Test() throws Exception {        
        Document adql = askQueryFromFile("/xml/QueryForLike--adql-v1.0-1.0.xml");        
        XSLHelper xsl = new XSLHelper();
        String xql = xsl.transformADQLToXQL(adql,"1.0","Resource","1.0");
        System.out.println("here is the xql fromadqlxsl = " + xql);
        assertTrue((xql.length() > 0));        
    }  
    
    /**
     * Method: testXSLADQLToXQLTest
     * Description: test to transform ADQL to XQL.
     * @throws Exception standard junit exception to be thrown.
     */   
    public void testXSLADQLToXQL5Test() throws Exception {        
        Document adql = askQueryFromFile("/xml/QueryForLike2--adql-v1.0-1.0.xml");        
        XSLHelper xsl = new XSLHelper();
        String xql = xsl.transformADQLToXQL(adql,"1.0","Resource","1.0");
        System.out.println("here is the xql fromadqlxsl = " + xql);
        assertTrue((xql.length() > 0));        
    }    
    
    /**
     * Method: testXSLADQLToXQLTest
     * Description: test to transform ADQL to XQL.
     * @throws Exception standard junit exception to be thrown.
     */   
    public void testXSLADQLToXQL6Test() throws Exception {        
        Document adql = askQueryFromFile("/xml/QueryForLike3--adql-v1.0-1.0.xml");        
        XSLHelper xsl = new XSLHelper();
        String xql = xsl.transformADQLToXQL(adql,"1.0","Resource","1.0");
        System.out.println("here is the xql fromadqlxsl = " + xql);
        assertTrue((xql.length() > 0));        
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