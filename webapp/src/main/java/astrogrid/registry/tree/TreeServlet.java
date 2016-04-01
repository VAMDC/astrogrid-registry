package astrogrid.registry.tree;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * Servlet to generate a tree view of the registry.
 * URLs handled by this servlet include the authority and path parts
 * of IVORNS.
 * <p>
 * This servlet works with an AstroGrid registry, using the
 * XQuery interface to the underlying database. It presents
 * three different views, depending on the details of the request URL.
 * <ul>
 *   <li>If the request is for the root of the tree (technically,
 *       if the servlet path is null), the servlet delegates
 *       to a tree-root JSP passing no special information;
 *       the JSP knows how to make hte display.</li>
 *   <li>If the request URL ends in a slash, the servlet associates
 *       it with a banching point in the tree. The servlet determines
 *       resources at that branching point and passes the names of those
 *       resourecs to the tree-branch JSP which displays them as links
 *       in a web page.</li>
 *   <li>If the request URL doesn't end in a slash, the servlet
 *       associates the URL with a resource document. The servlet fetches
 *       the document from the database and transcribes it directly
 *       as the response to the request, adding a link to a stylesheet
 *       that rendeers the XML into XHTML.</li>
 *   <li>If the request URL does not end in a slash and the <i>xpath</i>
 *       request-parameter is set, the servlet operates as above, but
 *       applies the given XPath when querying the registry. This obtains
 *       and returns an extract of the resource document.</li>
 * </ul>
 * <p>
 * The servlet responds to HTTP GET. It does not respond to other HTTP verbs
 * because it treats the registry as immutable.
 *
 * @author Guy Rixon
 */
public class TreeServlet extends HttpServlet {
  
  static private Log log = LogFactory.getLog(TreeServlet.class);
  
  /** Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected void doGet(HttpServletRequest request, 
                       HttpServletResponse response) throws ServletException, 
                                                            IOException {
    String path = request.getPathInfo();
    // Show a web page for the root of the tree.
    if (path == null) {
      showTreeRoot(request, response);
    }
    
    // Show a web-page listing branches at a level of the resource key.
    else if (path.endsWith("/")) {
      showPathBranch(request, response);
    }
    
    // Show the content for a specific resource.
    else {
      showResourceDocument(request, response);
    }
    

  }

  /**
   * Shows the root of the registry's tree of resources.
   * Delegates to the tree-root.jsp without passing any information.
   *
   * @param request servlet request
   * @param response servlet response
   */
  private void showTreeRoot(HttpServletRequest  request, 
                            HttpServletResponse response) throws IOException, 
                                                                 ServletException {
    request.getRequestDispatcher("/main/tree-root.jsp").forward(request, response);
  }

  private void showPathBranch(HttpServletRequest  request, 
                              HttpServletResponse response) throws IOException, 
                                                                   ServletException {
    
    // Build and execute an XQuery that extracts the identifiers from resources
    // with suitable names. TODO: exclude deleted and inactive resources.
    String prefix = request.getPathInfo().substring(1);
    String ivorn = "ivo://" + prefix;
    String xquery = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0';" +
                    "for $x in //ri:Resource " + 
                    "where $x/@status='active' " +
                    "and fn:starts-with($x/identifier,'" + ivorn + "') " +
                    "return string(substring($x/identifier, 6))";
//    System.out.println("Query is " + xquery);
    try {
      Set hrefs = new TreeSet();
      XMLDBRegistry xdb = new XMLDBRegistry();
      ResourceSet rs = xdb.query(xquery,"astrogridv1_0");
  //    System.out.println("Resources found: " + rs.getSize());
      
      // From the resources resulting from the XQuery, build a sorted set of
      // hrefs that can be used in a web page. The hrefs are relative. Using
      // a set rather than a list removes a lot of duplicates, which seems to
      // be impossible to do in the XQuery. A sorted set is used to order the
      // results on the page, since the ordering in the XQuery seems not to
      // work.
      for (int i = 0; i < rs.getSize(); i++) {
        XMLResource r = (XMLResource) rs.getResource(i);
        String content1 = r.getContent().toString();
        String content2 = content1.substring(prefix.length()+1);
        String href = "";
        int slash = content2.indexOf('/');
        if (slash == -1) {
          // An actual resource!
          href = content2;
        }
        else {
          // Just a branch.
          href = content2.substring(0,slash+1);
        }
        hrefs.add(href);
      }
      
      // Forward to a JSP for rendering. The ivorn attribute is needed to
      // make the title and the hrefs attribute makes the bulk of the content.
      // The hrefs are relative.
      request.setAttribute("ivorn", ivorn);
      request.setAttribute("hrefs", hrefs);
      request.getRequestDispatcher("/main/tree-branch.jsp").forward(request, response);
   
    } catch (XMLDBException ex) {
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }
  }
  
  
  /**
   * Shows the a branch of the registry's tree of resources.
   * Delegates to the tree-branch.jsp passing two attributes: "ivorn", 
   * the IVORN for this branch, to be used in the page title; "hrefs", a 
   * collection of relative hyperlinks to the resources below this branching
   * point.
   *
   * @param request servlet request
   * @param response servlet response
   */
  private void showResourceDocument(HttpServletRequest  request, 
                                    HttpServletResponse response) throws IOException {
    String xpath = request.getParameter("xpath");
    xpath = (xpath == null)? "" : xpath;
    String ivorn = "ivo:/" + request.getPathInfo();
    System.out.println("Looking for " + ivorn);
    String xquery = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0';" +
                    "for $x in //ri:Resource where $x/identifier='" +
                    ivorn +
                    "' return $x" + 
                    xpath;
    System.out.println("Query is " + xquery);
    try {
      XMLDBRegistry xdb = new XMLDBRegistry();
      ResourceSet rs = xdb.query(xquery,"astrogridv1_0");
      System.out.println("Resources found: " + rs.getSize());
      if (rs.getSize() == 0) {
        response.sendError(response.SC_NOT_FOUND);
        return;
      }
      String xslHref = request.getContextPath() + "/ResourceToDublinCoreDisplay.xsl";
      response.setContentType("text/xml");
      Writer out =  response.getWriter();
      XMLResource r = (XMLResource) rs.getResource(0);
      out.write("<?xml version='1.0'?>\n");
      out.write("<?xml-stylesheet type='text/xsl' href='");
      out.write(xslHref);
      out.write("'?>");
      out.write(r.getContent().toString());
      out.flush();
    } catch (XMLDBException ex) {
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }
  }
  
}
