package org.astrogrid.registry.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Assert;
import org.xml.sax.InputSource;
import org.junit.Test;

/**
 *
 * @author Guy Rixon
 */
public class RegistryValidatorTest {
  
  @Test
  public void testGoodVamdcTapFromSource() throws Exception {
    InputSource in = new InputSource(this.getClass().getResourceAsStream("/vamdc-tap-1.xml"));
    RegistryValidator sut = new RegistryValidator();
    Assert.assertFalse(sut.isInvalid(in));
  }
  
  @Test
  public void testBadVamdcTapFromSource() throws Exception {
    InputSource in = new InputSource(this.getClass().getResourceAsStream("/vamdc-tap-2.xml"));
    RegistryValidator sut = new RegistryValidator();
    Assert.assertTrue(sut.isInvalid(in));
  }
  
  @Test
  public void testGoodVamdcTapFromString() throws Exception {
    String xml = resourceToString("/vamdc-tap-1.xml");
    RegistryValidator sut = new RegistryValidator();
    Assert.assertFalse(sut.isInvalid(xml));
  }
  
  @Test
  public void testBadVamdcTapFromString() throws Exception {
    String xml = resourceToString("/vamdc-tap-2.xml");
    RegistryValidator sut = new RegistryValidator();
    Assert.assertTrue(sut.isInvalid(xml));
  }
  
  /**
   * Reads a class resource, typically an XML text, into a string.
   * 
   * @param name Name of resource.
   * @return The text of the resource.
   * @throws IOException 
   */
  private String resourceToString(String name) throws IOException {
    StringBuilder b = new StringBuilder();
    InputStream is = this.getClass().getResourceAsStream(name);
    Assert.assertNotNull(is);
    InputStreamReader isr = new InputStreamReader(is);
    int c;
    while ((c = isr.read()) != -1) {
      b.append((char)c);
    }
    return b.toString();
  }

}
