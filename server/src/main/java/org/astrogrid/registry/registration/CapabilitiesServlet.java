package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to transcribe IVOA service-metadata into an IVOA registration
 * document. The service metadata are capabaility elements read from the
 * service using VOSI.
 *
 * @author Guy Rixon
 */
public class CapabilitiesServlet extends RegistrarServlet {
  
  /**
   * Handles the HTTP GET method.
   * The response to GET is a form for editing capabilities.
   * The expression of the form is currently done by a JSP, because the form
   * is static. In future versions, the form might be produced by XSLT
   * from the current content of the resource document. Therefore, this servlet
   * is the preferred entry-point and it delegates to the JSP.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String ivornString = request.getParameter("IVORN");
    if (ivornString == null || ivornString.trim().length() == 0) {
      throw new ServletException("No identifier was given for the resource; " +
                                 "parameter IVORN is not set.");
    }
    String encodedIvorn = URLEncoder.encode(ivornString, "UTF-8");
    String redirectUri = "/registration/ServiceMetadataForm.jsp?IVORN=" + 
                         encodedIvorn;
    request.getRequestDispatcher(redirectUri).forward(request, response);
  }
  
  /** 
   * Handles the HTTP <code>POST</code> method.
   *
   * Fetches a list of capabilities as an XML document from a given URL.
   * Retrieves the registration with the given IVORN as a DOM and transcribes
   * the capabilities using XSLT.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    // Establish the URI from which the service metadata are to be read.
    String vosiUri = request.getParameter("VOSI");
    if (vosiUri == null || vosiUri.trim().length() == 0) {
      throw new ServletException("No URI was given for the service metadata; " +
                                 "parameter VOSI is not set.");
    }
    
    // Establish the IVORN of the registration document.
    String ivornString = request.getParameter("IVORN");
    if (ivornString == null || ivornString.trim().length() == 0) {
      throw new ServletException("No identifier was given for the resource; " +
                                 "parameter IVORN is not set.");
    }
    String encodedIvorn = URLEncoder.encode(ivornString, "UTF-8");
    URI ivorn;
    try {
      ivorn = new URI(ivornString);
    } catch (URISyntaxException ex) {
      throw new ServletException("The given IVORN is invalid.", ex);
    }
    
    // Generate the mopdification date in XSD format.
    Instant now = new Instant();
    String updated = now.toString();
    
    // Set the transformation to work on the registration document.
    // Get the registration document out of the registry.
    // The resource URI leads to the XML text.
    try {
      URL transformUrl = this.getClass().getResource("/xsl/GetCapabilities.xsl");
      RegistryTransformer transformer = new RegistryTransformer(transformUrl);
      URL resourceUrl = new URL(this.getContextUri(request) +
                                "/main/viewResourceEntry_body.jsp?IVORN=" +
                                encodedIvorn);
      transformer.setTransformationSource(resourceUrl);
      transformer.setTransformationParameter("vosi-uri", vosiUri);
      transformer.setTransformationParameter("updated", updated);
      transformer.transform();
      transformUrl = this.getClass().getResource("/xsl/VOSIFetch.xsl");
      RegistryTransformer transformer2 = new RegistryTransformer(transformUrl);
      transformer2.setTransformationSource(resourceUrl);
      transformer.transform();
      register(ivorn, transformer2.getResultAsDomNode());
    } catch (Exception ex) {
      throw new ServletException("Failed to transform a registration.", ex);
    }
    
    // On successful registration, send the client to a summary of the resource.
    String redirectUri = this.getContextUri(request) + 
                         "/main/browse.jsp?IvornPart=" + 
                         encodedIvorn;
    response.setStatus(response.SC_SEE_OTHER);
    response.setHeader("Location", redirectUri);    
  }
  
  /** 
   * Returns a short description of the servlet.
   */
  public String getServletInfo() {
    return "Adds capability elements to a service registration";
  }
  

}
