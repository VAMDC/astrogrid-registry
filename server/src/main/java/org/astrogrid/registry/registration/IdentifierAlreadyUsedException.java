package org.astrogrid.registry.registration;

/**
 * An execption to report that an IVO identifier is already taken.
 *
 * @author Guy Rixon
 */
public class IdentifierAlreadyUsedException extends Exception {
  public IdentifierAlreadyUsedException(String ivorn) {
    super("Identifier " + ivorn + " is already in use.");
  }
}
