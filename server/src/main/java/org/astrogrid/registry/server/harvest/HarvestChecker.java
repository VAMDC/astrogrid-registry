package org.astrogrid.registry.server.harvest;

import org.astrogrid.registry.server.IChecker;
import org.astrogrid.registry.server.DefaultChecker;
import org.astrogrid.registry.server.CheckerException;
import org.astrogrid.registry.common.RegistryValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import junit.framework.AssertionFailedError;
import java.io.IOException;


import org.astrogrid.util.DomHelper;

public class HarvestChecker extends DefaultChecker implements IChecker {
    
    //private static final String QUERY_ROOT = "VOResources";
    private static final String HARVEST_WS_NAMESPACE = "http://www.ivoa.net/wsdl";
    //private static final String OAI_NAMESPACE = "http://www.openarchives.org/OAI/2.0/";
    
    public boolean checkValidResources(Document resourcesDoc) throws CheckerException {
        try {
            NodeList nl = DomHelper.getNodeListTags(resourcesDoc,"Resource");
            boolean valid = true;
            boolean temp = false;
            if(nl.getLength() > 0) {
                for(int i = 0;i < nl.getLength();i++) {
                    temp = RegistryValidator.isValid(resourcesDoc,"Resource");
                    if(valid && !temp)
                        valid = false;
                }//for
            }//if
            return valid;            
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
    private boolean checkOAIPiece(Document resourcesDoc) {
        try {
            NodeList nl = DomHelper.getNodeListTags(resourcesDoc,"metadata");
            if(nl.getLength() == 0) {
                //nodewal the whole thing it should all be oai namespace
            }else {
                //nodewalk up and siblings
            }
        }catch(IOException ioe) {
            //ignore this cannot happen.
            //DomHelper throws an IOException if a "Node" is not a Document or Element but in this
            //case we know it is always a Document.
            return false;
        }
        return true;
    }
    
    public boolean checkValidWSContract(Document resourcesNode, String desiredWSInterfaceName) throws CheckerException {
        super.checkValidWSContract(resourcesNode,desiredWSInterfaceName);
        if(!resourcesNode.getDocumentElement().getNamespaceURI().equals(HARVEST_WS_NAMESPACE)) {
            throw new CheckerException("Soap Interface NameSpace did not match expected: " + HARVEST_WS_NAMESPACE + " but got: " + resourcesNode.getDocumentElement().getNamespaceURI());            
        }
        return true;
    }
}