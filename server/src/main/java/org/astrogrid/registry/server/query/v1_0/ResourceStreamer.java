package org.astrogrid.registry.server.query.v1_0;

import org.astrogrid.registry.server.query.ResourceStreamDelegate;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;

/**
 * Class: ResultStreamer
 * Description: RegistryXMLStreamDelegate is the main class for streaming results
 * to the client, this subclass overrides the abstract method getResourceContent to
 * do anything special to any single Resource before being sent to the client.  Also the class
 * passes up to the super class the set of Resources and any xml wrapper that is needed for the
 * XML.
 * @author kevinbenson
 *
 */
public class ResourceStreamer extends ResourceStreamDelegate {
	
    /**
     * Logging variable for writing information to the logs
     */
     private static final Log log = LogFactory.getLog(ResourceStreamer.class);    

	

	/**
	 * Constructor
	 * Simply passes the information up to the abstract super class RegistryXMLStreamDelegate.
	 * Passes a set of XML Resources to be iterated through and sent to the client.
	 * @param resSet ResourceSet much like ResultSet.  Set of XML Resources.
	 * @param xmlWrapper XML String to be used for wrapping around the XML (full set) of Resources.
	 */
	public ResourceStreamer(List resSet, String xmlWrapper) {
		super(resSet, xmlWrapper);
	}
	
	/**
	 * Constructor
	 * Simply passes the information up to the abstract super class RegistryXMLStreamDelegate.
	 * Passes a set of XML Resources to be iterated through and sent to the client.
	 * @param resSet ResourceSet much like ResultSet.  Set of XML Resources.
	 * @param xmlWrapper XML String to be used for wrapping around the XML (full set) of Resources.
	 * @param identOnly boolean to determine if only identifier elements are to be returned.	 * 
	 */	
	public ResourceStreamer(List resSet, String xmlWrapper, boolean identOnly) {
		super(resSet, xmlWrapper, identOnly);
	}	

	/**
	 * Method: getResourceContent
	 * Description: returns a String of XML for a Single XML Resource.  A Resource is
	 * passed in and analyzed to see if it needs anything special added to it such as schemaLocations or
	 * only to extract out the identifier elements.
	 * @param res Single XML Resource object from the database.
	 * @param identOnly boolean to determine if only identifier elements are needed.
	 * @return XML String of the Resource to be streamed out to the client.
	 * @throws org.xmldb.api.base.XMLDBException Error fetching/connecting to the database to get the Resource object contents.
	 */
	public String getResourceContent(Resource res, boolean identOnly) throws org.xmldb.api.base.XMLDBException {
		log.info("getResourceContent for 1.0 entered");
		StringBuffer resContent = new StringBuffer(res.getContent().toString());
		int tempIndex;
		if(identOnly) {
			tempIndex = resContent.indexOf(">",resContent.indexOf("identifier"));
			//ri:VOResources with the defined ri namespace was written out as a parent element.
			return "<ri:identifier xmlns:ri=\"http://www.ivoa.net/xml/RegistryInterface/v1.0\">" + resContent.substring(tempIndex+1,resContent.indexOf("<",tempIndex)) + "</ri:identifier>";
		}
		String schemaLocations = null;
        schemaLocations =  "http://www.ivoa.net/xml/RegistryInterface/v1.0 "  + schemaLocationBase + 
                           "registry/RegistryInterface/v1.0/RegistryInterface.xsd " +
                           "http://www.ivoa.net/xml/VOResource/v1.0 " + 
                           schemaLocationBase + "vo-resource-types/VOResource/v1.0/VOResource.xsd ";
        
		
		String temp = resContent.substring(0,resContent.indexOf(">"));
        //see if it has a schemaLocation attribute if not then we need to add it.
          if(temp.indexOf("schemaLocation") == -1) {
        	  if((tempIndex = temp.indexOf("type")) != -1) {
              	tempIndex = temp.indexOf("\"",tempIndex);
              	temp = temp.substring((tempIndex+1),temp.indexOf("\"",tempIndex+1));
                if(temp.endsWith("Registry") || temp.endsWith("Authority")) {
                    schemaLocations += " http://www.ivoa.net/xml/VORegistry/v1.0 " + schemaLocationBase + "vo-resource-types/VORegistry/v1.0/VORegistry.xsd";
                } else {
                    schemaLocations += " http://www.ivoa.net/xml/VODataService/v1.0 " + schemaLocationBase + "vo-resource-types/VODataService/v1.0/VODataService.xsd " +
                    "http://www.ivoa.net/xml/VOTable/v1.0 " + schemaLocationBase + "vo-formats/VOTable/v1.0/VOTable.xsd";
                    if(temp.endsWith("ConeSearch")) {
                        schemaLocations += " http://www.ivoa.net/xml/ConeSearch/v1.0 " + schemaLocationBase + "vo-resource-types/ConeSearch/v1.0/ConeSearch.xsd";    
                    }else if(temp.endsWith("SimpleImageAccess")) {
                        schemaLocations += " http://www.ivoa.net/xml/SIA/v1.0 " + schemaLocationBase + "vo-resource-types/SIA/v1.0/SIA.xsd";
                    }else if(temp.endsWith("TabularDB")) {
                        schemaLocations += " urn:astrogrid:schema:vo-resource-types:TabularDB:v1.0 " + schemaLocationBase + "vo-resource-types/TabularDB/v1.0/TabularDB.xsd";
                    }else if(temp.endsWith("OpenSkyNode")) {
                        schemaLocations += " http://www.ivoa.net/xml/OpenSkyNode/v1.0 " + schemaLocationBase + "vo-resource-types/OpenSkyNode/v1.0/OpenSkyNode.xsd";
                    }else if(temp.endsWith("CeaService") || temp.endsWith("CeaHttpApplicationType") || temp.endsWith("CeaApplicationType")) {
                        schemaLocations += " http://www.ivoa.net/xml/CEA/v1.0rc1 " + schemaLocationBase + "vo-resource-types/CEAService/v1.0rc1/CEAService.xsd";
                    }
                }
                //add schemaLocation.
                resContent.insert(resContent.indexOf(">")," xsi:schemaLocation=\"" + schemaLocations + "\"");
        }//if type
          }//if schemalocations
          return resContent.toString();          
	}
	
	
}