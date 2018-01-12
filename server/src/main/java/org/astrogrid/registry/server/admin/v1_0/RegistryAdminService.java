package org.astrogrid.registry.server.admin.v1_0;

import org.w3c.dom.Document;

//import org.codehaus.xfire.util.STAXUtils;

import org.astrogrid.registry.server.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.*;

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
public class RegistryAdminService extends org.astrogrid.registry.server.admin.RegistryAdminService {
                          
    /**
     * Logging variable for writing information to the logs
     */
   private static final Log log = 
                               LogFactory.getLog(RegistryAdminService.class);
   
   private static final String CONTRACT_VERSION = "1.0";  
   
   private static final String VORESOURCE_VERSION = "1.0";
   

   public static final String ADMIN_WSDL_NS = "http://www.astrogrid.org/wsdl/RegistryUpdate/v1.0";
   

   
   /**
    * Default Constructor: RegistryAdminService
    * Description: Instantiated by the XFire service for the Update WebService or via the
    * jsp pages (currently editEntry.jsp)
    *
    */
   public RegistryAdminService() {
	   super(CONTRACT_VERSION,VORESOURCE_VERSION,ADMIN_WSDL_NS);
   }
   
   public Document updateInternal(Document update) {
	   log.debug("start update");
	      Document returnDoc;
	      try {
	    	  returnDoc = super.updateResource(update);
	      }catch(SOAPFaultException soapexc) {
	    	  //exception happened get the full DOM FAULT Document and return it.
	    	  returnDoc = soapexc.getFaultDocument();
	      }
	      return returnDoc;
   }   
   /*
   public void remove(String id) throws RegistryException, XMLDBException {
	   super.remove(id);
   }
   
   public Document updateIndex(Document doc) throws XMLDBException, RegistryException {
	   return super.updateIndex(doc);
   }
   */
}