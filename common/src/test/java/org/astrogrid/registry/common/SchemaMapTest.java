package org.astrogrid.registry.common;

import java.net.URL;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit tests for SchemaMap.
 *
 * @author Guy Rixon
 */
public class SchemaMapTest {
  
  @Test
  public void testAllEntriesPresent() throws Exception {
    // The values in the schema map are the URLs leading to the schemata; the
    // keys are the namespace URIs. The URLs typically lead into the contracts
    // jar. For each key, check that there is a value that the value is a URL
    // and the the URL points to data. Don't bother reading the data.
    for (Map.Entry<String,URL> e : SchemaMap.ALL.entrySet()) {
      System.out.println("\n" + e.getKey() + " " + e.getValue());
      Assert.assertNotNull(e.getValue());
      e.getValue().openStream(); // throws exceptions if URL has no data
    }
  }
  
}
