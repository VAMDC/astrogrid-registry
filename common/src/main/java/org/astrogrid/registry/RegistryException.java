package org.astrogrid.registry;
/**
 * Class: RegistryException
 * Description: Main Exception class for the Registry. 
 * @todo introduce a fuller tree of registry exception subtypes?
 *
 */
public class RegistryException extends Exception {

   public RegistryException() {
      
   }

   public RegistryException(String msg) {
      //Need to log here.
      super(msg);
   }

   public RegistryException(Throwable cause) {
      //Need to log here.
      super(cause);
      //cause.printStackTrace();      
   }

   public RegistryException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);
      //cause.printStackTrace();      
   }  
   
   /** Constant for makeFault - input from client has caused problem */
   protected final static boolean CLIENTFAULT = true;
   /** Constant for makeFault - problem with server (or unknown) */
   protected final static boolean SERVERFAULT = false;   
}