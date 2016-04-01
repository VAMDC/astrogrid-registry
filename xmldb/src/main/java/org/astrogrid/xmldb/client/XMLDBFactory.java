package org.astrogrid.xmldb.client;

import org.astrogrid.config.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.net.MalformedURLException;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import org.astrogrid.util.DomHelper;
import org.apache.axis.AxisFault;

import java.io.DataOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xmldb.api.modules.XQueryService;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XUpdateQueryService;
import org.xmldb.api.base.Service;

import org.exist.xmldb.DatabaseInstanceManager;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import javax.xml.transform.OutputKeys;

import java.util.Properties;
import java.util.Enumeration;


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