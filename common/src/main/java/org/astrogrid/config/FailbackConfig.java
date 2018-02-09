/*
 * $Id: FailbackConfig.java,v 1.32 2004/11/08 10:04:10 mch Exp $
 *
 * Copyright 2003 AstroGrid. All rights reserved.
 *
 * This software is published under the terms of the AstroGrid Software License,
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 */
package org.astrogrid.config;

import java.util.*;
import javax.naming.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.security.AccessControlException;

/**
 * A configuration aggregator that sources property values from a variety of
 * sources. The following sources are tried in order.
 * <ul>
 * <li> Local cache (i.e. values set programmatically by
 * {@link #setProperty(java.lang.String, java.lang.Object)} have precedence)
 * <li> System properties of the application.
 * <li> JNDI environment.
 * <li> the file astrogrid.properties, found on the classpath.
 * </ul>
 * <p>
 * A call to {@link #getProperty(java.lang.String)} will try the sources in
 * order and return the first match. If there are no matches, it will throw.
 * <p>
 * A call to {@link #getProperty(java.lang.String, java.lang.Object)} will try
 * the sources in order and return the first match. If there are no matches it
 * will return the given default-value.
 * <p>
 * Initialisation is 'lazy' - particularly as we may not want to go looking for
 * configuration files if everything is in Jndi. However given the dangers of
 * double-checked locking, the initialisation routines are synchronised and
 * checked *within* for the initialisation flag
 * <p>
 * Failures are *all* reported as exceptions, unless a default is given. So if
 * you think a value might be missing and you don't want your app to fallover,
 * supply a default.
 * <p>
 * @author mch
 * @author Guy Rixon - Order of data sources changed and properties file 
 * restricted to astrogrid.properties on the classpath.
 */
public class FailbackConfig extends Config {
  
  /**
   * Name of properties file as a system resource.
   */
  private static final String RESOURCE_NAME = "astrogrid.properties";

  /**
   * Cache - only used for setProperty at the moment, so that Jndi can reload
   * on-the-fly as necessary
   */
  private Hashtable cache = new Hashtable();

  /**
   * initialised flags
   */
  private boolean jndiInitialised = false;
  private boolean fileInitialised = false;

  /**
   * Jndi context
   */
  private InitialContext jndiContext = null;

  /**
   * Properties file context
   */
  private Properties properties = null;

  /**
   * JNDI key to url that locates the properties file
   */
  private static String propertyUrlKey = "org.astrogrid.config.url";

  /**
   * JNDI key to properties file
   */
  private static String propertyKey = "org.astrogrid.config.filename";

  /**
   * prefix to keys when accessing JNDI services. No idea why this is
   * required...
   */
  private static String jndiPrefix = "java:comp/env/";

  /**
   * Usual filename for properties file in classpath, etc
   */
  private static String configFilename = "astrogrid.properties";

  /**
   * filename for default properties file that come with the distribution
   */
  private static String defaultFilename = "default.properties";

  /**
   * Call this before the first getProperty if you want to use a different
   * property file (eg for testing). Throws an exception if properties already
   * loaded, as it's too late then
   */
  public void setConfigFilename(String newName) {
    if (fileInitialised) {
      throw new ConfigException("Configuration already initialised - too late to set config filename", null);
    }
    configFilename = newName;
  }

  /**
   * Initialise Jndi access. Note that the context may be null (if there is no
   * Jndi service) even after initialisation. Synchronized for thread safety
   */
  private synchronized void initialiseJndi() {

    if (!jndiInitialised) {

      jndiInitialised = true;

      try {
        jndiContext = new InitialContext();

        try {
          jndiContext.lookup("java:comp/env"); //this should be present in a Servlet Container

          addLoadedFrom("JNDI");
        } catch (NameNotFoundException nnfe) {
          /*ignore if we can't find 'Dummy' */ }

        log.info("Config access to JNDI initialised (" + jndiContext + ")");
      } catch (ServiceUnavailableException sue) {
        //not a problem, but note
        log.debug("No JNDI service found (" + sue + ") so will not use JNDI for config...");
        jndiContext = null;
      } catch (NoInitialContextException nice) {
        //not a problem
        log.debug("No JNDI service found (" + nice + ") so will not use JNDI for config...");
        jndiContext = null;
      } catch (NamingException ne) {

        throw new ConfigException("Initialising Jndi Access", ne);
      }
    }
  }

