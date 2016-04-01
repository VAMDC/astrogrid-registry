package org.astrogrid.registry.server.query.v1_0;

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
public class RegistryQueryService extends DefaultQueryService implements ISearch {
    
    /**
     * Logging variable for writing information to the logs
     */
    private static final Log log = LogFactory.getLog(RegistryQueryService.class);    

    public static final String QUERY_WSDL_NS = "http://www.ivoa.net/wsdl/RegistrySearch/v1.0";
    
    private static final String CONTRACT_VERSION = "1.0";    
    
    private static final String VORESOURCE_VERSION = "1.0";
    
    private static final String QUERYINTERFACE_ROOT = "VOResources";   
    
    private static final NodeDescriber xqueryNodeDescriber = 
    	new NodeDescriber("http://www.ivoa.net/xml/RegistryInterface/v1.0",
    			"Resource","ri");

    public RegistryQueryService() {
        super(QUERY_WSDL_NS, CONTRACT_VERSION, VORESOURCE_VERSION);     
    }
    
    public RegistryQueryService(String wsdlNS, String contractVersion, String voVersion) {
        super(wsdlNS, contractVersion, voVersion);     
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
     * Method: processSingleResult
     * Description: process an actual Dom Node response.  This is now always 
     * either a Soap:Fault or one response Resource from GetResource(identifier).
     * @param resultDBNode - DOM Node to be processed.
     * @param responseWrapper - Actual Soap response name to be used as an element
     * with uri of WSDL_NS to be wrapped around the actual response hence first
     * element after soap:body.
     */
    public XMLStreamReader processSingleResult(Node resultDBNode,String responseWrapper) {
    	try {
    		return streamResults(resultDBNode, responseWrapper);
    	}catch(Exception e) {
    		SOAPFaultException sfe = new SOAPFaultException(e.getMessage(),e,QUERY_WSDL_NS,SOAPFaultException.QUERYSOAP_TYPE);
    		return processSingleResult(sfe.getFaultDocument(),null);
    	}
    }

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
    public XMLStreamReader processSingleResult(ResourceSet resultSet,String responseWrapper) {
    	try {
    		return streamResults(resultSet, responseWrapper, null, null, null, true);
    	}catch(Exception e) {
    		SOAPFaultException sfe = new SOAPFaultException(e.getMessage(),e,QUERY_WSDL_NS,SOAPFaultException.QUERYSOAP_TYPE);
    		return processSingleResult(sfe.getFaultDocument(),null);
    	}
    }
       
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
    public XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper) {
        return processResults(resultSet, responseWrapper, null, null, null);
    }
    
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
    public XMLStreamReader processResults(ResourceSet resultSet,String responseWrapper, String start, String max, String identOnly) {
    	try {
    		return streamResults(resultSet, responseWrapper, start, max, identOnly, false);
    	}catch(Exception e) {
    		SOAPFaultException sfe = new SOAPFaultException(e.getMessage(),e,QUERY_WSDL_NS,SOAPFaultException.QUERYSOAP_TYPE);
    		return processSingleResult(sfe.getFaultDocument(),null);
    	}
    }
    
   
    /**
     * Method: streamResults
     * Description: Streams results to the client for a Node.  Which is always either a single
     * Resource or a Soap Fault error.  Also takes an xml wrapper and used to wrap around the Node. Also
     * tends to analyze the single Resource to see if anything special needs to happen such as
     * adding schemalocations, dates, and such.
     * @param resultDBNode DOM Node containing normally a Fault Element or a Single Resource.
     * @param responseWrapper XML string of any wrapper xml to be placed around the Single Resource.
     * @return A Stream Reader which is streamed out to the client via XFire.
     * @throws IOException io problem with the stream.
     */
    private XMLStreamReader streamResults(Node resultDBNode,String responseWrapper) throws IOException {
            //check if it is a Fault, if so just return the resultDoc no need to use a wrapper;
            if(resultDBNode.getNodeName().indexOf("Fault") != -1 || 
               (resultDBNode.hasChildNodes() && resultDBNode.getFirstChild().getNodeName().indexOf("Fault") != -1)  ) {
                //All Faults should have been created by server.SOAPFaultException meaning a Document object.
                //return (Document)resultDBNode;
            	return STAXUtils.createXMLStreamReader(new StringReader(DomHelper.DocumentToString((Document)resultDBNode)));
            }

            NodeList childNodes = null;
            Document responseDoc = null;
            if(resultDBNode instanceof Document) {
                responseDoc = ((Document)resultDBNode);                
            }else if(resultDBNode instanceof Element) {
                responseDoc = ((Element)resultDBNode).getOwnerDocument();
            }
            childNodes = responseDoc.getElementsByTagNameNS("*","Resource");
            
            //See if some modifications needs to be made to this single Resource
            //and add the wrapper around it if needed.
            String schemaLocations = null;
            schemaLocations =  "http://www.ivoa.net/xml/VOResource/v1.0 "  + schemaLocationBase + 
            				   "registry/RegistryInterface/v1.0/RegistryInterface.xsd " + 
            				   "http://www.ivoa.net/xml/VOResource/v1.0 " + 
                               schemaLocationBase + "vo-resource-types/VOResource/v1.0/VOResource.xsd ";
            StringBuffer resultBuffer = new StringBuffer();
            if(responseWrapper != null && responseWrapper.trim().length() > 0) {
            	resultBuffer.append(("<riw:" + responseWrapper + " xmlns:riw=\"" + QUERY_WSDL_NS + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"));
            }            
                StringBuffer resContent = new StringBuffer(DomHelper.ElementToString((Element)childNodes.item(0)));
                String temp = resContent.substring(0,resContent.indexOf(">"));
                //see if it has a schemaLocation attribute if not then we need to add it.
                  if(temp.indexOf("schemaLocation") == -1) {
                	  
                	  int tempIndex;
                	  if((tempIndex = temp.indexOf("type")) != -1) {
  	                	tempIndex = temp.indexOf("\"",tempIndex);
  	                	temp = temp.substring((tempIndex+1),temp.indexOf("\"",tempIndex+1));
		                if(temp.endsWith("Registry") || temp.endsWith("Authority")) {
		                    schemaLocations += " http://www.ivoa.net/xml/VORegistry/v1.0 " + schemaLocationBase + "vo-resource-types/VORegistry/v1.0/VORegistry.xsd";
		                } else {
		                    schemaLocations += " http://www.ivoa.net/xml/VODataService/v1.0 " + schemaLocationBase + "vo-resource-types/VODataService/v1.0/VODataService.xsd " +
		                    "http://www.ivoa.net/xml/VOTable/v1.0 " + schemaLocationBase + "vo-formats/VOTable/v1.0/VOTable.xsd";
		                    if(temp.endsWith("ConeSearch")) {
		                        schemaLocations += " http://www.ivoa.net/xml/ConeSearch/v1.0 " + schemaLocationBase + "vo-resource-types/ConeSearch/v1.0/ConeSearch.xsd";    
		                    }else if(temp.endsWith("SimpleImageAccess")) {
		                        schemaLocations += " http://www.ivoa.net/xml/SIA/v1.0 " + schemaLocationBase + "vo-resource-types/SIA/v1.0/SIA.xsd";
		                    }else if(temp.endsWith("TabularDB")) {
		                        schemaLocations += " urn:astrogrid:schema:vo-resource-types:TabularDB:v1.0 " + schemaLocationBase + "vo-resource-types/TabularDB/v1.0/TabularDB.xsd";
		                    }else if(temp.endsWith("OpenSkyNode")) {
		                        schemaLocations += " http://www.ivoa.net/xml/OpenSkyNode/v1.0 " + schemaLocations + "vo-resource-types/OpenSkyNode/v1.0/OpenSkyNode.xsd";
		                    }else if(temp.endsWith("CeaService") || temp.endsWith("CeaHttpApplicationType") || temp.endsWith("CeaApplicationType")) {
		                        schemaLocations += " http://www.ivoa.net/xml/CEA/v1.0rc1 " + schemaLocations + "vo-resource-types/CEAService/v1.0rc1/CEAService.xsd";
		                    }
		                }
		                //add schemaLocation.
		                resContent.insert(resContent.indexOf(">")," xsi:schemaLocation=\"" + schemaLocations + "\"");
                }//if type
                  }//if schemalocations

                  resultBuffer.append(resContent.toString());
                if(responseWrapper != null && responseWrapper.trim().length() > 0) {
                	resultBuffer.append(("</riw:" + responseWrapper + ">"));
                }
                //return the results as a stream to the client.
                return STAXUtils.createXMLStreamReader(new StringReader(resultBuffer.toString()));
    }
    
    
    /**
     * Method: streamResults
     * Description: process an actual response from a query to the database. 
     * Goes through a set/collection of xmlresources processing them and sending the
     * result to the outputstream.
     * The Streaming of the set is done by the ResultStreamer and RegistryXMLDelegate classes.
     * @param resultSet - a collection of XMLResources from the query of the db.
     * @param responseWrapper - a string name to be used as the wrapped method name
     * hence first element of soap:body.
     * @param start - number of the starting point of the query.
     * @param max - number of maximum allowed resources to be returned.
     * @param identOnly - should the identifiers only be returned or the full Resource.
     * @param singleResource - boolean to determine if this is a single Resource
     * which means it does not require the common wrapper VOResources.
     */    
   private XMLStreamReader streamResults(ResourceSet resultSet,String responseWrapper, String start, String max, String identOnly, boolean singleResource) throws IOException, XMLDBException {

	   		//get the limit from the config and set the limit.
            int limit = conf.getInt("reg.amend.returncount",100);
            if(max != null && Integer.parseInt(max) < limit) {
            	limit = Integer.parseInt(max);
            }

            String more = "false";
            //always queries are set to go 1 more past the limit.  So
            //if the response has more than the limit then simply get rid of the last
            //resource in the collection.
            if(resultSet.getSize() > limit) {
            	more = "true";
            	resultSet.removeResource((resultSet.getSize() - 1));
            }            
            //if start for some reason is null then set it to 1.
            if(start == null || start.trim().length() == 0)
            	start = "1";

            //schemalocations for response Resources.
            String schemaLocations = null;
            schemaLocations =  "http://www.ivoa.net/xml/RegistryInterface/v1.0 "  + schemaLocationBase + 
                               "registry/RegistryInterface/v1.0/RegistryInterface.xsd " + 
                               "http://www.ivoa.net/xml/VOResource/v1.0 " + 
                               schemaLocationBase + "vo-resource-types/VOResource/v1.0/VOResource.xsd ";
            
            StringBuffer resultBuffer = new StringBuffer();
            //do we have a responsewrapper which should almost always be yes then
            //write it out to the outputstream.
            if(responseWrapper != null && responseWrapper.trim().length() > 0) {
            	resultBuffer.append(("<riw:" + responseWrapper + " xmlns:riw=\"" + QUERY_WSDL_NS + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"));
            }
            
            //if not a singleresource then write out the standard
            //VOReosurces wrapper .
            if(!singleResource) {
            	resultBuffer.append(("<ri:VOResources xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ri=\"http://www.ivoa.net/xml/RegistryInterface/v1.0\" xsi:schemaLocation=\"" + schemaLocations + "\" from=\"" + start + "\" more=\"" + more + "\" numberReturned=\"" + resultSet.getSize() +"\">"));
            	resultBuffer.append("</ri:VOResources>");            	
            }
            
            if(responseWrapper != null && responseWrapper.trim().length() > 0) {
            	resultBuffer.append(("</riw:" + responseWrapper + ">"));
            }            
            return new ResultStreamer(resultSet,resultBuffer.toString(), Boolean.valueOf(identOnly).booleanValue());
    }    
    
}