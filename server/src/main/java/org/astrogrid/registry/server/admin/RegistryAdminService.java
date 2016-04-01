package org.astrogrid.registry.server.admin;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import org.xmldb.api.base.ResourceSet;

import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import java.util.HashMap;
import java.util.Date;

import org.astrogrid.store.Ivorn;
import org.astrogrid.config.Config;
import org.astrogrid.registry.server.XSLHelper;

import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.common.RegistryValidator;
import org.astrogrid.util.DomHelper;
import org.astrogrid.registry.RegistryException;
import org.astrogrid.registry.server.SOAPFaultException;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.registry.server.InvalidStorageNodeException;
import org.astrogrid.registry.server.query.QueryHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;

import junit.framework.AssertionFailedError;
import org.xmldb.api.base.XMLDBException;

//import org.astrogrid.security.ServiceSecurityGuard;
/**
 * Class Name: RegistryAdminService
 * Description: This class represents the web service to the Administration
 * piece of the registry.  This class will handle inserts/updates Resources
 * in the registry.  It also makes sure Authority ids
 * do not conflict with one another.  A registry may manage 1 to many authority id's, but no 
 * other Regitry type can have those authority id's.  Read more on the manageAuths HashMap variable for more
 * information on this.
 * 
 * @author Kevin Benson
 * 
 */
public abstract class RegistryAdminService {
                          
    /**
     * Logging variable for writing information to the logs
     */
   private static final Log log = 
                               LogFactory.getLog(RegistryAdminService.class);
   
   
   protected XMLDBRegistry xdbRegistry = null;
   
   
   
   /**
    * @deprecated Not used anymore originally an outputstream to stream back results.
    */
   protected OutputStream out = null;

   /**
    * conf - Config variable to access the configuration for the server normally
    * jndi to a config file.
    * @see org.astrogrid.config.Config
    */   
   public static Config conf = null;


   /**
    * Hashmap of the Authories managed by this registry, and the authority
    * ids managed by other registries. Used for determining if things are valid
    * for updating to this registry and verify it is not owned by another registry. Uses the
    * AuthorityList object to keep track of Authority id's.  Both key and value are AuthorityList objects.
    * The HashMap is normally in the form of a key being a version number + main authority id and a value
    * being the form of a version number + authority id(being managed) + main authority (the owner). 
    */   
   public static HashMap manageAuths;
   
   protected AdminHelper adminHelper = null;
   
   protected AuthorityListManager alm = null;
   
