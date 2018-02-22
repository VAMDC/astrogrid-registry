package org.astrogrid.xmldb.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class: XMLDBFactory
 * Purpose: A Factory for storing and querying a XMLDB:API implemented database. 
 * 
 * @author Kevin Benson
 */
public class XMLDBFactory {

    private static final Log log = LogFactory.getLog(XMLDBFactory.class);
            
    private static boolean dbInitialized = false;
    
    private static XMLDBService xdbService = null;
        
    
    public static XMLDBService createXMLDBService() {        
        if(!XMLDBManager.isInitialized()) {
            log.error("Database initialization has not been done, Static methods are provided for doing this or more commonly use the Servlet EXistDatabaeManager.");
            log.error("You may call the static methods registerDB and shutdownDB in the XMLDBManager to register and shutdown the database");
            throw new IllegalStateException("Database initialization has not been done and the database is not registered.  Please see XMLDBManager for registering the database");
        }
        if(xdbService == null) {
            xdbService = new XMLDBServiceImpl();
        }
        return xdbService;
    }
    
}