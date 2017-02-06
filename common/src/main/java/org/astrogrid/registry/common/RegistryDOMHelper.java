package org.astrogrid.registry.common;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import org.astrogrid.config.Config;
import org.astrogrid.util.DomHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * Class: RegistryDOMHelper
 * Description: Mainly used as a DOMHelper for grabbing Authority ids, Resource keys, and identifiers.
 * 
 * @author Kevin Benson
 */
public class RegistryDOMHelper {
      
   /**
    * conf - Config variable to access the configuration for the server normally
    * jndi to a config file.
    * @see org.astrogrid.config.Config
    */      
   public static Config conf = null;

   /**
    * Logging variable for writing information to the logs
    */   
   private static final Log log = LogFactory.getLog(RegistryDOMHelper.class);

   /**
    * Default versionNumber used in the registry.
    */
   private static String versionNumber = null;
   
   /**
    * Static to be used on the initiatian of this class for the config
    */   
   static {
      if(conf == null) {        
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         versionNumber = conf.getString("reg.amend.defaultversion",null);
         if(versionNumber == null) {
             versionNumber = conf.getString("org.astrogrid.registry.version","0.10");
         }//if
      }//if
   }
  
  /**
   * A parser, set up for namespace-aware parsing. All documents created
   * by this class should use this parser. No schema validation is applied
   * during parsing.
   */
  protected static DocumentBuilder builder;
  static {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
     try {
       builder = factory.newDocumentBuilder();
     } catch (ParserConfigurationException ex) {
       throw new IllegalStateException("Failed to configure the XML parser", ex);
     }
  }
   
   /**
    * Method: getDefaultVersionNumber
    * Description: Get the default version number that the registry supports.  Normally 0.10 at the moment.
    * @return version number.
    */
   public static String getDefaultVersionNumber() {
       return versionNumber;
   }
   
   public static String findADQLVersionFromNode(Node currentNode) {
       String version = null;
       switch(currentNode.getNodeType() ) {       
           case Node.DOCUMENT_NODE :
               return findADQLVersionFromNode(((Document)currentNode).getDocumentElement());
           case Node.ELEMENT_NODE :
               NamedNodeMap attributeNodes = currentNode.getAttributes();
               for(int i = 0;i < attributeNodes.getLength();i++) {
                   Attr attribute = (Attr) attributeNodes.item(i);
                   if(attribute.getNodeValue() != null && attribute.getNodeValue().startsWith("http://www.ivoa.net/xml/ADQL")) {
                       return attribute.getNodeValue().substring(attribute.getNodeValue().lastIndexOf("v") + 1);
                   }//if
               }//for
               if(currentNode.getNamespaceURI() != null && currentNode.getNamespaceURI().startsWith("http://www.ivoa.net/xml/ADQL") ) {
                   return currentNode.getNamespaceURI().substring(currentNode.getNamespaceURI().lastIndexOf("v") + 1);
               }               
               version =  processChildNodes(currentNode.getChildNodes(),ADQL_VERSION_SEARCHTYPE);
               if(version != null) {
                   return version;
               }
       }
       return null;
   }
   
   
   
   /**
    * Method: getAuthorityID
    * Description: Gets the text out of the First authority id element.
    * Once it gets the NodeList then return the text in the first child if there is an AuthorityID elment,
    * otherwise split apart the identifier.  Identifier ex: ivo://{authorityid}/{resourcekey} 
    * @param doc xml element normally the full DOM root element.
    * @return AuthorityID text
    */
   public static String getAuthorityID(Element doc) {
       NodeList nl = doc.getElementsByTagNameNS("*","identifier" );
       String val = "";
       if(nl.getLength() == 0) {
           return null;
       }     
       NodeList valNodes = nl.item(0).getChildNodes();
       for(int i = 0;i < valNodes.getLength();i++) {
    	   val += valNodes.item(i).getNodeValue();
       }
       val = val.trim();
       //val = nl.item(0).getFirstChild().getNodeValue().trim();      
       int index = val.indexOf("/",7);
       if( index != -1 && index > 6) {
               return val.substring(6,index);
       } else {
               //small hack for certain registries that are not putting ivo://
               //in the identifier. Currently only STSCI is doing this.
           if(val.length() > 6) {
                   return val.substring(6);
           } else {
                   return val;
           }
       }       
   }

