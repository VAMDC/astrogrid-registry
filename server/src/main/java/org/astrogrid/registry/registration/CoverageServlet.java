package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet to write the coverage information for a resource document.
 *
 * @author Guy Rixon
 */
public class CoverageServlet extends RegistrarServlet {
  
  /**
   * Handles the HTTP GET method.
   * The response to GET is a form for editing coverage.
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
    String redirectUri = "/registration/CoverageForm.jsp?IVORN=" + 
                         encodedIvorn;
    request.getRequestDispatcher(redirectUri).forward(request, response);
  }
  
  /** 
   * Handles the HTTP <code>POST</code> method.
   * 
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
    response.setContentType("text/plain");
    try {
      
      // Find the resource to be edited. Its XML text comes out of a JSP
      // elsewhere in the web application, indexed by the IVORN.
      URI ivorn = new URI(request.getParameter("IVORN"));
      if (ivorn == null) {
        throw new ServletException("No resource was specified (parameter IVORN is not set)");
      }
      String encodedIvorn = URLEncoder.encode(ivorn.toString(), "UTF-8");
      String resourceUri = this.getContextUri(request) +
                           "/main/viewResourceEntry_body.jsp?IVORN=" +
                           encodedIvorn;
      
      // Generate the modification date in XSD format.
      Instant now = new Instant();
      String updated = now.toString();
      
      // Set up and execute the transformation.
      URL transformUrl = this.getClass().getResource("/xsl/Coverage.xsl");
      RegistryTransformer transformer = new RegistryTransformer(transformUrl);
      transformer.setTransformationSource(new URL(resourceUri));
      transformer.setTransformationParameter("updated",          updated);
      transformer.setTransformationParameter("wavebandRadio",    request.getParameter("Radio"));
      transformer.setTransformationParameter("wavebandMm",       request.getParameter("mm"));
      transformer.setTransformationParameter("wavebandIR",       request.getParameter("IR"));
      transformer.setTransformationParameter("wavebandOptical",  request.getParameter("Optical"));
      transformer.setTransformationParameter("wavebandUV",       request.getParameter("UV"));
      transformer.setTransformationParameter("wavebandEUV",      request.getParameter("EUV"));
      transformer.setTransformationParameter("wavebandXRay",     request.getParameter("X-ray"));
      transformer.setTransformationParameter("wavebandGammaRay", request.getParameter("Gamma-ray"));
      transformer.setTransformationParameter("region",           request.getParameter("region"));
      transformer.setTransformationParameter("boxC1",            request.getParameter("box.C1"));
      transformer.setTransformationParameter("boxC2",            request.getParameter("box.C2"));
      transformer.setTransformationParameter("boxS1",            request.getParameter("box.S1"));
      transformer.setTransformationParameter("boxS2",            request.getParameter("box.S2"));
      transformer.setTransformationParameter("boxCoordSys",      request.getParameter("box.coordsys"));
      transformer.setTransformationParameter("circleC1",         request.getParameter("circle.C1"));
      transformer.setTransformationParameter("circleC2",         request.getParameter("circle.C1"));
      transformer.setTransformationParameter("circleRadius",     request.getParameter("circle.Radius"));
      transformer.setTransformationParameter("circleCoordSys",   request.getParameter("circle.coordsys"));
      transformer.transform();
      
      // Put the document into the registry.
      register(ivorn, transformer.getResultAsDomNode());
      
      // Redirect the browser to a summary view of this resource.
      String redirectUri = this.getContextUri(request) + 
                         "/main/browse.jsp?IvornPart=" + 
                         encodedIvorn;
      response.setStatus(response.SC_SEE_OTHER);
      response.setHeader("Location", redirectUri);     
    } catch (Exception ex) {
      throw new ServletException("Failed to edit coverage data into the registration.", ex);
    } 
  }
  
  /** Returns a short description of the servlet.
   */
  public String getServletInfo() {
    return "Updates the Dublin core of a resource document with values taken from a web form.";
  }
}