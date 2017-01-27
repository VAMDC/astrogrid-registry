/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.astrogrid.registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Guy Rixon
 */
public class TestRegistry {
  
  /**
   * Installs the database files and directories. Pre-loads the self-registration
   * for the registry.
   * 
   * @param parentDirectory The directory in which to build the database.
   * @return The database-configuration file.
   * @throws IOException 
   */
  public static void installRegistry(File parentDirectory) throws IOException {
    
    // Create the directory for the database if it does not exist.
    if (!parentDirectory.exists()) {
      parentDirectory.mkdirs();
    }
    
    // Copy in the database-confoguration file. The database engine will
    // expect to find the data directory as a sibling of the configuration file.
    File conf = new File(parentDirectory, "conf.xml");
    copyResourceToFile("/conf.xml", conf);
    
    // Create the data directory.
    File data = new File(parentDirectory, "data");
    if (!data.exists()) {
      data.mkdirs();
    }
  }
  
  public static void emptyRegistry(File parentDirectory) throws IOException {
    File data = new File(parentDirectory, "data");
    for (File f : data.listFiles()) {
      f.delete();
    }
  }
  
  public static File getDatabaseConfiguration(File parentDirectory) throws IOException {
    return new File(parentDirectory, "conf.xml");
  }
  
  private static void copyResourceToFile(String resource, File file) throws IOException {
    InputStream in = TestRegistry.class.getResourceAsStream(resource);
    OutputStream out = new FileOutputStream(file);
    int c = in.read();
    while (c != -1) {
      out.write(c);
      c = in.read();
    }
    out.flush();
    out.close();
    in.close();
  }

}