   /**
    * Method: getResourceKey
    * Description: Gets the text out of the First ResourceKey element.
    * Once it gets the NodeList then return the text in the first child if there is a ResourceKey element,
    * otherwise split apart the identifier elment. Identifier ex: ivo://{authorityid}/{resourcekey}
    * {resourcekey} is optional.
    * @param doc xml element normally the full DOM root element.
    * @return ResourceKey text
    */  
   public static String getResourceKey(Element doc) {
       NodeList nl = doc.getElementsByTagNameNS("*","identifier" );
       if(nl.getLength() == 0) {
           return null;
       }
       String val = "";
       NodeList valNodes = nl.item(0).getChildNodes();
       for(int i = 0;i < valNodes.getLength();i++) {
    	   val += valNodes.item(i).getNodeValue();
       }
       val = val.trim();
       //val = nl.item(0).getFirstChild().getNodeValue().trim();
       int index = val.indexOf("/",7);
       if(index != -1 && index > 6 &&  val.length() > (index+1)) 
           return val.substring(index+1);
       //it is just an empty ResourceKey which is okay.
       return "";
   }
   
   
   /**
    * Method: getIdentifier
    * Description: Return the Identifier element.  Goes about a longer way to support 0.9 version by
    * getting the AuthorityId and ResourceKey then put it together. 
    * Identifier ex: ivo://{authorityid}/{resourcekey} where {resourcekey} is optional.
    * 
    * @param nd org.w3c.dom.Node containing an Identifier element.
    * @return String of the identifier.
    * @throws IOException
    */
   public static String getIdentifier(Node doc) throws IOException {
	  NodeList nl = ((Element)doc).getElementsByTagNameNS("*","identifier" );
      if(nl.getLength() == 0)
    	  return null;
      String val = null;
      val = nl.item(0).getFirstChild().getNodeValue().trim();       
      return val;
   }
   
   private static final int VORESOURCE_VERSION_SEARCHTYPE = 0;
   private static final int ADQL_VERSION_SEARCHTYPE = 1;
   //private static final int CONTRACT_VERSION_SEARCHTYPE = 2;
   

   private static String processChildNodes(NodeList children,int searchType) {
       String version = null;
       for(int i = 0;i < children.getLength();i++) {
           if(searchType == VORESOURCE_VERSION_SEARCHTYPE)
               version = findVOResourceVersionFromNode(children.item(i));
           if(searchType == ADQL_VERSION_SEARCHTYPE) 
               version = findADQLVersionFromNode(children.item(i));           
           if(version != null) {
               return version;
           }//if
       }//for
       return null;
   }
   
