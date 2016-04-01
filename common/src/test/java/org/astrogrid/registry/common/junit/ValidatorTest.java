package org.astrogrid.registry.common.junit;

import java.io.InputStream;
import java.util.Iterator;

import org.astrogrid.util.DomHelper;
import org.w3c.dom.Document;

import junit.framework.*;
import org.astrogrid.contracts.SchemaMap;
import org.astrogrid.registry.common.RegistryValidator;
import java.util.Map;

/**
 * Class: ValidatorTest
 * Description: A Class that simply tests out a lot of various XML resources based on the Registry schemas.
 * @author Kevin Benson
 *
 */
public class ValidatorTest extends TestCase {
    
    
    public ValidatorTest() {
        
    }
    
    /**
     * Setup our test.
     *
     
    public void setUp() throws Exception {
        super.setUp() ;
    }
    */
    
    public void testPrintMap() throws Exception {
        Map schemaLocations = SchemaMap.ALL;
        for (Iterator i = schemaLocations.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry)i.next();
            assertNotNull(e.getKey());
            System.out.println("Key = " + e.getKey() + " value = " + e.getValue());
            assertNotNull(e.getValue());
            
        }//for
    }

    /*
    public void testValidRegistry10() throws Exception {
        Document queryDoc = askQueryFromFile("ARegistryv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
    

    
    public void testValidCEAEntriesv10() throws Exception {
        Document queryDoc = askQueryFromFile("CEAEntriesv10.xml");
        RegistryValidator.isValid(queryDoc);
    }
    
    public void testValidCEAHTTPEntriesv10() throws Exception {
        Document queryDoc = askQueryFromFile("CEAHttpappsv10.xml");
        RegistryValidator.isValid(queryDoc);
    }

    
    public void testValidCEAHTTPEntriesLivev10() throws Exception {
        Document queryDoc = askQueryFromFile("HTTPAppsLivev10.xml");
        RegistryValidator.isValid(queryDoc);
    }   
    
    public void testValidCambridgev10() throws Exception {
        Document queryDoc = askQueryFromFile("Cambridge0_10.xml");
        RegistryValidator.isValid(queryDoc);
    }    
        
    
        
    
    public void testValidFullMSSL() throws Exception {
        Document queryDoc = askQueryFromFile("MSSLRegistryEntryv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
       
    public void testValidFileStore10() throws Exception {
        Document queryDoc = askQueryFromFile("filestore-onev10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
    
    public void testValidMySpace10() throws Exception {
        Document queryDoc = askQueryFromFile("myspacev10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }

    public void testValidAGServiceKinds10() throws Exception {
        Document queryDoc = askQueryFromFile("AGServiceKindsv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
    
    public void testValidSIAP10() throws Exception {
        Document queryDoc = askQueryFromFile("INT-WFS-SIAPv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
    
    public void testValidAuthority10() throws Exception {
        Document queryDoc = askQueryFromFile("AstrogridStandardAuthorityv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }

    
    public void testValidCEAMSSLRegistryEntryv10() throws Exception {
        Document queryDoc = askQueryFromFile("CEAMSSLRegistryEntryv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
    
    public void testValidTabularSkyServicev10() throws Exception {
        Document queryDoc = askQueryFromFile("TabularSkyService_MSSL_TRACEv10.xml");
        RegistryValidator.isValid(queryDoc,"VOResources");
    }
        
    public void testValidCommunityv10() throws Exception {
        Document queryDoc = askQueryFromFile("Communityv10.xml");
        RegistryValidator.isValid(queryDoc);
    }
    */    
    
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