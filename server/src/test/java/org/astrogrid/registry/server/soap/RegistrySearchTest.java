/*
 * Copyright 2018 University of Cambridge.
 *
 * This class is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.astrogrid.registry.server.soap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.astrogrid.registry.TestRegistry;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Guy Rixon
 */
public class RegistrySearchTest {
  
  /**
   * Setup our test.
   *
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    // Make the database ready for the test.
    File target = new File("target");
    TestRegistry.installRegistry(target);
    TestRegistry.emptyRegistry(target);
    File fi = TestRegistry.getDatabaseConfiguration(target);
    Properties props = new Properties();
    props.setProperty("create-database", "true");
    props.setProperty("configuration", fi.getAbsolutePath());
    XMLDBManager.registerDB(props);

    TestRegistry.loadResourcesForSoapTests();
  }

  
  @Test
  public void testXQuery() throws Exception {    
    InputStream r = new FileInputStream(new File("src/test/resources/test-xquery-request.xml"));
    SoapEnvelope request = new SoapEnvelope(r);
    SoapEnvelope response = new SoapEnvelope();
    RegistrySearchSoap.xQuerySearch(request, response);
    response.print(System.out);
    Assert.assertNull(response.fault);
    Assert.assertNotNull(response.operation);
    Assert.assertEquals("XQuerySearchResponse", response.operation.getLocalName());
  }
  
  @Test
  public void testResolve() throws Exception {  
    InputStream r = new FileInputStream(new File("src/test/resources/test-resolve-request.xml"));
    SoapEnvelope request = new SoapEnvelope(r);
    SoapEnvelope response = new SoapEnvelope();
    RegistrySearchSoap.getResource(request, response);
    response.print(System.out);
    Assert.assertNull(response.fault);
    Assert.assertEquals("ResolveResponse", response.operation.getLocalName());
  }
  
  @Test
  public void testBadOperation() throws Exception {
    SoapEnvelope request = new SoapEnvelope();
    SoapEnvelope response = new SoapEnvelope();
    RegistrySearchSoap.getResource(request, response);
    response.print(System.out);
    response.findChildElement(response.body, SoapEnvelope.SOAP_NS, "Fault");
  }

}
