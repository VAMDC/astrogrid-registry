package org.astrogrid.registry.registration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An instant of time. This is a trivial extension of java.util.Date in
 * which the toString() method is overridden to produce date-time values in
 * the form needed by the registry schemata.
 *
 * @author Guy Rixon
 */
public class Instant extends Date {
  
  /**
   * The format to be used by toString().
   */
  SimpleDateFormat format; 
  
  /**
   * Constructs a Instant representing the tiem of construction.
   */
  public Instant() {
    super();
    this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  }
  
  /**
   * Constructs an Instant from a given Date.
   */
  public Instant(Date when) {
    super();
    this.setTime(when.getTime());
    this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  }
  
  /**
   * Represents the Instant in the dialect of ISO8601 used by the
   * IVOA registry.
   */
  public String toString() {
    return this.format.format(this);
  }
  
}
