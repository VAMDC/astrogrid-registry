package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.astrogrid.util.DomHelper;


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
    String ivornString = request.getParameter("IVORN");
    if (ivornString == null || ivornString.trim().length() == 0) {
      throw new ServletException("No identifier was given for the resource; " +
                                 "parameter IVORN is not set.");
    }
    String encodedIvorn = URLEncoder.encode(ivornString, "UTF-8");
    URL resourceUrl = new URL(this.getContextUri(request) +
            "/main/viewResourceEntry_body.jsp?IVORN=" +
            encodedIvorn);
    String xsiType = null;
    NodeList urlText = null;
    String capURL = "";
    try {
	    Document checkDoc = DomHelper.newDocument(resourceUrl);
	    org.w3c.dom.NodeList nl = checkDoc.getElementsByTagNameNS("*","Resource");
	    if(nl.getLength() == 0) {
	    	throw new ServletException("Could not find Resource in the Registry.  Identifier given but is not correct.");
	    }
	    xsiType = ((Element)nl.item(0)).getAttributeNS("http://www.w3.org/2001/XMLSchema-instance","type");
	    NodeList capList = ((Element)nl.item(0)).getElementsByTagName("capability");
	   
	    for(int k = 0;k < capList.getLength();k++) {
	    	if( ((Element)capList.item(0)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI/v0.3#capabilities") ||
					((Element)capList.item(0)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI/v0.4#capabilities")	||
					((Element)capList.item(0)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI#capabilities") ||
					((Element)capList.item(0)).getAttribute("standardID").equals("ivo://ivoa.net/std/VOSI#capabilities")) {
	    		urlText= ((Element)capList.item(0)).getElementsByTagName("accessURL").item(0).getChildNodes();
	    		if(capURL.length() == 0) {
					for(int j = 0;j < urlText.getLength();j++) {
						if(urlText.item(j).getNodeType() == Node.TEXT_NODE) {
							//System.out.println("yes text node lets try to concat");
							capURL += urlText.item(j).getNodeValue();
						}//if
					}//for
	    		}//if
	    		k = capList.getLength();
	    	}//if
	    }//for
    }catch(javax.xml.parsers.ParserConfigurationException pc) {
    	throw new ServletException("Parser Configuration Exception happened when trying to read the given Resource out of the registry.");    	
    }catch(org.xml.sax.SAXException se) {
    	throw new ServletException("SAX Exception happened when trying to read the given Resource out of the registry.");
    }
    String redirectUri = "/registration/ServiceMetadataForm.jsp?IVORN=" + 
                         encodedIvorn;
    if( xsiType != null && xsiType.endsWith("Application") ) {
    	redirectUri += "&appResource=true";
    }
    if(urlText != null && capURL.trim().length() > 0) {
    	redirectUri += "&" + URLEncoder.encode(capURL, "UTF-8");
    	
    }
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
    
    // Establish the URI from which the service metadata are to be read.
    String vosiUri = request.getParameter("VOSI_Capabilities");
    String vosiAppDataUri = request.getParameter("VOSI_AppData");
    if ((vosiUri == null || vosiUri.trim().length() == 0) &&
        (vosiAppDataUri == null || vosiAppDataUri.trim().length() == 0)) {
      throw new ServletException("No URI was given for the  metadata; " +
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
    
    // Locate the registration document in the database.
    URL resourceUrl = new URL(this.getContextUri(request) +
                  "/main/viewResourceEntry_body.jsp?IVORN=" +
                  encodedIvorn);
           
    // Retrive the registration document from the database and update it in memory.
    Node updatedRegistration = addCapabilities(resourceUrl, vosiUri);
          
    // Update the registration in the database. 
    register(ivorn, updatedRegistration);
      
    // Optionally, update the CEA data.
    if (vosiAppDataUri != null && vosiAppDataUri.trim().length() > 0) {
    	System.out.println("transforming for CEA app data");
      try {    	  
        register(ivorn,vosiHarvest.harvestApplicationInfo(vosiAppDataUri,DomHelper.newDocument(resourceUrl)));
      } catch (Exception e) {
        throw new ServletException("Failed to update CEA data in a registration", e);
      }
    }
    
    // On successful registration, send the client to a summary of the resource.
    String redirectUri = this.getContextUri(request) + 
                         "/main/browse.jsp?IvornPart=" + 
                         encodedIvorn;
    response.setStatus(HttpServletResponse.SC_SEE_OTHER);
    response.setHeader("Location", redirectUri);    
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
   * Reads a registration document from a given URL and inserts the
   * capabilities on a second, given URI. Any capabilities previously in the
   * document are discarded. The URL for the document may point directly into
   * the database.
   * 
   * @param registrationDocument URL giving the XML text of the registration document.
   * @param vosiUri URL giving the XML text of the capabilities document.
   * @return
   * @throws ServletException If either document cannot be read, or if the transformation fails.
   */
  protected Node addCapabilities(
      URL     registrationDocument, 
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
