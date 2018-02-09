/*
 * $Id: ConfigException.java,v 1.3 2004/06/26 14:58:43 jdt Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */
///CLOVER:OFF 
package org.astrogrid.config;

/**
 * Thrown when problems encountered with getting configuration properties.
 * Catching things like type errors and always rethrowing as these makes
 * it easier for sysadmins etc to locate the problem when messing around
 * with config files
 *
 * @author M Hill
 */

public class ConfigException extends RuntimeException {
   
   public ConfigException(String msg, Throwable th) {
      super(msg, th);
   }
   
   public ConfigException(String msg) {
      super(msg);
   }
   
}

/*
$Log: ConfigException.java,v $
Revision 1.3  2004/06/26 14:58:43  jdt
disable clover for these classes

Revision 1.2  2004/03/01 13:34:18  mch
Added message-only constructor

Revision 1.1  2004/02/16 22:46:51  mch
For reporting configuration errors

 */

