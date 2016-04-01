package org.astrogrid.registry.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

import org.w3c.dom.*;
import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException;

import net.sf.saxon.TransformerFactoryImpl;

import org.astrogrid.contracts.http.filters.ContractsFilter;
import org.astrogrid.registry.RegistryException;

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
    
    private static final String ASTROGRID_SCHEMA_BASE = "http://software.astrogrid.org/schema/";
    
    private static String schemaLocationBase;

    
    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
          if(schemaLocationBase == null) {              
              schemaLocationBase = ContractsFilter.getContextURL() != null ? ContractsFilter.getContextURL() + "/schema/" :
                                   ASTROGRID_SCHEMA_BASE;
          }//if
    }    
    
   /**
    * Empty constructor -- should delete later, this is automatic.
    *
    */
   public XSLHelper() {
      
   }
   
   /**
    * Load a Stylesheet from the ClassLoader (usually from jars) given a particular directory and name.
    * @param name A string directory & name of the xsl stylesheet file name. Normally in the classpath or in a jar file.
    * @return A InputStream to a xsl stylesheet.
    */
   private InputStream loadStyleSheet(String name) {
       logger.info("Loading from Classloader = " + name);
       ClassLoader loader = this.getClass().getClassLoader();
       return loader.getResourceAsStream(name);
   }
      

   /**
    * Method: transformADQLToXQL
    * Description: Transforms a particular adql version into XQL (XQuery) for use with the eXist db. Other
    * parameters are passed in to help build the XQuery such as the root node to be queried on.  And the namespaces
    * that need to be declared for queries. Supports multiple versions of ADQL if a stylesheet is provided.
    * @param doc ADQL Select/Where XML Node to be processed through a XSL stylesheet
    * @param versionNumber Version number of ADQL used as part of the adql stylesheet name.
    * @param rootNode Actually a String of the root Resource (with prefix) to be queries on.
    * @param namespaceDeclare A long string of all the 'declare namespace' for the XQL.
    * @return A XQL (XQuery) String to be used for querying on the XML database.
    */
   public String transformADQLToXQL(Node doc, String versionNumber,String rootNode, String contractVersion) throws RegistryException {
      
      Source xmlSource = new DOMSource(doc);
      logger
            .debug("transformADQLToXQL(Node, String) - the file resource = ADQLToXQL-"
                    + versionNumber + ".xsl");      
      try {
    	 
         Source xslSource = new StreamSource(
                  new InputStreamReader(loadStyleSheet(XSL_DIRECTORY + "ADQLToXQL-" + versionNumber + "-" + contractVersion +  ".xsl"),"UTF-8"));
         /*
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
          
         builderFactory.setNamespaceAware(true);
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         resultDoc = builder.newDocument();
         */
         
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         //DOMResult result = new DOMResult(resultDoc);
         StringWriter sw = new StringWriter();
         StreamResult result = new StreamResult(sw);
         
         Transformer transformer = transformerFactory.newTransformer(xslSource);
         transformer.setParameter("resource_elem", rootNode);
         //transformer.setParameter("declare_elems", namespaceDeclare);         
         transformer.transform(xmlSource,result);
         String xqlResult = sw.toString();
         if (xqlResult.startsWith("<?")) {
            xqlResult = xqlResult.substring(xqlResult.indexOf("?>")+2);
         }
         xqlResult = xqlResult.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;","&");
         return xqlResult;
      /*}catch(ParserConfigurationException pce) {
        logger.error("transformADQLToXQL(Node, String)", pce);
        throw new RegistryException(pce);
      */
      }catch(TransformerConfigurationException tce) {
        logger.error("transformADQLToXQL(Node, String)", tce);
        throw new RegistryException(tce);
      }catch(TransformerException te) {
        logger.error("transformADQLToXQL(Node, String)", te);
        throw new RegistryException(te);
      }catch(UnsupportedEncodingException uee) {
          logger.error(uee);
          throw new RegistryException(uee);
      }
   }
      
   /**
    * Method: transformToOAI
    * Description: Transforms XML from a given query in the XML database to a OAI GetRecord XML. Used mainly by 
    * third party tool which shows various OAI xml verbs in the correct XML format. Handles multiple versions of
    * XML from the Registry depending on the version number from the main vr namespace.
    * @param doc XML Root Node of a document from a  query of the given database.
    * @param versionNumber version number from the vr namespace, which is the main Resource namespace defined.
    * @return XML Document object of a OAI GetRecord XML.
    */
   public Document transformToOAI(Node doc, String versionNumber) throws RegistryException {
      String fileName = "Resourcev" + versionNumber + "-OAI.xsl";

      Source xmlSource = new DOMSource(doc);
      Document resultDoc = null;
      
      try {
          Source xslSource = new StreamSource(new InputStreamReader(loadStyleSheet(XSL_DIRECTORY + fileName),"UTF-8"));            
          DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();          
         builderFactory.setNamespaceAware(true);
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         resultDoc = builder.newDocument();
         //DocumentFragment df = resultDoc.createDocumentFragment();
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         
         DOMResult result = new DOMResult(resultDoc);
         Transformer transformer = transformerFactory.newTransformer(xslSource);
         
         transformer.transform(xmlSource,result);
         return resultDoc;         
      }catch(ParserConfigurationException pce) {
        logger.error("transformToOAI(Node, String)", pce);
        throw new RegistryException(pce);
      }catch(TransformerConfigurationException tce) {
        logger.error("transformToOAI(Node, String)", tce);
        throw new RegistryException(tce);
      }catch(TransformerException te) {
        logger.error("transformToOAI(Node, String)", te);        
        throw new RegistryException(te);
      }catch(UnsupportedEncodingException uee) {
          logger.error(uee); 
          throw new RegistryException(uee);
      }
   }
   
   /**
    * Method: transformUpdate
    * Description: Allows the registry to transform the XML before going into the database. It is multiple versioned
    * based on the vr namespace and can be applied to regular updates (from Astrogrid) or on harvests from other
    * registries. 
    * This usefullness is best described in some examples:
    * ex 1: 0.9 - used substitution groups hence same xml could be expressed in 2 or 3 ways meaning xql queries very
    * difficult, so a xsl stylesshet was applied to make the xml consistent and be able to query easier.
    * ex 2: 0.10 - no more subtituion groups, but the way the main Resource element was described any number of XML name 
    * with any number of namespaces could be of type Resouce.  So XSL is used to again put it in the way that is 
    * consistent in the db.
    * @param doc XML to be transformed.
    * @param versionNumber version number of main vr namespace.
    * @param harvestUpdate is this used on a harvest, if so use a different xsl stylesheet name.
    * @return XML to be updated into the database.
    */
   public Document transformUpdate(Node doc,String versionNumber,boolean harvestUpdate) throws RegistryException {
       
       Source xmlSource = new DOMSource(doc);
       Document resultDoc = null;
       String harvestName = "";
       if(harvestUpdate)
           harvestName = "Harvest_";
       String styleSheetName = "UpdateProcess_" + harvestName + versionNumber + ".xsl";
       logger.debug("transformUpdate(Node, String) - the stylesheet name = "
            + styleSheetName);
       try {
    	  
          Source xslSource = new StreamSource(new InputStreamReader(loadStyleSheet(XSL_DIRECTORY + styleSheetName),"UTF-8"));
          DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();           
          builderFactory.setNamespaceAware(true);
          DocumentBuilder builder = builderFactory.newDocumentBuilder();
          resultDoc = builder.newDocument();
          //DocumentFragment df = resultDoc.createDocumentFragment();
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          
          DOMResult result = new DOMResult(resultDoc);
          Transformer transformer = transformerFactory.newTransformer(xslSource);
          
          transformer.transform(xmlSource,result);
          return resultDoc;  
       }catch(ParserConfigurationException pce) {
        logger.error("transformUpdate(Node, String)", pce);
        throw new RegistryException(pce);        
       }catch(TransformerConfigurationException tce) {
        logger.error("transformUpdate(Node, String)", tce);
        throw new RegistryException(tce);        
       }catch(TransformerException te) {
        logger.error("transformUpdate(Node, String)", te);
        throw new RegistryException(te);        
       }catch(UnsupportedEncodingException uee) {
           logger.error(uee);
           throw new RegistryException(uee);           
       }
    }   
   
   /**
    * Method: transformVersionConversion
    * Description: This method will go away in the near future, a helper xsl method to
    * transform 0.10 XML Resources to 1.0 XML Resources.
    * 
    * @param doc XML to be transformed.
    * @return XML 1.0 Resource DOM.
    */
   public Document transformVersionConversion(Node doc) throws RegistryException {
       
       Source xmlSource = new DOMSource(doc);
       Document resultDoc = null;
       String harvestName = "";    
       String styleSheetName = "Convert0_10-1_0.xsl";
       logger.debug("transformUpdate(Node, String) - the stylesheet name = "
            + styleSheetName);
       try {
    	  
          Source xslSource = new StreamSource(new InputStreamReader(loadStyleSheet(XSL_DIRECTORY + styleSheetName),"UTF-8"));
          DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();           
          builderFactory.setNamespaceAware(true);
          DocumentBuilder builder = builderFactory.newDocumentBuilder();
          resultDoc = builder.newDocument();
          //DocumentFragment df = resultDoc.createDocumentFragment();
          net.sf.saxon.TransformerFactoryImpl transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
          
          DOMResult result = new DOMResult(resultDoc);
          Transformer transformer = transformerFactory.newTransformer(xslSource);
          
          transformer.transform(xmlSource,result);
          return resultDoc;  
       }catch(ParserConfigurationException pce) {
        logger.error("transformUpdate(Node, String)", pce);
        throw new RegistryException(pce);        
       }catch(TransformerConfigurationException tce) {
        logger.error("transformUpdate(Node, String)", tce);
        throw new RegistryException(tce);        
       }catch(TransformerException te) {
        logger.error("transformUpdate(Node, String)", te);
        throw new RegistryException(te);        
       }catch(UnsupportedEncodingException uee) {
           logger.error(uee);
           throw new RegistryException(uee);           
       }
    }   
   
}