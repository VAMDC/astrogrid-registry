package org.astrogrid.registry.server;

import javax.xml.stream.*;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
//import xmlstreamreader

import org.astrogrid.registry.server.query.ISearch;
import org.astrogrid.registry.server.query.QueryFactory;

import java.util.Hashtable;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.MessageContext;

/**
 * Class: SoapDispatcher
 * Description: The dispatcher handles all soap requests and responses for a Query.  Called via the
 * SoapServlet. SoapRequests (Bodies) are placed into a DOM and by analyzing the uri 
 * determine the correct query service for the correct contract.  Responses are Stream based (NOT DOM) into an 
 * XMLStreamReader with the help of PipedStreams.
 * @author kevinbenson
 *
 */
public class SoapDispatcher {

  Hashtable interfaceMappings = null;
  
  /**
   * Method: Constructor
   * Description: Setup any initiations, mainly hashtable of uri to query 
   * interface versions.
   */
  public SoapDispatcher() {
	  interfaceMappings = new Hashtable();
	  //Small hashtable for determing the query interface.
	  interfaceMappings.put("http://www.ivoa.net/wsdl/RegistrySearch/v1.0","1.0");
	  interfaceMappings.put("http://www.helio-vo.eu//wsdl/RegistrySearch/v0.1","1.0SandBox");
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

	     //form a DOM of the request.
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	     DocumentBuilder builder = dbf.newDocumentBuilder();
	     Document inputDoc = STAXUtils.read(builder,reader,true);
	     //all the soap requests in the body will have a namespaceuri that 
	     //corresponds to the wsdl namespace given.
	     //And the first root element should be the method name I want to call.
	     String inputURI = inputDoc.getDocumentElement().getNamespaceURI();
	     ISearch query;
	     
	     //Create a QueryService based on the uri.
	     //Originally there were multiple QueryServices but now were coming down to just
	     //the main 1.0 one.
	     if(interfaceMappings.containsKey(inputURI)) {
	    	 //okay get the ISearch query interface.
	    	 query = QueryFactory.createQueryService((String)interfaceMappings.get(inputURI));
	  	 }else {
	  		 //very old clients just might not match which must be 0.1
	  		 //query interface.
	    	 query = QueryFactory.createQueryService("1.0");
	  	 }
	     XMLStreamReader responseReader = null;
	     if(query != null) {
	    	 
	    	 //Ok get the local name from the root element of the soap request
	    	 //it should match the method name I want to call.
	    	 String interfaceName = inputDoc.getDocumentElement().getLocalName().intern();
	    	 //since this service will be used a lot, supposedly .intern() can be quicker
	    	 //than .equals() so lets try it out.
	    	 //each method should return a XMLStreamReader that is streamed back to the client.
	    	 if(interfaceName == "Search".intern()) {
	    		 responseReader = query.Search(inputDoc);	    		 
	    	 }else if(interfaceName == "XQuerySearch".intern()) {
	    		 responseReader = query.XQuerySearch(inputDoc);
	    	 }else if(interfaceName == "KeywordSearch".intern()) {
		    	 responseReader = query.KeywordSearch(inputDoc);	    		 
	    	 }else if(interfaceName == "GetResource".intern()) {
	    		 responseReader = query.GetResource(inputDoc);	    		 
	    	 }else if(interfaceName == "GetResourceByIdentifier".intern()) {
	    		 responseReader = query.GetResource(inputDoc);	    		 
	    	 }else if(interfaceName == "GetRegistries".intern()) {
	    		 responseReader = query.GetRegistries(inputDoc);	    		 
	    	 }else if(interfaceName == "GetIdentity".intern()) {
	    		 responseReader = query.GetIdentity(inputDoc);
	    	 }else if(interfaceName == "loadRegistry".intern()) {
	    		 responseReader = query.loadRegistry(inputDoc);	    		 
	    	 }else {
	    		System.out.println("No interfacename found to call"); 
	    	 }
	     }//if
	 	 //System.out.println("returning responsereader");
	 	 return responseReader;
	 }catch(Exception e) {
		 e.printStackTrace();
	 }
	 return null;
  }
}