package org.astrogrid.registry.server.harvest.v1_0;

/*
import java.rmi.RemoteException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
*/

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;


import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.harvest.IHarvest;
import org.astrogrid.registry.RegistryException;
import java.io.IOException;
import java.util.Date;

/*
import java.net.URL;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Hashtable;
import java.net.MalformedURLException;
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * RegistryHarvestService is no longer a web service class, but still posses the 
 * harvesting mechanism that is used by server side servlets which uses
 * automatic harvest mechanism and manual harvest by the user.
 */
public class RegistryHarvestService extends org.astrogrid.registry.server.harvest.DefaultHarvestService implements IHarvest {

   private static final Log log =
                          LogFactory.getLog(RegistryHarvestService.class);
   
   public static final String MAIN_HARVEST_SET = "ivo_managed";
    
   public static final String CONTRACT_VERSION = "1.0";
   
   private static final String VORESOURCE_VERSION = "1.0";
   
   private static final String NAMESPACE_URI = "http://www.ivoa.net/wsdl/RegistryHarvest/v1.0";
   
   public RegistryHarvestService() {
       super(CONTRACT_VERSION, VORESOURCE_VERSION);
   }
   
   public String getNameSpaceURI() {
   	return NAMESPACE_URI;
   }
      
   /**
    * This is the main method which uses the HarvestThread class to begin
    * harvesting and updates.  This method will interrogate Resource entries
    * and see how to call the Resources via the AccessURL and determine if
    * it is a WebService or WebBrowser.  Then makes the appropriately call
    * to the web service or browser grabbing there Resources and update into
    * this Registry.
    *
    * @param dt An optional date used to harvest from a particular date
    * @param resources Set of Resources to harvest on, normally a Registry Resource.
    */
   public void beginHarvest(Node resource, Date dt, String lastResumptionToken) throws RegistryException, IOException  {
      log.debug("entered beginharvest(Node)");
      String accessURL = null;
      String invocationType = null;
      boolean isRegistryType;
      NodeList nl = null;
      

      String identifier = RegistryDOMHelper.getIdentifier(resource);
      log.debug("1.0 - identifier in beginHarvest = " + identifier);
      //String versionNumber = RegistryDOMHelper.getRegistryVersionFromNode(resource);

      //get the accessurl and invocation type.
      //invocationtype is either WebService or WebBrowser.
      Node typeAttribute = resource.getAttributes().getNamedItem("xsi:type");
      if(typeAttribute == null) {
    	  //A couple of registries might not use 'xsi' so lets try to look it up via NS call.
    	  typeAttribute = resource.getAttributes().getNamedItemNS("http://www.w3.org/2001/XMLSchema-instance","type");
      }
      isRegistryType = (typeAttribute != null) &&
                       (typeAttribute.getNodeValue().indexOf("Registry") >= 0);
      if(!isRegistryType) {
    	  
      }

      //Need to look at the oai url explicity.
      NodeList nlInterface = ((Element) resource).getElementsByTagNameNS("*","interface");
      
      if(nlInterface.getLength() > 0) {
    	  for(int j = 0;j < nlInterface.getLength();j++) {
    	    typeAttribute = nlInterface.item(j).getAttributes().getNamedItem("xsi:type");
    	      if(typeAttribute == null) {
    	    	  //A couple of registries might not use 'xsi' so lets try to look it up via NS call.
    	    	  typeAttribute = nlInterface.item(j).getAttributes().getNamedItemNS("http://www.w3.org/2001/XMLSchema-instance","type");
    	      }
    	      if(typeAttribute != null && typeAttribute.getNodeValue().indexOf("OAI") != -1) {
    	    	
    	    	nl = ((Element)nlInterface.item(j)).getElementsByTagNameNS("*","accessURL");
    	    	if(nl.getLength() > 0)
    	    		break;
    	      }//if
    	  }
      }
      
      if(nl == null || nl.getLength() == 0) {
          log.error("Error did not find a AccessURL");
          rha.addStatError(identifier,VORESOURCE_VERSION,"No accessURL found");
          throw new RegistryException("No accessURL found");
      }
      if(!nl.item(0).hasChildNodes()) {
          log.error("Error did not find any text to the accessURL");
          rha.addStatError(identifier,VORESOURCE_VERSION,"No text found for the accessURL, seems to be an empty element");
          throw new RegistryException("No text found for the accessURL");          
      }
      accessURL = nl.item(0).getFirstChild().getNodeValue();
      
      if(accessURL.indexOf("?wsdl") != -1) {
          accessURL = accessURL.substring(0,accessURL.indexOf("?wsdl"));
      }
      log.info("The access URL = " + accessURL + " invocationType = " + typeAttribute.getNodeValue() + " identifier = " + identifier);
      if(dt != null) {
    	   log.info(" with date = " + dt.toString());
      }else {
    	  log.info("dt date is null");
      }
      try {
          String results = beginHarvest(accessURL, typeAttribute.getNodeValue(),dt, lastResumptionToken, identifier, null);
//        here we need to add new harvestdate to the stat
          rha.addStatNewDate(identifier,VORESOURCE_VERSION);
          if(results != null)
              rha.addStatInfo(identifier, VORESOURCE_VERSION, results);
      }catch(RegistryException re) {
          log.error(re);
          //rha.addStatError(identifier,VORESOURCE_VERSION,re.getMessage());
          throw re;
      }
      log.debug("exist beginHarvest(Node)");
   }
   
}