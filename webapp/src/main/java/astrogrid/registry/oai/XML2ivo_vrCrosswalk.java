
/**
*Copyright (c) 2000-2002 OCLC Online Computer Library Center,
*Inc. and other contributors. All rights reserved.  The contents of this file, as updated
*from time to time by the OCLC Office of Research, are subject to OCLC Research
*Public License Version 2.0 (the "License"); you may not use this file except in
*compliance with the License. You may obtain a current copy of the License at
*http://purl.oclc.org/oclc/research/ORPL/.  Software distributed under the License is
*distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
*or implied. See the License for the specific language governing rights and limitations
*under the License.  This software consists of voluntary contributions made by many
*individuals on behalf of OCLC Research. For more information on OCLC Research,
*please see http://www.oclc.org/oclc/research/.
*
*The Original Code is XML2oai_dc.java.
*The Initial Developer of the Original Code is Jeff Young.
*Portions created by ______________________ are
*Copyright (C) _____ _______________________. All Rights Reserved.
*Contributor(s):______________________________________.
*/


package astrogrid.registry.oai;

import ORG.oclc.oai.server.crosswalk.*;

import java.util.Properties;
import org.astrogrid.util.DomHelper;
import ORG.oclc.oai.server.verb.CannotDisseminateFormatException;
import org.astrogrid.contracts.http.filters.ContractsFilter;


/**
 * Convert native "item" to oai_dc. In this case, the native "item"
 * is assumed to already be formatted as an OAI <record> element,
 * with the possible exception that multiple metadataFormats may
 * be present in the <metadata> element. The "crosswalk", merely
 * involves pulling out the one that is requested.
 */
public class XML2ivo_vrCrosswalk extends Crosswalk {
    private static String elementName = "Resource";
    private static final String prefix = "vr:";
    
    private String versionStr = null;
    
    private static final String ASTROGRID_SCHEMA_BASE = "http://software.astrogrid.org/schema/";
    protected static String schemaLocationBase;

    //private static final String elementStart = "<" + elementName;
    //private static final String elementEnd = elementName + ">";
    
    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
          if(schemaLocationBase == null) {              
              schemaLocationBase = ContractsFilter.getContextURL() != null ? ContractsFilter.getContextURL() + "/schema/" :
                                   ASTROGRID_SCHEMA_BASE;
          }
    }  
    

    /**
     * The constructor assigns the schemaLocation associated with this crosswalk. Since
     * the crosswalk is trivial in this case, no properties are utilized.
     *
     * @param properties properties that are needed to configure the crosswalk.
     */
    public XML2ivo_vrCrosswalk(Properties properties) {
     super("http://www.ivoa.net/xml/VOResource/v" + properties.getProperty("registry_version") + " http://www.ivoa.net/xml/VOResource/VOResource-v" + properties.getProperty("registry_version") + ".xsd ");
     this.versionStr = properties.getProperty("registry_version");
     
    }

    /**
     * Can this nativeItem be represented in DC format?
     * @param nativeItem a record in native format
     * @return true if DC format is possible, false otherwise.
     */
    public boolean isAvailableFor(Object nativeItem) {
        String fullItem = (String)nativeItem;
        if ((fullItem.indexOf(("<" + elementName)) >= 0) || (fullItem.indexOf(("<" + prefix + ":" + elementName)) >= 0) )
            return true;
        return false;
   }

    /**
     * Perform the actual crosswalk.
     *
     * @param nativeItem the native "item". In this case, it is
     * already formatted as an OAI <record> element, with the
     * possible exception that multiple metadataFormats are
     * present in the <metadata> element.
     * @return a String containing the XML to be stored within the <metadata> element.
     * @exception CannotDisseminateFormatException nativeItem doesn't support this format.
     */
    public String createMetadata(Object nativeItem)
   throws CannotDisseminateFormatException {
	   String fullItem = (String)nativeItem;
	   try {
		   org.w3c.dom.Document doc = DomHelper.newDocument("<wrapper xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + fullItem + "</wrapper>");
		   org.w3c.dom.NodeList nl = doc.getElementsByTagNameNS("*","Resource");
		   if(nl.getLength() > 0) {
			   int tempIndex;
			    StringBuffer resContent = new StringBuffer(DomHelper.ElementToString((org.w3c.dom.Element)nl.item(0)));
				String temp = resContent.substring(0,resContent.indexOf(">"));
		        //see if it has a schemaLocation attribute if not then we need to add it.
		          if(temp.indexOf("schemaLocation") == -1 && versionStr.equals("1.0")) {
		      		String schemaLocations = null;
		            schemaLocations =  "http://www.ivoa.net/xml/RegistryInterface/v1.0 "  + schemaLocationBase + 
		                               "registry/RegistryInterface/v1.0/RegistryInterface.xsd " +
		                               "http://www.ivoa.net/xml/VOResource/v1.0 " + 
		                               schemaLocationBase + "vo-resource-types/VOResource/v1.0/VOResource.xsd ";
		        	  if((tempIndex = temp.indexOf("type")) != -1) {
		              	tempIndex = temp.indexOf("\"",tempIndex);
		              	temp = temp.substring((tempIndex+1),temp.indexOf("\"",tempIndex+1));
		                if(temp.endsWith("Registry") || temp.endsWith("Authority")) {
		                    schemaLocations += " http://www.ivoa.net/xml/VORegistry/v1.0 " + schemaLocationBase + "vo-resource-types/VORegistry/v1.0/VORegistry.xsd";
		                } else {
		                    schemaLocations += " http://www.ivoa.net/xml/VODataService/v1.0 " + schemaLocationBase + "vo-resource-types/VODataService/v1.0/VODataService.xsd " +
		                    "http://www.ivoa.net/xml/VOTable/v1.0 " + schemaLocationBase + "vo-formats/VOTable/v1.0/VOTable.xsd";
	                        schemaLocations += " http://www.ivoa.net/xml/ConeSearch/v1.0 " + schemaLocationBase + "vo-resource-types/ConeSearch/v1.0/ConeSearch.xsd";    
	                        schemaLocations += " http://www.ivoa.net/xml/SIA/v1.0 " + schemaLocationBase + "vo-resource-types/SIA/v1.0/SIA.xsd";
	                        schemaLocations += " http://www.ivoa.net/xml/SSA/v0.4 " + schemaLocationBase + "vo-resource-types/SSA/v0.4/SSA.xsd";
	                        schemaLocations += " urn:astrogrid:schema:vo-resource-types:STAP:v1.0 " + schemaLocationBase + "vo-resource-types/STAP/v1.0/STAP.xsd";
	                        schemaLocations += " http://www.ivoa.net/xml/CEA/v1.0rc1 " + schemaLocationBase + "vo-resource-types/CEAService/v1.0rc1/CEAService.xsd";
		                }
		                //add schemaLocation.
		                resContent.insert(resContent.indexOf(">")," xsi:schemaLocation=\"" + schemaLocations + "\"");
		        	  }//if type
		          }//if schemalocations

		          //return DomHelper.ElementToString((org.w3c.dom.Element)nl.item(0));
		          return resContent.toString();
		   } else {
			   throw new CannotDisseminateFormatException(getSchemaLocation());
		   }
	   }catch(Exception e) {
		   e.printStackTrace();
		   throw new CannotDisseminateFormatException(getSchemaLocation());
	   }
   
    }
}
