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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Object representation of a SOAP message. Each instance wraps a DOM tree.
 * 
 * @author Guy Rixon
 */
public class SoapEnvelope extends Dominator {
  
  protected static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
  
  protected Document message;
  
  protected Element envelope;
  
  protected Element body;
  
  protected Element operation;
  
  protected Element fault;
  
  /**
   * Constructs an empty envelope.
   */
  public SoapEnvelope() throws IOException, ServletException {
    super();
    
    message = getBuilder().newDocument();
    envelope = message.createElementNS(SOAP_NS,"soap:Envelope");
    message.appendChild(envelope);
    body = message.createElementNS(SOAP_NS, "soap:Body");
    envelope.appendChild(body);
  }
  
  /**
   * Constructs an envelope, possibly containing a request, from 
   * a character stream.
   */
  public SoapEnvelope(InputStream in) throws SAXException, IOException, Exception {
    super();
    
    DocumentBuilder builder = getBuilder();
    message = builder.parse(in);
    
    envelope = message.getDocumentElement();
    body = findChildElement(envelope, SOAP_NS, "Body");
    operation = findFirstChildElement(body);
  }
  
  
  
  public Document getDocument() {
    return message;
  }
  
  public Element getOperation() {
    return operation;
  }
  
  /**
   * Creates an empty operation element with the given QName. If there was 
   * previously an operation element it is removed.
   * 
   * @param namespace Namespace of new element.
   * @param localName Name of new element
   * @return The new element.
   */
  public Element setOperation(String namespace, String localName) {
    Element e = message.createElementNS(namespace, localName);
    if (operation != null) {
      body.replaceChild(operation, e);
    } else {
      body.appendChild(e);
    }
    operation = e;
    return operation;
  }
  
  /**
   * Creates a Fault structure in the body.
   */
  public void setFault(
      boolean isServerAtFault,
      String msg
  ) {
    fault = addChildElement(body, SOAP_NS, "soap:Fault");
    Element faultcode = addChildElement(fault, "faultcode");
    if (isServerAtFault) {
      faultcode.setTextContent("SOAP-ENV:Server");
    } else {
      faultcode.setTextContent("SOAP-ENV:Client");
    }
    Element faultstring = addChildElement(fault, "faultstring");
    faultstring.setTextContent(msg);
  }

  public String getOperationLocalName() {
    return operation.getLocalName();
  }
  
  public void print(Writer out) throws IOException, ServletException {
    super.print(message, out);
  }
  
  public void print(OutputStream out) throws IOException, ServletException {
    super.print(message, out);
  }
}
