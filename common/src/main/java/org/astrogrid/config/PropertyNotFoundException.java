/*
 * $Id: PropertyNotFoundException.java,v 1.1 2004/02/16 22:47:20 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */

package org.astrogrid.config;

/**
 * Thrown when a property cannot be found.
 * This is a runtime error so that applications break when <i>required</i>
 * properties cannot be found, without having to build all the catches in.
 *
 * @author M Hill
 */

public class PropertyNotFoundException extends RuntimeException {
   
   public PropertyNotFoundException(String msg) {
      super(msg);
   }
   
}

/*
$Log: PropertyNotFoundException.java,v $
Revision 1.1  2004/02/16 22:47:20  mch
For reporting configuration errors

 */

