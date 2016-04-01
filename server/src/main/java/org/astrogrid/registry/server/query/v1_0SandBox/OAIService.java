package org.astrogrid.registry.server.query.v1_0SandBox;


import org.astrogrid.registry.server.query.DefaultOAIService;

/**
 * Class: OAIService
 * Description: Main Query Service for OAI Harvests.  All the work is actually
 * done in the super abstract class.  Note this webservice actually constructs an
 * internal http url to the internal HTTP-GET OAI service and takees that DOM and
 * returns it.
 * 
 *
 * @author Kevin Benson
 */
public class OAIService extends DefaultOAIService  implements org.astrogrid.registry.server.query.IOAIHarvestService  {
    
    public static final String OAI_WSDL_NS = "http://www.ivoa.net/wsdl/RegistryHarvest/v1.0";
    
    public static final String CONTRACT_VERSION = "1.0";    

    public OAIService() {
        super(OAI_WSDL_NS, CONTRACT_VERSION);       
    }
   
}