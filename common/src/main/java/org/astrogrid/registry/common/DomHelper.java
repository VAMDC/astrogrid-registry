/*
   $Id: DomHelper.java,v 1.16 2005/09/08 07:30:54 clq2 Exp $

   (c) Copyright...
*/

package org.astrogrid.registry.common;


import java.io.*;

import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


/**
 * An implementation-neutral class for loading Xml documents from files into
 * a DOM.
 * <P>
 * The problem with using Apache's XMLUtils or similar is that it requires a particular
 * DOM implementation installed.  This does not...
 *
 * @author M Hill
 */
public class DomHelper
{

    /** Convenience routine for returning the value of an element of the given
    * name that is a *direct* child of the given parent Element.
     * - Just noticed this is not quite a duplicate of getNodeTextValue - I'm not
     * sure they're quite right.
     */
    public static String getValue(Element parent, String child) {
      
      assert parent != null : "Attempted to get value of "+child+" of null parent element";
      
      NodeList nodes = parent.getChildNodes();
      
      if ((nodes==null) || (nodes.getLength()==0)) {
          return "";
      }
      
      for (int i = 0; i < nodes.getLength(); i++) {
         if ((nodes.item(i) instanceof Element) && (nodes.item(i).getLocalName().equals(child))) {
            return getValue( (Element) nodes.item(i));
         }
      }

      return "";
    }
    
    /** Convenience routine for returning the value of an element.  Allows for values that are
     * child nodes (which seems to happen when the value is on a separate line from
     * the opening tag) .
     * - Just noticed this is not quite a duplicate of getNodeTextValue - I'm not
     * sure they're quite right.
     */
    public static String getValue(Element element) {

      assert element != null : "Attempted to get value of null element";
      
       if (element.getNodeValue() != null) {
          return element.getNodeValue();
       }
       if (element.hasChildNodes()) {
          return element.getChildNodes().item(0).getNodeValue();
       }
       return "";
    }
    
   /**
    * Convenience class returns Document from given URL
    */
   public static Document newDocument(URL documentLocation) throws ParserConfigurationException, SAXException, IOException
   {
      return newDocument(new BufferedInputStream(documentLocation.openStream()));
   }
   
   /**
    * Convenience class returns Document from given <b>String of XML</b>.
    */
   public static Document newDocument(String xmlDocument) throws ParserConfigurationException, SAXException, IOException
   {
      //return newDocument(new BufferedInputStream(new StringBufferInputStream(xmlDocument)));
      //return newDocument(new BufferedInputStream(new ByteArrayInputStream(xmlDocument.getBytes())));
        return newDocument(new InputSource(new StringReader(xmlDocument)));
   }

   /**
    * Convenience class returns Document from given file
    */
   public static Document newDocument(File documentFile) throws ParserConfigurationException, SAXException, IOException
   {
      return newDocument(new BufferedInputStream(new FileInputStream(documentFile)));
   }


   /**
    * Loads and returns the root DOM node (Element) from the given
    * XML file
    */
   public static Document newDocument(InputStream in) throws ParserConfigurationException, SAXException, IOException
   {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         //dbf.setValidating(true);
         //dbf.setIgnoringElementContentWhitespace(true);  //not available in 1.3
         dbf.setNamespaceAware(true);
         
         DocumentBuilder builder = dbf.newDocumentBuilder();
         return builder.parse(in);
   }
   
   /**
    * Loads and returns the root DOM node (Element) from the given
    * XML file
    */
   public static Document newDocument(InputSource in) throws ParserConfigurationException, SAXException, IOException
   {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         //dbf.setValidating(true);
         //dbf.setIgnoringElementContentWhitespace(true);  //not available in 1.3
         dbf.setNamespaceAware(true);
         
         DocumentBuilder builder = dbf.newDocumentBuilder();
         return builder.parse(in);
   }   
   

   /**
    * Creates an empty document
   */
   public static Document newDocument() throws ParserConfigurationException {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();
      return builder.newDocument();
      
   }
      
   /**
    * Writes document to stream
    */
   public static void DocumentToStream(Document doc, OutputStream out) {
      //naughtily implemented as an XMLUtil call until I work out how to do it independently
      XMLUtils.DocumentToStream(doc, out);
   }
   
   /**
    * Writes document to writer
    */
   public static void DocumentToWriter(Document doc, Writer out) {
      //naughtily implemented as an XMLUtil call until I work out how to do it independently
      XMLUtils.DocumentToWriter(doc, out);
   }
   
