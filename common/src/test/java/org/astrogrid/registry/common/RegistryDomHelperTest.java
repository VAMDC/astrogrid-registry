package org.astrogrid.registry.common;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author Guy Rixon
 */
public class RegistryDomHelperTest {
  
  @Test
  public void testDocumentFromString() throws Exception {
    Document q = RegistryDOMHelper.documentFromString("<KeywordSearch><keywords>Imaging</keywords></KeywordSearch>");
    Assert.assertNotNull(q);
  }
}
