package org.astrogrid.registry.common;

import org.astrogrid.test.AstrogridAssert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A Small helper class to validate XML from the Registry primarily updates to the Registry.
 * @author Kevin Benson
 *
 */
public class RegistryValidator {
    

    /**
     * Method: isValid
     * Description: Performs a validation on the XML from xerces to see if it is a valid XML for the Registry.
     * @param doc XML document to be validated
     * @param rootElement - rootElement of the XML
     * @return true if it is valid otherwise an Exception is thrown up.
     */
    public static boolean isValid(Document doc, String rootElement) {
        NodeList nl = doc.getElementsByTagNameNS("*",rootElement);
        for(int i = 0;i < nl.getLength(); i++) { 
            AstrogridAssert.assertSchemaValid((Element)nl.item(i),rootElement,SchemaMap.ALL);
        }
        return true;
    }
    
    /**
     * Method: isValid
     * Description: Performs a validation on the XML from xerces to see if it is a valid XML for the Registry.
     * @param doc XML document to be validated
     * @param rootElement - rootElement of the XML
     * @return true if it is valid otherwise an Exception is thrown up.
     */
    public static boolean isValid(Element elem, String rootElement) {
        AstrogridAssert.assertSchemaValid(elem,rootElement,SchemaMap.ALL);        
        return true;
    }
    
    /**
     * Method: isValid
     * Description: Grabs the rootElement of the XML from the Local or Node Name of the given XML document then
     * validates the XML.
     * @param doc XML document to be validated.
     * @return true if it is valid otherwise an Exception is thrown up.
     */
    public static boolean isValid(Document doc) {
        String rootElement = doc.getDocumentElement().getLocalName();
        if(rootElement == null) {
            rootElement = doc.getDocumentElement().getNodeName();
        }
        AstrogridAssert.assertSchemaValid(doc,rootElement,SchemaMap.ALL);
        return true;
    }
}