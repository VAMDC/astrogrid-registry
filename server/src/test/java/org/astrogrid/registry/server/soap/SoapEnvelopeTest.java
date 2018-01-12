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
import java.util.Properties;
import org.astrogrid.registry.TestRegistry;
import org.astrogrid.xmldb.client.XMLDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 *
 * @author Guy Rixon
 */
public class SoapEnvelopeTest {
  
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
  public void testCreateEmptyEnvelope() throws Exception {
    SoapEnvelope sut = new SoapEnvelope();
    Assert.assertNotNull(sut);
  }
  
  @Test
  public void testParseMessage() throws Exception {
    FileInputStream r1 = new FileInputStream(new File("src/test/resources/test-resolve-request.xml"));
    SoapEnvelope sut = new SoapEnvelope(r1);
    Assert.assertNotNull(sut.message);
    Assert.assertNotNull(sut.envelope);
    Assert.assertEquals(SoapEnvelope.SOAP_NS, sut.envelope.getNamespaceURI());
    Assert.assertEquals("Envelope", sut.envelope.getLocalName());
    Assert.assertNotNull(sut.body);
    Assert.assertNotNull(sut.operation);
    sut.print(System.out);
  }
  
  @Test
  public void testSetOperation() throws Exception {
    SoapEnvelope sut = new SoapEnvelope();
    Element operation = sut.setOperation("whatever", "etc:something");
    Assert.assertNotNull(operation);
    Assert.assertEquals("something", operation.getLocalName());
    Assert.assertEquals("whatever", operation.getNamespaceURI());
  }
  
  @Test
  public void testAddExternalChild() throws Exception {
    SoapEnvelope sut1 = new SoapEnvelope();
    sut1.setOperation("whatever", "etc:op1");
    Element q1 = sut1.addChildElement(sut1.operation, "questionable");
    
    SoapEnvelope sut2 = new SoapEnvelope();
    sut2.setOperation("whatever", "etc:op2");
    sut2.addExternalChild(sut2.operation, q1);
    sut2.print(System.out);
    Element q2 = sut2.findChildElement(sut2.operation, "questionable");
    Assert.assertNotNull(q2);
  }

}
