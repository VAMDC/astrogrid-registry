package org.astrogrid.registry.server.admin; 

/**
 * Class: AdminFactory
 * Description: creates an RegistryAdminService based on a contract version.  All
 * contracts should have a RegistryAdminService to perform admin tasks to the db.
 * @author kevinbenson
 *
 */
public class AdminFactory {
    
	/**
	 * Method: createAdminService
	 * Description: creates a RegistryAdminService and returns the IAdmin interface that all
	 * AdminServices must implement.
	 * @param contractVersion contract version number to distinguish which Admin Service to instantiate.
	 * @return interface IAdmin
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
    public static IAdmin createAdminService(String contractVersion) throws IllegalAccessException, 
                                                                            InstantiationException, 
                                                                            ClassNotFoundException {
       Class cl = Class.forName("org.astrogrid.registry.server.admin.v" + contractVersion.replace('.','_') + ".RegistryAdminService");
       return (IAdmin)cl.newInstance();
    }
    
}