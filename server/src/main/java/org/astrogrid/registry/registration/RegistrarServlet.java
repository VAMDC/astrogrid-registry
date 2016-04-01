package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.astrogrid.registry.server.admin.AdminHelper;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.admin.v1_0.RegistryAdminService;

import org.astrogrid.util.DomHelper;

import org.astrogrid.registry.common.RegistryValidator;
import junit.framework.AssertionFailedError;

/**
 * Servlet that knows how to find the URL of its containing web application.
 *
 * @author Guy Rixon
 */
public abstract class RegistrarServlet extends HttpServlet {
  
  static Log log = 
      LogFactory.getLog("org.astrogrid.registry.registration.RegistrarServlet");
  
  RegistryAdminService ras = new RegistryAdminService();
  
  protected void validate(URI ivorn, Node resource) throws ServletException {
        	  Element resElem;
        	  NodeList nl;
        	  if(resource.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE) {
        		  System.out.println("doc from validate in RegistrarServlet = " + DomHelper.DocumentToString((Document)resource) );
        		  resElem = ((Document)resource).getDocumentElement();
        	  }else if(resource.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
        		  System.out.println("element from validate in RegistrarServlet = " + DomHelper.ElementToString((Element)resource) );
        		  resElem = (Element)resource;
        	  }else {
        		  throw new ServletException("Cound not validate document, the registration is not a type understood to validate.");  
        	  }
        	  
          try {
        	  System.out.println("validating local name = " + resElem.getLocalName());
       		  RegistryValidator.isValid(resElem,resElem.getLocalName());
          }catch(AssertionFailedError afe) {
              afe.printStackTrace();
              log.error("Error invalid document = " + afe.getMessage());
              throw new ServletException("Invalid update, document not valid " + afe.getMessage());           
          }//catch
  }
  
  /**
   * Generates a resource document and enters it into the registry.
   */
  protected void register(URI ivorn, Node resource) throws ServletException {
	System.out.println("validating");
	validate(ivorn,resource);
    System.out.println("registering");
    try {
      
      // The identifier for the document in the DB is a mutation of the IVORN:
      // strip off the "ivo://" prefix; replace each white-space character with
      // an undercore; append ".xml".
      String id1 = ivorn.getAuthority() + ivorn.getPath();
      String id2 = id1.replaceAll("[^\\w*]","_") + ".xml";
      
      // Insert the document into the DB.
      //XMLDBRegistry registry = new XMLDBRegistry();
      //registry.storeXMLResource(id2, "astrogridv1_0", resource);
      Document doc = ras.updateInternal((Document)resource);
      if(doc.getDocumentElement().getLocalName().equals("Fault")) {
    	  throw new ServletException("Failed to enter Document a Fault occurred, XML Text = " + DomHelper.DocumentToString(doc));
      }
      //log.info(id2 + " in astrogridv1_0 " + " was replaced with a new version.");
      
    } catch (Exception ex) {
      throw new ServletException("Failed to enter a document into the registry", ex);
    }
  }
  
  /**
   * Determines the URI of the request down to the context path.
   */
  protected String getContextUri(HttpServletRequest request) throws MalformedURLException {
    URL requestUrl = new URL(request.getRequestURL().toString());
    return "http://" + 
           requestUrl.getAuthority() + 
           request.getContextPath();
  }
}
