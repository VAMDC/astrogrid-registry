package org.astrogrid.registry.server; 


public class CheckerFactory {
    
    /**
     * Method: createQueryChecker
     * Description: Creates an IChecker instance interface for validating resources or SOAP messages.
     * @param contractVersion a contract version is the contract version of the registry interface "spec" version.
     * Currently default is 0.1, soon to be 0.8 and hopefully finally 1.0 in the near future.
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static IChecker createQueryChecker(String contractVersion) throws IllegalAccessException, 
                                                                            InstantiationException, 
                                                                            ClassNotFoundException {
       Class cl = null;
       try {
           cl = Class.forName("org.astrogrid.registry.server.query.v" + contractVersion.replace('.','_') + ".QueryChecker");
       }catch(ClassNotFoundException ce) {
           cl = Class.forName("org.astrogrid.registry.server.query.QueryChecker");
       }
       //cl.isIstance(ISearch)
       return (IChecker)cl.newInstance();
    }
    
    public static IChecker createHarvestChecker() throws IllegalAccessException, 
        InstantiationException, 
        ClassNotFoundException {
        Class cl = Class.forName("org.astrogrid.registry.server.harvest.HarvestChecker");
        return (IChecker)cl.newInstance();
    }
    
    
    public static IChecker createAdminChecker() throws IllegalAccessException, 
    InstantiationException, 
    ClassNotFoundException {
        Class cl = Class.forName("org.astrogrid.registry.server.admin.AdminChecker");
        return (IChecker)cl.newInstance();
    }
    
    
    
}