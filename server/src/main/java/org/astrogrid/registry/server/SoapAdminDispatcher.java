package org.astrogrid.registry.server;

import javax.xml.stream.*;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.admin.AdminFactory;


import java.util.Hashtable;
import org.codehaus.xfire.util.STAXUtils;
import org.codehaus.xfire.MessageContext;

/**
 * Class: SoapAdminDispatcher
 * Description: The dispatcher handles all soap requests and responses for Admin Service.  Called via the
 * SoapServlet. SoapRequests (Bodies) are placed into a DOM and by analyzing the uri 
 * determine the admin service.  Should note that the Admin Service on updates/inserts still
 * deals in the DOM approach only Query Service uses streaming.  Admin uses DOM and then streams out
 * the DOM in the end.
 * @author kevinbenson
 *
 */
public class SoapAdminDispatcher {
	
  Hashtable interfaceMappings = null;
	

  /**
   * Method: Constructor
   * Description: Setup any initiations, mainly hashtable of uri to query 
   * interface versions.
   */
  public SoapAdminDispatcher() {
	  interfaceMappings = new Hashtable();
	  //Small hashtable for determing the query  interface.
	  interfaceMappings.put("http://www.astrogrid.org/wsdl/RegistryUpdate/v0.1","0.1");	  
	  interfaceMappings.put("http://www.astrogrid.org/wsdl/RegistryUpdate/v1.0","1.0");	  
	  
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
	     //form a DOM for the request.
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	     DocumentBuilder builder = dbf.newDocumentBuilder();
	     Document inputDoc = STAXUtils.read(builder,reader,true);
	     String inputURI = inputDoc.getDocumentElement().getNamespaceURI();
	     IAdmin admin;
	     if(interfaceMappings.containsKey(inputURI)) {
	    	 //okay get the ISearch query interface.
	    	 admin = AdminFactory.createAdminService((String)interfaceMappings.get(inputURI));
	  	 }else {
	  		//very old clients just might not match which must be 0.1
	  		//query interface.
	  		admin = AdminFactory.createAdminService("0.1");
	  	 }	     
	     
	     return admin.Update(inputDoc);
	 }catch(Exception e) {
		 e.printStackTrace();
	 }
	 return null;
  }  
  
}