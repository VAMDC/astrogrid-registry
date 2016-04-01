package org.astrogrid.registry.server.query; 

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
       Class cl = Class.forName("org.astrogrid.registry.server.query.v" + contractVersion.replace('.','_') + ".RegistryQueryService");
       //cl.isIstance(ISearch)
       return (ISearch)cl.newInstance();
    }
    
}