package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.xmldb.api.base.ResourceSet;

/**
 * Servlet to check proposed IVO identifiers.
 * The candidate identifier is sent as the parameter IVORN of a posted
 * request. Two checks are made: that the authority is managed in this
 * registry; and that the identifier is not already in use (if it has been
 * used but the resource has status="deleted" then the identifer may be
 * reused). If the identifier is accepted, then a dummy resource is entered in
 * the registry and the client is forwarded to the form for editing that
 * resource.
 *
 * @author Guy Rixon
 */
public class NewIdentifierServlet extends RegistrarServlet {

  /**
   * The collection (of XML documents in the DB) to be searched.
   * This is fixed to be the VOResource-1.0 collection as the query
   * used below only worls with that schema.
   */
  static public String COLLECTION_NAME = "astrogridv1_0";
  
  /**
   * Handles the HTTP GET method.
   * The representation of the resource is an XHTML form for editing the
   * Dublin Core.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
	  doPost(request,response);
  }
  
  /**
   * Handles the HTTP <code>POST</code> method.
   * Checks identifiers and creates place-holder resources. If any exception is
   * raised in checking or creating, forwards the request to the error-handling
   * JSP with the exception sent as the attribute named "exception".
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
    URI ivorn = null;
    try {
      
      // Find out which resource we're creating and check that the
      // name is allowable.
      ivorn = new URI("ivo://" +
                      request.getParameter("authority") + 
                      "/" + 
                      request.getParameter("resourceKey"));
      this.check(ivorn);
      
      String capURL = request.getParameter("vosiURL");
      System.out.println("here is the capURL = " + capURL);
      
      // Generate the creation date in XSD format.
      Instant now = new Instant();
      String created = now.toString();
      String updated = now.toString();
    
      // Create the resource by XSLT transformation and register it.
      URL transformUrl = this.getClass().getResource("/xsl/NewResource.xsl");
      RegistryTransformer transformer = new RegistryTransformer(transformUrl);
      transformer.setTransformationSource(this.getClass().getResource("/xml/NewResource.xml"));
      transformer.setTransformationParameter("xsiType",    request.getParameter("xsiType"));
      transformer.setTransformationParameter("created",    created);
      transformer.setTransformationParameter("updated",    updated);
      transformer.setTransformationParameter("identifier", ivorn);

      transformer.transform();
      
      // Put the results into the registry.
      register(ivorn, transformer.getResultAsDomNode());
      
      String vosiParam = "";
      if(capURL != null && capURL.trim().length() > 0) {
    	 vosiParam = "&vosiURL=" + URLEncoder.encode(capURL.toString(), "UTF-8");
      }
      
      // Delegate the success response to the DC editor.
      String uri = this.getContextUri(request) +
                   "/registration/DublinCore.jsp?IVORN=" +
                    URLEncoder.encode(ivorn.toString(), "UTF-8") +
                    vosiParam;
      response.setHeader("Location", uri);
      response.setStatus(response.SC_SEE_OTHER);
      
    }
    catch (Exception e) {
      
      // Delegate the error response to the error JSP.
      request.setAttribute("IVORN", ivorn);
      request.setAttribute("exception", e);
      request.getRequestDispatcher("IdentifierError.jsp").forward(request, response);
      return;
    }
    
    
  }
  
  /** Returns a short description of the servlet.
   */
  public String getServletInfo() {
    return "Checks IVO identifiers and creates place-holder resources.";
  }
  
  /**
   * Checks an IVO identifier. Throws an exception if the identifier is
   * unacceptable.
   */
  private void check(URI ivorn) throws Exception {
    
    // Is it trying to be an IVORN?
    if (!(ivorn.getScheme().equals("ivo"))) {
      throw new IdentifierSyntaxException("The identifier must start with 'ivo://'.");
    }
    
    // Is it made only of the right characters? This covers both authority and
    // resource key. Note that many characters that might appear in the 
    // authority are trapped by the URI constructor called above.
    String schemePart = ivorn.getRawSchemeSpecificPart();
    if (schemePart.indexOf('!') != -1 ||
        schemePart.indexOf(';') != -1 ||
        schemePart.indexOf(':') != -1 ||
        schemePart.indexOf('@') != -1 ||
        schemePart.indexOf('&') != -1 ||
        schemePart.indexOf('$') != -1 ||
        schemePart.indexOf(',') != -1 ||
        schemePart.indexOf('?') != -1 ||
        schemePart.indexOf('#') != -1) {
      throw new IdentifierSyntaxException("IVO identifiers may not contain " +
                                          "any of these characters: " +
                                          "! ; : @ & $ , ? #");
      
    }
    
    // Is the authority at least three characters long?
    if (ivorn.getAuthority().length() < 3) {
      throw new IdentifierSyntaxException("The authority part must be at least " +
                                      "three characters long.");
    }
    
    // Does the authority begin with an alphanumeric?
    if (! Character.isLetterOrDigit(ivorn.getAuthority().charAt(0))) {
      throw new IdentifierSyntaxException("The authority part must start + " +
                                          "with a letter or number.");
    }
    
    // Is it free of fragments? (An indentifier for a whole resource may not
    // include a fragment part.)
    if (ivorn.getFragment() != null) {
      throw new IdentifierSyntaxException(
          "An identifier for a whole resource " +
          "may not be a URI fragment; the # character must not " +
          "appear in the resource path.");
    }
    
    // Is it free of server queries? Identifiers don't have queries.
    if (ivorn.getQuery() != null) {
      throw new IdentifierSyntaxException(
          "An identifier may not have a query part; " +
          "the ? character must not be present.");
    }
     
    // Is it already used?
    String q = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
               "for $r in //ri:Resource[identifier = '" + ivorn.toString() +"'] return $r";
    System.out.println(q);
    XMLDBRegistry db = new XMLDBRegistry();
    ResourceSet rs = db.query(q, COLLECTION_NAME);
    if (rs.getSize() > 0) {
      throw new IdentifierAlreadyUsedException(ivorn.toString());
    }
  }
  
}
