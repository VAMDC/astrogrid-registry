package org.astrogrid.registry.common;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Class: RegistrySchemaMap
 * Description: static class that provides maps of all namespace - schema locations in this project.
 * useful to pass to schema-validation methods in {@link org.astrogrid.test.AstrogridAssert}
 *@ see org.astrogrid.test.AstrogridAssert
 */
public class SchemaMap {

    /** Construct a new SchemaMap
     * 
     */
    private SchemaMap() {
    }
    
    public static final Map<String,URL> ALL; 
    
    /**
     * Static area for placing all namespaces.
     */
    static {
        // populate the map.
        ALL = new HashMap<String,URL>();
        
        //ADQL schemas
        ALL.put("http://www.ivoa.net/xml/ADQL/v1.0",SchemaMap.class.getResource("/schema/adql/ADQL/v1.0/ADQL.xsd"));
        ALL.put("http://www.ivoa.net/xml/ADQL/v0.9",SchemaMap.class.getResource("/schema/adql/ADQL/v0.9/ADQL.xsd"));
        ALL.put("http://www.ivoa.net/xml/ADQL/v0.8",SchemaMap.class.getResource("/schema/adql/ADQL/v0.8/ADQL.xsd"));
        ALL.put("http://www.ivoa.net/xml/ADQL/v0.7.4",SchemaMap.class.getResource("/schema/adql/ADQL/v0.7.4/ADQL.xsd"));
        ALL.put("http://adql.ivoa.net/v0.73",SchemaMap.class.getResource("/schema/adql/ADQL/v0.7.3/ADQL.xsd"));
                
        //CEA schemas
        ALL.put("http://www.ivoa.net/xml/CEA/base/v1.1", SchemaMap.class.getResource("/schema/cea/CEABase/v1.1/CEABase.xsd"));        
        ALL.put("http://www.ivoa.net/xml/CEA/types/v1.1",SchemaMap.class.getResource("/schema/cea/CEATypes/v1.1/CEATypes.xsd"));
        ALL.put("http://www.astrogrid.org/schema/CEAImplementation/v2.0",SchemaMap.class.getResource("/schema/cea/CEAImplementation/v2.0/CEAImplementation.xsd"));
        ALL.put("http://www.astrogrid.org/schema/CEAExecutionRecord/v1.1",SchemaMap.class.getResource("/schema/cea/CEAExecutionRecord/v1.1/CEAExecutionRecord.xsd"));
        ALL.put("http://www.ivoa.net/xml/UWS/v0.9.2",SchemaMap.class.getResource("/schema/cea/UWS/v0.9/UWS.xsd"));
//        ALL.put("http://www.ivoa.net/xml/UWS/v1.0rc1",SchemaMap.class.getResource("/schema/cea/UWS/v1.0/UWS.xsd"));
        
        //obsolete CEA schemas - but still used in the original CEC interface
        ALL.put("http://www.astrogrid.org/schema/CommonExecutionArchitectureBase/v1", SchemaMap.class.getResource("/schema/cea/CommonExecutionArchitectureBase/v1.0/CommonExecutionArchitectureBase.xsd"));        
        ALL.put("http://www.astrogrid.org/schema/CEATypes/v1",SchemaMap.class.getResource("/schema/cea/CEATypes/v1.0/CEATypes.xsd"));
        ALL.put("http://www.astrogrid.org/schema/CEAImplementation/v1",SchemaMap.class.getResource("/schema/cea/CEAImplementation/v1.0/CEAImplementation.xsd"));
        ALL.put("http://www.astrogrid.org/schema/CEAImplementation/v2.0rc1",SchemaMap.class.getResource("/schema/cea/CEAImplementation/v2.0rc1/CEAImplementation.xsd"));
        
        //VAMDC schemata:
        ALL.put("http://www.vamdc.org/xml/VAMDC-TAP/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/VAMDC-TAP/v1.0/VAMDC-TAP.xsd"));
        ALL.put("http://www.vamdc.org/xml/XSAMS-consumer/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/XSAMS-consumer/v1.0/XSAMS-consumer.xsd"));
        //ALL.put("http://www.vamdc.eu/xml/TAPXSAMS/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/TapXSAMS/v1.0/TAPXsams.xsd"));
        //ALL.put("http://www.vamdc.eu/xml/TAPXSAMS/v1.01",SchemaMap.class.getResource("/schema/vo-resource-types/TapXSAMS/v1.01/TAPXsams.xsd"));
        //ALL.put("http://www.ivoa.net/xml/TAP/v0.1",SchemaMap.class.getResource("/schema/vo-resource-types/TAP/v0.1/TAPRegExt-v0.1.xsd"));
        

        //Contract schemas
        ALL.put("urn:astrogrid:schema:architecture:Contract:v0.1",SchemaMap.class.getResource("/schema/architecture/Contract/v0.1/Contract.xsd"));
        
        //jes schemas
        ALL.put("http://www.astrogrid.org/schema/AGParameterDefinition/v1",SchemaMap.class.getResource("/schema/jes/AGParameterDefinition/v1.0/AGParameterDefinition.xsd"));        
        ALL.put("http://www.astrogrid.org/schema/AGWorkflow/v1",SchemaMap.class.getResource("/schema/jes/AGWorkflow/v1.0/AGWorkflow.xsd"));
        ALL.put("http://www.astrogrid.org/schema/Credentials/v1",SchemaMap.class.getResource("/schema/jes/Credentials/v1.0/Credentials.xsd"));
        ALL.put("http://www.astrogrid.org/schema/ExecutionRecord/v1",SchemaMap.class.getResource("/schema/jes/ExecutionRecord/v1.0/ExecutionRecord.xsd"));

        
        //registry schemas
        ALL.put("http://www.ivoa.net/xml/RegistryInterface/v1.0",SchemaMap.class.getResource("/schema/registry/RegistryInterface/v1.0/RegistryInterface.xsd"));
        ALL.put("http://www.ivoa.net/xml/RegistryInterface/v0.1",SchemaMap.class.getResource("/schema/registry/RegistryInterface/v0.1/RegistryInterface.xsd"));
        
        //oai schemas
        ALL.put("http://www.openarchives.org/OAI/2.0/",SchemaMap.class.getResource("/schema/oai/OAI/v2.0/OAI.xsd"));
        
        //dsa schemas
        ALL.put("urn:astrogrid:schema:dsa:TableMetaDoc:v0.2",SchemaMap.class.getResource("/schema/dsa/DSAMetadoc/v0.2/TableMetaDoc.xsd"));
        // (this one's namespace ought to have included *:dsa:*, but it
        //  was originally defined as shown below so I'm not changing it now)
        ALL.put("urn:astrogrid:schema:TableMetaDoc:v1",SchemaMap.class.getResource("/schema/dsa/DSAMetadoc/v1.0/TableMetaDoc.xsd"));
        ALL.put("urn:astrogrid:schema:dsa:TableMetaDoc:v1.1",SchemaMap.class.getResource("/schema/dsa/DSAMetadoc/v1.1/TableMetaDoc.xsd"));
        
        //stc schemas (go with adql imports usually)
        
        ALL.put("http://www.w3.org/1999/xlink",SchemaMap.class.getResource("/schema/stc/STC/v1.30/XLINK.xsd"));        	
        ALL.put("http://www.ivoa.net/xml/STC/stc-v1.30.xsd",SchemaMap.class.getResource("/schema/stc/STC/v1.30/STC.xsd"));        	
        ALL.put("http://www.ivoa.net/xml/STC/stc-v1.10.xsd",SchemaMap.class.getResource("/schema/stc/STC/v1.10/STC.xsd"));
        ALL.put("http://www.ivoa.net/xml/STC/stc-v1.20.xsd",SchemaMap.class.getResource("/schema/stc/STC/v1.20/STC.xsd"));
        
        ALL.put("http://www.ivoa.net/xml/STC/STCcoords/v1.10",SchemaMap.class.getResource("/schema/stc/STCcoords/v1.10/STCcoords.xsd"));
        ALL.put("http://www.ivoa.net/xml/STC/STCcoords/v1.20",SchemaMap.class.getResource("/schema/stc/STCcoords/v1.20/STCcoords.xsd"));
        ALL.put("http://www.ivoa.net/xml/STC/STCregion/v1.10",SchemaMap.class.getResource("/schema/stc/STCRegion/v1.10/STCregion.xsd"));
        ALL.put("http://www.ivoa.net/xml/STC/STCregion/v1.20",SchemaMap.class.getResource("/schema/stc/STCRegion/v1.20/STCregion.xsd"));
        
        //votable schemas
        ALL.put("http://www.ivoa.net/xml/VOTable/v1.0",SchemaMap.class.getResource("/schema/vo-formats/VOTable/v1.0/VOTable.xsd"));
        ALL.put("http://www.ivoa.net/xml/VOTable/v1.1",SchemaMap.class.getResource("/schema/vo-formats/VOTable/v1.1/VOTable.xsd"));

        //vo-resource-types
        ALL.put("http://www.ivoa.net/xml/CEAService/v0.2",SchemaMap.class.getResource("/schema/vo-resource-types/CEAService/v0.2/CEAService.xsd"));
        ALL.put("http://www.ivoa.net/xml/CEA/v1.0rc1",SchemaMap.class.getResource("/schema/vo-resource-types/CEAService/v1.0rc1/CEAService.xsd"));
        ALL.put("http://www.ivoa.net/xml/CEA/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/VOCEA/v1.0/VOCEA.xsd"));
        
        ALL.put("http://www.ivoa.net/xml/ConeSearch/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/ConeSearch/v1.0/ConeSearch.xsd"));
        ALL.put("http://www.ivoa.net/xml/ConeSearch/v0.3",SchemaMap.class.getResource("/schema/vo-resource-types/ConeSearch/v0.3/ConeSearch.xsd"));
        
        ALL.put("urn:astrogrid:schema:vo-resource-types:STAP:v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/STAP/v1.0/STAP.xsd"));
        ALL.put("http://www.ivoa.net/xml/SSA/v0.4",SchemaMap.class.getResource("/schema/vo-resource-types/SSA/v0.4/SSA.xsd"));
                
        ALL.put("http://www.ivoa.net/xml/SIA/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/SIA/v1.0/SIA.xsd"));        
        ALL.put("http://www.ivoa.net/xml/SIA/v0.7",SchemaMap.class.getResource("/schema/vo-resource-types/SIA/v0.7/SIA.xsd"));
        ALL.put("http://www.ivoa.net/xml/OpenSkyNode/v0.1",SchemaMap.class.getResource("/schema/vo-resource-types/OpenSkyNode/v0.1/OpenSkyNode.xsd"));
        ALL.put("http://www.ivoa.net/xml/SkyNode/v0.2",SchemaMap.class.getResource("/schema/vo-resource-types/OpenSkyNode/v0.2/OpenSkyNode.xsd"));      
        ALL.put("urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3",SchemaMap.class.getResource("/schema/vo-resource-types/TabularDB/v0.3/TabularDB.xsd"));
        ALL.put("http://www.ivoa.net/xml/VODataService/v0.5",SchemaMap.class.getResource("/schema/vo-resource-types/VODataService/v0.5/VODataService.xsd"));
        ALL.put("http://www.ivoa.net/xml/VODataService/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/VODataService/v1.0/VODataService.xsd"));        
        // VODataServiceCoverage-* omitted - no target namespace declared. doesn't look valid as a stand-alone schema
       
      	ALL.put("http://www.ivoa.net/xml/VORegistry/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/VORegistry/v1.0/VORegistry.xsd"));        	
        ALL.put("http://www.ivoa.net/xml/VORegistry/v0.3",SchemaMap.class.getResource("/schema/vo-resource-types/VORegistry/v0.3/VORegistry.xsd"));
       
        ALL.put("http://www.ivoa.net/xml/VOResource/v0.10",SchemaMap.class.getResource("/schema/vo-resource-types/VOResource/v0.10/VOResource.xsd"));
        ALL.put("http://www.ivoa.net/xml/VOResource/v1.0",SchemaMap.class.getResource("/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd"));
        
      	ALL.put("http://www.ivoa.net/xml/VOApplication/v1.0rc1",SchemaMap.class.getResource("/schema/vo-resource-types/VOApplication/v1.0rc1/VOApplication.xsd"));
        // likewise, omitted VOResourceRelType-*        
        ALL.put("http://www.ivoa.net/xml/VOStandard/v0.1",SchemaMap.class.getResource("/schema/vo-resource-types/VOStandard/v0.1/VOStandard.xsd"));
        ALL.put("http://www.ivoa.net/xml/VOStandard/v0.2beta1",SchemaMap.class.getResource("/schema/vo-resource-types/VOStandard/v0.2beta1/VOStandard.xsd"));

        ALL.put("urn:astrogrid:schema:RegistryStoreResource:v1",SchemaMap.class.getResource("/schema/registry/RegistryUpdate/v1.0/RegistryStoreResource.xsd"));

        
        
        //This is an aid to instantiating resource documents with oXygen et al.; it provides a global element for each resource type.
        ALL.put("urn:astrogrid:schema:vo-resource-types:AllResourceTypes:v0.2",SchemaMap.class.getResource("/schema/vo-resource-types/AllResourceTypes/v0.2/AllResourceTypes.xsd"));
        

    }
    
    public static URL getSchemaURL(String namespace) {
	    return ALL.get(namespace);
    }

  /**
   * Converts the map into a comma-separated list of namespace URIs and
   * schema locations. This list may be the value of a schemaLocation attribute.
   * 
   * @return The list of URI-URL pairs.
   */
  public static String toCommaSeparatedList() {
    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, URL> e : ALL.entrySet()) {
      result.append(e.getKey());
      result.append(' ');
      result.append(e.getValue().toString());
      result.append(' ');
    }
    return result.toString();
  }
  
}