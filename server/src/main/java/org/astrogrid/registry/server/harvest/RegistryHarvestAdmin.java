package org.astrogrid.registry.server.harvest;

import org.astrogrid.registry.server.admin.RegistryAdminService;
import org.astrogrid.registry.common.RegistryValidator;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.admin.AuthorityList;
import org.astrogrid.util.DomHelper;
import org.astrogrid.registry.server.XSLHelper;
import org.astrogrid.registry.server.InvalidStorageNodeException;
import org.astrogrid.registry.RegistryException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.base.XMLDBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import junit.framework.AssertionFailedError;
import org.xmldb.api.base.Collection;


/**
 * Class: RegistryHarvestAdmin
 * Description: Similiar to RegistryAdminService in that it stores XML Resources
 * into the database along with methods to store status information.  The main difference
 * is the XML Resources to be stored are normally coming from an OAI harvest request/response.
 * So the update method must transform the OAI document verify it is valid and then store the information
 * into the database.
 * 
 * @author Kevin Benson
 * @TODO - Move the status information into some other class.
 *
 */
public class RegistryHarvestAdmin extends RegistryAdminService {
    
    /**
     * Logging variable for writing information to the logs
     */
	  private static final Log log =
          LogFactory.getLog(RegistryHarvestAdmin.class);
   
   /**
    * Default constructor which currently does not set or do anything.
    */
   public RegistryHarvestAdmin() {
       super(null,null,null);
   }
   
   /**
    * Method: addStatNewDate
    * Description: Adds Date information to the status document for a 
    * XML Resource.  The name of the status document is based around the 
    * identifier (normally identifier for a Registry type) since this method is
    * called from Harvesting.  The Date stored is used in the next harvest cycle to
    * call a Registry based on a 'from' date to get recently changed Resources.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    */
   public void addStatResourceHarvestDate(String identifier, String versionNumber, Date updateResDate) throws RegistryException {
   	   log.debug("begin addStatNewDate identifier = " + identifier + " version = " + versionNumber);
       Document statDoc = getStatus(identifier, versionNumber);
       if(statDoc != null) {
       	   log.debug("statDoc not null add Date elements");
           DateFormat shortDT = DateFormat.getDateTimeInstance();
           NodeList nl = statDoc.getElementsByTagNameNS("*","MostRecentResourceUpdateMillis");
           if(nl.getLength() > 0) {
               nl.item(0).getFirstChild().setNodeValue(String.valueOf(updateResDate.getTime()));
           }else {
               Element elem2 = statDoc.createElement("MostRecentResourceUpdateMillis");
               elem2.appendChild(statDoc.createTextNode(String.valueOf(updateResDate.getTime())));
               statDoc.getDocumentElement().appendChild(elem2);
           }
           nl = statDoc.getElementsByTagNameNS("*","MostRecentResourceUpdate");
           if(nl.getLength() > 0) {
               nl.item(0).getFirstChild().setNodeValue(shortDT.format(updateResDate));
           }else {
               Element elem = statDoc.createElement("MostRecentResourceUpdate");
               elem.appendChild(statDoc.createTextNode(shortDT.format(updateResDate)));
               statDoc.getDocumentElement().appendChild(elem);
           }
           log.debug("storeStat");
           storeStat(identifier, versionNumber, statDoc);
       }
       log.debug("end addStatNewDate");
   }   

   /**
    * Method: addStatNewDate
    * Description: Adds Date information to the status document for a 
    * XML Resource.  The name of the status document is based around the 
    * identifier (normally identifier for a Registry type) since this method is
    * called from Harvesting.  The Date stored is used in the next harvest cycle to
    * call a Registry based on a 'from' date to get recently changed Resources.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    */
   public void addStatNewDate(String identifier, String versionNumber) throws RegistryException {
   	   log.debug("begin addStatNewDate identifier = " + identifier + " version = " + versionNumber);
       Document statDoc = getStatus(identifier, versionNumber);
       if(statDoc != null) {
       	   log.debug("statDoc not null add Date elements");
           Date statsTimeMillis = new Date();
           DateFormat shortDT = DateFormat.getDateTimeInstance();
           NodeList nl = statDoc.getElementsByTagNameNS("*","StatsDateMillis");
           if(nl.getLength() > 0) {
               nl.item(0).getFirstChild().setNodeValue(String.valueOf(statsTimeMillis.getTime()));
           }else {
               Element elem2 = statDoc.createElement("StatsDateMillis");
               elem2.appendChild(statDoc.createTextNode(String.valueOf(statsTimeMillis.getTime())));
               statDoc.getDocumentElement().appendChild(elem2);
           }
           nl = statDoc.getElementsByTagNameNS("*","StatsDate");
           if(nl.getLength() > 0) {
               nl.item(0).getFirstChild().setNodeValue(shortDT.format(statsTimeMillis));
           }else {
               Element elem = statDoc.createElement("StatsDate");
               elem.appendChild(statDoc.createTextNode(shortDT.format(statsTimeMillis)));
               statDoc.getDocumentElement().appendChild(elem);
           }
           log.debug("storeStat");
           storeStat(identifier, versionNumber, statDoc);
       }
       log.debug("end addStatNewDate");
   }

