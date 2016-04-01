package org.astrogrid.registry.server.query;

import org.astrogrid.registry.server.IChecker;
import org.astrogrid.registry.server.DefaultChecker;
import org.astrogrid.registry.server.CheckerException;
import org.w3c.dom.Document;

public class QueryChecker extends DefaultChecker implements IChecker {
    
    private static final String QUERY_ROOT = "VOResources";
    private static final String QUERY_WS_NAMESPACE = "http://www.ivoa.net/wsdl";    
    
    /**
     * Method checkValidResources
     * Description: Use the super class for validating.
     * @param resourcesDoc XML DOM containing registry XML Resource types.
     * @return usually will always return true or throw a CheckerException.
     * @throws CheckerException exception thrown with validation as part of the getMessage of the exception.
     */    
    public boolean checkValidResources(Document resourcesDoc) throws CheckerException {
        return super.checkValidResources(resourcesDoc);
    }
    
    /**
     * Method: checkValidWSContract
     * Description: validates a SOAP XML Message to check that the interface method name is what is expected.
     * @param resourcesNode XML DOM of the soap message
     * @param desiredWSInterfaceName desired probably shoudl be called expected,  Interface name
     * @return true if valid
     * @throws CheckerException not a valid soap message response to the registry interface.
     */    
    public boolean checkValidWSContract(Document resourcesNode, String desiredWSInterfaceName) throws CheckerException {
        super.checkValidWSContract(resourcesNode,desiredWSInterfaceName);
        if(!resourcesNode.getDocumentElement().getNamespaceURI().equals(QUERY_WS_NAMESPACE)) {
            throw new CheckerException("Soap Interface NameSpace did not match expected: " + QUERY_WS_NAMESPACE + " but got: " + resourcesNode.getDocumentElement().getNamespaceURI());            
        }
        return true;
    }
    
}