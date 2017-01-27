package org.astrogrid.registry.server.query;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import junit.framework.AssertionFailedError;
import javax.xml.stream.*;

import org.xmldb.api.base.ResourceSet;

/**
 * Interface: ISearch
 * Description: Standard methods used for the Query Service which implements
 * this interface.
 * @author kevinbenson
 *
 */
public interface ISearch  {
    
	/**
	 * Method: XQuerySearch
	 * Description: Performs an XQuery search passed in and returns results
	 * to the client.
	 * @param query  XQuery from the Soap Body
	 * @return streamer to the client via Xfire.
	 */    
    public XMLStreamReader XQuerySearch(Document query);
    
	/**
	 * Method: KeywordSearch
	 * Description: Performs a keyword search.  Soap body contents contain a 
	 * space seperated keywords and a boolean to determine to 'or' the words together.
	 * @param query  Soap body contents of the keywords for a query.
	 * @return streamer to the client via Xfire.
	 */      
    public XMLStreamReader KeywordSearch(Document query);
    
	/**
	 * Method: GetResource
	 * Description: Grabs a Single Resource from the database based on the unique
	 * identifier string passed into the soap body.
	 * @param query  Soap body contents of the identifier.
	 * @return streamer to the client via Xfire.
	 */     
    public XMLStreamReader GetResource(Document query);
    
	/**
	 * Method: GetRegistries
	 * Description: Queries for all Registries and returns it to the client.
	 * @param query  Soap body contents contains nothing needed to make the query.
	 * @return streamer to the client via Xfire.
	 */      
    public XMLStreamReader GetRegistries(Document query);
    
	/**
	 * Method: loadRegistry
	 * Description: Queries for the main Registry that describes this Registry.
	 * Use GetIdentity now, this is an older interface method.
	 * @param query  Soap body contents contains nothing needed to make the query.
	 * @return streamer to the client via Xfire.
	 */      
    public XMLStreamReader loadRegistry(Document query);   
    
	/**
	 * Method: GetIdentity
	 * Description: Queries for the main Registry that describes this Registry.
	 * @param query  Soap body contents contains nothing needed to make the query.
	 * @return streamer to the client via Xfire.
	 */      
    public XMLStreamReader GetIdentity(Document query);  
    
    /**
     * Method: GetQueryHelper
     * Description: Grabs a QueryHelper object to help form and do the queries.
     * @return Query Helper object.
     * @see org.astrogrid.registry.server.query.QueryHelper
     */
    public QueryHelper getQueryHelper();
    
    /**
     * Method: getWSDLNamespace
     * Description: Get the Namespace used in the WSDL for all the interface
     * methods, used for streaming back the correct soap body contents.
     * @return wsdl namespace string.
     */
    public String getWSDLNameSpace();
    
    /**
     * Method: getContractVersion
     * Description: Get the Contract version number used for this query service.
     * @return contract version number
     */    
    public String getContractVersion();
    
    /**
     * Method: getResourceVersion
     * Description: Get the voresource version number tied to this contract.
     * @return resource version number
     */      
    public String getResourceVersion();
    
    public boolean validateSOAPSearch(Document searchDoc) throws AssertionFailedError;
    
    public boolean validateSOAPResolve(Document resolveDoc) throws AssertionFailedError;

    /**
     * Method: processSingleResult
     * Description: process an actual Dom Node response.  This is now always 
     * either a Soap:Fault or one response Resource from GetResource(identifier).
     * @param resultDBNode - DOM Node to be processed.
     * @param responseWrapper - Actual Soap response name to be used as an element
     * with uri of WSDL_NS to be wrapped around the actual response hence first
     * element after soap:body.
     */
    public XMLStreamReader processSingleResult(Node resultDBNode,String responseWrapper);
    
    /**
     * Method: processSingleResult
     * Description: process an actual response from a query to the database. 
     * Goes through a set/collection of xmlresources processing them and sending the
     * result to the outputstream. (In this case being single the resourceset only has 1
     * resource). Actually calls the common streamResults method to do
     * the real processing.
     * @param resultSet - a collection of XMLResources from the query of the db.
     * @param responseWrapper - a string name to be used as the wrapped method name
     * hence first element of soap:body.
     */
    public XMLStreamReader processSingleResult(ResourceSet resultSet,String responseWrapper);    
    
    /**
     * Method: processResults
     * Description: process an actual response from a query to the database. 
     * Goes through a set/collection of xmlresources processing them and sending the
     * result to the outputstream. Actually calls the common streamResults method to do
     * the real processing.
     * @param resultSet - a collection of XMLResources from the query of the db.
     * @param responseWrapper - a string name to be used as the wrapped method name
     * hence first element of soap:body.
     */ 
    public XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper);    
    
    /**
     * Method: processResults
     * Description: process an actual response from a query to the database. 
     * Goes through a set/collection of xmlresources processing them and sending the
     * result to the outputstream. Actually calls the common streamResults method to do
     * the real processing.
     * @param resultSet - a collection of XMLResources from the query of the db.
     * @param responseWrapper - a string name to be used as the wrapped method name
     * hence first element of soap:body.
     * @param start - number of the starting point of the query.
     * @param max - number of maximum allowed resources to be returned.
     * @param identOnly - should the identifiers only be returned or the full Resource.
     */ 
    public XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper, String start, String max, String identOnly);    
}