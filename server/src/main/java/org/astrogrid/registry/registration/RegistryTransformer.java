package org.astrogrid.registry.registration;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.servlet.ServletException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * A specialized transformer for registration documents. The source of the
 * XML is always a URL and the results are presented in a DOM.
 *
 * @author Guy Rixon
 */
public class RegistryTransformer {
  
  private static Log log = 
      LogFactory.getLog("org.astrogrid.registry.registration.RegistryTransformer");
  
  /**
   * The XSLT processor to prepare a registration from a template.
   */
  private Transformer transformer;
  
  private Source transformationSource;
  
  /**
   * The destination of the transformed resource.
   * The exact sub-type of Result depends on the constructor.
   */
  private Result transformationResult;
  
  /**
   * Constructs a transformer whose result is streamed to a pre-set
   * destination.
   */
  public RegistryTransformer(URL resource, Writer target) throws ServletException {
    try {
      StreamSource source = new StreamSource(resource.openStream());
      this.transformer = TransformerFactory.newInstance().newTransformer(source);
      this.transformationResult = new StreamResult(target);
    }
    catch (Exception ex) {
      throw new ServletException("Can't set up the XSLT transformation.", ex);
    }
  }
  
  
  /**
   * Constructs a transformer whose result is available as a DOM.
   */
  public RegistryTransformer(URL resource) throws ServletException {
    try {
      StreamSource source = new StreamSource(resource.openStream());
      this.transformer = TransformerFactory.newInstance().newTransformer(source);
      this.transformationResult = new DOMResult();
    }
    catch (Exception ex) {
      throw new ServletException("Can't set up the XSLT transformation.", ex);
    }
  }
  
  /**
   * Defines the source of data for the transformation.
   */
  public void setTransformationSource(URL source) throws IOException, ServletException {
    if (source == null) {
      throw new ServletException("You must supply a URL for the transform.");
    }
    this.transformationSource = new StreamSource(source.openStream());
  }
  
  /**
   * Defines the source of data for the transformation.
   */
  public void setTransformationSource(Node doc) throws IOException, ServletException {
    if (doc == null) {
      throw new ServletException("You must supply a Node for the transform.");
    }
    this.transformationSource =  new DOMSource(doc);
  }
  
  /**
   * Sets a parameter of the XSLT transformation that produces the
   * resource document.
   */
  public void setTransformationParameter(String name, Object value) throws ServletException {
    if (this.transformer == null) {
      throw new ServletException("You must set the transform before setting its parameters.");
    }
    if (value != null) {
      this.transformer.setParameter(name, value);
    }
  }
  
  /**
   * Runs the transformation on the pre-set source and caches the result.
   */
  public void transform() throws ServletException {
    if (this.transformer == null) {
      throw new ServletException("You must set the transform before running the transformer.");
    }
    if (this.transformationSource == null) {
      throw new ServletException("You must set the transformation source before running the transformer.");
    }
    if (this.transformationResult == null) {
      throw new ServletException("You must set the transformation target before running the transformer.");
    }
    
    
    try {
      this.transformer.transform(this.transformationSource, this.transformationResult);
    }
    catch (Exception e) {
      throw new ServletException("XSLT transformation failed", e);
    }
  }
  
  /**
   * Delivers the result of transformation as a DOM node.
   */
  public Node getResultAsDomNode() {
    return ((DOMResult)this.transformationResult).getNode();
  }
  
}
