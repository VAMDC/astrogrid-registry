package org.astrogrid.registry.server.admin;

import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.astrogrid.registry.common.RegistryDOMHelper;
import org.astrogrid.registry.server.query.QueryConfigExtractor;
import org.astrogrid.util.DomHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Iterator;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.base.Resource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class AuthorityListManager { 
    
   private static final Log LOG = LogFactory.getLog(AuthorityListManager.class);
   
   /**
    * Database interface.
    */
   private final XMLDBRegistry xdbRegistry;
   
  
   
   public AuthorityListManager(XMLDBRegistry xdbRegistry) {
       this.xdbRegistry = xdbRegistry;
   }
   
   
   public AuthorityListManager() {
       xdbRegistry = new XMLDBRegistry();
   }
   
    
   public void clearManagedAuthoritiesForOwner(String owner, String versionNumber) {
        java.util.Collection values = RegistryAdminService.manageAuths.values();
        AuthorityList al = null;
        Iterator iter = values.iterator();
        while(iter.hasNext()) {
            al = (AuthorityList)iter.next();
            if(owner.equals(al.getOwner()) && versionNumber.equals(al.getVersionNumber())) {
                //javadocs says this will remove it from the manageAuths hashmap.
                //see HashMap.values()
                iter.remove();
            }//if
        }//while
    }
    
    /**
     * Method: clearManagedCache
     * Description: Not really needed, used as a convenience method to clear the managed authority cache
     * of the Hashmap; normally by the clear cache jsp page.
     * 
     * @param versionNumber the hash map of the version to be cleard. No longer used.
     * 
     */
    public void clearManagedCache() {
        RegistryAdminService.manageAuths.clear();
    }    
    
    
    /**
     * Method: populateManagedMaps
     * Description: Small method to query for Registry type xml entries and get there Managed Authority ids
     * for placing into a HashMap.  This hashmap is used to verify no conflicts in authority id's.  Only one
     * registry can manage a particular Authority id.
     * 
     * @param collectionName
     * @param versionNumber
     * @throws XMLDBException
     */
    public void populateManagedMaps(String collectionName, String versionNumber) throws XMLDBException {
        LOG.debug("start populateManagedMaps");       
        //get All the Managed Authorities, the getManagedAutories() does not
        //perform a query every time only once.
        HashMap versionManaged = null;
        versionManaged = getManagedAuthorities(collectionName, versionNumber);
        
        RegistryAdminService.manageAuths.putAll(versionManaged);
        LOG.info("After loading Managed Authorities from Query = " + RegistryAdminService.manageAuths.size() + " for registry version = " + versionNumber);
        LOG.debug("end populateManagedMaps");
    }
    
    public HashMap getManagedAuthorities(String collectionName, String regVersion) throws XMLDBException {
        HashMap manageAuthorities = new HashMap();
        String xqlQuery = QueryConfigExtractor.getXQLDeclarations(regVersion) + QueryConfigExtractor.queryForRegistries(regVersion);
        Document registries = null;
        try {
            ResourceSet rs = xdbRegistry.query(xqlQuery, collectionName);
            if(rs.getSize() == 0) {
                return manageAuthorities;
            }
            Resource xmlr = rs.getMembersAsResource();
            registries = DomHelper.newDocument(xmlr.getContent().toString());           
        }catch(ParserConfigurationException pce) {
          LOG.error(pce);
        }catch(IOException ioe){
          LOG.error(ioe);
        }catch(SAXException sax) {
          LOG.error(sax);
        }
        
        NodeList resources = registries.getElementsByTagNameNS("*","Resource");
        LOG.info("Number of Resources found loading up registries = " + resources.getLength());
        String val = null;
        Element resourceElem = null;
        for(int j = 0;j < resources.getLength();j++) {
        	resourceElem = (Element)resources.item(j);
        	if(resourceElem.hasAttribute("status") &&
        	   resourceElem.getAttribute("status").equals("active")) {
	            String mainOwner = RegistryDOMHelper.getAuthorityID(resourceElem);
	            NodeList mgList = resourceElem.getElementsByTagNameNS("*","managedAuthority");
	            LOG.info("mglist size = " + mgList.getLength());
	            for(int i = 0;i < mgList.getLength();i++) {
	                val = mgList.item(i).getFirstChild().getNodeValue();
	                manageAuthorities.put(new AuthorityList(val,regVersion),new AuthorityList(val, regVersion, mainOwner));
	            }//for
        	}//if
        }//for
        return manageAuthorities;
    }   
}