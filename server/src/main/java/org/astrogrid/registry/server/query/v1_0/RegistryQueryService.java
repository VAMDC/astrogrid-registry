package org.astrogrid.registry.server.query.v1_0;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.astrogrid.util.DomHelper;
import org.astrogrid.registry.server.query.QueryHelper;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.xml.sax.SAXException;
import org.xmldb.api.modules.XMLResource;

/**
 * Class: RegistryQueryService Description: The main class for all queries to
 * the Registry to go to via Web Service or via internal calls such as jsp pages
 * or other classes. The main focus is Web Service Interface methods are here
 * such as Search, KeywordSearch, and GetResourceByIdentifier. Most are actually
 * implemented in the parent abstract class DefaultQueryService. Most of the
 * work here is for processing the response and sending the response on the
 * OutputStream. Also a couple of rarely used validate methods for the Soap
 * messages
 *
 * @author Kevin Benson
 */
public class RegistryQueryService {

  /**
   * Logging variable for writing information to the logs
   */
  private static final Log log = LogFactory.getLog(RegistryQueryService.class);

  public static final String QUERY_WSDL_NS = "http://www.ivoa.net/wsdl/RegistrySearch/v1.0";

  private static final String CONTRACT_VERSION = "1.0";

  private static final String VORESOURCE_VERSION = "1.0";

  private static final String COLLECTION_NAME = "astrogridv1_0";

  private static final String RI_NAMESPACE = "http://www.ivoa.net/xml/RegistryInterface/v1.0";

  private static final String RI_LOCAL_NAME = "Resource";

  private final XMLDBRegistry xdbRegistry;

  protected QueryHelper queryHelper = null;


  public RegistryQueryService() {
    xdbRegistry = new XMLDBRegistry();
  }
  
  /**
   * Raises the registration for a single resource and parses it to 
   * a DOM document.
   * 
   * @param ivorn IVORN for the resource of choice, including its ivo:// prefix.
   * @return The registration (null if the resource is not registered).
   * 
   * @throws org.astrogrid.registry.server.query.v1_0.GetResourceException If the query cannot be executed.
   */
  public Document getResourceToDocument(String ivorn) throws GetResourceException  {
    try {  
      String xml = getResourceToText(ivorn);
      return (xml == null)? null : DomHelper.newDocument(xml);  
    } catch (ParserConfigurationException ex) {
      throw new GetResourceException(ivorn, ex);
    } catch (SAXException ex) {
      throw new GetResourceException(ivorn, ex);
    } catch (IOException ex) {
      throw new GetResourceException(ivorn, ex);
    }
  }
  
  /**
   * Raises the registration for a single resource.
   * 
   * @param ivorn IVORN for the resource of choice, including its ivo:// prefix.
   * @return The registration (null if the resource is not registered).
   * 
   * @throws org.astrogrid.registry.server.query.v1_0.GetResourceException If the query cannot be executed.
   */
  public String getResourceToText(String ivorn) throws GetResourceException  {
    System.out.println("Searching for " + ivorn);
    try {
      // Form the internal identifier for the resource in the database.
      String id = ivorn.substring(6).replace('.', '_').replace('/', '_');
      
      // Query the database.
      XMLResource results = xdbRegistry.getResource(id, COLLECTION_NAME);
      if (results == null) {
        System.out.println(ivorn + "was not found");
        return null;
      }
      
      // Return the results as XML text.
      System.out.println("Found " + ivorn);
      return results.getContent().toString();
      
    } catch (XMLDBException ex) {
      throw new GetResourceException(ivorn, ex);
    }
  }
  
  public String getVosiCapabilityUrl(String ivorn) throws XQueryException {
    String xql = "declare namespace ri='http://www.ivoa.net/xml/RegistryInterface/v1.0';\n" 
        + "   for $x in //ri:Resource" 
        + "   where $x/identifier='" + ivorn + "'"
        + "   return string($x/capability[@standardID='ivo://ivoa.net/std/VOSI#capabilities']/interface/accessURL)";
    return xQueryToText(xql);
  }

