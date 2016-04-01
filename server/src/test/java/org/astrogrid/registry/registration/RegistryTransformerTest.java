package org.astrogrid.registry.registration;

import java.net.URL;
import junit.framework.TestCase;
import org.w3c.dom.Node;

/**
 * JUnit tests for {@link RegistryTransformer}.
 *
 * @author Guy Rixon
 */
public class RegistryTransformerTest extends TestCase {
  
  public void testCoverage() throws Exception {
    URL transform = this.getClass().getResource("/xsl/Coverage.xsl");
    RegistryTransformer sut = new RegistryTransformer(transform);
    
    URL source = this.getClass().getResource("/xml/NewResource.xml");
    sut.setTransformationSource(source);
    
    sut.setTransformationParameter("region", "circle");
    sut.setTransformationParameter("circleC1", "0");
    sut.setTransformationParameter("circleC2", "90");
    sut.setTransformationParameter("circleradius", "60");
    sut.setTransformationParameter("circleCoordSys", "IRCS");
    
    sut.transform();
    
    Node n = sut.getResultAsDomNode();
  }
  
}
