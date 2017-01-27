package org.astrogrid.xmldb.client;

import org.astrogrid.config.Config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.exist.xmldb.DatabaseInstanceManager;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

import java.util.Properties;
import java.util.Enumeration;


/**
 * Class: XMLDBFactory
 * Purpose: A Factory for storing and querying a XMLDB:API implemented database. 
 * 
 * @author Kevin Benson
 */
public class XMLDBManager {

    private static final Log log = LogFactory.getLog(XMLDBManager.class);
    
    public static Config conf = null;    
    
    private static String dbDriver = null;
    
    private static boolean dbInitialized = false;
        
    static {
       if(conf == null) {
          conf = org.astrogrid.config.SimpleConfig.getSingleton();
          dbDriver = conf.getString("xmldb.driver","org.exist.xmldb.DatabaseImpl");          
       }//if
    }
    
    /**
     * XMLDBFactory constructor of the factory.
     * @throws IllegalStateException if the database has not been initialied.
     */
    public XMLDBManager()  throws IllegalStateException {
        if(!dbInitialized) {
            log.error("Database initialization has not been done, Static methods are provided for doing this or more commonly use the Servlet EXistDatabaeManager.");
            log.error("You may call the static methods registerDB and shutdownDB to register and shutdown the database, shutdown will NOT happen if the database is not internal in-process");
            throw new IllegalStateException("Database initialization has not been done and the database is not registered.");
        }
    }
    
    /**
     * Method: setDBInitlized
     * Purpose: set rather the database is initlized or not.
     * 
     */
    public static void setDBInitialized(boolean dbInit) {
        dbInitialized = dbInit;
    }
    
    public static boolean isInitialized() {
        return dbInitialized;
    }
    
    /**
     * Method: registerDB
     * Purpose: register a XMLDB database.  And to create the database if necessary on internal
     * db instances.
     * @param confFile - a configuration file for internal database normally used on eXist xmldb.
     * @throws IllegalAccessException cannot access the class of the database driver.
     * @throws ClassNotFoundException cannot find the class of the database driver.
     * @throws InstantiationException cannot instantiate the class of the database driver
     * @throws XMLDBException cannot register the database driver.
     */
    public static void registerDB(Properties props)  throws IllegalAccessException, 
                                     ClassNotFoundException, 
                                     InstantiationException, 
                                     XMLDBException  {
        if(!dbInitialized) {
            Class cl = Class.forName( dbDriver );
            Database database = (Database) cl.newInstance();
            if(props != null) {
                Enumeration enumProps = props.keys();
                String key = null;
                while(enumProps.hasMoreElements()) {
                    key = (String)enumProps.nextElement();
                    database.setProperty(key,(String)props.getProperty(key));
                }
            }//if
            DatabaseManager.registerDatabase( database );
            setDBInitialized(true);
        }//if
    }

    /**
     * Method: shutdownDB
     * Purpose: Only for internal eXist xmldb use.  Shuts down the eXist database on the closing down of the servlet container.
     * @throws XMLDBException cannot shutdown the database or obtain the root collection.
     */    
    public static void shutdownDB() throws XMLDBException {
            log.info("try shutting down database");
            XMLDBService xdbService = XMLDBFactory.createXMLDBService();
            Collection coll = xdbService.openAdminCollection();
            //Collection coll = DatabaseManager.getCollection( xmldbURI + "/db", "admin", "" );
            DatabaseInstanceManager mgr = (DatabaseInstanceManager)coll.getService("DatabaseInstanceManager", "1.0");
            mgr.shutdown();
            setDBInitialized(false);
            log.info("database shutdown");
    }
}