   /**
    * Writes document to string
    */
   public static String DocumentToString(Document doc) {
      //naughtily implemented as an XMLUtil call until I work out how to do it independently
      return XMLUtils.DocumentToString(doc);
   }
   
   /**
    * Writes document to string
    */
   public static void ElementToStream(Element body,OutputStream out) {
      //naughtily implemented as an XMLUtil call until I work out how to do it independently
      XMLUtils.ElementToStream(body,out);
   }
   
   
   /**
    * Writes document to string
    */
   public static String ElementToString(Element body) {
      //naughtily implemented as an XMLUtil call until I work out how to do it independently
      return XMLUtils.ElementToString(body);
   }
   
   /**
    * Writes document to string in nice indented format, etc
    */
   public static void PrettyElementToStream(Element body, OutputStream out) {
      XMLUtils.PrettyElementToStream(body, out);
   }
   
   /**
    * Writes document to string in nice indented format, etc
    */
   public static void PrettyDocumentToStream(Document body, OutputStream out) {
      XMLUtils.PrettyDocumentToStream(body, out);
   }
   
   public static String getNodeAttrValue(Element elem,String tagName) {
      return getNodeAttrValue(elem, tagName, null);
   }


   public static String getNodeAttrValue(Element elem,String tagName, String namespacePrefix) {
      String val = elem.getAttribute(tagName);
      if(val == null || val.trim().length() <= 0) {
         val = elem.getAttribute("xmlns:" + tagName);
      }
      if( (val == null || val.trim().length() <= 0) && namespacePrefix != null) {
         val = elem.getAttributeNS(namespacePrefix,tagName);
      }//if
      return val;
   }
   

   public static String getNodeTextValue(Node nd,String tagName) throws IOException {
      NodeList nl = getNodeListTags(nd,tagName);
      if(nl.getLength() > 0 && nl.item(0).getFirstChild() != null) {
         return nl.item(0).getFirstChild().getNodeValue();
      }
      return null;
   }


   public static String getNodeTextValue(Node nd,String tagName, String namespacePrefix)  throws IOException {
      NodeList nl = getNodeListTags(nd,tagName,namespacePrefix);
      if(nl.getLength() > 0 && nl.item(0).getFirstChild() != null) {
         return nl.item(0).getFirstChild().getNodeValue();
      }
      return null;
   }

   /**
    * Finds a NodeList based on a tagname.
    * @param doc
    * @param tagName
    * @param prefix
    * @return
    */
   public static NodeList getNodeListTags(Node nd,String tagName) throws IOException
   {
      return getNodeListTags(nd,tagName,null);
   }

   /**
    * Finds a NodeList based on a tagname and/or it's prefix/namespace
    * @param doc
    * @param tagName
    * @param prefix
    * @return
    */
   public static NodeList getNodeListTags(Node nd,String tagName, String namespacePref) throws IOException
   {
      if(nd instanceof Document) {
         return getNodeListTags((Document)nd,tagName,namespacePref);
      }else if(nd instanceof Element) {
         return getNodeListTags((Element)nd,tagName,namespacePref);
      }else {
         throw new IOException("The Node is not an instance of Document or Element");
      }
   }
   
   private static NodeList getNodeListTags(Document doc, String tagName, String namespacePref) {
      NodeList nl = doc.getElementsByTagName(tagName);
      if(nl.getLength() == 0 && namespacePref == null) {
          //look for it with some kind of namespace.
          nl = doc.getElementsByTagNameNS("*",tagName );
      }      
      if(nl.getLength() == 0 && namespacePref != null) {
         nl = doc.getElementsByTagNameNS(namespacePref,tagName );
      }
      if(nl.getLength() == 0 && namespacePref != null) {
         nl = doc.getElementsByTagName(namespacePref + ":" + tagName );
      }
      return nl;
   }
   
   private static NodeList getNodeListTags(Element elem, String tagName, String namespacePref) {
      NodeList nl = elem.getElementsByTagName(tagName);
      if(nl.getLength() == 0 && namespacePref == null) {
          //look for it with some kind of namespace.
          nl = elem.getElementsByTagNameNS("*",tagName );
      }      
      if(nl.getLength() == 0 && namespacePref != null) {
         nl = elem.getElementsByTagNameNS(namespacePref,tagName );
      }
      if(nl.getLength() == 0 && namespacePref != null) {
         nl = elem.getElementsByTagName(namespacePref + ":" + tagName );
      }
      return nl;
   }
   
   
}

