package org.astrogrid.registry.server.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.text.DateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.astrogrid.util.DomHelper;

/**
 * Class: AdminHelper
 * Description: Originally written as a Helper class for the RegistryAdminService
 * still used for grabbing managed authority id nodes, but that is about all.
 * @author kevinbenson
 *
 */
public class AdminHelper {
    
    /**
     * Logging variable for writing information to the logs
     */
   private static final Log log = 
                               LogFactory.getLog(AdminHelper.class);    
    

    
    /**
     * Method: createStats
     * Description: Small stat data on entries that are managed by this Registry
     * at the moment just the identifier and date created.  Returns a Node to be stored
     * in the database.  Now deprecating because not really used anymore.
     *
     * @param tempIdent The identifier for this Resource
     * @return Node representing the <ResourceStat> Element
     * 
     * @deprecated
     */     
    public Node createStats( String tempIdent ) {
        return createStats(tempIdent,true);
    }    
    
    /**
     * Method: createStats
     * Description: Small stat data on entries that are managed by this Registry
     * at the moment just the identifier and date created.  Returns a Node to be stored
     * in the database.  Now deprecating because not really used anymore.
     *
     * @param tempIdent The identifier for this Resource
     * @param addMills add time information in the Node.
     * @return Node representing the <ResourceStat> Element
     * 
     * @deprecated
     */ 
    public Node createStats(String tempIdent, boolean addMillis) {
        log.debug("start createStats");
        Date statsTimeMillis = new Date();
        DateFormat shortDT = DateFormat.getDateTimeInstance();
        String statsXML = "<ResourceStat><Identifier>ivo://" + tempIdent +
                                 "</Identifier>";
        if(addMillis) {
            statsXML += "<StatsDateMillis>" +
                        statsTimeMillis.getTime() +
                        "</StatsDateMillis>";
        }
        statsXML += "<StatsDate>" +
                         shortDT.format(statsTimeMillis) +
                     "</StatsDate></ResourceStat>";
        try {
           log.debug("end createStats");
           return DomHelper.newDocument(statsXML).getDocumentElement();
        }
        catch ( Exception e ) {
        // This will be improved shortly with other Exception handling!
           e.printStackTrace();
           log.error(e);
       }
       return null;
    }    
    
    /**
     * Method: getManagedAuthorityID
     * Description: Grabs the first managedAuthority element from a Registry type.
     * Normally used for updating the Registries main Registry type for adding a sibling
     * node of another managed authority id.
     * 
     * @param doc Document DOM of a Registry type.
     * @return an Element node of the managedAuthority.
     */
    public Node getManagedAuthorityID(Document doc) {
       log.debug("start getManagedAuthorityID");
           NodeList nl = doc.getElementsByTagNameNS("*","ManagedAuthority");
           
           if(nl.getLength() == 0) {
               nl = doc.getElementsByTagNameNS("*","managedAuthority");
           }          
           
           //NodeList nl = DomHelper.getNodeListTags(doc,"ManagedAuthority","vg");
           if(nl.getLength() > 0)
              return nl.item(0);
           log.debug("end getManagedAuthorityID");
       return null;
    }
    
    /**
     * Method: getManagedAuthorities
     * Description: Get a Node list of of managedAuthority elements.
     * @param regNode Registry type Element.
     * @return Node list of managedAuthorities.
     */
    public NodeList getManagedAuthorities(Element regNode) {
        log.debug("start getManagedAuthorityID");
        NodeList nl = regNode.getElementsByTagNameNS("*","ManagedAuthority");
        if(nl.getLength() == 0) {
            nl = regNode.getElementsByTagNameNS("*","managedAuthority");
        }
        return nl;
     }  
    
}