package org.astrogrid.registry.server.harvest; 

/**
 * Class: HarvestFactory
 * Description: Small factory class that instatntiates the RegistryHarvestService
 * that must(should) exist for all contracts and returns the interface IHarvest wich
 * the Service must implement.
 * @author kevinbenson
 *
 */
public class HarvestFactory {
    
	/**
	 * Method: createHarveestService
	 * Description: creates a HarvestService and returns the IHarvest interface
	 * that they implement to start any harvesting.
	 * @param contractVersion contract version.
	 * @return interface of IHarvest
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
    public static IHarvest createHarvestService(String contractVersion) throws IllegalAccessException, 
                                                                            InstantiationException, 
                                                                            ClassNotFoundException {
       Class cl = Class.forName("org.astrogrid.registry.server.harvest.v" + contractVersion.replace('.','_') + ".RegistryHarvestService");
       //cl.isIstance(ISearch)
       return (IHarvest)cl.newInstance();
    }
    
}