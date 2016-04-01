package org.astrogrid.registry.server;

import org.astrogrid.registry.server.IChecker;
import org.astrogrid.registry.server.CheckerException;
import org.astrogrid.registry.common.RegistryValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import junit.framework.AssertionFailedError;
import java.io.IOException;


import org.astrogrid.util.DomHelper;

/**
 * Class: DefaultChecker
 * Description: implements IChecker class.  This class is the default checker for 
 * validationg registry resources, checking to make sure they are valid against the schema.  Primary
 * purpose has been used with jsp pages for checking purposes. This is the top level class for
 * the admin, query, and harvest checkers.  
 * Also will validate the SOAP message to check if the XML Soap used the correct soap response.  See
 * the sub-classes for validation against the actual namespace in the SOAP response.
 * message
 */
public class DefaultChecker implements IChecker {
    
    private static final String QUERY_ROOT = "VOResources";
    
    /**
     * Method checkValidResources
     * Description: validates xml for the registry.  Particular multiple type resources which MUST
     * be surrounded by VOResources or individual Resource elements.
     * @param resourcesDoc XML DOM containing registry XML Resource types.
     * @return usually will always return true or throw a CheckerException.
     * @throws CheckerException exception thrown with validation as part of the getMessage of the exception.
     */
    public boolean checkValidResources(Document resourcesDoc) throws CheckerException {
        try {
            NodeList nl = DomHelper.getNodeListTags(resourcesDoc,QUERY_ROOT);        
            if(nl.getLength() == 0) {
                nl = DomHelper.getNodeListTags(resourcesDoc,"Resource");
                if(nl.getLength() == 0) {
                    throw new CheckerException("Nothing to Validate");
                }else {
                    return RegistryValidator.isValid(resourcesDoc,"Resource");
                }
            }else {
                return RegistryValidator.isValid(resourcesDoc,QUERY_ROOT);
            }
        }catch(AssertionFailedError afe) {
            throw new CheckerException(afe.getMessage());
        }catch(IOException ioe) {
            //ignore this cannot happen.
            //DomHelper throws an IOException if a "Node" is not a Document or Element but in this
            //case we know it is always a Document.
            return false;
        }  
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
        if(resourcesNode.getDocumentElement().getLocalName() != null){
            if(!resourcesNode.getDocumentElement().getLocalName().equals(desiredWSInterfaceName)){
                throw new CheckerException("Soap Interface Name did not match expected: " + desiredWSInterfaceName + " but got: " + resourcesNode.getDocumentElement().getLocalName());
            }
        }else {
           if(!resourcesNode.getDocumentElement().getNodeName().equals(desiredWSInterfaceName)) {
               throw new CheckerException("Soap Interface Name did not match expected: " + desiredWSInterfaceName + " but got: " + resourcesNode.getDocumentElement().getNodeName());               
           }
        }
        return true;
    }
    
}