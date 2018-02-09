/*
 * $Id: PropertyKey.java,v 1.2 2008/09/17 08:16:05 pah Exp $
 * 
 * Created on May 2, 2005 by Paul Harrison (pharriso@eso.org)
 * Copyright 2005 ESO. All rights reserved.
 *
 * This software is published under the terms of the ESO 
 * Software License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */

package org.astrogrid.config;


/**
 * The config lookup keys. This class provides a static declaration of the keys
 * that are used in VOS configurations. It is centralized to promote reuse of
 * key names between components, which makes administrators lives easier.
 * 
 * @author Paul Harrison (pharriso@eso.org) May 2, 2005
 * @version $Name:  $
 * @since initial Coding
 */
public class PropertyKey implements Comparable {
   private final String key;
   private final String description;
   
   public PropertyKey(String key, String description) {
      this.key = key;
      this.description = description;
   }

   /**
    * @return Returns the description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return Returns the key.
    */
   public String getKey() {
      return key;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object o)
   {
      if (o instanceof PropertyKey) {
        
         String s = ((PropertyKey)o).key;
         return key.compareTo(s);
      }
      else {
         throw new ClassCastException("Property key not specified");
      }
   }

   /**
    * @see java.lang.Object#equals(Object)
    */
   public boolean equals(Object object)
   {
      if (!(object instanceof PropertyKey)) {
         return false;
      }
      PropertyKey rhs = (PropertyKey) object;
      return key.equals(rhs.key);
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer result = new StringBuffer(key);
      result.append(" - ");
      result.append(description);
      return result.toString();
   }
   

 
   
 
}

/*
 * $Log: PropertyKey.java,v $
 * Revision 1.2  2008/09/17 08:16:05  pah
 * result of merge of pah_community_1611 branch
 *
 * Revision 1.1.2.1  2008/05/17 20:55:13  pah
 * safety checkin before interop
 *
 * Revision 1.2  2005/08/23 12:35:38  pharriso
 * tighten up visibilities to prevent accidental misuse
 *
 * Revision 1.1  2005/07/08 13:11:08  pharriso
 * make propertylookup keys have to be preregistered and provide a simpler interface to properties through the KeyUse interface.
 *
 * Revision 1.1  2005/05/13 11:52:20  pharriso
 * started to separate out the interface with view to making proper factoryadded some unit tests.
 *
 */
