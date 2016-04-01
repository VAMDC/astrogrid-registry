package org.astrogrid.registry.server;

import javax.xml.stream.*;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

import org.astrogrid.registry.server.query.IOAIHarvestService;
import org.astrogrid.registry.server.query.OAIHarvestFactory;

import java.util.Hashtable;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.MessageContext;

/**
 * Class: SoapHarvestDispatcher
 * Description: The dispatcher handles all soap requests and responses dealing with Harvest Soap calls.  
 * Called via the
 * SoapServlet. SoapRequests (Bodies) are placed into a DOM and by analyzing the uri 
 * determine if which harvest contract is being called and which interface method to call.  
 * Calls the interface method and returns the results.  Though a stream is 
 * returned as a response.  Should note currently Harvest Service is still DOM 
 * based at this moment. Only Query Service streams results. 
 * @author kevinbenson
 *
 */
public class SoapHarvestDispatcher {

  Hashtable interfaceMappings = null;
  
  /**
   * Method: Constructor
   * Description: Setup any initiations, mainly hashtable of uri to query 
   * interface versions.
   */
  public SoapHarvestDispatcher() {
	  interfaceMappings = new Hashtable();
	  //Small hashtable for determing the query  interface.
	  interfaceMappings.put("http://www.ivoa.net/wsdl/RegistryHarvest/v1.0","1.0");
	  interfaceMappings.put("http://www.ivoa.net/wsdl/RegistryHarvest/v0.9","0.9");
	  interfaceMappings.put("http://www.ivoa.net/wsdl/RegistryHarvest/v0.1","0.1");
  }

  /**
   * Method: invoke
   * Description: Called by SoapServlet (XFire) for all soap requests and 
   * responses.
   * @param context - MessageContext that is ued to extract the soap request.
   * @return XMLStreamReader - response XMLStreamReader that contains the 
   * soap response populated by InputStream (PipedInputStream)
   */
  public XMLStreamReader invoke(MessageContext context) {
	 try {
		 //get the soap request.
	     XMLStreamReader reader = context.getInMessage().getXMLStreamReader();
	     //form a DOM for the request to analyze the DOM.
	     //Note this is the DOM inside the Soap body.
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	     DocumentBuilder builder = dbf.newDocumentBuilder();
	     Document inputDoc = STAXUtils.read(builder,reader,true);
	     //get the namespace uri to see if we have a mapping of that
	     //namespace to a contract.
	     String inputURI = inputDoc.getDocumentElement().getNamespaceURI();
	     IOAIHarvestService oaiquery;
	     if(interfaceMappings.containsKey(inputURI)) {
	    	 //okay get the oai query interface from the oai factory.
	    	 oaiquery = OAIHarvestFactory.createOAIHarvestService((String)interfaceMappings.get(inputURI));
	  	 }else {
	  		 //very old clients just might not match which must be 0.1
	  		 //query interface.
	  		oaiquery = OAIHarvestFactory.createOAIHarvestService("0.1");
	  	 }
	     XMLStreamReader responseReader = null;
	     if(oaiquery != null) {
	    	 //The method/interface name to be called should be the soap body
	    	 //first child element.  Get the local name and compare names and call the
	    	 //method.
	    	 String interfaceName = inputDoc.getDocumentElement().getLocalName().intern();	    	 
	    	 if(interfaceName == "Identify".intern()) {
	    		 responseReader = oaiquery.Identify(inputDoc);	    		 
	    	 }else if(interfaceName == "ListRecords".intern()) {
	    		 responseReader = oaiquery.ListRecords(inputDoc);
	    	 }else if(interfaceName == "ListIdentifiers".intern()) {
	    		 responseReader = oaiquery.ListIdentifiers(inputDoc);	    		 
	    	 }else if(interfaceName == "ListMetadataFormats".intern()) {
	    		 responseReader = oaiquery.ListMetadataFormats(inputDoc);	    		 
	    	 }else if(interfaceName == "ListSets".intern()) {
	    		 responseReader = oaiquery.ListSets(inputDoc);	    		 
	    	 }else if(interfaceName == "ResumeListSets".intern()) {
	    		 responseReader = oaiquery.ResumeListSets(inputDoc);	    		 
	    	 }else if(interfaceName == "GetRecord".intern()) {
	    		 responseReader = oaiquery.GetRecord(inputDoc);	    		 
	    	 }else {
	    		System.out.println("no interfacename found, nothing to call"); 
	    	 }
	     }//if
	 	 return responseReader;
	 }catch(Exception e) {
		 e.printStackTrace();
	 }
	 return null;
  }
}