  /**
   * Evaluates an XQuery and parses the results into DOM documents. The number
   * of documents depends on the query: each element at the top level of the
   * results is wrapped as a separate document to preserve wellformedness. An
   * efficient query will provide a unique, top-level element so that only one
   * document is needed.
   *
   * @param xql The query text.
   * @return The documents (list is empty if no results were raised).
   * @throws XQueryException If the query cannot be executed or if the results
   * cannot be parsed.
   */
  public List<Document> xQueryToDocuments(String xql) throws XQueryException {
    try {
      List<XMLResource> resources = xQueryToXMLResources(xql);
      List<Document> results = new ArrayList<Document>(resources.size());
      for (XMLResource x : resources) {
        String xml = x.getContent().toString();
        results.add(DomHelper.newDocument(xml));
      }
      return results;
    } catch (XMLDBException ex) {
      throw new XQueryException(xql, ex);
    } catch (ParserConfigurationException ex) {
      throw new XQueryException(xql, ex);
    } catch (SAXException ex) {
      throw new XQueryException(xql, ex);
    } catch (IOException ex) {
      throw new XQueryException(xql, ex);
    }
  }

  /**
   * Evaluates an XQuery and returns the results as XML text. The results are a
   * sequence of well-formed XML-elements, one for each resources emitted by the
   * database. If the query raises more than one such resource, and if the query
   * does not wrap them in an enclosing element, the result String as a whole
   * may not be a well-formed XML-document.
   * <p>
   * This method is best suited to the case where the query results are shown
   * directly to the user and are not parsed.
   *
   * @param xql The query
   * @return The XML text (empty if no results were raised).
   * @throws XQueryException If the database cannot execute the query.
   */
  public String xQueryToText(String xql) throws XQueryException {
    StringBuilder b = new StringBuilder();
    try {
      for (XMLResource x : xQueryToXMLResources(xql)) {
        b.append(x.getContent().toString());
      }
    } catch (XMLDBException ex) {
      throw new XQueryException(xql, ex);
    }
    return b.toString();
  }

