package org.astrogrid.registry.registration;

import org.astrogrid.config.SimpleConfig;
import org.astrogrid.registry.server.xmldb.XMLDBRegistry;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.astrogrid.util.DomHelper;

/**
 * Java bean to handle the list of authority identifiers managed in
 * a registry. 
 *
 * @author Guy Rixon
 */
public class PublishingAuthorities {
  
  /**
   * The collection (of XML documents in the DB) to be searched.
   * This is fixed to be the VOResource-1.0 collection as the query
   * used below only worls with that schema.
   */
  static public String COLLECTION_NAME = "astrogridv1_0";
  
  /**
   * Reveals the authority identifiers managed in this registry.
   * This involves querying the registry and reading its self-registration,
   * so the set of authorities returned is always up to date.
   *
   * @return The array of authority identifiers.
   */
  public String[] getAuthorities() throws XMLDBException {
    
    // Find out the primary publishing-authority for this registry.
    // This informs the query for the other authorities.
    String authority = 
        SimpleConfig.getSingleton().getString("reg.amend.authorityid", "dunno");
    
    // Query to get a complete list of publishing authorities.
    String q = "declare namespace ri = 'http://www.ivoa.net/xml/RegistryInterface/v1.0'; " +
               "declare namespace vg = 'http://www.ivoa.net/xml/VORegistry/v1.0'; " +
               "declare namespace xsi = 'http://www.w3.org/2001/XMLSchema-instance'; " +
               "for $r in //ri:Resource[@xsi:type = 'vg:Registry' and match-all(identifier,'.*" + authority + ".*')] " +
               "return $r/managedAuthority";
    System.out.println(q);
    XMLDBRegistry db = new XMLDBRegistry();
    ResourceSet rs = db.query(q, COLLECTION_NAME);
    System.out.println(rs.getSize());
    String[] a = new String[(int)rs.getSize()];
    for (int i = 0; i < rs.getSize(); i++) {
      XMLResource r = (XMLResource)rs.getResource(i);
      System.out.println(r.getContent());
      Node d = r.getContentAsDOM();
      NodeList c = d.getChildNodes();
      StringBuffer b  = new StringBuffer();
      for (int j = 0; j < c.getLength(); j++) {
        Node n = c.item(j);
        try {
        	if(n.getNodeType() == Node.TEXT_NODE) {
        		System.out.println("yes it is a text node");
        		b.append(n.getNodeValue());
        	}else if(n.getNodeType() == Node.ELEMENT_NODE) {
            	System.out.println("getFirstChild val = " + n.getFirstChild().getNodeValue());
            	b.append(n.getFirstChild().getNodeValue());        		
        	}
        }catch(Exception e) {e.printStackTrace();}
      }
      a[i] = b.toString();
    }
    return a;
  }
    
}
