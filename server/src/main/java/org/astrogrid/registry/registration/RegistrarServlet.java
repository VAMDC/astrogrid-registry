package org.astrogrid.registry.registration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.astrogrid.registry.server.admin.v1_0.RegistryAdminService;

import org.astrogrid.util.DomHelper;

import org.astrogrid.registry.common.RegistryValidator;
import org.astrogrid.registry.server.SOAPFaultException;
import org.astrogrid.registry.server.query.v1_0.RegistryQueryService;

/**
 * Servlet that knows how to find the URL of its containing web application.
 *
 * @author Guy Rixon
 */
public abstract class RegistrarServlet extends HttpServlet {

  static Log log
      = LogFactory.getLog("org.astrogrid.registry.registration.RegistrarServlet");

  RegistryAdminService ras = new RegistryAdminService();

  /**
   * Checks that a given resource is schema-valid.
   * @param ivorn The IVORN for the resource (used for reporting errors)
   * @param resource The DOM of the resource.
   * @throws ServletException If the resource is not valid.
   * @throws IOException If the resource cannot be serialised for validation.
   */
  protected void validate(URI ivorn, Node resource) throws ServletException, IOException {
    Element elementToCheck;
    if (resource instanceof Document) {
      elementToCheck = ((Document) resource).getDocumentElement();
    } else if (resource instanceof Element) {
      elementToCheck = (Element) resource;
    } else {
      String message = "Registration for " + ivorn + " is invalid: not even an XML element";
      log.error(message);
      throw new ServletException(message);
    }
    RegistryValidator validator = new RegistryValidator();
    if (validator.isInvalid(elementToCheck)) {
      String message = "Registration " + ivorn + " is invalid: " + validator.getErrorMessages();
      log.error(message);
      throw new ServletException(message);
    } 
  }

  /**
   * Generates a resource document and enters it into the registry.
   *
   * @param ivorn
   * @param resource
   * @throws javax.servlet.ServletException
   */
  protected void register(String ivorn, Node resource) throws ServletException, IOException {
    try {
      URI ivoid = new URI(ivorn);
      register(ivoid, resource);
    } catch (URISyntaxException e) {
      throw new ServletException(e);
    }
  }

  /**
   * Generates a resource document and enters it into the registry.
   */
  protected void register(URI ivorn, Node resource) throws ServletException, IOException {
    System.out.println("validating");
    validate(ivorn, resource);
    System.out.println("registering");
    try {

      // The identifier for the document in the DB is a mutation of the IVORN:
      // strip off the "ivo://" prefix; replace each white-space character with
      // an undercore; append ".xml".
      String id1 = ivorn.getAuthority() + ivorn.getPath();
      String id2 = id1.replaceAll("[^\\w*]", "_") + ".xml";

      // Insert the document into the DB.
      //XMLDBRegistry registry = new XMLDBRegistry();
      //registry.storeXMLResource(id2, "astrogridv1_0", resource);
      Document doc = ras.updateInternal((Document) resource);
      if (doc.getDocumentElement().getLocalName().equals("Fault")) {
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
    return "http://"
        + requestUrl.getAuthority()
        + request.getContextPath();
  }

  /**
   * Raises a resource as a DOM
   *
   * @param ivorn IVORN identifying the resource.
   * @return The DOM.
   * @throws SOAPFaultException
   */
  protected Document getResource(String ivorn) throws SOAPFaultException {
    if (ivorn == null) {
      throw new IllegalArgumentException("No resource was selected (IVORN is null)");
    }
    RegistryQueryService server = new RegistryQueryService();
    return server.getQueryHelper().getResourceByIdentifier(ivorn);
  }
}
