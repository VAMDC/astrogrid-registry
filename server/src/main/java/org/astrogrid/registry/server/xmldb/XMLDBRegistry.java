package org.astrogrid.registry.server.xmldb;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.astrogrid.xmldb.client.XMLDBFactory;
import org.astrogrid.xmldb.client.XMLDBService;
import org.astrogrid.config.Config;
import org.astrogrid.registry.server.InvalidStorageNodeException;
import org.astrogrid.registry.common.DomHelper;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: XMLDBRegistry
 * Description: Class that does all the lower level to the database
 * such as the actual Storing/Inserting/Querying of the database.
 * @author kevinbenson
 *
 */
public class XMLDBRegistry {
    
    private XMLDBService xdb = null;
    
    
    /**
     * conf - Config variable to access the configuration for the server normally
     * jndi to a config file.
     * @see org.astrogrid.config.Config
     */   
    public static Config conf = null;

    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
       if(conf == null) {
          conf = org.astrogrid.config.SimpleConfig.getSingleton();
       }
    }
    
    /**
     * Logging variable for writing information to the logs
     */
     private static final Log log = LogFactory.getLog(XMLDBRegistry.class);
    
    /**
     * Constructor
     * Description: creates an xmldb service which is a connectiong to the
     * database.  The connection might be established each time, but the
     * actual creating of the service should be static and is not created
     * each time.
     * 
     * @see org.astrogrid.xmldb.client.XMLDBService
     *
     */
    public XMLDBRegistry() {
        xdb = XMLDBFactory.createXMLDBService();
    }
    
    /**
     * Extracts from an IVORN the internal name of a resource. The ivo://
     * prefix is removed and all characters that might upset eXist are replaced 
     * with underscores.
     * 
     * @param ivorn The external identifier.
     * @return The internal identifier.
     */
    public String internalIdentifier(String ivorn) {
      return ivorn.substring(6).replaceAll("[^\\w*]","_");
    }
    
    /**
     * Method: query
     * Description: Queries the xml database, on the collection of the registry. This method
     * will read from properties a xql expression and fill out the expression then perform the query. This
     * is a convenience in case of customization for other xml databases.
     * @param xqlString an XQuery to query the database
     * @param collectionName the location in the database to query (sort of like a table)
     * @return xml DOM object returned from the database, which are Resource elements
     * @throws XMLDBException error connecting to the db 
     */
    public ResourceSet query(String xqlString, String collectionName) throws XMLDBException {
       log.debug("start query");
       Collection coll = null;
       try {
           coll = xdb.openCollection(collectionName);
           log.info("Now querying in collection = " + collectionName + " query = " + xqlString);
           //start a time to see how long the query took.
           long beginQ = System.currentTimeMillis(); 
           ResourceSet rs = xdb.queryXQuery(coll,xqlString);
           
           log.info("Total Query Time = " + (System.currentTimeMillis() - beginQ));
           log.info("Number of results found in query = " + rs.getSize());
           return rs;
       } finally {
           try {
               xdb.closeCollection(coll);
           }catch(XMLDBException xmldb) {
               log.error(xmldb);
           }//try
       }//finally
    }
    
    /**
     * Method: getResource
     * Description: get a single XML Resource by its identifier.  Does not do a query to the db
     * since the identifier is like a primary key name stored in the db. It can look it up via the
     * name.
     * @param ident unique string in the db also happens to be the identifier element.
     * @param collectionName the location in the database to query (sort of like a table)
     * @return XMLResource object from the database.
     * @throws XMLDBException error connecting to the db
     */
    public XMLResource getResource(String ident, String collectionName) throws XMLDBException {
        XMLResource xmr = null;    
        Collection coll = null;
        try {
            coll = xdb.openCollection(collectionName);
            xmr = (XMLResource)xdb.getResource(coll,ident + ".xml");
        } finally {
            try {
                xdb.closeCollection(coll);
            }catch(XMLDBException xmldb) {
                log.error(xmldb);
            }
        }
        return xmr;
    }
    
    /**
     * Method: ListRootCollections
     * Description: Returns a string array of all the collections based off the
     * root.  Does not do any recursion down the tree just the collections under /db
     * @return  Array of collection names.
     * @throws XMLDBException error connecting to the db
     */
    public String[] listRootCollections() throws XMLDBException {        
        Collection coll = null;
        try {
            coll = xdb.openCollection();
            return coll.listChildCollections();        
        } finally {
            try {
                xdb.closeCollection(coll);
            }catch(XMLDBException xmldb) {
                log.error(xmldb);
            }
        }
    }
        
    /**
     * Method: storeXMLResource
     * Description: stores an xml Resource into the database.  It will 
     * insert or update automatically.  This method lets you pass in a Node DOM
     * which is checked to make sure is an Element or Document DOM to be stored
     * into the db.
     * NOTE: Had problems in the past submitting just a Node element to the DB though it is
     * part of the xmldb api such as losing a namespace.  
     * So currently convert it to a string and store that into the db which
     * gets placed correctly into the DB.
     * @param ident unique string that is used as the name for storage in the db.
     * @param collectionName like a table name is the location of where it will be stored in the db.
     * @param node DOM Node to be stored.
     * @return if it is successfull in storing the XML.
     * @throws XMLDBException error connecting to the db
     * @throws InvalidStorageNodeException if it is not a valid DOM type (Document or Elment).
     */
    public boolean storeXMLResource(String ident, String collectionName, Node node) throws XMLDBException, InvalidStorageNodeException {
        if(Node.ELEMENT_NODE == node.getNodeType()) {
            return storeXMLResource(ident, collectionName, DomHelper.ElementToString((Element)node));
        } else if(Node.DOCUMENT_NODE == node.getNodeType()) {
            return storeXMLResource(ident, collectionName, DomHelper.DocumentToString((Document)node));
        }
        throw new InvalidStorageNodeException("Unknown or Invalid Node trying to be stored in the registry db.  Only Document and Element nodes are allowed");
    }

    /**
     * Method: storeXMLResource
     * Description: stores an xml Resource into the database.  It will 
     * insert or update automatically.   
     * So currently convert it to a string and store that into the db which
     * gets placed correctly into the DB.
     * @param ident unique string that is used as the name for storage in the db.
     * @param collectionName like a table name is the location of where it will be stored in the db.
     * @param node DOM Node to be stored.
     * @return if it is successfull in storing the XML.
     * @throws XMLDBException error connecting to the db
     */    
    private boolean storeXMLResource(String ident,String collectionName, String node) throws XMLDBException {
        Collection coll = null;
        try {
            coll = xdb.openAdminCollection(collectionName);
            xdb.storeXMLResource(coll,ident,node);
        }finally {
            xdb.closeCollection(coll);
        }
        return true;
    }
    
    /**
     * Method: getCollection
     * Description: get a Collection object from the DB.  Which houses information
     * about that Collection (think of Collection as similiar to a table).
     * @param collectionName the location in the database to query (sort of like a table)
     * @param admin login as an admin user.
     * @return collection object.
     * @throws XMLDBException error connecting to the db
     */
    public Collection getCollection(String collectionName, boolean admin) throws XMLDBException {
        if(admin)
            return xdb.openAdminCollection(collectionName);
        else
            return xdb.openCollection(collectionName);
    }
    
    /**
     * Method: storeXMLResource
     * Description: stores an xml Resource into the database.  It will 
     * insert or update automatically.  This method lets you pass in a Node DOM
     * which is checked to make sure is an Element or Document DOM to be stored
     * into the db.
     * NOTE: Had problems in the past submitting just a Node element to the DB though it is
     * part of the xmldb api such as losing a namespace.  
     * So currently convert it to a string and store that into the db which
     * gets placed correctly into the DB.
     * @param ident unique string that is used as the name for storage in the db.
     * @param coll Collection object.
     * @param node DOM Node to be stored.
     * @return if it is successfull in storing the XML.
     * @throws XMLDBException error connecting to the db
     * @throws InvalidStorageNodeException if it is not a valid DOM type (Document or Elment).
     */ 
    public boolean storeXMLResource(Collection coll, String ident, Node node) throws XMLDBException, InvalidStorageNodeException {
    	
    	 if(Node.ELEMENT_NODE == node.getNodeType()) {  
    		 xdb.storeXMLResource(coll,ident,DomHelper.ElementToString((Element)node));
    		 return true;        
    	 }else if(Node.DOCUMENT_NODE == node.getNodeType() ) {
    		 xdb.storeXMLResource(coll,ident,DomHelper.DocumentToString((Document)node));
    		 return true;        
    	 }    	
        throw new InvalidStorageNodeException("Unknown or Invalid Node trying to be stored in the registry db.  Only Document and Element nodes are allowed");
    }
    
    /**
     * Method: closeCollection
     * Description: closes a collection object.
     * @param coll Collection object
     * @return true if successfull otherwise an XMLDBException should have been thrown
     * @throws XMLDBException error connecting to the db
     */
    public boolean closeCollection(Collection coll) throws XMLDBException {
        xdb.closeCollection(coll);
        return true;
    }
        
    /**
     * Method: removeResource
     * Description: Removes a Resource from the database.
     * @param ident unique string which is the name of the Resource in the database.
     * @param collectionName colleciton/table in the database the Resource lives in.
     * @throws XMLDBException error connecting to the db
     */
    public void removeResource(String ident, String collectionName) throws XMLDBException {
        Collection coll = null;
        try {    
            coll = xdb.openAdminCollection(collectionName);
            xdb.removeResource(coll,ident);
        } finally {
            xdb.closeCollection(coll);
        }
    }
}