   /*
   public void addResumptionToken(String identifier, String versionNumber, String resumptionToken) throws RegistryException {
       Document statDoc = getStatus(identifier, versionNumber);
       if(statDoc != null) {
           //Date statsTimeMillis = new Date();
           //DateFormat shortDT = DateFormat.getDateTimeInstance();
           NodeList nl = statDoc.getElementsByTagNameNS("*","LastResumptionToken");
           if(nl.getLength() > 0) {
               nl.item(0).getFirstChild().setNodeValue(resumptionToken);
           }else {
               Element elem2 = statDoc.createElement("LastResumptiontoken");
               elem2.appendChild(statDoc.createTextNode(resumptionToken));
               statDoc.getDocumentElement().appendChild(elem2);
           }//else
       }//if
   }
   */
   
    
   /**
    * Method: addStatInfo
    * Description: Adds an Info element to the status document for a 
    * XML Resource.  The Info Element contains the current date attribute and a 
    * text description of any usefull Information.  Typically a comma seperated list of
    * the identifiers harvested from a particular registry.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    */   
   public void addStatInfo(String identifier, String versionNumber, String info) throws RegistryException {
   	   log.debug("begin addStatInfo identifier = " + identifier + " version = " + versionNumber + " info = " + info);
       Document statDoc = getStatus(identifier, versionNumber);
       if(statDoc != null) {
       	   log.debug("found statDoc add the info elements");
           NodeList nl = statDoc.getElementsByTagNameNS("*","HarvestInfo");
           java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
           java.util.Calendar rightNow = java.util.Calendar.getInstance();
           Element elem = statDoc.createElement("Info");
           elem.setAttribute("date",sdf.format(rightNow.getTime()));
           elem.appendChild(statDoc.createTextNode(info));
           //see how long the Info list is and remove it.
           NodeList childNodes =  statDoc.getDocumentElement().getElementsByTagNameNS("*","Info");
           //log.info("Number of INFO elements found = " + childNodes.getLength());
           if(childNodes.getLength() > 60) {
      		   //log.info("removing child node length = " + childNodes.getLength());
      		  
        	   while(childNodes.getLength() > 30) {
        		   statDoc.getDocumentElement().removeChild(childNodes.item(0));
        	   }//for
           }
           if(nl.getLength() > 0) {
               nl.item(0).appendChild(elem);
           }else {
               Element lui = statDoc.createElement("HarvestInfo");
               lui.appendChild(elem);
               statDoc.getDocumentElement().appendChild(lui);
           }
           storeStat(identifier, versionNumber, statDoc);
       }else {
           log.error("No StatusDocument found to add info of " + info);
       }
       log.debug("end addStatInfo");
   }
   
