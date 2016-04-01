package org.astrogrid.registry.server;

import org.astrogrid.registry.RegistryException;

public class InvalidStorageNodeException extends RegistryException {

    private static final String MESSAGE = "Only Doument and Element Nodes are allowed for storage into the Registry XMLDB database.  You have submitted an invalid type of Node.";
    
    public InvalidStorageNodeException() {
        super(MESSAGE);
    }
        
    public InvalidStorageNodeException(String msg) {
      //Need to log here.
      super(msg);
    }

    public InvalidStorageNodeException(Throwable cause) {
      //Need to log here.
      super(cause);
      cause.printStackTrace();      
    }

    public InvalidStorageNodeException(String msg, Throwable cause) {
      //Need to log here.
      super(msg, cause);
      cause.printStackTrace();      
    }  
}