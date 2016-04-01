package org.astrogrid.registry.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException;
/** 
 * Class: XSLHelper
 * Description: A small XSL helper class that simply loads up xsl stylesheets and tranforms the XML. 
 * @todo fix exception handling
 * @todo factor commonality of methods into a private helper method*/
public class XSLHelper {
    
    /**
     * Commons Logger for this class
     */
    private static final Log logger = LogFactory.getLog(XSLHelper.class);
    
    private static final String XSL_DIRECTORY = "xsl/";
   
   
   /**
    * Empty constructor -- should delete later, this is automatic.
    *
    */
   public XSLHelper() {
      
   }
   
   /**
    * Method: loadStyleSheet
    * Description: Load a Stylesheet from the CLASSPath jars given a particular name.
    * @param name A string name of the xsl stylesheet file name. Normally in the classpath or in a jar file.
    * @return A InputStream to a xsl stylesheet.
    */
   private InputStream loadStyleSheet(String name) {
       ClassLoader loader = this.getClass().getClassLoader();
       return loader.getResourceAsStream(name);
   }
      
   
   /**
    * Method: transformResourceToResource
    * Description: A nice helper method normally used for clients, to allow of tranforming one XML conforming to a 
    * Registry schema back to another XML conforming to a different Registry schema version. ex: 0.10 to 0.9 is the
    * most commonly used for the portal.  Handy use if a client cannot be easily upgradable to a new version of the
    * registry. The source and target versions are passed in for forming the xsl stylesheet name.  Meaning multiple
    * version tranformations can be supported (version numbers come from the main vr namespace).
    * @param doc XML Document root node of the sourceVersion.
    * @param sourceVersion version number of the source of the XML. ex: 0.10
    * @param targetVersion version number of the result/target for the XML ex: 0.9
    * @return XML of the converted schema.
    */
   public Document transformResourceToResource(Node doc, String sourceVersion, String targetVersion) {
       //sourceVersion = sourceVersion.replace('.','_');
       //targetVersion = targetVersion.replace('.','_');
       
       String fileName = "VOResource-v" + sourceVersion + "-v" + targetVersion + ".xsl";

       
       Source xmlSource = new DOMSource(doc);
       Document resultDoc = null;
       
       ClassLoader loader = this.getClass().getClassLoader();
       InputStream is = null;
       is = loader.getResourceAsStream(XSL_DIRECTORY + fileName);
       Source xslSource = new StreamSource(is);
             
       DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
       try {
          builderFactory.setNamespaceAware(true);
          DocumentBuilder builder = builderFactory.newDocumentBuilder();
          resultDoc = builder.newDocument();
          //DocumentFragment df = resultDoc.createDocumentFragment();
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          
          DOMResult result = new DOMResult(resultDoc);
          Transformer transformer = transformerFactory.newTransformer(xslSource);
          
          transformer.transform(xmlSource,result);
       }catch(ParserConfigurationException pce) {
         logger.error("transformResourceToResource(Node, String)", pce);
         pce.printStackTrace();
       }catch(TransformerConfigurationException tce) {
         logger.error("transformResourceToResource(Node, String)", tce);
         tce.printStackTrace();
       }catch(TransformerException te) {
         logger.error("transformResourceToResource(Node, String)", te);
         te.printStackTrace();
       }
       //@todo never return null on error.
       if(resultDoc == null) {
           logger.error("IN tranformResouceToResource resultDoc was null");
       }
       /*
       else {
           logger.info("THE RESULTDOC IN transformResourceToResource = "  + DomHelper.DocumentToString(resultDoc));
       }
       */
       return resultDoc;
    }
   
}