  /**
   * Initialise access to properties file. Looks first in jndi for url to file,
   * then system environment, then classpath, finally working directory.
   */
  private synchronized void initialiseFile()  {
    
    // Ensure that there is somewhere to put the properties from the file.
    // Discard any old values.
    if (properties == null) {
      properties = new Properties();
    } else {
      properties.clear();
    }
    
    // Try to load the properties file from the same package as the loading class.
    InputStream is1 = this.getClass().getResourceAsStream(RESOURCE_NAME);
    if (is1 != null) {
      try {
        properties.load(is1);
        fileInitialised = true;
        return;
      } catch (IOException ex) {
        String msg = String.format(
            "%s was found in the package %s but cannot be read",
            RESOURCE_NAME,
            this.getClass().getPackage()
        );
        throw new RuntimeException(msg, ex);
      }
    }
    
    // Try to load the properties file from the top of the package tree.
    InputStream is2 = this.getClass().getResourceAsStream("/" + RESOURCE_NAME);
    if (is2 != null) {
      try {
        properties.load(is2);
        fileInitialised = true;
        return;
      } catch (IOException ex) {
        String msg = String.format(
            "%s was found at the top of the package tree but cannot be read",
            RESOURCE_NAME,
            this.getClass().getPackage()
        );
        throw new RuntimeException(msg, ex);
      }
    }

    log.warn(RESOURCE_NAME + " was not found anywhere on the classpath."); 
    fileInitialised = false;
  }

  /**
   * Looks for given config filename absolutely or in classpath and working
   * directory, and loads it if found. Returns false if not found. This could
   * probably make use of Config.resolveFile()
   */
  private boolean lookForConfigFile(String givenFilename) {

    //replace ${stuff} with sys.env values for stuff
    String filename = resolveEnvironmentVariables(givenFilename);
    if (filename.equals(givenFilename)) {
      log.debug("Looking for " + givenFilename);
      givenFilename = givenFilename + " => " + filename;
    } else {
      log.debug("Looking for " + givenFilename + " => " + filename);
    }

    //if it's absolute, look absolutely
    File f = new File(filename);
    //if (f.isAbsolute()) {
    if (f.exists()) {
      loadFromFile(f);
      return true;
    }
    //return false;
    //}

    //look for file in classpath.
    //see http://www.javaworld.com/javaworld/javaqa/2003-08/01-qa-0808-property.html
    //NB this works via URL as we don't expect to get config files from inside jars
    log.debug("Looking for " + givenFilename + " on classpath");
    //      URL configUrl = ClassLoader.getSystemResource(filename);
    URL configUrl = this.getClass().getResource(filename);
    if (configUrl != null) {
      try {
        loadFromUrl(configUrl);

        log.info("Configuration file loaded from '" + configUrl + "' (found in classpath)");

        return true;

      } catch (IOException ioe) {
        throw new ConfigException(ioe + " loading property file at '" + configUrl + "' (from classpath)", ioe);
      }
    }

    //look for it in the working directory
    log.debug("Looking for " + givenFilename + " in working directory");
    if (f.exists()) {
      loadFromFile(f);
      return true;
    }

    return false;
  }

  private void loadFromFile(File f) {
    try {
      loadFromUrl(f.toURL());
      log.info("Configuration file loaded from '" + f.getAbsoluteFile() + "'");
      return;
    } catch (IOException ioe) {
      throw new ConfigException(ioe + " loading property file at '" + f.getAbsoluteFile(), ioe);
    }
  }

  /**
   * Loads the properties from the given stream. While this should only be
   * called once during part of the normal initialisation process, we also allow
   * public access so that test harnesses etc can load their own properties
   * differently.
   */
  public synchronized void loadFromUrl(URL url) throws IOException {
    if (properties == null) {
      properties = new Properties();
    }

    //we load them into a local variable properties instance first, so that
    //we can override the included config settings with these ones.
    Properties localProperties = new Properties();
    localProperties.load(url.openStream());

    addLoadedFrom(url.toString()); //add to string indicating what's happening

    //look for chain; if this file contains the property 'include.config.filename'
    //then load that into the global properties
    String includeFile = localProperties.getProperty("include.config.filename");
    if (includeFile != null) {
      boolean found = lookForConfigFile(includeFile);
      if (!found) {
        throw new ConfigException("include config file '" + includeFile + "' not found");
      }
    }

    //override any existing set propertis with the local ones loaded above
    properties.putAll(localProperties);

  }

