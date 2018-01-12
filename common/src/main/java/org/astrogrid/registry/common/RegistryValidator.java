package org.astrogrid.registry.common;

import java.io.IOException;
import java.io.StringReader;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A Small helper class to validate XML from the Registry primarily updates to
 * the Registry.
 *
 * @author Kevin Benson
 *
 */
public class RegistryValidator {

  private final XMLReader parser;

  private final SaxHandler handler;

  
  public RegistryValidator() {
    try {
      handler = new SaxHandler();
      String locationString = SchemaMap.toCommaSeparatedList();
      parser = XMLReaderFactory.createXMLReader();
      parser.setFeature("http://xml.org/sax/features/validation", true);
      parser.setFeature("http://apache.org/xml/features/validation/schema", true);
      parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", locationString);
      parser.setErrorHandler(handler);
      parser.setContentHandler(handler);
    } catch (SAXException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public boolean isValidInstance(Document doc, String expectedName) {
    return isValidInstance(doc.getDocumentElement(), expectedName);
  }
  
  public boolean isValidInstance(Element element, String expectedName) {
    return (element.getLocalName().equals(expectedName) && !isInvalid(element));
  }

  /**
   * Validates XML text. If there are parsing errors these are retained in
   * the instance state.
   * 
   * @param xml The XML text to be parsed.
   * @return True if the XML is schema-valid, false otherwise.
   */
  public boolean isInvalid(String xml) {
    return isInvalid(new InputSource(new StringReader(xml)));
  }
  
  /**
   * Validates an XML DOM by generating the XML text and reparsing it. 
   * If there are parsing errors these are retained in
   * the instance state.
   * 
   * @param root The DOM.
   * @return True if the XML is schema-valid, false otherwise.
   */
  public boolean isInvalid(Element root) {
    String xml = XMLUtils.ElementToString(root);
    return isInvalid(xml);
  }
  
  /**
   * Validates XML text. If there are parsing errors these are retained in
   * the instance state.
   * 
   * @param source The XML source to be parsed.
   * @return True if the XML is schema-valid, false otherwise.
   */
  public boolean isInvalid(InputSource source) {
    try {
      parser.parse(source);
      return handler.sawError;
    } catch (SAXException e) {
      return true;
    } catch (IOException e) {
      return true;
    }
  }
  
  /**
   * Reveals the parsing errors and warnings.
   * @return The messages.
   */
  public String getErrorMessages() {
    return handler.getMessages();
  }

  /**
   * A SAX handler that records parsing errors but does not retain the parsed
   * form of the document.
   */
  private static class SaxHandler extends DefaultHandler {

    private StringBuilder buff = new StringBuilder();

    public String getMessages() {
      return buff.toString();
    }

    boolean sawError = false;

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
      sawError = true;
      System.err.println("Error:" + this.getMessage(e));
      this.buff.append("\n").append(this.getMessage(e));
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
      sawError = true;
      this.buff.append("\n").append(this.getMessage(e));
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
      System.err.println("Warn: " + this.getMessage(e));
      this.buff.append("\n").append(this.getMessage(e));
    }

    private String getMessage(SAXParseException e) {
      return e.getMessage()
          + " Found at line "
          + e.getLineNumber()
          + ", column "
          + e.getColumnNumber()
          + " in "
          + e.getSystemId();
    }

    /**
     * Clears any error state remaining from the previous parsing.
     */
    public void clear() {
      sawError = false;
      buff = new StringBuilder();
    }
  }

}
