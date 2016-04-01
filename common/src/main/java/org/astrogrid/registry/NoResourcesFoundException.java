package org.astrogrid.registry;

/**
 * Class: NoResourcesFoundException
 * Description: Will probably soon go away and just let the Registry return no resources, but currently the server side
 * query mechanism will throw a AxisFault with this NoResourcesFoundException in it.
 * @author Kevin Benson
 *
 */
public class NoResourcesFoundException extends RegistryException {

   public NoResourcesFoundException(String msg) {
      //Need to log here.
      super(msg);
   }

   public NoResourcesFoundException(Throwable cause) {
      //Need to log here.
      super(cause);      
   }

   public NoResourcesFoundException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);      
   }  
}