  /**
   * Keys in the current JDK Property implementation must not contain
   * whitespace, colons or equals
   */
  public static void assertKeyValid(String key) {
    assert key.indexOf(":") == -1 : "Key '" + key + "' contains an illegal character - a colon";
    assert key.indexOf(" ") == -1 : "Key '" + key + "' contains an illegal character - a space";
    assert key.indexOf("=") == -1 : "Key '" + key + "' contains an illegal character - an equals sign";
  }

  /**
   * Gets a property value.
   *
   * @param key Name of property.
   * @return The value.
   * @throws PropertyNotFoundException
   */
  public Object getProperty(String key) {

    assertKeyValid(key);

    String lookedIn = "Cache";

    // Search the programmatically-set properties.
    if (cache.containsKey(key)) {
      return cache.get(key);
    }

    // Look for a system property.
    lookedIn = lookedIn + ", sysenv";
    String sysPropValue = System.getProperty(key);
    if (sysPropValue != null) {
      return sysPropValue;
    }

    // Search the JNDI environment, if JNDI is active.
    try {
      if (!jndiInitialised) {
        initialiseJndi();
      }

      if (jndiContext != null) {
        lookedIn = lookedIn + ", JNDI";
        return jndiContext.lookup(jndiPrefix + key);
      } else {
        lookedIn = lookedIn + ", (No JNDI)";
      }
    } catch (NameNotFoundException nnfe) {
    } //ignore, we'll look somewhere else
    catch (NamingException ne) {
      throw new ConfigException(ne + " locating key=" + key + " in JNDI", ne);
    }

    // Search the properties file, if there is one.
    // "Initializing the file" loads its property values into the
    // instance variable called properties.
    if (!fileInitialised) {
      initialiseFile();
    }
    if (properties == null) {
      lookedIn = lookedIn + ", (no config file)";
    } else {
      String value = properties.getProperty(key);
      lookedIn = lookedIn + ", config file(s) (" + loadedFrom() + ")";

      if (value != null) {
        return value;
      }
    }

    throw new PropertyNotFoundException("Could not find '" + key + "' in: " + lookedIn);
  }

  /**
   * Set property. Stores in cache so it overrides all other properties with the
   * same key.
   */
  public void setProperty(String key, Object value) {
    //note that we cannot store in the 'Properties' instance as that can
    //only handle strings
    if (value == null) {
      if (cache.containsKey(key)) {
        cache.remove(key);
      }
    } else {
      cache.put(key, value);
    }
  }

  /**
   * Returns array of values for the given key. Throws exception if property not
   * found in Jndi, then configuration file, then system environment.
   */
  public Object[] getProperties(String key) {

    assertKeyValid(key);

    String lookedIn = "Cache";

    //first look in cache
    if (cache.containsKey(key)) {
      Object o = cache.get(key);
      if (o instanceof Object[]) {
        return (Object[]) o;
      } else {
        return new Object[]{o};
      }
    }

    //first look in jndi
    try {
      if (!jndiInitialised) {
        initialiseJndi();
      }

      if (jndiContext != null) {
        lookedIn = lookedIn + ", JNDI";
        Context javacontext = (Context) jndiContext.lookup(jndiPrefix);
        NamingEnumeration en = jndiContext.list(jndiPrefix + key);
        Vector values = new Vector();
        while (en.hasMoreElements()) {
          NameClassPair pair = (NameClassPair) en.next();
          String value = null;
          //not sure how all this works, so for now will ignore naming exceptions
          try {
            value = javacontext.lookup(pair.getName()).toString();
          } catch (NamingException ne) {
            value = "??Failed lookup: " + ne;
          } //ignore - value=??failed
          values.add(value);
        }
        return values.toArray();
      } else {
        lookedIn = lookedIn + ", (No JNDI)";
      }
    } catch (NameNotFoundException nnfe) {
    } //ignore, we'll look somewhere else
    catch (NamingException ne) {
      throw new ConfigException(ne + " locating key=" + key + " in JNDI", ne);
    }

    //try the properties file. It can only hold one value per key, so we
    //look for the key (and return a single element array if found) and/or
    //key.1, key.2, key.3 until a key returns null.
    if (!fileInitialised) {
      initialiseFile();
    }

    if (properties == null) {
      lookedIn = lookedIn + ", (no config file)";
    } else {
      lookedIn = lookedIn + ", config file(s) (" + loadedFrom() + ")";

      String value = properties.getProperty(key);
      String value1 = properties.getProperty(key + ".1");

      //check that there aren't both settings without number and settings with
      if ((value != null) && (value1 != null)) {
        throw new ConfigException("Both single value and sets of values defined for key " + key + " in property file");
      }

      //only one value set
      if (value != null) {
        return new Object[]{value};
      }

      if (value1 != null) {
        Vector values = new Vector();
        int v = 2;
        while (value1 != null) {
          values.add(value1);
          value1 = properties.getProperty(key + "." + v);
          v++;
        }
        return values.toArray();
      }

    }

    //try the system environment
    lookedIn = lookedIn + ", sysenv";
    String value = System.getProperty(key);
    if (value != null) {
      return new Object[]{value};
    }

    throw new PropertyNotFoundException("Could not find '" + key + "' in: " + lookedIn);
  }