   public static String findVOResourceVersionFromNode(Node currentNode) {
       switch(currentNode.getNodeType() ) {       
           case Node.DOCUMENT_NODE :
               return findVOResourceVersionFromNode(((Document)currentNode).getDocumentElement());
           case Node.ELEMENT_NODE :
               NodeList nl = ((Element)currentNode).getElementsByTagNameNS("*","identifier");
               if(nl.getLength() > 0 && nl.item(0).getNamespaceURI() != null) {
                   return nl.item(0).getNamespaceURI().substring(nl.item(0).getNamespaceURI().lastIndexOf("v") + 1);
               }
       }
       return null;
   }
   
   
   /**
    * Method: getRegistryVersionFromNode
    * Description: Look through a Node and any of its parent nodes for a VOResource (vr) namespace, and
    * extract the version number from that namespace.  If it cannot be found return the default version number.
    * @param nd org.w3c.dom.Node for processing of searching for the vr namespace.
    * @return version number from a namespace.
    */
   public static String getRegistryVersionFromNode(Node nd) {
       if(nd == null || Node.ELEMENT_NODE != nd.getNodeType()) {
           log.debug("not a ELEMENT NODE TIME TO JUST DEFAULT IT");
           if(nd != null) {
               Node parentNodeTry = nd.getParentNode();
               if(parentNodeTry != null) {
                   return getRegistryVersionFromNode(parentNodeTry);
               }//if
           }
           return versionNumber;
       }
       
       String version = nd.getNamespaceURI();
       log.debug("Node Name = " + nd.getNodeName() + " Version = " + version);
       if(version != null && version.trim().length() > 0 &&
          version.startsWith("http://www.ivoa.net/xml/VOResource")) {
           return version.substring(version.lastIndexOf("v")+1);
       }
       //darn did not find a namespace uri that was VOResource.
       version = DomHelper.getNodeAttrValue((Element)nd,"vr","xmlns");
       log.debug("Node Name = " + nd.getNodeName() + " Version = " + version);
       if(version != null && version.trim().length() > 0) {
           
           return version.substring(version.lastIndexOf("v")+1);
       }
       //darn no vr namespace defined either.
       version = DomHelper.getNodeAttrValue((Element)nd,"xmlns");
       if(version != null && version.trim().length() > 0 &&
          version.startsWith("http://www.ivoa.net/xml/VOResource")) {
           return version.substring(version.lastIndexOf("v")+1);
       }
       //darn no default namespace either, okay it must be on the parent one
       Node parentNode = nd.getParentNode();
       if(parentNode != null) {
           return getRegistryVersionFromNode(parentNode);
       }
       //log.error("Could not find a Registry version number on the nodes BAD MEANS NO NAMESPACE DEFINED," +
       //          " defaulting to config.");
       return versionNumber;
   }
  
   /**
    * Prints a DOM document to a given stream.
    * Copied from http://stackoverflow.com/questions/2325388/what-is-the-shortest-way-to-pretty-print-a-org-w3c-dom-document-to-stdout
    * @param doc
    * @param out
    * @throws IOException
    * @throws TransformerException 
    */
   public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(doc), 
         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
  }
   
  /**
    * Prints a DOM tree to a given stream.
    * Copied from http://stackoverflow.com/questions/2325388/what-is-the-shortest-way-to-pretty-print-a-org-w3c-dom-document-to-stdout
    * @param dom
    * @param out
    * @throws IOException
    * @throws TransformerException 
    */
   public static void printDom(Node dom, OutputStream out) throws IOException, TransformerException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

    transformer.transform(new DOMSource(dom), 
         new StreamResult(new OutputStreamWriter(out, "UTF-8")));
  }
  
  /**
   * Parses a string of XML text into a document.
   * @param xmlText XML to be parsed.
   * @return The document.
   * @throws IOException If the parser somehow fails to read the string (should be impossible!)
   * @throws SAXException If the XML is not well formed.
   */
  public static Document documentFromString(String xmlText) throws IOException, SAXException {
    return builder.parse(new InputSource(new StringReader(xmlText)));
  }
  
  /**
   * Creates a DOM from an XML text held in a system resource.
   * 
   * @param resource Name of the resource.
   * @return The DOM.
   * @throws javax.xml.parsers.ParserConfigurationException
   * @throws org.xml.sax.SAXException
   * @throws java.io.IOException
   */
  public static Document documentFromResource(String resource) 
      throws ParserConfigurationException, SAXException, IOException {
    InputStream is = RegistryDOMHelper.class.getResourceAsStream(resource);
    if (is == null) {
      throw new IllegalArgumentException("Resource " + resource + " was not found");
    }
    else {
      return builder.parse(is);
    }
  }

  public static Document emptyDocument() {
    return builder.newDocument();
  }  

}