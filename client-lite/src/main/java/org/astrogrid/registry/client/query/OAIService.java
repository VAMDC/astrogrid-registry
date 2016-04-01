package org.astrogrid.registry.client.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL; 
import java.util.Vector; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException; 
import org.apache.axis.client.Call; 
import org.apache.axis.client.Service; 
import org.apache.axis.message.SOAPBodyElement; 
import org.apache.axis.utils.XMLUtils; 
import org.w3c.dom.Document; 
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;
import java.util.List;
import java.util.Iterator;
import java.util.Date;

import org.astrogrid.registry.RegistryException;
//import org.astrogrid.registry.common.InterfaceType;
import org.astrogrid.registry.common.XSLHelper;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;

import org.xml.sax.SAXException;
import java.rmi.RemoteException;

import javax.wsdl.factory.WSDLFactory;

import org.astrogrid.config.Config;
import org.astrogrid.store.Ivorn;
import org.astrogrid.util.DomHelper;


/**
 * 
 * Class: RegistryService
 * Type: Interface
 * Description: A interface used to be given to clients from the Delegate Factory for querying the registry.
 * Essentially this is the interface to the delegate. See QueryRegistry for the implemented class of this
 * interface.
 * 
 * @see org.astrogrid.registry.client.query.QueryRegistry
 * 
 * @link http://www.ivoa.net/twiki/bin/view/IVOA/IVOARegWp03
 * @author Kevin Benson
 */
public interface OAIService  {
       
    public Document identify() throws RegistryException;
    
    public Document listRecords() throws RegistryException;
    
    public Document listRecords(String resumptionToken) throws RegistryException;    
   
    public Document listRecords(Date fromDate) throws RegistryException;
   
    public Document listRecords(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws RegistryException;
   
    public Document listMetadataFormats(String identifier) throws RegistryException;
   
    public Document getRecord(String identifier) throws RegistryException;
   
    public Document getRecord(String identifier, String metadataPrefix) throws RegistryException;
   
    public Document listIdentifiers() throws RegistryException;

    public Document listIdentifiers(String resumptionToken) throws RegistryException;
   
    public Document listIdentifiers(String metadataPrefix, Date fromDate, Date untilDate, String resumptionToken) throws RegistryException;   
}