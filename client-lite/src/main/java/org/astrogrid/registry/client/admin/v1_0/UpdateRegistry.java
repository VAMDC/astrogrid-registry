package org.astrogrid.registry.client.admin.v1_0;

import java.net.URL;

import org.astrogrid.registry.client.admin.RegistryAdminService;

/**
 * Class Name: RegistryAdminService
 * Description: This class represents the client webservice delegate to the Administration piece of the
 * web service.  It uses the same Interface as the server side webservice so they both implement and handle
 * the same web service method names.  The primary goal of this class is to establish a Axis-Message style
 * webservice call to the server.
 * 
 * @see org.astrogrid.registry.common.RegistryAdminInterface
 * @author Kevin Benson
 * @todo document the method bodies
 *
 * 
 */
public class UpdateRegistry extends org.astrogrid.registry.client.admin.UpdateRegistry implements RegistryAdminService {

   private String NAMESPACE_URI =  "http://www.astrogrid.org/wsdl/RegistryUpdate/v1.0";
   
   //public static final String ADMIN_URL_PROPERTY = "org.astrogrid.registry.admin.endpoint.v1_0";
   
   
  // public static Config conf = null;    

   
   /** @todo shouldn't be necessary to take a local reference to this static - call simple config.getSingleton each time instead */
   /*
   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
      }      
   } 
   */  
   
   public String getSoapBodyNamespaceURI() {
	   return this.NAMESPACE_URI;
   }

  
  /**
   * Main constructor to allocate the endPoint variable.
   * @todo what happens with nulls?
   * @param endPoint location to the web service.
   * @author Kevin Benson
   */ 
  public UpdateRegistry(URL endPoint) {
	  super(endPoint);
  }
   
}