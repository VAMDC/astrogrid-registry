package org.astrogrid.registry.client.query.v1_0;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import org.w3c.dom.Document; 

import org.astrogrid.registry.RegistryException;



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
public interface RegistryService extends org.astrogrid.registry.client.query.RegistryService  {

    /**
     * Method: search
     * Description: client inteface method to call web service method search(Document).
     * @param xadql XML string of ADQL to be parsed into a Document object.
     * @return XML Document of all the Resources in the registry constrained by the adql query.
     * @throws RegistryException
     */   
    public Document search(String xadql, int from, boolean identifiersOnly) throws RegistryException;   
    
    
  /**
    * Method: search
    * Description: client inteface method to call web service method search(Document).
    * @param xadql XML string of ADQL to be parsed into a Document object.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */   
   public Document search(String xadql, int from, int max, boolean identifiersOnly) throws RegistryException;
   
   /**
    * Method: search
    * Description: client inteface method to call web service method for querying the registry in ADQL.
    * @param adql the adql:where is passed to the web service call.  It can be any form of adql.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */
   public Document search(Document adql, int from, boolean identifiersOnly) throws RegistryException;   
   
   
   /**
    * Method: search
    * Description: client inteface method to call web service method for querying the registry in ADQL.
    * @param adql the adql:where is passed to the web service call.  It can be any form of adql.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */
   public Document search(Document adql, int from, int max, boolean identifiersOnly) throws RegistryException;   

   /**
    * Method: searchFromSADQL
    * Description: client inteface method to call web service method search(Document).
    * @param xadql SQL type string to be parsed into the current ADQL XML DOM.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */      
   public Document searchFromSADQL(String sadql, int from, boolean identifiersOnly) throws RegistryException;
   
   
   /**
    * Method: searchFromSADQL
    * Description: client inteface method to call web service method search(Document).
    * @param xadql SQL type string to be parsed into the current ADQL XML DOM.
    * @return XML Document of all the Resources in the registry constrained by the adql query.
    * @throws RegistryException
    */      
   public Document searchFromSADQL(String sadql, int from, int max, boolean identifiersOnly) throws RegistryException;

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
   public Document keywordSearch(String keywords,boolean orValues, int from, boolean identifiersOnly) throws RegistryException;   
   
   
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
   public Document keywordSearch(String keywords,boolean orValues, int from, int max, boolean identifiersOnly) throws RegistryException;   

   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords, int from, boolean identifiersOnly) throws RegistryException;
   
   /**
    * Method: keywordSearch
    * Description: client inteface method to call web service method key word search a keyword type
    * search (with default setting of querying for all words).
    * @param keywords a space seperated string 
    * @return XML Document of all the Resources in the registry constrained by the keyword query.
    * @throws RegistryException
    */   
   public Document keywordSearch(String keywords, int from, int max, boolean identifiersOnly) throws RegistryException;
   

   /**
    * Method: getEndpointsByIdentifier
    * Description: Get all the endpoint (urls) for an identifier for this particular standardID defined on
    * the capability element.  Normally suspect just 1 url will be returned but could be many.
    * @param identifier  resource identifier 
    * @param capabilityStandardID standard ivo id defined on the capability
    * @return String array of url(s)
    * @throws RegistryException
    */   
   public String[] getEndpointsByIdentifier(String identifier, String capabilityStandardID) throws RegistryException;
   
   /**
    * Method: getEndpointsByIdentifier
    * Description: Get all the endpoint (urls) for an identifier for this particular standardID defined on
    * the capability element and for a particular version on the interface.  Normally suspect just 1 url will be returned but could be many.
    * @param identifier  resource identifier 
    * @param capabilityStandardID standard ivo id defined on the capability
    * @interfaceVersion interface version.
    * @return String array of url(s)
    * @throws RegistryException
    */      
   public String[] getEndpointsByIdentifier(String identifier, String capabilityStandardID, String interfaceVersion) throws RegistryException;
   
   /**
    * Method: getEndpointByIdentifier
    * Description: Get the first endpoint (url) for an identifier for this particular standardID defined on
    * the capability element.
    * @param identifier  resource identifier 
    * @param capabilityStandardID standard ivo id defined on the capability
    * @return String array of url(s)
    * @throws RegistryException
    */   
   public String getEndpointByIdentifier(String identifier, String capabilityStandardID) throws RegistryException;
   
   /**
    * Method: getEndpointByIdentifier
    * Description: Get the first endpoint (urls) for an identifier for this particular standardID defined on
    * the capability element and for a particular version on the interface.  Normally suspect just 1 url will be returned but could be many.
    * @param identifier  resource identifier 
    * @param capabilityStandardID standard ivo id defined on the capability
    * @interfaceVersion interface version.
    * @return String array of url(s)
    * @throws RegistryException
    */         
   public String getEndpointByIdentifier(String identifier, String capabilityStandardID, String interfaceVersion) throws RegistryException;
   
   public String[] getEndpoints(String capabilityStandardID) throws RegistryException;
      
}