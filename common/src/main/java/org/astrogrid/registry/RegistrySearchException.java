package org.astrogrid.registry;

public class RegistrySearchException extends RegistryException {

   public RegistrySearchException(String msg) {
      //Need to log here.
      super(msg);
   }

   public RegistrySearchException(Throwable cause) {
      //Need to log here.
      super(cause);
      cause.printStackTrace();      
   }

   public RegistrySearchException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);
      cause.printStackTrace();      
   }
  
}