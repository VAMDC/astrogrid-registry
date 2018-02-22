package org.astrogrid.xmldb.client;

import org.w3c.dom.Node;
import org.xmldb.api.modules.XQueryService;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XUpdateQueryService;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;


/**
 * Class: XMLDBFactory
 * Purpose: A Factory for storing and querying a XMLDB:API implemented database. 
 * 
 * @author Kevin Benson
 */
public interface XMLDBService {

    
    /**
     * Method: openCollection
     * Purpsoe: open a basic or query type xmldb collection to the root collection "/db".  Uses standard query admin and password if any given in the properties/JNDI.
     * @return XMLDB Collection object
   * @throws org.xmldb.api.base.XMLDBException
     */
    public Collection openCollection() throws XMLDBException;
    
    /**
     * Method: openCollection
     * Purpsoe: open a basic or query type xmldb collection to given collection.  Uses standard query admin and password if any given in the properties/JNDI.
   * @param collection
     * @param String collection path - after the "/db" root collection.
     * @return XMLDB Collection object
   * @throws org.xmldb.api.base.XMLDBException
     */
    public Collection openCollection(String collection) throws XMLDBException;
    
    /**
     * Method: openAdminCollection
     * Purpsoe: open a admin type xmldb collection to the root collection "/db".  Uses management/admin  admin and password if any given in the properties/JNDI. Use for storing or managing the database.
     * @return XMLDB Collection object
     * @throws XMLDBException if the collection cannot be open for such things as username and password needed or incorrect. 
     * 
     */    
    public Collection openAdminCollection() throws XMLDBException;

    /**
     * Method: openAdminCollection
     * Purpsoe: open a admin type xmldb collection to given collection.  Uses management/admin admin and password if any given in the properties/JNDI. Use for storing or managing the database.
   * @param collection
     * @param String collection path - after the "/db" root collection.
     * @return XMLDB Collection object
     * @throws XMLDBException if the collection cannot be open for such things as username and password needed or incorrect. 
     */    
    public Collection openAdminCollection(String collection) throws XMLDBException;

    /**
     * Method: closeCollection
     * Purpose: Close the given XMLDB collection object and releasing all its resources.
   * @param coll
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database. 
     * @throws XMLDBException if the collection cannot be closed. 
     */
    public void closeCollection(Collection coll) throws XMLDBException;

    /**
     * Method: getXUpdateService
     * Purpose: give back the standard xmldb XUpdateQueryService from a given collection.
   * @param coll
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database. 
     * @throws XMLDBException if the service cannot be found.
     * @return XUpdateQueryService for updating the xmldb.
     */
    public XUpdateQueryService getXUpdateQueryService(Collection coll) throws XMLDBException;
    
    /**
     * Method: query
     * Purpose: Query the xmldb by XQueryService.
   * @param coll
   * @param queryString
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database. 
     * @throws XMLDBException if the service cannot be found. 
     * @return comon QueryService to be used for Xpath or Xquery service types.
     */
     public ResourceSet queryXQuery(Collection coll, String queryString) throws XMLDBException;
     
     /**
      * Method: query
      * Purpose: Query the xmldb by XQueryService.
      * @param Collection the given open collection to a paticular collection (like table) in the xmldb database. 
      * @throws XMLDBException if the service cannot be found. 
      * @return comon QueryService to be used for Xpath or Xquery service types.
      */
      public ResourceSet queryXPath(Collection coll, String queryString) throws XMLDBException;
     
    
    /**
     * Method: getXQueryService
     * Purpose: get a XQuery Service type from the XMLDB:API.
     * @throws XMLDBExceptin exception finding a XQueryService
     * @return XQueryService
     */
    public XQueryService getXQueryService(Collection coll)  throws XMLDBException;
    
    /**
     * Method: getXPathQueryService
     * Purpose: get a XPath Query Service type from the XMLDB:API.
     * @throws XMLDBExceptin exception finding a XPathQueryService
     * @return XPathQueryService
     */
    public XPathQueryService getXPathQueryService(Collection coll)  throws XMLDBException;
    


    /**
     * Method: getResource
     * Purpose: Give back one resource known in the database by its id.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @id A uniquie id in the given collection. 
     * @throws XMLDBException if the resource cannot be found (id does not exist); or collection mysteriously closed.
     * @return Resource object from the given id. 
     */
    public Resource getResource(Collection coll, String id)  throws XMLDBException;
    
    public void removeResource(Collection coll, String id) throws XMLDBException;
    
    /**
     * Method: storeXMLResource
     * Purpose: Store a XML based resource from a given node into a given collection. A random id will be assigned.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @param Node a w3c.dom.Node object for storing XML into the database.  
     * @throws XMLDBException if something is goes wrong in the storing mechanism. 
     */
    public void storeXMLResource(Collection coll, Node content)  throws XMLDBException;

    /**
     * Method: storeXMLResource
     * Purpose: Store a XML based resource from a given node into a given collection.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @param id A given unique name to the XML resource (unique to the collection).
     * @param Node a w3c.dom.Node object for storing XML into the database. 
     * @throws XMLDBException if something is goes wrong in the storing mechanism. 
     */
    public void storeXMLResource(Collection coll, String id, Node content)  throws XMLDBException;
    
    /**
     * Method: storeXMLResource
     * Purpose: Store a XML based resource from a given xml string into a given collection. A random id will be assigned.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @param String a string of xml.  
     * @throws XMLDBException if something is goes wrong in the storing mechanism. 
     */  
    public void storeXMLResource(Collection coll, String xml) throws XMLDBException;
    
    /**
     * Method: storeXMLResource
     * Purpose: Store a XML based resource from a given xml string into a given collection.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @param id A given unique name to the XML resource (unique to the collection).
     * @param String a string of xml. 
     * @throws XMLDBException if something is goes wrong in the storing mechanism. 
     */    
    public void storeXMLResource(Collection coll, String id, String xml) throws XMLDBException;
    

    /**
     * Method: storeResource
     * Purpose: Store a XML/Binary based resource from a given object into a given collection.
     * @param Collection the given open collection to a paticular collection (like table) in the xmldb database.
     * @param id A given unique name to the XML/Binary resource (unique to the collection).
     * @param Object A particular object for storing into the database, depending on the object will be stored as XML/Binary.  May even give File objects for sotring XML. 
     * @throws XMLDBException if something is goes wrong in the storing mechanism. 
     */    
    public void storeResource(Collection coll, String id, Object content)  throws XMLDBException;
}