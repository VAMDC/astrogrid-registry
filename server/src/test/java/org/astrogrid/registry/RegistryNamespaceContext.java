/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.astrogrid.registry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 *
 * @author Guy Rixon
 */
public class RegistryNamespaceContext implements NamespaceContext {
  
  private final Map<String,String> namespaces;
  
  public RegistryNamespaceContext() {
    namespaces = new HashMap<String,String>();
    namespaces.put("rw", "http://www.ivoa.net/wsdl/RegistrySearch/v1.0");
    namespaces.put("ri", "http://www.ivoa.net/xml/RegistryInterface/v1.0");
  }

  public String getNamespaceURI(String prefix) {
    if (prefix == null) {
      throw new IllegalArgumentException();
    }
    else if (namespaces.containsKey(prefix)) {
      return namespaces.get(prefix);
    }
    else {
      return XMLConstants.NULL_NS_URI;
    }
  }

  public String getPrefix(String namespaceUri) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Iterator getPrefixes(String namespaceURI) {
    return namespaces.keySet().iterator();
  }
  
  

}