  /**
   * Executes an XQuery and returns the results in XMLDB's internal format.
   * Significant adjustments are made to the XQUery text before execution.
   *
   * @param xql XQuery text.
   * @return The results (list if empty if the query raised no results).
   * @throws XMLDBException If the
   */
  protected List<XMLResource> xQueryToXMLResources(String xql) throws XMLDBException {

    System.out.println("Given xql: " + xql);
    log.debug("start XQuerySearch");

    System.out.println("Extracted Xquery text= " + xql);
    int tempIndexCheck1 = 0;
    int tempIndexCheck2;

    /*
              * Hmmmm right now Astrogrid knows it is vor:Resource in our db, but others do not and
              * might send vr:Resource we will need to translate/replace those and possibly
              * add a vor namespace. New RI spec now says "//Resource" is a special keyword for the root.
              * @todo use a full path this actually replaces things always to //vor:Resource be nice to be
              * Astrogrid/vor:Resource, but hard to do till we move up to 1.0
     */
    if (xql.contains("//RootResource")) {
      xql = xql.replaceAll("//RootResource", "//RootResource:" + RI_LOCAL_NAME);
    } else if (xql.contains("/RootResource")) {
      xql = xql.replaceAll("/RootResource", "//RootResource:" + RI_LOCAL_NAME);
    } else if (xql.contains("RootResource")) {
      xql = xql.replaceAll("RootResource", "//RootResource:" + RI_LOCAL_NAME);
    }

    boolean cont = true;
    String[] paramCheck;
    String xqlTemp = null;
    /*
              * Hack: Task Launcher and Resource queries will typically send a 'matches' xquery with
              * a lot of 'or' statements these tend to come back in around 6 seconds, but if I switch it to
              * using eXist specefic text method then it comes back in about 2 seconds sometimes 1 second.
              * Problem: Changes things to use eXist near() function but near() does not have full
              * regular expression support like xqueryspec-matches or eXistspecefic-match-all.  A user
              * will be able to do wildcards'*' and '?' and thats about it which is all I ever see anybody
              * do anyways.  We can't quite use match-all because of the way eXist tokenizes strings
              * whereby match-all(node,'x-ray') will find every word 'x' and every word 'ray' :(  The
              * near() function works better it is similiar on the tokenizing of match-all but requires
              * the words to be next to one another unles a user puts in a 3rd parameter integer to let it
              * be further seperated.
     */
    if (xql.split(" or ").length > 2) {
      while (cont) {
        //use split.length to get how many
        if ((tempIndexCheck1 = xql.indexOf("contains(", tempIndexCheck1 + 10)) != -1) {
          paramCheck = xql.substring(tempIndexCheck1, (tempIndexCheck2 = xql.indexOf(')', tempIndexCheck1))).split(",");
          //System.out.println("it had a contains and paramcheck = " + paramCheck.length);
          if (paramCheck.length == 2) {
            xql = xql.replaceAll("contains\\(", " near(");
          }
        } else {
          cont = false;
        }
      }
      tempIndexCheck1 = 0;
      cont = true;
      while (cont) {
        if ((tempIndexCheck1 = xql.indexOf("matches(", tempIndexCheck1 + 10)) != -1) {
          paramCheck = xql.substring(tempIndexCheck1, (tempIndexCheck2 = xql.indexOf(')', tempIndexCheck1))).split(",");
          if (paramCheck.length == 2) {
            //same thing as a near and its case insensitive which is what registry is supposed to be.
            xql = xql.replaceAll("matches\\(", " near(");
          }
          if (paramCheck.length == 3) {
            if (paramCheck[2].trim().charAt(1) == 's' || paramCheck[2].trim().charAt(1) == 'i'
                || paramCheck[2].trim().charAt(1) == 'm' || paramCheck[2].trim().charAt(1) == 'x') {
              //ok there doing the matches with just a regular expression setting lets
              //drop it and use near.
              xqlTemp = xql.substring(0, tempIndexCheck1) + paramCheck[0].replaceAll("matches", "near") + "," + paramCheck[1] + ") " + xql.substring(tempIndexCheck2 + 1);
              xql = xqlTemp;
            }//if
          }//if
        } else {
          cont = false;
        }//else
      }//while
      xql = xql.replaceAll("\\.\\*:", "*:");
    }//if

    /*
              * Hack for older clients there is a bug with eXist with a particular query using the older
              * slower style of where clause with variable and wildcards and lots of constraints.
              * Very odd kind of bug hard to really see what is wrong.  You can use the 
              * variable just fine on smaller type queries but bigger queries really seem to have a problem
              * with it in the where clause.  So change all of them if there is a where caluse.
              * Only one that is different is the full-text search workbench sometimes does that needs
              * to stay as $r//*
              * :( bummer i have to do this.
              * When more updated workbenches come alive then we need to remove this hack. 
     */
    if (xql.contains("where") && (xql.contains("&=") || xql.contains("&amp;="))) {
      //ok using older eXist style functions lets to a relaceAll on $r/ and
      //hoopefully will be good after that. With the exception of $r//* it needs to stay.
      if (!xql.contains("$r//*")) {
        xql = xql.replaceAll("\\$r/", "");
      } else {
        xql = xql.replaceAll("\\$r/", "./");
      }
    }
    
    xql = "declare namespace RootResource = '" + RI_NAMESPACE + "'; " + xql;

    log.info("Modified query text, to be run on the database: " + xql);

    // Eexecute the query and prepare the results for output.
    ResourceSet rs = xdbRegistry.query(xql, COLLECTION_NAME);
    for (long i = 0; i < rs.getSize(); i++) {
      XMLResource x = (XMLResource) rs.getResource(i);
      System.out.println(x.getContent());
    }
    List<XMLResource> resSet = cloneResources(rs);
    
    return resSet;
  }

  private List<XMLResource> cloneResources(ResourceSet inputResources) throws XMLDBException {
    List<XMLResource> clonedResult = new ArrayList<XMLResource>((int) inputResources.getSize());
    for (long i = 0; i < inputResources.getSize(); i++) {
      XMLResource x = (XMLResource) inputResources.getResource(i);
      clonedResult.add(x);
    }
    return clonedResult;
  }

  public String getResourceVersion() {
    return VORESOURCE_VERSION;
  }

  public QueryHelper getQueryHelper() {
    if (queryHelper == null) {
      queryHelper = new QueryHelper(QUERY_WSDL_NS, CONTRACT_VERSION, VORESOURCE_VERSION);
    }
    return queryHelper;
  }
  
}
