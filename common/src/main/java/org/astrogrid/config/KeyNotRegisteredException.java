/*
 * $Id: KeyNotRegisteredException.java,v 1.2 2008/09/17 08:16:05 pah Exp $
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

public class KeyNotRegisteredException extends ConfigException {

   /**
    * @param msg
    * @param th
    */
   public KeyNotRegisteredException(String msg, Throwable th) {
      super(msg, th);
   }

   /**
    * @param msg
    */
   public KeyNotRegisteredException(String msg) {
      super(msg);
   }

  
}


/*
 * $Log: KeyNotRegisteredException.java,v $
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
