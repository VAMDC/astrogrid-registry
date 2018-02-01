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
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Base class for objects that manipulate DOM trees.
 * 
 * @author Guy Rixon
 */
public class Dominator {
  
  protected DocumentBuilderFactory builderFactory;

  protected TransformerFactory transformerFactory;
  
  public Dominator() {
    builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(true);
    transformerFactory = TransformerFactory.newInstance();
  }
  
  public void print(Document doc, Writer out) throws IOException, ServletException {
    XMLUtils.DocumentToWriter(doc, out);
  }
  
  public void print(Document doc, OutputStream out) throws IOException {
    XMLUtils.DocumentToStream(doc, out);
  }
  
  
  protected final DocumentBuilder getBuilder() {
    try {
      return builderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
  
  protected final Element findChildElement(
      Element parent, 
      String localName
  ) throws Exception {
    NodeList nl = parent.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node n = nl.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element e = (Element) n;
        if (localName.equals(e.getNodeName())) {
          return (Element) n;
        }
      }
    }
    throw new Exception("No " + localName + " elements were found in " + parent.getNodeName());
  }
  
  protected final Element findChildElement(
      Element parent,
      String namespace,
      String localName
  ) throws Exception {
    NodeList nl = parent.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node n = nl.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element e = (Element) n;
        if (namespace.equals(e.getNamespaceURI()) && localName.equals(e.getLocalName())) {
          return (Element) n;
        }
      }
    }
    throw new Exception("No " + localName + " elements were found in " + parent.getNodeName());
  }

  protected final Element findFirstChildElement(
      Element parent
  ) throws Exception {
    NodeList nl = parent.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++) {
      Node child = nl.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        return (Element) child;
      }
    }
    throw new Exception("No child elements were found in " + parent.getNodeName());
  }
  
  /**
   * Creates a child element and adds it to the given parent.
   * 
   * @param parent
   * @param localName
   * @return The new child-element.
   */
  protected final Element addChildElement(
      Element parent,
      String localName
  ) {
    Document d = parent.getOwnerDocument();
    Element e = d.createElement(localName);
    parent.appendChild(e);
    return e;
  }
  
  /**
   * Creates a child element and adds it to the given parent.
   * 
   * @param parent Parent element.
   * @param namespace Namespace of child element
   * @param name Prefixed name of child element, e.g. soap:Fault.
   * @return The new child-element.
   */
  protected final Element addChildElement(
      Element parent,
      String namespace,
      String name
  ) {
    Document d = parent.getOwnerDocument();
    Element e = d.createElementNS(namespace, name);
    parent.appendChild(e);
    return e;
  }
  
  protected final void addExternalChild(Element parent, Element e) {
    Document d = parent.getOwnerDocument();
    Node child = d.importNode(e, true); // Import node and its sub-tree
    parent.appendChild(child);
  }

}
