package org.astrogrid.registry;

public class RegistryUpdateException extends RegistryException {

   public RegistryUpdateException(String msg) {
      //Need to log here.
      super(msg);
   }

   public RegistryUpdateException(Throwable cause) {
      //Need to log here.
      super(cause);
      cause.printStackTrace();      
   }

   public RegistryUpdateException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);
      cause.printStackTrace();      
   }
  
}