   /**
    * Static to be used on the initiatian of this class for the config
    */
   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         manageAuths = new HashMap();
         //otherAuths = new HashMap();         
      }      
   }
   
   /**
    * Default Constructor: RegistryAdminService
    * Description: Instantiated by the XFire service for the Update WebService or via the
    * jsp pages (currently editEntry.jsp)
    *
   
   public RegistryAdminService() {
       xdbRegistry = new XMLDBRegistry();
       adminHelper = new AdminHelper();
       alm = new AuthorityListManager(xdbRegistry);
   } */ 
   
   private String contractVersion = null;
   private String voResourceVersion = null;
   private String adminwsdlNS = null;
   
   public RegistryAdminService(String contractVersion, String voResourceVersion, String adminwsdlNS) {
	   this.contractVersion = contractVersion;
	   this.voResourceVersion = voResourceVersion;
	   this.adminwsdlNS = adminwsdlNS;
       xdbRegistry = new XMLDBRegistry();
       adminHelper = new AdminHelper();
       alm = new AuthorityListManager(xdbRegistry);
   }
      
   /**
    * Method: updateResponse
    * Description: Called by the web service method and jsp's, performs an update to the registry. It can
    * handle many Resource elements if necessary to do multiple updates to the registry.  If a element
    * is not present in the database it automatically inserts.  Only Resource elements that are managed by
    * this registry are allowed, with the exception to Registry types(for discovering new registries)
    * and Authority types(for inserting an authority id to be managed by this registry).
    * 
    * @param update XML DOM containing Resource XML elements.
    * @param version specify the version.  This is the VOResource 'vr namespace' version. May be null in
    * which case will see if there is a version attribute or try to find it from the vr namespace and if that
    * fails grabs the default version from the properties.
    * @return Nothing is returned except an empty UpdateResponse element for conforming with SOAP standards
    * of a wrapped wsdl.
    */
   protected Document updateResource(Document update) throws SOAPFaultException {
      log.debug("start updateResource");
      long beginUpdate = System.currentTimeMillis();
      boolean error = false;
      String soapErrorMessage = "";
      String version = voResourceVersion;

      //get the main authority id for this registry.
      String authorityID = conf.getString("reg.amend.authorityid");
      
      log.debug("Default AuthorityID for this Registry = " + authorityID);
      
      // Transform the xml document into a consistent way.
      // xml can come in a few various forms.  This xsl will make it
      // consistent in the db and throughout this registry.
      
      Document xsDoc = null;          
      boolean hasStyleSheet = false;            
      
      if(update == null) {
    	  
          throw new SOAPFaultException("Server Error: " + "Nothing on request 'null sent'","Nothing on request 'null sent'", adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);          
      }
      
      
      //Get all the Resource elements
      NodeList nl = null;
      String versionNumber = null;
      
      if(version != null)
    	  versionNumber = version;
      else {
    	  version = update.getDocumentElement().getAttribute("version");
    	  if(version.trim().length() > 0) {
    		  versionNumber = version;
    	  }else {
    		  versionNumber = RegistryDOMHelper.findVOResourceVersionFromNode((Node)update.getDocumentElement());
    		  if(versionNumber == null) {
    			  throw new SOAPFaultException("Server Error: " + "Cannot determine version number (voresource version) to make update","Cannot determine version number (voresource version) to make update", adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
    		  }//if
    	  }
      }

      //Make a date. The registry automatically fills in the updated attribute for a Resource.
      String updateFormat = "yyyy-MM-dd'T'HH:mm:ss";
      if(versionNumber.equals("0.10")) {
    	  updateFormat = "yyyy-MM-dd";
      }
      SimpleDateFormat sdf = new SimpleDateFormat(updateFormat);
      Date updateDate = new Date();
      String updateDateString = sdf.format(updateDate);
      
      
      log.debug("Registry Version being updated = " + versionNumber);      
      String collectionName = "astrogridv" + versionNumber.replace('.','_');
      log.debug("Collection Name = " + collectionName);
      //do we have a stylesheet to massage the data to make it consistent in the db.
      hasStyleSheet = true;//conf.getBoolean("reg.custom.updatestylesheet." + versionNumber,false);
      //perform the transformation if necessary; either way set xsDoc to the
      //Document element to perform the updates through.
      XSLHelper xs = new XSLHelper();
      if(hasStyleSheet) {
         log.debug("performing transformation before analysis of update for versionNumber = " + versionNumber);
         try {
             xsDoc = xs.transformUpdate((Node)update.getDocumentElement(),versionNumber,false);
         }catch(RegistryException re) {
             log.error("Problem with xsl transformation of xml in the update method will try to use raw xml from web service ");
             log.error(re);
             xsDoc = update;
         }
      } else {
         xsDoc = update;
      }
      
      //Get all the Resource nodes.
      nl = xsDoc.getElementsByTagNameNS("*","Resource");
      if(nl.getLength() == 0) {
          throw new SOAPFaultException("Server Error: " + "No Resources Found to be updated be sure you are submitting 'Resource' elements not 'resource' or other variants","No Resources Found to be updated be sure you are submitting 'Resource' elements not 'resource' or other variants", adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);          
      }
      
      //is validation turned on.
      boolean validateXML = true;//conf.getBoolean("reg.amend.validate",false);
      if(validateXML) {
          try {
        	  String validRootElement = xsDoc.getDocumentElement().getLocalName();
        	  //System.out.println("In RAS before validate element = " + DomHelper.ElementToString(xsDoc.getDocumentElement()));
        	  if(validRootElement.equals("Update") || validRootElement.indexOf("Update") != -1) {
        		  validRootElement = xsDoc.getDocumentElement().getFirstChild().getLocalName();
        	  }
              //validate the xml (start with the element below the <UPDATE> from the soap body)
              RegistryValidator.isValid(xsDoc,validRootElement);
          }catch(AssertionFailedError afe) {
              afe.printStackTrace();
              log.error("Error invalid document = " + afe.getMessage());
              throw new SOAPFaultException("Server Error: " + "Invalid update, document not valid ","Server Error: " + "Invalid update, document not valid " + afe.getMessage(), adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);           
          }//catch
      }//if
      log.info("Number of Resources to be attempted for update = " + nl.getLength());      
      AuthorityList someTestAuth = new AuthorityList(authorityID,versionNumber);
      //our cache of managed authorityid's is empty (container must have started).
      //so go and see if we can fill it up based on Managed Authority elements from
      //Registry types.
      if(manageAuths.isEmpty()) {
          try {
              alm.populateManagedMaps(collectionName, versionNumber);
          }catch(XMLDBException xmldbe) {
              xmldbe.printStackTrace();
              throw new SOAPFaultException("Server Error: " + xmldbe.getMessage(),xmldbe, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
          }          
      }else if(!manageAuths.isEmpty() && !manageAuths.containsKey(someTestAuth)) {
          //todo: I do not believe this is needed
          //I guess sort of a safety check to make sure we have processed all our
          //authority id's into the hashmap.
          try {
              alm.populateManagedMaps(collectionName, versionNumber);
          }catch(XMLDBException xmldbe) {
              xmldbe.printStackTrace();
              throw new SOAPFaultException("Server Error: " + xmldbe.getMessage(),xmldbe,adminwsdlNS,  SOAPFaultException.ADMINSOAP_TYPE);              
          }
      }

      if(manageAuths.isEmpty()) {
          //okay this must be the very first time into the registry where
          //registry is empty.
          log.info("Empty Registry; no registry types found");
      }
      
      Node root = null;
      String ident = null;
      String resKey = null;
      String tempIdent = null;
      boolean addManageError = false;
      String manageNodeVal = null;
      AuthorityList tempAuthorityListKey = null;
      AuthorityList tempAuthorityListVal = null;      

      final int resourceNum = nl.getLength();
      //go through the various resource entries.
      for(int i = 0;i < resourceNum;i++) {
         error = false;
         long beginQ = System.currentTimeMillis();
         Element currentResource = (Element)nl.item(0);
         ident = RegistryDOMHelper.getAuthorityID(currentResource);
         log.info("here is the auth id(1) = " + ident);
         if(ident == null || ident.trim().length() <= 0) {
             throw new SOAPFaultException("Server Error: " + "Could not find the AuthorityID from the Identifier","Could not find the AuthorityID from the Identifier", adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);             
         }//if
         log.debug("here is the ident/authid = " + ident);
         resKey = RegistryDOMHelper.getResourceKey(currentResource);
         log.debug("here is the reskey = " + resKey);
         log.info("inspecting in updateResource this auth id = " + ident + " and reskey = " + resKey);
         //set the currentResource element.
         
         //Sometimes there are other namespaces defined above the
         //Resource element so be sure this Resource element has a
         //those defined namespaces. Otherwise there could be a slight risk
         //of having invalid xml.
         Node parentNode = currentResource.getParentNode();
         if(parentNode != null && Node.ELEMENT_NODE == parentNode.getNodeType()) {
             
             //get the attributes.
             NamedNodeMap attrNNM = parentNode.getAttributes();             
             for(int k= 0;k < attrNNM.getLength();k++) {
                 Node attrNode = attrNNM.item(k);
                 if(!currentResource.hasAttribute(attrNode.getNodeName()) &&
                    !currentResource.hasAttributeNS(attrNode.getNamespaceURI(),attrNode.getLocalName())) {
                     //okay were only after namespaces. if it is xmlns then set it on our resource.
                     if(attrNode.getNodeName().indexOf("xmlns") != -1 && 
                        attrNode.getNodeValue().indexOf("VOResourcesUpdate") == -1) {
                         currentResource.setAttributeNodeNS((Attr)attrNode.cloneNode(true));
                         //currentResource.setAttributeNS(attrNode.getNamespaceURI(),attrNode.getNodeName(),
                         //                               attrNode.getNodeValue());
                     }//else
                 }//if
             }//for
         }//if
         
         
         if(!currentResource.hasAttribute("status")) {
        	 currentResource.setAttribute("status", "active");
         }
         
         //set our new updated date attribute as well for the current date&time.
         currentResource.setAttribute("updated",updateDateString);
         
         
         //set a temporary identifier.
         tempIdent = ident;
         if(resKey != null && resKey.trim().length() > 0) tempIdent += "/" + resKey;
      
         try {
        	 if(xdbRegistry.getResource(tempIdent.replaceAll("[^\\w*]","_"),collectionName) == null) {
        		 currentResource.setAttribute("created", updateDateString);
        	 }
         } catch(XMLDBException xdbe) {
             log.error(xdbe);
             throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe,adminwsdlNS,  SOAPFaultException.ADMINSOAP_TYPE);
         } 
             
         log.debug("serverside update ident = " + ident + " reskey = " + 
                  resKey + " the nl getlenth here = " + nl.getLength());
         
         //Get the xsi:type.
         String nodeVal = "no type";         
         if(currentResource.hasAttributes()) {
             Node typeAttribute = currentResource.getAttributes().getNamedItem("xsi:type");
             if(typeAttribute != null) {
                 nodeVal = typeAttribute.getNodeValue();
             }//if
         }//if
         log.debug("The xsi:type for the Resource = " + nodeVal);
         
         //if we do then update it in the db.
         //do we manage this authority id, if so then add the resource. Unless it is a Registry
         //type then we need to process its new managedAuthority list to make sure there is no 
         //conflict with authority id's.
         if(manageAuths.containsValue(new AuthorityList(ident,versionNumber, authorityID)) &&
            nodeVal.indexOf("Registry") == -1) {
            //Essentially chop off other elemens wrapping the Resource element, and put
            //our own element. Would have been nice just to store the Resource element
            //at the root level, but it seems the XQueries on the database have problems
            //with that.
            root = xsDoc.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1","agr:AstrogridResource");
            root.appendChild(currentResource);
            try {
               xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml",
                                            collectionName, root/*currentResource*/);
               /*
               xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml",
                                            "statv" + versionNumber.replace('.','_'), 
                                            adminHelper.createStats(tempIdent));
               */               
            } catch(XMLDBException xdbe) {
               log.error(xdbe);
               throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe,adminwsdlNS,  SOAPFaultException.ADMINSOAP_TYPE);
            } catch(Exception e) {
                log.error(e);
                throw new SOAPFaultException("Server Error: " + e.getMessage(),e, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
            }
         }else {
             //Okay it is either not managed by this Registry or a Registry type.
            addManageError = true;
            //check if it is a registry type.
            if(nodeVal.indexOf("Registry") != -1) {
                addManageError = false;
                log.debug("This is a RegistryType");
                root = xsDoc.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1","agr:AstrogridResource");
                root.appendChild(currentResource);                
                NodeList manageList = adminHelper.getManagedAuthorities(currentResource);
                boolean managedAuthorityFound = false;
                if(manageList.getLength() > 0)
                    alm.clearManagedAuthoritiesForOwner(ident,versionNumber);
                for(int k = 0;k < manageList.getLength();k++) {
                    manageNodeVal = manageList.item(k).getFirstChild().getNodeValue();
                    log.debug("try adding new manage node for this registry = " + manageNodeVal);
                    if(manageAuths.containsKey((tempAuthorityListKey = new AuthorityList(manageNodeVal,versionNumber)))) {
                        tempAuthorityListVal = (AuthorityList)manageAuths.get(tempAuthorityListKey);
                        log.error("Error - mismatch: Tried to update a Registry Type that has this managed Authority: " + manageNodeVal +
                                  " with this main Identifiers Authority ID " + ident + " This mismatches with another Registry Type that ownes/manages " + 
                                  " this same authority id, other registry type authority id: " + tempAuthorityListVal.getOwner());
                        error = true;
                        soapErrorMessage += "Error - mismatch: Tried to update a Registry Type that has this managed Authority: " + manageNodeVal +
                        " with this main Identifiers Authority ID " + ident + " This mismatches with another Registry Type that ownes/manages " + 
                        " this same authority id, other registry type authority id: " + tempAuthorityListVal.getOwner();
                        continue;
                        /*
                        return SOAPFaultException.createAdminSOAPFaultException("Mismatch on Authority id(s)",new RegistryException("Error - mismatch: Tried to update a Registry Type that has this managed Authority: " + manageNodeVal +
                                        " with this main Identifiers Authority ID " + ident + " This mismatches with another Registry Type that ownes/manages " + 
                                        " this same authority id, other registry type authority id: " + tempAuthorityListVal.getOwner()));
                        */
                     }//if
                     manageAuths.put(new AuthorityList(manageNodeVal, versionNumber),
                                     new AuthorityList(manageNodeVal, versionNumber, ident));
                     if(manageNodeVal.trim().equals(ident.trim()))
                         managedAuthorityFound = true;
                }//for
                if(!managedAuthorityFound) {
                	/*
                    log.error("Server Error: " + "Trying to update the main Registry Type for this Registry with no matching Managed AuthorityID with the Identifiers AuthorityID; The AuthorityID = " + ident);
                    error = true;
                    soapErrorMessage += "Trying to update the main Registry Type for this Registry with no matching Managed AuthorityID with the Identifiers AuthorityID; The AuthorityID = " + ident;
                    //return SOAPFaultException.createAdminSOAPFaultException("No matching authority id found",new RegistryException("Trying to update the main Registry Type for this Registry with no matching Managed AuthorityID with the Identifiers AuthorityID; The AuthorityID = " + ident));
                     * 
                     */
                }
                if(error) {
                    continue;
                }

                try {
                	//System.out.println("okay storing the registry type root doc = " + DomHelper.ElementToString((Element)root) + " and collectionanme = " + collectionName);
                    xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml", 
                                                 collectionName, root /*currentResource*/);
                    /*
                    if(xdbRegistry.getResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml",collectionName) == null) {
                        xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml",
                                                     "statv" + versionNumber.replace('.','_'), 
                                                     adminHelper.createStats(tempIdent,false));                        
                     }
                     */
                 } catch(XMLDBException xdbe) {
                     log.error(xdbe);
                     throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                 } catch(InvalidStorageNodeException isne) {
                     log.error(isne);
                     throw new SOAPFaultException("Server Error: " + isne.getMessage(),isne,adminwsdlNS,  SOAPFaultException.ADMINSOAP_TYPE);                     
                 }
             }else if(nodeVal.indexOf("Authority") != -1) {
                 // Okay it is an AuthorityType and if no other registries 
                 // manage this authority then we can place it in this 
                 // registry as a new managed authority.
                 if(!manageAuths.containsKey(new AuthorityList(ident,versionNumber))) {
                    log.info(
                    "This is an AuthorityType and not managed by other authorities");
                    addManageError = false;
                    // Grab our current Registry resource we need to add
                    // a new managed authority tag.
                    QueryHelper queryHelper = new QueryHelper(versionNumber);
                    Document loadedRegistry = null;
                    /*try {*/
                    	ResourceSet mainRegRS = queryHelper.loadMainRegistry();
                    try{
                    	
                    	if(mainRegRS.getSize() != 1) {
                    		throw new SOAPFaultException("Server Error: Could not find this registries main Registry Resource which is needed","Server Error: Could not find this registries main Registry Resource which is needed", adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                    	}
	                    loadedRegistry = DomHelper.newDocument(queryHelper.loadMainRegistry().getResource(0).getContent().toString());
                    }catch(org.xmldb.api.base.XMLDBException xdbe) {
                    	throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);                    
                    }catch(javax.xml.parsers.ParserConfigurationException pce) {
                    	throw new SOAPFaultException("Server Error: " + pce.getMessage(),pce, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                    }catch(org.xml.sax.SAXException sae) {
                    	throw new SOAPFaultException("Server Error: " + sae.getMessage(),sae, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                    }catch(java.io.IOException ioe) {
                    	throw new SOAPFaultException("Server Error: " + ioe.getMessage(),ioe, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                    }

                    // Get a ManagedAuthority Node region/area
                    // so we can append a sibling to it, and
                    // use its info for creating another ManagedAuthority
                    // element.
                    Node manageNode = 
                        adminHelper.getManagedAuthorityID(loadedRegistry);
                    if(manageNode != null) {
                       log.debug(
                         "creating new manage element for authorityid = " + ident);
                       // Create a new ManagedAuthority element.
                       Element newManage = loadedRegistry.
                                           createElementNS(
                                             manageNode.getNamespaceURI(),
                                             manageNode.getNodeName());
                       // Put in the text node the new ident.
                       log.info("adding ident for managed authority = " + ident);
                       newManage.appendChild(loadedRegistry.
                                             createTextNode(ident));
                       
                       // For some reason the DOM model threw exceptions 
                       // when I tried to insert it as a sibling after 
                       // another existing ManagedAuthority tag, so just 
                       // add it to the end for now; just by luck the managedAuthority element is the last
                       // child for a Registry type otherwise we would be creating not valid to schema xml here.
                       // but something we should look at.
                       NodeList resListForAuth = loadedRegistry.getElementsByTagNameNS("*","Resource");
                       resListForAuth.item(0).appendChild(newManage);
                       manageAuths.put(new AuthorityList(ident,versionNumber),new AuthorityList(ident,versionNumber,authorityID));

                       // Update our currentResource into the database
                       root = xsDoc.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1","agr:AstrogridResource");
                       root.appendChild(currentResource);
                       try {
                    	   /*
                           xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml", 
                                                        "statv" + versionNumber.replace('.','_'), 
                                                        adminHelper.createStats(tempIdent));
                           */
                           xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml", 
                                                        collectionName, root /*currentResource*/);
                       } catch(XMLDBException xdbe) {
                           log.error(xdbe);
                           throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);                               
                       } catch(InvalidStorageNodeException isne) {
                           log.error(isne);
                           throw new SOAPFaultException("Server Error: " + isne.getMessage(),isne, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
                       }
                       
                       // Now get the information to re-update the
                       // Registry Resource which is for this registry.
                       ident = RegistryDOMHelper.getAuthorityID(loadedRegistry.
                                              getDocumentElement());
                       log.debug("the ident from loaded registry right before update = " + ident);
                       resKey = RegistryDOMHelper.getResourceKey(loadedRegistry.
                                               getDocumentElement());
                       log.debug("the resKey form loaded registry right before update = " + resKey);
                       //tempIdent = "ivo://" + ident;
                       tempIdent = ident;
                       if(resKey != null) tempIdent += "/" + resKey;
                       resListForAuth = loadedRegistry.getElementsByTagNameNS("*","Resource");
                       Element elem2 = loadedRegistry.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1","agr:AstrogridResource");
                       elem2.appendChild(resListForAuth.item(0));
                       try {
                           log.debug("updating the new registy");
                           xdbRegistry.storeXMLResource(tempIdent.replaceAll("[^\\w*]","_") + ".xml", 
                                                        collectionName, elem2);
                       } catch(XMLDBException xdbe) {
                           log.error(xdbe);
                           throw new SOAPFaultException("Server Error: " + xdbe.getMessage(),xdbe,adminwsdlNS,  SOAPFaultException.ADMINSOAP_TYPE);                               
                       } catch(InvalidStorageNodeException isne) {
                           log.error(isne);
                           throw new SOAPFaultException("Server Error: " + isne.getMessage(),isne, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);                     
                       }                                                    
                    }else {
                       log.error("Skipping this update of resource - but somehow the Registries" +
                                 " main RegistryType has no ManagedAuthority");
                       // This resource is already owned by another 
                       // Registry.
                    }//else                           
                 }//if      
                  }//elseif   
            if(addManageError) {
               log.debug("Error authority id not managed by this registry throwing SOAPFault exception; the authority id = " + ident);
               soapErrorMessage += "Error authority id not managed by this registry throwing SOAPFault exception; the authority id = " + ident;
               continue;
               //return SOAPFaultException.createAdminSOAPFaultException("AuthorityID not managed by this registry",new RegistryException("Trying to update an entry that is not managed by this Registry authority id = " + ident));
            }//if
         }//else
         log.info("Time taken to update an entry = " + 
                  (System.currentTimeMillis() - beginQ) +
                  " for ident  = " + tempIdent);
         //currentResource.getParentNode().removeChild(currentResource);         
      }//for

      Document returnDoc = null;      
      if(soapErrorMessage.trim().length() > 0) {
          throw new SOAPFaultException("Server Error: " + soapErrorMessage,soapErrorMessage, adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);
      }
      // errored Resource that was not able to be updated in the db.
      try {      
          returnDoc = DomHelper.newDocument();
          returnDoc.appendChild(returnDoc.createElementNS(adminwsdlNS,"ru:UpdateResponse"));          
      }catch(ParserConfigurationException pce) {
          pce.printStackTrace();
          log.error(pce);
          throw new SOAPFaultException("Server Error: " + "Could not create successfull DOM for soap body",
                  new RegistryException("Could not create successfull DOM for soap body"),adminwsdlNS, SOAPFaultException.ADMINSOAP_TYPE);          
      }
      
      log.info("Time taken to complete update on server = " +
               (System.currentTimeMillis() - beginUpdate) + "milliseconds");
      log.debug("end updateResource");

      return returnDoc;
   }
   
   public void remove(String id) throws RegistryException, XMLDBException {
       String collectionName = "astrogridv" + voResourceVersion.replace('.','_');
       if(id == null || id.trim().length() <= 0) {
           throw new RegistryException("Cannot have empty or null identifier");
       }
       String queryIvorn = id;
       if(Ivorn.isIvorn(id)) { 
           queryIvorn = id.substring(6);
       }
       
       id = queryIvorn.replaceAll("[^\\w*]","_");
       id += ".xml";
       xdbRegistry.removeResource(id, collectionName);
   }
   
   public Document updateIndex(Document doc) throws XMLDBException, RegistryException {
	   String collectionName = "system/config/db/astrogridv" + voResourceVersion.replace('.','_');
	   xdbRegistry.storeXMLResource("astrogrid.xconf",collectionName,doc);
	   return null;
   }
    
}