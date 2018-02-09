/*
 * $Id: KeyUse.java,v 1.2 2008/09/17 08:16:05 pah Exp $
 * 
 * Created on 08-Jul-2005 by Paul Harrison (pharriso@eso.org)
 * Copyright 2005 ESO. All rights reserved.
 *
 * This software is published under the terms of the ESO 
 * Software License, a copy of which has been included 
 * with this distribution in the LICENSE.txt file.  
 *
 */ 

package org.astrogrid.config;

import java.io.PrintWriter;

/**
 * Interface defining the operations expected with keys in getting and setting config values. 
 * @author Paul Harrison (pharriso@eso.org) 08-Jul-2005
 * @version $Name:  $
 * @since initial Coding
 */
public interface KeyUse {
   /**
    * register a key that will be used as a property lookup. It is necessary to do this before an key is used to look something up.
    * @param key
    */
   public void registerKey(PropertyKey key);
   /**
    * write a list of registered keys out to the specified stream.
    * @param out
    */
   public void listkeys(PrintWriter out);
   /**
    * Setp a property in the underlying config.
    * @param key
    * @param val
    */
   public void setProperty(PropertyKey key, String val);
   /**
    * Get a property from the underlying config. This method should pre-check if the key has been registered.
    * @param key
    * @return
    */
   public String getProperty(PropertyKey key);
}


/*
 * $Log: KeyUse.java,v $
 * Revision 1.2  2008/09/17 08:16:05  pah
 * result of merge of pah_community_1611 branch
 *
 * Revision 1.1.2.1  2008/05/17 20:55:13  pah
 * safety checkin before interop
 *
 * Revision 1.1  2005/07/08 13:11:08  pharriso
 * make propertylookup keys have to be preregistered and provide a simpler interface to properties through the KeyUse interface.
 *
 */
