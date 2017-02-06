package org.astrogrid.registry.server.query; 

import org.astrogrid.registry.server.query.v1_0.RegistryQueryService;

/**
 * Class: QueryFactory
 * Description: Factory class normally instantiated by XFire or JSP helper class to 
 * get a Query Servive to perform queries on the database.
 * @author kevinbenson
 *
 */
public class QueryFactory {

	/**
	 * Method: createQueryService
	 * Description: Instantiates the Query Serviced based on a contract version.  Should
	 * always be one RegistryQueryService per contract and returns that QueryService based on the 
	 * interface ISearch
	 * @param contractVersion contract version.
	 * @return interface ISearch for performing Queries.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
  public static ISearch createQueryService(String contractVersion) throws IllegalAccessException, 
                                                                            InstantiationException, 
                                                                            ClassNotFoundException {
    /* 
    Class cl = Class.forName("org.astrogrid.registry.server.query.v" + contractVersion.replace('.','_') + ".RegistryQueryService");
    return (ISearch)cl.newInstance();
    */
      
    // Only contract version 1.0 is supported now.
    if (!contractVersion.equals("1.0")) {
      throw new IllegalArgumentException("Contract version " + contractVersion + " is no longer supported");
    }
    return new RegistryQueryService();
  }
  
  public static ISearch createQueryService() {
    return new RegistryQueryService();
  }
    
}