package org.astrogrid.registry.server.http.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import org.astrogrid.registry.TestRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Guy Rixon
 */
public class SoapServletTest {
  
  @Before
  public void setUpDatabase() throws Exception {
    System.setProperty("org.vamdc.registry.url", "http://localhost/registry");

    // Make the database ready for the test.
    File target = new File("target");
    TestRegistry.installRegistry(target);
    TestRegistry.emptyRegistry(target);
    TestRegistry.configure(target);

    TestRegistry.loadResourcesForSoapTests();
  }
  
  @After
  public void shutDownDatabase() throws Exception {
    TestRegistry.shutDown();
  }
  
  @Test
  public void testDispatchXQuery() throws Exception {    
    InputStream r = new FileInputStream(new File("src/test/resources/test-xquery-request.xml"));
    SoapServlet sut = new SoapServlet();
    sut.init();
    sut.dispatch(r, new OutputStreamWriter(System.out));
  }
  
  @Test
  public void testDispatchResolve() throws Exception {    
    InputStream r = new FileInputStream(new File("src/test/resources/test-resolve-request.xml"));
    SoapServlet sut = new SoapServlet();
    sut.init();
    sut.dispatch(r, new OutputStreamWriter(System.out));
  }

}
