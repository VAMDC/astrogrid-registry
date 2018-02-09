/*
 * $Id: ConfigFactory.java,v 1.5 2008/09/17 08:16:05 pah Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.astrogrid.config;

import java.util.Hashtable;

/**
 * For those who might want to create multiple configuration <i>instances</i>,
 * this factory can be used.  NB multiple configuration <i>files</i> can be
 * read into a single instance.
 * <p>
 * Alternatively use is SimpleConfig, a static Singleton for
 * easy key/value property getting and setting.
 * <p>
 * If Astrogrid's config implementation changes, change this factory method.
 * <p>
 *
 * @author M Hill
 */


public class ConfigFactory
{
   /** Created PropertyConfig instances */
   private static final Hashtable configs = new Hashtable();
   
   /**
    * Creates an instance of an XML/Document-based (ie XPath/Node list)
    * configuration
    * @param id specifies a particular instance
    */
   public static synchronized Config getConfig(Object id)
   {
      Config config = (Config) configs.get(id);
   
      if (config == null) {
         config = new FailbackConfig();
         configs.put(id, config);
      }

      return config;
   }
   
 
   
}

