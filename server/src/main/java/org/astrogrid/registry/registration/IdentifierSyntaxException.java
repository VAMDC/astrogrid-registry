package org.astrogrid.registry.registration;

/**
 * An Exception to report syntax errors in an IVO identifier.
 *
 * @author Guy Rixon
 */
public class IdentifierSyntaxException extends Exception {
  public IdentifierSyntaxException(String reason) {
    super(reason);
  }
}
