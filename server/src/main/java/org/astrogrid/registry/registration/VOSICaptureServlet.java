package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.astrogrid.registry.server.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;


/**
 * Servlet to transcribe IVOA service-metadata into an IVOA registration
 * document. The service metadata are capabaility elements read from the
 * service using VOSI.
 *
 * @author Guy Rixon
 */
public class VOSICaptureServlet extends RegistrarServlet {
	
	VOSIHarvest vosiHarvest = new VOSIHarvest();
  
  /**
   * Handles the HTTP GET method.
   * The response to GET is a form for editing capabilities.
   * The expression of the form is currently done by a JSP, because the form
   * is static. In future versions, the form might be produced by XSLT
   * from the current content of the resource document. Therefore, this servlet
   * is the preferred entry-point and it delegates to the JSP.
   * 
   * @param request
   * @param response
   * @throws javax.servlet.ServletException
   * @throws java.io.IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String ivorn = request.getParameter("IVORN");
    String redirectUri = "/registration/ServiceMetadataForm.jsp?IVORN=" + 
                         URLEncoder.encode(ivorn, "UTF-8");
    request.getRequestDispatcher(redirectUri).forward(request, response);
  }
  
  /** 
   * Handles the HTTP <code>POST</code> method.
   *
   * Fetches a list of capabilities as an XML document from a given URL.
   * Retrieves the registration with the given IVORN as a DOM and transcribes
   * the capabilities using XSLT. May also update the CEA application-data
   * in the registration.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws javax.servlet.ServletException
   * @throws java.io.IOException
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
    
      // Raise the registration document from the database.
      String ivorn = request.getParameter("IVORN");
      Document resource = getResource(ivorn);
           
      // Update the registration document in memory.
      String vosiUri = request.getParameter("VOSI_Capabilities");
      Node updatedRegistration = addCapabilities(resource, vosiUri);
          
      // Update the registration in the database. 
      register(ivorn, updatedRegistration);
    
    
      // On successful registration, send the client to a summary of the resource.
      String redirectUri = this.getContextUri(request) + 
                         "/main/browse.jsp?IvornPart=" + 
                         URLEncoder.encode(ivorn, "UTF-8");
      response.setStatus(HttpServletResponse.SC_SEE_OTHER);
      response.setHeader("Location", redirectUri);    
    } catch (SOAPFaultException ex) {
      throw new ServletException("Failed to update capabilities for a registration", ex);
    }
  }
  
  /** 
   * Supplies a short description of the servlet.
   * 
   * @return The description.
   */
  @Override
  public String getServletInfo() {
    return "Adds capability elements to a service registration";
  }
  
  /**
   * Given the DOM of a registration document, adds capability elements
   * read from a URL. Any capabilities previously in the
   * document are discarded. The URL for the document may point directly into
   * the database.
   * 
   * @param registrationDocument DOM of the registration document.
   * @param vosiUri URL giving the XML text of the capabilities document.
   * @return
   * @throws ServletException If either document cannot be read, or if the transformation fails.
   */
  protected Node addCapabilities(
      Document registrationDocument, 
      String  vosiUri
  ) throws ServletException {
    try {
      URL transformUrl = this.getClass().getResource("/xsl/GetCapabilities.xsl");
      RegistryTransformer transformer = new RegistryTransformer(transformUrl);
      transformer.setTransformationSource(registrationDocument);
      transformer.setTransformationParameter("vosi-uri", vosiUri);
      transformer.setTransformationParameter("updated", new Instant().toString());
      transformer.transform();
      return transformer.getResultAsDomNode();
    }
    catch (IOException e1) {
      throw new ServletException("Failed to add capabilities to a registration document", e1);
    }
  }
  

}
