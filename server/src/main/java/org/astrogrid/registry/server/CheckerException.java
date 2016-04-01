package org.astrogrid.registry.server;

import org.astrogrid.registry.RegistryException;

public class CheckerException extends RegistryException {
        
    public CheckerException(String msg) {
      //Need to log here.
      super(msg);
    }

    public CheckerException(Throwable cause) {
      //Need to log here.
      super(cause);
      cause.printStackTrace();      
    }

    public CheckerException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);
      cause.printStackTrace();      
    }  
}