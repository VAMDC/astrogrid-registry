package org.astrogrid.registry.server;


import org.w3c.dom.Document;


public interface IChecker {
    
    public boolean checkValidResources(Document resourcesNode) throws CheckerException;
    
    public boolean checkValidWSContract(Document resourcesNode, String desiredWSInterfaceName) throws CheckerException;
    
    
}