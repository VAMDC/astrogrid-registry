package org.astrogrid.registry.registration;

import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.astrogrid.registry.server.InvalidStorageNodeException;
import org.astrogrid.registry.server.admin.AdminHelper;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmldb.api.base.XMLDBException;

/**
 * Utility class to put registration documents into the registry.
 * Objects of this class are stateless and reusable.
 * The documents presented for registrations are not validated.
 *
 * @author Guy Rixon
 */
public class Registrar {
  
  static Log log = 
      LogFactory.getLog("org.astrogrid.registry.registration.Registrar");
  
  /**
   * Enters a registration document in the registry.
   */
  public void register(Document document, URI ivorn) 
      throws XMLDBException, InvalidStorageNodeException {
        
    // At a low level, the registry requires a resource to be wrapped in
    // a special element. This code is taken from RegistryAdminService.
    Element top = document.getDocumentElement();
    Element wrapper = 
       document.createElementNS("urn:astrogrid:schema:RegistryStoreResource:v1",
                                "agr:AstrogridResource");
    wrapper.appendChild(top);
    
    // The identifier for the document in the DB is a mutation of the IVORN:
    // strip off the "ivo://" prefix; replace each white-space character with
    // an undercore; append ".xml".
    String id1 = ivorn.getAuthority() + ivorn.getPath();
    String id2 = id1.replaceAll("[^\\w*]","_") + ".xml";
    
    // Now insert the wrapped document into the DB.
    XMLDBRegistry registry = new XMLDBRegistry();
    registry.storeXMLResource(id2, "astrogridv1_0", wrapper);
    log.info(id2 + " in astrogridv1_0 " + " was replaced with a new version.");
    
    // Now enter some statistics in a parallel collection.
    AdminHelper help = new AdminHelper();
    registry.storeXMLResource(id2, "statv1_0", help.createStats(id2));
    log.info(id2 + " in statv1_0 " + " was replaced with a new version.");
  }
  
}
