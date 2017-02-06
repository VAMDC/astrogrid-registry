package org.astrogrid.registry.server.admin;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The set of authorities managed by a registry.
 * 
 * @author Guy Rixon
 */
public class ManagedAuthorities extends HashMap<AuthorityList,AuthorityList> {
  
  /**
   * Determines if an authority is managed by this registry. Compares with the
   * internal list of managed authorities ignoring registry versions.
   * 
   * @param authority The authority to be tested.
   * @return True if the authority is managed here.
   */
  public boolean isManaged(String authority) {
    for (AuthorityList a : keySet()) {
      if (authority.equals(a.getAuthorityID())) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Reports the set of managed authorities as a comma-separated list.
   * 
   * @return The list.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    Iterator<AuthorityList> i = keySet().iterator();
    while(i.hasNext()) {
      sb.append(i.next().getAuthorityID());
      if (i.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append('}');
    return sb.toString();
  }

}