   /**
    * Method: addStatError
    * Description: Adds an Error element to the status document for a 
    * XML Resource.  The Error Element contains the current date attribute and a 
    * text description of any Error occurred during harvesting.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    */    
   public void addStatError(String identifier, String versionNumber, String error) throws RegistryException {
   	   log.debug("begin addStatError identifier = " + identifier + " versionNumber = " + versionNumber + " error = " + error);
       Document statDoc = getStatus(identifier, versionNumber);
       if(statDoc != null) {
       	   log.debug("statDoc not null add the Error elements");
           NodeList nl = statDoc.getElementsByTagNameNS("*","HarvestInfo");
           java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
           java.util.Calendar rightNow = java.util.Calendar.getInstance();
           Element elem = statDoc.createElement("Error");
           elem.setAttribute("date",sdf.format(rightNow.getTime()));
           elem.appendChild(statDoc.createTextNode(error));  
           
         //see how long the Info list is and remove it.
           NodeList childNodes =  statDoc.getDocumentElement().getElementsByTagNameNS("*","Error");
           if(childNodes.getLength() > 60) {
        	   while(childNodes.getLength() > 30) {
        		  // log.info("removing child node length = " + childNodes.getLength());
        		   statDoc.getDocumentElement().removeChild(childNodes.item(0));
        	   }//for
           }
           if(nl.getLength() > 0) {
               nl.item(0).appendChild(elem);
           }else {
               Element lui = statDoc.createElement("HarvestInfo");
               lui.appendChild(elem);
               statDoc.getDocumentElement().appendChild(lui);
           }
           storeStat(identifier, versionNumber, statDoc);
       }else {
           log.error("No Status Document Found cannot set the error = " + error);
       }
       log.debug("end addStatError");
   }
   
   /**
    * Method: getStatus
    * Description: Get a Status Document for a particular Resource.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    * @return Document DOM object of the status document.  
    */
   public Document getStatus(String identifier, String versionNumber) throws RegistryException {
   	   log.debug("getStatus identifier = " + identifier + " versionNumber = " + versionNumber);
       String tempIdent = identifier;
       Document statDoc = null;
       if(identifier.startsWith("ivo"))
        tempIdent = identifier.substring(6);
       
       try {        
           XMLResource xmlr = xdbRegistry.getResource(tempIdent.replaceAll("[^\\w*]","_"),"statv" + versionNumber.replace('.','_'));           
           if(xmlr != null) {
               try {
                log.debug("it was not null in getStatus and content = " + xmlr.getContent().toString());
                statDoc = DomHelper.newDocument(xmlr.getContent().toString());
               }catch(Exception e) {
               	   log.error("exception trying to create statDoc from getStatus");
                   log.error(e);
               }
           }
       } catch(XMLDBException xe) {
               throw new RegistryException(xe);
       }    
       if(statDoc == null) {
       	 log.debug("No statDoc found so creating empty statDoc");
         statDoc = createNewStat(identifier, versionNumber);
       }
       log.debug("end getStatus");
       return statDoc;
   }
   
   /**
    * Method: createNewStat
    * Description: Creates a blank status document for a Resource in
    * the stat location.  A blank status document is just an empty HarvestInfo element.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    * @return Document DOM object of the {empty} status document.
    */
   private Document createNewStat(String identifier, String versionNumber)  throws RegistryException {
   	log.debug("begin createNewStat identifier = " + identifier + " version = " + versionNumber);
   	Document doc = null;
   	try {
   		doc = DomHelper.newDocument("<HarvestInfo></HarvestInfo>");
   		storeStat(identifier,versionNumber,doc.getDocumentElement());
   	}catch(Exception e) {
   		log.error(e);
   	}
   	log.debug("end createNewStat");
   	return doc;
   }

