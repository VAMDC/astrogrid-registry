package org.astrogrid.registry.server.query.v1_0SandBox;

import java.io.IOException;

import org.astrogrid.registry.server.SOAPFaultException;
import org.astrogrid.registry.server.query.DefaultQueryService;
import org.astrogrid.registry.server.query.ISearch;

import org.codehaus.xfire.util.STAXUtils;
import java.io.StringReader;

import org.astrogrid.registry.common.NodeDescriber;

import org.astrogrid.registry.common.RegistryValidator;
import junit.framework.AssertionFailedError;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;

import org.astrogrid.util.DomHelper;
import javax.xml.stream.*;


/**
 * Class: RegistryQueryService
 * Description: The main class for all queries to the Registry to go to via Web Service or via internal
 * calls such as jsp pages or other classes.  The main focus is Web Service Interface methods are here
 * such as Search, KeywordSearch, and GetResourceByIdentifier. Most are actually implemented
 * in the parent abstract class DefaultQueryService.  Most of the work here is for
 * processing the response and sending the response on the OutputStream.  Also a couple
 * of rarely used validate methods for the Soap messages
 *
 * @author Kevin Benson
 */
public class RegistryQueryService extends org.astrogrid.registry.server.query.v1_0.RegistryQueryService implements ISearch {
    
    /**
     * Logging variable for writing information to the logs
     */
    private static final Log log = LogFactory.getLog(RegistryQueryService.class);    

    public static final String QUERY_WSDL_NS = "http://www.helio-vo.eu/wsdl/RegistrySearch/v0.1";
    
    private static final String CONTRACT_VERSION = "0.1";    
    
    private static final String VORESOURCE_VERSION = "0.1";
    
    private static final String QUERYINTERFACE_ROOT = "VOResources";   
    
    private static final NodeDescriber xqueryNodeDescriber = 
    	new NodeDescriber("http://www.helio-vo.eu//wsdl/RegistrySearch/v0.1",
    			"Resource","ri");

    public RegistryQueryService() {
        super(QUERY_WSDL_NS, CONTRACT_VERSION, VORESOURCE_VERSION);     
    }
   
    public String getWSDLNameSpace() {return this.QUERY_WSDL_NS;}
    public String getContractVersion() { return this.CONTRACT_VERSION;}
    public String getResourceVersion() { return this.VORESOURCE_VERSION;}
    public String getQueryInterfaceRoot(String wsInterfaceMethod) {return QUERYINTERFACE_ROOT;}
    
    public NodeDescriber getXQuerySearchRootResourceNode() {
    	return xqueryNodeDescriber;
    }
    
    public org.astrogrid.registry.server.query.QueryHelper getQueryHelper() {
        if(queryHelper == null) {
            queryHelper = new org.astrogrid.registry.server.query.QueryHelper(this);
        }
        return queryHelper;
    }
    
    
    private boolean validateResources(Document resourcesDoc) throws AssertionFailedError {
    	return RegistryValidator.isValid(resourcesDoc,"VOResources");
    }
    
    private boolean validateSingleResource(Document resourceDoc) throws AssertionFailedError {
    	return RegistryValidator.isValid(resourceDoc,"Resource");
    }
    
    public boolean validateSOAPSearch(Document searchDoc) throws AssertionFailedError {
    	validateResources(searchDoc);
    	String errorMessage = "";
      	if(!searchDoc.getDocumentElement().getNamespaceURI().equals(QUERY_WSDL_NS)) {
    		errorMessage = "Error the NamespaceURI in the SOAP is " + searchDoc.getDocumentElement().getNamespaceURI() + " but should be " + QUERY_WSDL_NS;
    	}
    	if(!searchDoc.getDocumentElement().getLocalName().equals("SearchResponse")){
    		errorMessage += "The Local Name in the SOAP was " + searchDoc.getDocumentElement().getLocalName() + " but it should be SearchResponse ";
    	}
    	if(errorMessage.trim().length() > 0)
    		throw new AssertionFailedError(errorMessage);
    	return true;
    }

    public boolean validateSOAPResolve(Document resolveDoc) throws AssertionFailedError {
    	validateSingleResource(resolveDoc);
    	String errorMessage = "";
      	if(!resolveDoc.getDocumentElement().getNamespaceURI().equals(QUERY_WSDL_NS)) {
    		errorMessage = "Error the NamespaceURI in the SOAP is " + resolveDoc.getDocumentElement().getNamespaceURI() + " but should be " + QUERY_WSDL_NS;
    	}
    	if(!resolveDoc.getDocumentElement().getLocalName().equals("ResolveResource")){
    		errorMessage += "The Local Name in the SOAP was " + resolveDoc.getDocumentElement().getLocalName() + " but it should be ResolveResource ";
    	}
    	if(errorMessage.trim().length() > 0)
    		throw new AssertionFailedError(errorMessage);    	
    	return true;
    }
    
    /**
     * Method: Search
     * Description: Web Service method to take ADQL DOM and perform a query on the
     * registry.  Takes in a DOM so it can handle multiple versions of ADQL.
     * 
     * @param query - DOM object containing ADQL. Which is xsl'ed into XQuery language for the query.
     * @return - Resource DOM object of the Resources from the query of the registry. 
     * 
     */
    public XMLStreamReader Search(Document query) {
       log.debug("start Search");
       long beginQ = System.currentTimeMillis();
       return null;
       /*
       //transform the ADQL to an XQuery for the registry.
       String xqlQuery = null;
       String start, max, identOnly;
       try {
           //xqlQuery = queryHelper.getQuery(query);
    	   //Need to get the query and this will be a mapping one now.
           start = DomHelper.getNodeTextValue(query,"from");
           max = DomHelper.getNodeTextValue(query,"max");
           if(max == null && contractVersion.equals("0.1")) {
               max = conf.getString("reg.amend.returncount","100");
           }
           identOnly = DomHelper.getNodeTextValue(query,"identifiersOnly");
       }catch(Exception e) {
           e.printStackTrace();
           sfe = new SOAPFaultException("Server Error: " + e.getMessage(),e, queryWSDLNS, SOAPFaultException.QUERYSOAP_TYPE);
           return processSingleResult(sfe.getFaultDocument(),null);
       }
       log.debug("The XQLQuery From ADQLSearch = " + xqlQuery);

       try {
 	      //perform the query and log how long it took to query.
 	      ResourceSet resultSet = queryHelper.queryRegistry(xqlQuery, start, max);
 	      log.info("Time taken to complete search on server = " +
 	              (System.currentTimeMillis() - beginQ));
 	      log.debug("end Search");
 	            
 	      //To be correct we need to transform the results, with a correct response element 
 	      //for the soap message and for the right root element around the resources.
 	      return processResults(resultSet,"SearchResponse", start, max, identOnly);
       }catch(SOAPFaultException soapexc) {
     	  return processSingleResult(soapexc.getFaultDocument(),null);
       }
       */
    }
    
    
    
  
   
    
}