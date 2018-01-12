/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.astrogrid.registry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.admin.v1_0.RegistryAdminService;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 *
 * @author Guy Rixon
 */
public class TestRegistry {
  public static final String COLLECTION_NAME = "astrogridv1_0";
  
  /**
   * Installs the database files and directories. Pre-loads the self-registration
   * for the registry.
   * 
   * @param parentDirectory The directory in which to build the database.
   * @return The database-configuration file.
   * @throws IOException 
   */
  public static void installRegistry(File parentDirectory) throws IOException {
    
    // Create the directory for the database if it does not exist.
    if (!parentDirectory.exists()) {
      parentDirectory.mkdirs();
    }
    
    // Copy in the database-confoguration file. The database engine will
    // expect to find the data directory as a sibling of the configuration file.
    File conf = new File(parentDirectory, "conf.xml");
    copyResourceToFile("/conf.xml", conf);
    
    // Create the data directory.
    File data = new File(parentDirectory, "data");
    if (!data.exists()) {
      data.mkdirs();
    }
  }
  
  public static void emptyRegistry(File parentDirectory) throws IOException {
    File data = new File(parentDirectory, "data");
    for (File f : data.listFiles()) {
      f.delete();
    }
  }
  
  public static void configure(File parentDirectory) 
      throws IllegalAccessException, 
             ClassNotFoundException, 
             InstantiationException, 
             XMLDBException, 
             IOException {
    File fi = getDatabaseConfiguration(parentDirectory);
    Properties props = new Properties();
    props.setProperty("create-database", "true");
    props.setProperty("configuration", fi.getAbsolutePath());
    XMLDBManager.registerDB(props);
  }
  
  public static File getDatabaseConfiguration(File parentDirectory) throws IOException {
    return new File(parentDirectory, "conf.xml");
  }
  
  public static void shutDown() throws XMLDBException {
    XMLDBManager.shutdownDB();
  }
  
  public static void loadResourcesForSoapTests() throws Exception {
    // Add a self-registration.
    RegistryAdminService rasv1_0 = new RegistryAdminService();
    Document resultDoc1 = rasv1_0.updateInternal(RegistryDOMHelper.documentFromResource("/xml/ARegistryv1_0.xml"));
    Assert.assertEquals("UpdateResponse", resultDoc1.getDocumentElement().getLocalName());

    // Add a resource to support the getResource test
    Document adilRegistration = RegistryDOMHelper.documentFromResource("/xml/cdms.xml");
    Document resultDoc2 = rasv1_0.updateInternal(adilRegistration);
    Assert.assertEquals("UpdateResponse", resultDoc2.getDocumentElement().getLocalName());

    // Check that the resource went in and is accessible.
    XMLDBRegistry xdbRegistry = new XMLDBRegistry();
    ResourceSet rs = xdbRegistry.query("/", COLLECTION_NAME);
    Assert.assertNotNull(rs);
    XMLResource x1 = (XMLResource) rs.getResource(1);
    System.out.println(x1.getContent());
    XMLResource x = xdbRegistry.getResource(
        xdbRegistry.internalIdentifier("ivo://registry.test/cdms"),
        COLLECTION_NAME
    );
    Assert.assertNotNull(x);
  }
  
  private static void copyResourceToFile(String resource, File file) throws IOException {
    InputStream in = TestRegistry.class.getResourceAsStream(resource);
    OutputStream out = new FileOutputStream(file);
    int c = in.read();
    while (c != -1) {
      out.write(c);
      c = in.read();
    }
    out.flush();
    out.close();
    in.close();
  }

}