   /**
    * Method: storeStat
    * Description: stores an XML Status Document (DOM Node) into the database.
    * 
    * @param identifier - unique string for the XML Resource, since this is called from the 
    * harvest mechanism it is typically the identifier for a Registry type.
    * @param versionNumber - XML Resource version number (0.10 or 1.0).  Used for determining the
    * collection/table to store the document in the database e.g. statv{versionNumber}
    * @param statDoc - A DOM Document/Element to be stored in the database in the status location
    * based on the identifier and versionNumber parameters.   
    */
   private void storeStat(String identifier, String versionNumber, Node statDoc) throws RegistryException {
   	   log.debug("begin storeStat identifier = " + identifier + " versionNumber = " + versionNumber);
       String tempIdent = identifier;
       if(identifier.startsWith("ivo"))
        tempIdent = identifier.substring(6);
       
       try {
       	log.debug("store into the database");
       xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml",
               "statv" + versionNumber.replace('.','_'), statDoc);
       }catch(InvalidStorageNodeException in) {
           log.error(in);
           throw new RegistryException(in);
       }catch(XMLDBException xe) {
           throw new RegistryException(xe);
       }
       log.debug("end storeStat");
   }
    
    /**
     * Method: harvestingUpdate
     * Description: Also an update method that updates into this Registry's db. But it does no
     * special checking.  This is used internally by harvesting other registries to
     * go ahead and place the Resources into our db.  It does check the Registry type entries
     * coming in to verify the authority id's are not conflicting with other Registries. And it
     * also validates the XML to verify they validate agains the schema before being stored into the 
     * database. 
     * 
     * @param update A DOM of XML of  one or more Resources.
     * @param verisonNumber the version of the registry to be updated, this discovers the colleciton/table name
     * that is being updated.
     */
   public String harvestingUpdate(Document update, String identifier, String versionNumber) throws XMLDBException, InvalidStorageNodeException, IOException {
       log.debug("start updateNoCheck");
       Node root = null;
       String ident = null;
       String resKey = null;
       String tempIdent = null;
       AuthorityList tempAuthorityListKey = null;
       AuthorityList tempAuthorityListVal = null;
       boolean validateResourceXML = true;
       String updateFormat = "yyyy-MM-dd'T'HH:mm:ss";
       String returnString = "List below of Invalid Resources not updated and then a list of Updated Resources\r\n";
       XSLHelper xs = new XSLHelper();
       
       SimpleDateFormat sdf = new SimpleDateFormat(updateFormat);
       Date compareDate = null;
       try {
    	   compareDate = sdf.parse("1980-01-01T00:00:00");
       }catch(java.text.ParseException pe) {
    	   pe.printStackTrace();
       }
       //Date updateDate = new Date();
       //String updateDateString = sdf.format(updateDate);
       
       if(update == null) {
           throw new IOException("Error nothing to update 'null sent as Document'");
       }
       
       String authorityID = conf.getString("reg.amend.authorityid");      
       
       //String attrVersion = null;
       //the vr attribute can live at either or both of those elments and we just need to get the first one.
       //It is possible the <update> element will not be there hence we need to look at the root element
       //NodeList nl = DomHelper.getNodeListTags(update,"Resource","vr");
       NodeList nl = update.getElementsByTagNameNS("*","Resource");
       if(nl.getLength() == 0) {
           nl = update.getElementsByTagNameNS("*","resource");
       }
       
       if(nl.getLength() == 0) {
           log.debug("Nothing to Update");
           return "";
       }
       log.debug("the nl length of resoruces = " + nl.getLength());      

       //String versionNumber = attrVersion.replace('.','_');      
       
       //Currently all docs now have a xsl stylesheet when dealing
       //with OAI.  This might change in the near future.
       boolean hasStyleSheet = true;//conf.getBoolean("reg.custom.harveststylesheet." + versionNumber,false);
       Document xsDoc = null;
       //log.debug("Before " + DomHelper.DocumentToString(update));
       
       if(hasStyleSheet) {
          log.debug("performing transformation before analysis of update for versionNumber = " + versionNumber);
          try {
        	  NodeList convertCheck = update.getDocumentElement().getElementsByTagNameNS("*","Resource");
        	  log.info("checking versions and convertCheck");
        	  //Will factor away in the future.  Allows for a Registry to publish
        	  //0.10 data and have it transformed to 1.0 and stored into the 1.0 table.
        	  //To do this a Registry Type had to be stored into the 1.0 table/collection
        	  //that had a harvesting url that produced 0.10 data.
        	  if(versionNumber.equals("1.0") && convertCheck.getLength() > 0 &&
        	     convertCheck.item(0).getNamespaceURI().indexOf("0.10") != -1) {
        		  log.info("yes it needs converting to 1.0");
       			  xsDoc = xs.transformUpdate((Node)update.getDocumentElement(),"0.10",true);
       			  xsDoc = xs.transformVersionConversion((Node)xsDoc.getDocumentElement());
       			  log.info("XML result After converting 0.10-1.0 = " + DomHelper.DocumentToString(xsDoc));
        	  }
        	  else {
        	  	//Transform the OAI document to a typical VOResources document.
        	  	//OAI schema pretty much allows 'any' elements.  By
        	  	//transforming to VOREsources we can validate the XML.
        		  xsDoc = xs.transformUpdate((Node)update.getDocumentElement(),versionNumber,true);
        	  }
          }catch(RegistryException re) {
              log.error("Problem with xsl transformation of xml in the update method will try to use raw xml from web service ");
              log.error(re);
              xsDoc = update;
          }
       } else {
          xsDoc = update;
       }
       
       Hashtable defaultNS = new Hashtable();
	   try {
		   NamedNodeMap nnm = update.getDocumentElement().getAttributes();
		   for(int ww = 0; ww < nnm.getLength();ww++) {
			   log.info("NNM index = " + ww);
			   Node tmpNode = nnm.item(ww);
			   if(tmpNode.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/") &&
			      tmpNode.getNodeName().startsWith("xmlns:") &&
			      !tmpNode.getNodeValue().startsWith("http://www.openarchives.org")) {
				   //a default namespace found
				   defaultNS.put(tmpNode.getNodeName(), tmpNode.getNodeValue());
			   }
		   }
		   update.getDocumentElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:tmp", "http://www.astrogrid.org/test");
	   }catch(Exception e) {
		   log.info("exception trying to getattribute node");
		   e.printStackTrace();
	   }
       
       
       //collection/table name to be used for storing into the db.
       String collectionName = "astrogridv" + versionNumber.replace('.','_');
       log.debug("Collection Name = " + collectionName);
       

       //validate Resources individually this becomes true
       //if the whole VOResources document has errors and is invalid.
       boolean validateSingleResources = false;
	   int loopi = 0;
       
       //Grab all the Resource Elements.
       nl = xsDoc.getElementsByTagNameNS("*","Resource");
       if(defaultNS.size() > 0) {
	       while(loopi < nl.getLength()) {
//			   log.info("loopi = " + loopi + " and nl.getlength = " + nl.getLength());
			   try {
				   Object []defaultNSKey = defaultNS.keySet().toArray();
				   for(int ns = 0;ns < defaultNSKey.length;ns++) {
					   ((Element)nl.item(loopi)).setAttributeNS("http://www.w3.org/2000/xmlns/", (String)defaultNSKey[ns], (String)defaultNS.get((String)defaultNSKey[ns]));
				   }
				   loopi++;
			   }catch(Exception e) {
				   log.error("default namespaces found but could not add it to the individual resource elements");
				   loopi++;
	           }//catch
		   }//while
       }
       //log.debug("After = " + DomHelper.DocumentToString(xsDoc));
       
       //adding back the && check for 0.10 collection there is just
       //no point in validating 0.10 during harvests to many mistakes and
       //invalid to schema.
       if(validateResourceXML && !collectionName.equals("astrogridv0_10")) {
           try {
               //validate the xml, the xsl should have made it into a well-formed xml doc with a 
        	   //wrapper(root element) valid to schema, as far as the rest of the xml the validator
        	   //will check to see if it conforms to schema.
               RegistryValidator.isValid(xsDoc);
           }catch(AssertionFailedError afe) {
               afe.printStackTrace();
               validateSingleResources = true;
               log.warn("Error invalid document = " + afe.getMessage());
               log.warn("though validation error occurred, Resource Elements will be revalidated individually to better see the error.");
           }//catch
       }

       
       //If we are validating Single Resources because some are 
       //invalid then loop through the Single Resoruces and remove 
       //the invalid entries.
       if(validateSingleResources) {
       	   log.info("Number of Resources to try validating individually (invalid will be removed and logged in the status page) = " + nl.getLength());
       	   loopi = 0;
    	   while(loopi < nl.getLength()) {
    		   //log.info("loopi = " + loopi + " and nl.getlength = " + nl.getLength());
    		   try {
    			   
    			   RegistryValidator.isValid(((Element)nl.item(loopi)),"Resource");
    			   loopi++;
    		   }catch(AssertionFailedError afe) {
    			   //log.warn("Invalid Resource for Identifier: ivo://" + RegistryDOMHelper.getAuthorityID( ((Element)nl.item(loopi)) ) + "/" +  RegistryDOMHelper.getResourceKey( ((Element)nl.item(loopi)) ));
    			   //log.warn(afe.getMessage());
    			   returnString += "Invalid Resource that did not get updated - ivo://" + RegistryDOMHelper.getAuthorityID( ((Element)nl.item(loopi)) ) + "/" +  RegistryDOMHelper.getResourceKey( ((Element)nl.item(loopi)) ) + " \r\n Error:" + afe.getMessage() + "\r\n";
    			   //ident = RegistryDOMHelper.getAuthorityID( currentResource);
    		       //resKey = RegistryDOMHelper.getResourceKey( currentResource);
    			   xsDoc.getDocumentElement().removeChild(nl.item(loopi));           
               }//catch
    	   }//while
       }
       
       log.info("Number of Resources to be updated = " + nl.getLength());
       
       
       AuthorityList authCheck = new AuthorityList(authorityID,versionNumber);
       //ManaghAuths contains a hashmap of all the authorityid's and the authorityid
       //that owns/manages.  If it is not populated yet then have it
       //populated via the AuthorityListManager class.
       if(manageAuths.isEmpty()) {
           try {
               alm.populateManagedMaps(collectionName, versionNumber);
           }catch(XMLDBException xmldbe) {
               xmldbe.printStackTrace();
               log.error(xmldbe);
           }
       }else if(!manageAuths.isEmpty() && !manageAuths.containsKey(authCheck)) {
       	   //hmmm somehow the manageAuths is populated with something but does
       	   //not have our main authorityid of this registry in it which it normally should have.
       	   //so re-populate the map just in case (possibly registry setup to harvest but has not self-registered
       	   //which is ok to do).
           try {
               alm.populateManagedMaps(collectionName, versionNumber);
           }catch(XMLDBException xmldbe) {
               xmldbe.printStackTrace();
               log.error(xmldbe);              
           }
       }
       
       //hmmm user is doing harvesting before he setup the registry, okay let it go.
       if(manageAuths.isEmpty()) {
           log.debug("Appears user is doing harvesting for versionNumber=" + versionNumber + " and has not self registered");
           //okay this must be the very first time into the registry where
           //registry is empty. So put a an empty entry for this version.          
       }
       String errorMessages = "";

       //get how many resources there are. Use a 'final' 
       //because as we modify the nodelist placing it in the db
       //the number will be reduced so use a final.
       final int resourceNum = nl.getLength();
       log.info("Attempting to update number of Resources = " + resourceNum);
       boolean updateResource = true;
       //get a Collection object we use this so we can store XML Resources
       //a little faster instead of open, store, close each individual resource.
       //Now we can open, store all, close.
       Collection harvestColl = xdbRegistry.getCollection(collectionName,true);
       Date updateRes = null;
       try {
    
       	//loop through the resources.
       for(int i = 0;i < resourceNum;i++) {
          updateResource = true;
          //only get the '0' element because as we append/modify
          //the nodelist it will be removed hence like a queue.
          Element currentResource = (Element)nl.item(0);
          ident = RegistryDOMHelper.getAuthorityID( currentResource);
          resKey = RegistryDOMHelper.getResourceKey( currentResource);
          

          //check if we are harvesting ourselves or another registry sending back
          //authorityid's already owned by this registry.
          if(manageAuths.containsValue(new AuthorityList(ident,versionNumber,authorityID))) {
              log.error("Either your harvesting your own Registry or another Registry is submitting authority id's owned by this registry. Ident = " + ident);
              currentResource.getParentNode().removeChild(currentResource);
              continue;
          } else {
              //tempIdent = "ivo://" + ident;
              tempIdent = ident;
              if(resKey != null) tempIdent += "/" + resKey;
              log.debug("the ident in updateNoCheck = " + tempIdent);
              
              //Do some special processing if it is a Registry Type
              //to make sure we get the authority id's it manages
              //set in our list and verify there is no conflicts.
              if(currentResource.hasAttributes()) {                 
                  Node typeAttribute = currentResource.getAttributes().getNamedItem("xsi:type");
                  String nodeVal = null;
                  if(typeAttribute != null) {
                      nodeVal = typeAttribute.getNodeValue();
                  }
                  String updateString = currentResource.getAttribute("updated");
                  try { 
                	  updateRes = sdf.parse(updateString);                  
                	  if(!validateSingleResources && compareDate != null && updateRes != null && updateRes.after(compareDate)) {
                		  compareDate = sdf.parse(updateString);
                	  }
                  }catch(java.text.ParseException pe) {
                  	   pe.printStackTrace();
                  }

                  log.debug(
                  "Checking xsi:type for a Registry: = " 
                  + nodeVal);
                      //check if it is a registry type.
                      if(nodeVal != null && nodeVal.indexOf("Registry") != -1)
                      {
                         log.debug("A RegistryType in harvestingUpdate add stats");
                         //update this registry resource into our registry.
                            NodeList manageList = adminHelper.getManagedAuthorities(currentResource);
                            if(currentResource.hasAttribute("status") &&
                               currentResource.getAttribute("status").equals("active")) {
	                            if(manageList.getLength() > 0)
	                                alm.clearManagedAuthoritiesForOwner(ident, versionNumber);
	                            else {
	                                log.warn("Registry type from a Harvest has no ManagedAuthorities; AuthorityID = " + ident + " versionNumber = " + versionNumber);
	                                errorMessages += "Registry type from a Harvest has no ManagedAuthorities, this is okay but rare logging as error for double checking; AuthorityID = " + ident + " versionNumber = " + versionNumber + "\n";
	                                updateResource = false;
	                            }
                            
	                            //verify there are no conflicts on the authority id
	                            //already being owned by some other Registry.
	                            //only check active Registry types.
	                            for(int k = 0;k < manageList.getLength();k++) {
	                                String manageNodeVal = manageList.item(k).getFirstChild().getNodeValue();
	                                if(manageAuths.containsKey((tempAuthorityListKey = new AuthorityList(manageNodeVal,versionNumber)))) {
	                                    tempAuthorityListVal = (AuthorityList)manageAuths.get(tempAuthorityListKey);
	                                    log.error("Error - mismatch: Tried to update a Registry Type that has this managed Authority: " + manageNodeVal +
	                                         " with this main Identifiers Authority ID " + ident + " This mismatches with another Registry Type that ownes/manages " + 
	                                         " this same authority id, other registry type authority id: " + tempAuthorityListVal.getOwner() + "; NO UPDATE WILL HAPPEN FOR THIS Registry Type Resouce Entry");
	                                    errorMessages += "Error - mismatch: Tried to update a Registry Type that has this managed Authority: " + manageNodeVal +
	                                    " with this main Identifiers Authority ID " + ident + " This mismatches with another Registry Type that ownes/manages " + 
	                                    " this same authority id, other registry type authority id: " + tempAuthorityListVal.getOwner() + "; NO UPDATE WILL HAPPEN FOR THIS Registry Type Resouce Entry\n";
	                                    updateResource = false;                                   
	                                }//if                           
	                                if(manageNodeVal != null && manageNodeVal.trim().length() > 0) {
	                                    manageAuths.put(tempAuthorityListKey, new AuthorityList(manageNodeVal,versionNumber,ident));
	                                }//if
	                            }//for
                            }else {
                            	//not an active resource lets clear it of
                            	//any managed authorities if there are any.
                            	if(manageList.getLength() > 0) {
	                                alm.clearManagedAuthoritiesForOwner(ident, versionNumber);
                            	}//if
                            }
                            if(errorMessages.trim().length() > 0) {
                            	//throw new IOException(errorMessages);
                            	/*
                                try {
                                    addStatError(tempIdent, versionNumber, errorMessages);
                                }catch(RegistryException re) { }
                                errorMessages = "";
                                */
                            }                            
                      }//if
                  }//if
                      if(updateResource) {
                      		//store the Resource into the db.
                          root = xsDoc.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1","agr:AstrogridResource");
                          root.appendChild(currentResource);
                          xdbRegistry.storeXMLResource(harvestColl, tempIdent.replaceAll("[^\\w*]","_") + ".xml", root); 
                                                         //collectionName, root /*currentResource*/);
                          //xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml", 
                                                       //collectionName, root /*currentResource*/);
                          returnString += "Updated: " + tempIdent + ",\r\n";
                      }else {
                      	//could not update the resource because of some other error.
                      	//So remove the resource so we can continue through the loop.
                        currentResource.getParentNode().removeChild(currentResource);
                      }
                      
          }//else
       }//for
       try {
	       if(resourceNum > 0 && compareDate.before(sdf.parse("1982-01-01T00:00:00"))) {
	    	   log.error("Error: No 'updated' dates were found on the Resources to set the Status of Last Harvest Date.");
	    	   //log.er
	       }else {
	    	   addStatResourceHarvestDate(identifier, versionNumber, compareDate);    	   
	       }
       }catch(java.text.ParseException pe) {
    	   pe.printStackTrace();
       }catch(org.astrogrid.registry.RegistryException re) {
    	   re.printStackTrace();
       }
       
       }finally {
       	//close the collection were done.
           xdbRegistry.closeCollection(harvestColl);
           //System.out.println("harvesting update took this long with remove= " + (System.currentTimeMillis() - begin));
       }
       log.debug("end updateNoCheck");
       
       //return string should have comma seperated list of identifiers updated.       
       return returnString;
   }
}