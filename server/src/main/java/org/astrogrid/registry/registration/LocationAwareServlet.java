package org.astrogrid.registry.registration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet that knows how to find the URL of its containing web application.
 *
 * @author Guy Rixon
 */
public abstract class LocationAwareServlet extends HttpServlet {
  
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
