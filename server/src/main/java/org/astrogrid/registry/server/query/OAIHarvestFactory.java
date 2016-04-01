package org.astrogrid.registry.server.query; 

/**
 * Class: OAIHarvestFactory
 * Description: Harvest factory to get a Harvest Service to perform a 
 * harvest query and return results in the OAI format.
 * @author kevinbenson
 *
 */
public class OAIHarvestFactory {
    
	/**
	 * Method: createOAIHarvestService
	 * Description: create a Harvest Service to return results in the OAI harvest format.  
	 * Gets a Harvest Service based on a contract version and returns the interface of that
	 * service.
	 * @param contractVersion contract version
	 * @return interface IOAIHarvestService which contains all the necessary OAI query methods
	 * to return results to the client.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
    public static IOAIHarvestService createOAIHarvestService(String contractVersion) throws IllegalAccessException, 
                                                                            InstantiationException, 
                                                                            ClassNotFoundException {
       Class cl = Class.forName("org.astrogrid.registry.server.query.v" + contractVersion.replace('.','_') + ".OAIService");
       //cl.isIstance(ISearch)
       return (IOAIHarvestService)cl.newInstance();
    }
    
}