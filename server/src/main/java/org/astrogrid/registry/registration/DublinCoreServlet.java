package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

/**
 * A servlet to update the Dublin Core metadata of an IVOA registry-entry.
 * New values for individual elements are read from the parameters of the
 * HTTP request. An XSLT stylesheet does the editing. On successful
 * completion, the response is delegated to a JSP that summarizes the resource.
 *
 * @author Guy Rixon
 */
public class DublinCoreServlet extends RegistrarServlet {
  
  /**
   * Handles the HTTP GET method.
   * The representation of the resource is an XHTML form for editing the
   * Dublin Core.
   * 
   * @param request HTTP request
   * @param response HTTP response
   * @throws javax.servlet.ServletException
   * @throws java.io.IOException
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
	  
    
    try {
    	response.setContentType("text/html");
      
      // Get the document to be transformed from the database, as a DOM.
      Document entry = getResource(request.getParameter("IVORN"));
      
      // Transform the resource and send the result as the HTTP response.
      URL transformUrl = this.getClass().getResource("/xsl/ResourceToDublinCoreForm.xsl");
      RegistryTransformer transformer = 
          new RegistryTransformer(transformUrl, response.getWriter()); 
      transformer.setTransformationSource(entry);
      transformer.transform();
    }
    catch (Exception ex) {
      throw new ServletException("Failed to transform a resource document", ex);
    } 
  }
  
  /** 
   * Handles the HTTP <code>POST</code> method.
   * Retrieves a resource from the registry, updates it according to the
   * request, replaces it in the registry and redirect the client to a
   * summary page for that resource.
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
      response.setContentType("text/plain");
      
      // Generate the modification date in XSD format.
      String updated = new Instant().toString();
      
      // Find out which resource is to be updated.
      String ivorn = request.getParameter("IVORN");
      
      // Get the document from the database.
      Document resource = getResource(ivorn);
      
      // Update the resource document by an XSLT transformation.
      URL transformUrl = this.getClass().getResource("/xsl/DublinCore.xsl");
      RegistryTransformer transformer = new RegistryTransformer(transformUrl);
      transformer.setTransformationSource(resource);
      
      transformer.setTransformationParameter("updated", updated);
      transformer.setTransformationParameter("status", request.getParameter("status"));
      transformer.setTransformationParameter("title", request.getParameter("title"));
      transformer.setTransformationParameter("shortName", request.getParameter("shortName"));
      transformer.setTransformationParameter("publisherName", request.getParameter("curation.publisher"));
      System.out.println("publisher name in request = " + request.getParameter("curation.publisher"));
      transformer.setTransformationParameter("publisherId", request.getParameter("curation.publisher.ivo-id"));
      transformer.setTransformationParameter("creatorName", request.getParameter("curation.creator.name"));
      transformer.setTransformationParameter("creatorId", request.getParameter("curation.creator.ivo-id"));
      transformer.setTransformationParameter("creatorLogo", request.getParameter("curation.logo"));
      transformer.setTransformationParameter("date", request.getParameter("curation.date"));
      transformer.setTransformationParameter("version", request.getParameter("curation.version"));
      transformer.setTransformationParameter("contactName", request.getParameter("curation.contact.name"));
      transformer.setTransformationParameter("contactId", request.getParameter("curation.contact.name.ivo-id"));
      transformer.setTransformationParameter("contactAddress", request.getParameter("curation.contact.address"));
      transformer.setTransformationParameter("contactEmail", request.getParameter("curation.contact.email"));
      transformer.setTransformationParameter("contactTelephone", request.getParameter("curation.contact.telephone"));
      transformer.setTransformationParameter("subject", request.getParameter("content.subject"));
      transformer.setTransformationParameter("description", request.getParameter("content.description"));
      transformer.setTransformationParameter("referenceURL", request.getParameter("content.referenceURL"));
      transformer.setTransformationParameter("contentSource", request.getParameter("content.source"));      
      transformer.setTransformationParameter("type", request.getParameter("content.type"));
      transformer.setTransformationParameter("contentLevel", request.getParameter("content.contentLevel"));
      transformer.setTransformationParameter("vosiURL", request.getParameter("vosiURL"));
      //System.out.println("web url = " + request.getParameter("capability.interface.webbrowserRefURL"));
      transformer.setTransformationParameter("webbrowserRefURL", request.getParameter("capability.interface.webbrowserRefURL"));
      transformer.transform();
      
      // Put the updated document back into the registry.
      URI ivoid = new URI(ivorn);
      register(ivoid, transformer.getResultAsDomNode());
      
      // Redirect the browser to a summary view of this resource.
      String redirectUri = this.getContextUri(request) + 
                         "/main/browse.jsp?IvornPart=" + 
                         URLEncoder.encode(ivorn, "UTF-8");
      response.setStatus(HttpServletResponse.SC_SEE_OTHER);
      response.setHeader("Location", redirectUri);     
    } catch (Exception ex) {
      throw new ServletException("Failed to transform a resource document", ex);
    } 
  }
  
  /** Returns a short description of the servlet.
   * @return 
   */
  @Override
  public String getServletInfo() {
    return "Updates the Dublin core of a resource document with values taken from a web form.";
  }
  
}