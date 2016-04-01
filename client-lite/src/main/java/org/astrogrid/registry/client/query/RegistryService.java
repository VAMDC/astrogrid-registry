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
import org.astrogrid.registry.client.query.ResourceData;


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
public interface RegistryService  {

   /**
    * Method: getRegistries
    * Description: client inteface method to call web service method for obtaining all the registr types.
    * @return XML Document of all the Registry type Resources known.
    * @throws RegistryException
    * @deprecated Should be factored out.
    */
   public Document getRegistries() throws RegistryException;
 
  /**
    * Method: search
    * Description: client inteface method to call web service method search(Document).
    * @param xadql XML string of ADQL to be parsed into a Document object.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */   
   public Document search(String xadql) throws RegistryException;   
   
   /**
    * Method: search
    * Description: client inteface method to call web service method for querying the registry in ADQL.
    * @param adql the adql:where is passed to the web service call.  It can be any form of adql.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */
   public Document search(Document adql) throws RegistryException;   

   /**
    * Method: searchFromSADQL
    * Description: client inteface method to call web service method search(Document).
    * @param xadql SQL type string to be parsed into the current ADQL XML DOM.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */      
   public Document searchFromSADQL(String sadql) throws RegistryException;
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search that lets you put in word(s) seperated by spaces and then by default is and or you can set to true
    * to look for all the workds.
    * @param keywords a space seperated string
    * @param ovValue do you want to look for all the words, by default false. 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */      
   public Document keywordSearch(String keywords,boolean orValues) throws RegistryException;   

   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords) throws RegistryException;
   
   /**
    * Method: search
    * Description: client inteface method to call web service method search(Document).
    * @param xadql XML string of ADQL to be parsed into a Document object.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */   
   public Document xquerySearch(String xquery) throws RegistryException;     
      
   /**
    * Method: loadRegistry
    * Description: Return the self describing Registry Type entry.
    * @return DOM object of the Registry Type entry (self describing Registry entry being called).
    * @throws RegistryException
    * @deprecated Use getIdentity() instead.
    */
   public Document loadRegistry()  throws RegistryException;

   /**
    * Method: getIdentity
    * Description: Return the self describing Registry Type entry.
    * @return DOM object of the Registry Type entry (self describing Registry entry being called).
    * @throws RegistryException
    */   
   public Document getIdentity()  throws RegistryException;
   public HashMap managedAuthorities() throws RegistryException;
   
   /**
    * Method: getResourceByIdentifier
    * Description: client inteface method to call web service method getResourceByIdentifier(String). 
    * Grabs Resource(s)
    * (normally 1 Resource), from a particular identifier (the primary key in a sense). Identifier is
    * made up of a authroityID+ResourceKey. Normally most clients pass both of these in returning only 1
    * XML Resource hence name is getResoruce.  Recently you may pass in just a AuthorityID allowing you to
    * get multiple Resources back. 
    * @param ident Ivorn object which is a ivo://authorityid+ResourceKey.
    * @return XML Document of all the Resources in the registry constrained by the query.
    * @throws RegistryException
    */
   public Document getResourceByIdentifier(Ivorn ident) throws RegistryException;

   /**
    * Method: getResourceByIdentifier
    * Description: client inteface method to call web service method getResourceByIdentifier(Document). 
    * Grabs Resource(s)
    * (normally 1 Resource), from a particular identifier (the primary key in a sense). Identifier is
    * made up of a authroityID+ResourceKey. Normally most clients pass both of these in returning only 1
    * XML Resource hence name is getResoruce.  Recently you may pass in just a AuthorityID allowing you to
    * get multiple Resources back. 
    * @param ident String object which is a ivo://authorityid+ResourceKey.
    * @return XML Document of all the Resources in the registry constrained by the query.
    * @throws RegistryException
    */
   public Document getResourceByIdentifier(String ident) throws RegistryException;
   public ResourceData getResourceDataByIdentifier(Ivorn ident) throws RegistryException;
   
   public ResourceData[] getResourceDataByRelationship(Ivorn ident) throws RegistryException;
   public ResourceData[] getResourceDataByRelationship(String ident) throws RegistryException;   
   


   /**
    * Method: getEndPointByIdentifier
    * Description: client inteface method to call web service method getResourceByIdentifier(String). 
    * Grabs a URL from the Interface XML element. 
    * @param ident Ivorn object which is a ivo://authorityid+ResourceKey.
    * @return String version of a URL from the Interface Element in a XML Resource.
    * @throws RegistryException
    */   
   public String getEndPointByIdentifier(Ivorn ident) throws RegistryException;
   
   /**
    * Method: getEndPointByIdentifier
    * Description: client inteface method to call web service method getResourceByIdentifier(String). 
    * Grabs a URL from the Interface XML element. 
    * @param ident Ivorn object which is a ivo://authorityid+ResourceKey.
    * @return String version of a URL from the Interface Element in a XML Resource.
    * @throws RegistryException
    */   
   public String getEndPointByIdentifier(String ident) throws RegistryException;
   
   
   
}