package org.astrogrid.registry.server.admin.v1_0;

import org.w3c.dom.Document;
import org.astrogrid.util.DomHelper;
import java.io.StringReader;

import org.astrogrid.registry.server.admin.IAdmin;

import org.codehaus.xfire.util.STAXUtils;

import org.xmldb.api.modules.XMLResource;

import org.astrogrid.registry.RegistryException;
import org.astrogrid.registry.server.SOAPFaultException;
import org.xmldb.api.base.XMLDBException;

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
public class RegistryAdminService extends org.astrogrid.registry.server.admin.RegistryAdminService implements IAdmin {
                          
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
   
   
   /**
    * Method: Update
    * Description: Update Web Service method, performs an update to the registry. Actually calls updateResponse.  It can
    * handle many Resource elements if necessary to do multiple updates to the registry.  If a element
    * is not present in the database it automatically inserts.  Only Resource elements that are managed by
    * this registry are allowed, with the exception to Registry types(for discovering new registries)
    * and Authority types(for inserting an authority id to be managed by this registry).
    * 
    * @param update XML DOM containing Resource XML elements
    * @return XMLStreamReeader is returned normally with an empty UpdateResponse element for conforming with SOAP standards
    * of a wrapped wsdl. Or if an error happens a Soap Fault is returned.
    */
   public XMLStreamReader Update(Document update) {
      log.debug("start update");
      Document returnDoc = updateInternal(update);
      return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString(returnDoc)));      
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