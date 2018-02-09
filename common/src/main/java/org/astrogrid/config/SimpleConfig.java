/*
 * $Id: SimpleConfig.java,v 1.11 2004/03/18 22:34:29 mch Exp $
 *
 * (C) Copyright Astrogrid...
 */

package org.astrogrid.config;


import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A static singleton for easy one-line access to an application-global
 * configuration file.
 * <p>
 * Use like this:
 *    SimpleConfig.getSingleton().getProperty(key);
 * <p>
 * A static singleton so that all packages have easy access to the *same*
 * configuration set in one runtime environment, although this may be loaded
 * from several files.  So although this configuration is a common access point, it can be used to
 * access several configuration files.  Any application can call the 'load'
 * methods and properties will be loaded from the given file/url/etc.
 * <p/>
 * This does mean that packages using the configuration file must make sure their
 * keys are unique. The most reliable way to do this is to prefix each key with
 * the package name (ie the code namespace) but this doesn't produce very nice
 * property files for humans to edit.
 * <p/>
 * @todo work out a nice way of ensuring keys are unique or throw exceptions if not.
 * <p/>
 * Not entirely happy with the mixed static/instanceness of this - maybe ought
 * to break it into two but don't see it's necessary yet...
 *
 * @author M Hill
 */

public abstract class SimpleConfig
{
   /** Singleton Instance used to provide load methods */
   private static final Config instance = ConfigFactory.getConfig(SimpleConfig.class);
   
   /**
    * Returns the instance for applications to work with - saves having to
    * mirror-implement all the instance methods here */
   public static Config getSingleton() {
      return instance;
   }
   
   /**
    * Static access to load from a file at the given filepath
    */
   public static void load(String filepath) throws IOException
   {
      File f = new File(filepath);
      load(f.toURL());
   }

   /**
    * Static access to load from a url
    */
   public static void load(URL url) throws IOException
   {
      instance.loadFromUrl(url);
   }
   
   /**
    * Static access to the instance method
    */
   public static String loadedFrom()
   {
      return instance.loadedFrom();
   }

   /**
    * Sets the given property - useful for tests.
    */
   public static void setProperty(String key, String value)
   {
      instance.setProperty(key, value);
   }

   /**
    * Returns the property value of the given key; throws an exception if the
    * key is not set
    */
   public static String getProperty(String key)
   {
      return instance.getString(key);
   }

   /**
    * Returns the property value of the given key, or the given default if
    * the key is not found or the properties file has not been created (eg
    * this has not been initialised)
    */
   public static String getProperty(String key, Object defaultValue)
   {
      if (defaultValue == null)  {
         return instance.getString(key, null);
      }
      else {
         return instance.getString(key, defaultValue.toString());
      }
   }

}