  /**
   * Set property to array. Stores in cache so it overrides all other properties
   * with the same key.
   */
  public void setProperties(String key, Object[] values) {
    if (values == null) {
      if (cache.containsKey(key)) {
        cache.remove(key);
      }
    } else {
      cache.put(key, values);
    }
  }

  /**
   * Returns a list of keys. This list is made up of the values in the cache,
   * JNDI, properties file and system environment keys; note that duplicate keys
   * will be hidden.
   */
  public Set keySet() {

    if (!jndiInitialised) {
      initialiseJndi();
    }
    if (!fileInitialised) {
      initialiseFile();
    }

    Set allKeys = new HashSet();

    //cache
    allKeys.addAll(cache.keySet());

    //jndi
    if (jndiContext != null) {
      try {
        Hashtable jndi = jndiContext.getEnvironment();
        allKeys.addAll(jndi.keySet());
      } catch (NamingException ne) {
        throw new ConfigException("Getting Environment from " + jndiContext, ne);
      }
    }

    //property files
    if (properties != null) {
      allKeys.addAll(properties.keySet());
    }

    //sys env
    allKeys.addAll(System.getProperties().keySet());

    return allKeys;
  }

  /**
   * Dumps config contents
   */
  public void dumpConfig(Writer writer) {

    PrintWriter out = new PrintWriter(writer);

    out.println("Configuration loaded from: " + loadedFrom());
    out.println();

    //-- cache --
    if (cache.isEmpty()) {
      out.println("(Cache is empty)");
    } else {
      out.println("Cache:");
      Enumeration c = cache.keys();
      while (c.hasMoreElements()) {
        Object key = c.nextElement();
        out.println(formKeyValue(key, cache.get(key)));
      }
    }

    //-- JNDI --
    out.println();
    if (jndiContext != null) {
      out.println("JNDI:");
      try {
        out.println("JNDI Environment:");
        Hashtable env = jndiContext.getEnvironment();
        Enumeration j = env.keys();
        while (j.hasMoreElements()) {
          Object key = j.nextElement();
          out.println(formKeyValue(key, env.get(key)));
        }
        out.println("JNDI Names:");
        Context javacontext = (Context) jndiContext.lookup(jndiPrefix);
        NamingEnumeration n = jndiContext.list(jndiPrefix);

        while (n.hasMoreElements()) {
          NameClassPair key = (NameClassPair) n.next();
          String value = "??Failed lookup";
          //not sure how all this works, so for now will ignore naming exceptions
          try {
            value = javacontext.lookup(key.getName()).toString();
          } catch (NamingException ne) {
          } //ignore - print fail value
          out.println(formKeyValue(key, value));
        }

      } catch (NamingException ne) {
        ne.printStackTrace(out);
      }

    } else {
      out.println("(No JNDI)");
    }
    out.println();

    //--- Property Files ---
    if (properties != null) {
      out.println("Properties from file(s):");
      Enumeration p = properties.keys();
      while (p.hasMoreElements()) {
        Object key = p.nextElement();
        out.println(formKeyValue(key, properties.getProperty(key.toString())));
      }
    } else {
      out.println("(No Config File)");
    }
    out.println();

    //-- System environment variables ----
    out.println("System Environment Variables:");
    try {
      Enumeration s = System.getProperties().keys();
      while (s.hasMoreElements()) {
        Object key = s.nextElement();
        out.println(formKeyValue(key, System.getProperty(key.toString())));
      }
    } catch (AccessControlException ace) {
      //might not be allowed blanket access to system enviornment variables...
      out.println("No blanket access permitted: " + ace);
    }

    out.flush();
  }

  /**
   * Formats a key/value pair for printing. Used by dumpConfig. Does noddy check
   * for 'password' in the key string and hides value if present
   */
  public String formKeyValue(Object key, Object value) {
    if (key.toString().toLowerCase().indexOf("password") > -1) {
      return "  " + key + " = <hidden>";
    } else {
      return "  " + key + " = " + value;
    }
  }

}
