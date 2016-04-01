package org.astrogrid.registry.server.query;

import org.astrogrid.registry.server.ConfigExtractor;


/**
 * Class: QueryConfigExtractor
 * Description: QueryConfigExtractor is class that reads properties of various queries that are used internally
 * here in the registry.  The Registry has to use some queries internally for harvesting and other various methods.
 * Because we allow for the customization of working with other xml databases, these queries are in a properties file
 * in case they desire to change the queries to work with there xml databases better.
 * @author Kevin Benson
 *
 */
public class QueryConfigExtractor extends ConfigExtractor {     
    
    /**
     * final variable for the default AuthorityID associated to this registry.
     */
    private static final String AUTHORITYID_PROPERTY =
                                           "reg.amend.authorityid";    
        
    
    public static String getDefaultContractVersion() {
        return conf.getString("reg.custom.query.defaultContractVersion","1.0");
    }
    
    /**
     * Method: getXQLDeclarations
     * Description: get the declare namespaces for a particular version.
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return the namespaces.
     */
    public static String getXQLDeclarations(String versionNumber) {
        return conf.getString("reg.custom.declareNS." + versionNumber,"");
    }    
        

    /**
     * Method: queryForRegistries
     * Description: Query for Registry types in the registry.
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String queryForRegistries(String versionNumber) {
    	
        return conf.getString("reg.custom.query.registrytypes." + versionNumber).replaceAll("<rootnode>",
               getRootNodeName(versionNumber));
    }
    
    /**
     * Method: queryForVOSI
     * Description: Query for Resources that have VOSI capability
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String queryForVOSI(String versionNumber) {
    	
        return conf.getString("reg.custom.query.vosicap." + versionNumber).replaceAll("<rootnode>",
               getRootNodeName(versionNumber));
    }    

    /**
     * Method: queryForResource
     * Description: Query for a particular in the resource based on the identifier.
     * @param identifier - A identifier string.
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String queryForResource(String identifier, String versionNumber) {
        String mainQuery = null;
        
        mainQuery = conf.getString("reg.custom.query.singleidentifer." + versionNumber).replaceAll("<id>",identifier);
        return mainQuery.replaceAll("<rootnode>",
        getRootNodeName(versionNumber));
    }
    
    /**
     * Method: queryForAllResource
     * Description: Query for Resources based on any part of the identifier string.
     * @param identifier - A identifier String.
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String queryForAllResource(String identifier, String versionNumber) {
        String mainQuery = null;  
        mainQuery = conf.getString("reg.custom.query.multipleidentifier." + versionNumber).replaceAll("<id>",identifier);
        return mainQuery.replaceAll("<rootnode>",
        getRootNodeName(versionNumber));
    }

    /**
     * Method: queryForMainRegistry
     * Description: Query for the main registry type.
     * 
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String queryForMainRegistry(String versionNumber) {
        String mainQuery = null;
        String authorityID = conf.getString(AUTHORITYID_PROPERTY);
        mainQuery = conf.getString("reg.custom.query.registrytypewithident." + versionNumber).replaceAll("<id>",authorityID);
        return mainQuery.replaceAll("<rootnode>",
               getRootNodeName(versionNumber));
    }
    

    /**
     * Method: getStartQuery
     * Description: This is the start part of most queries.  Used mainly for building queries up. 
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String getStartQuery(String versionNumber) {
        return conf.getString("reg.custom.query.start").replaceAll("<rootnode>",
               getRootNodeName(versionNumber));
    }
    
    /**
     * Method: getAllQuery
     * Description: Query for all Resources.
     * @param versionNumber - versionNumber of the registry (from vr namespace).
     * @return String of the query.
     */
    public static String getAllQuery(String versionNumber) {    	
        return conf.getString("reg.custom.query.everything").replaceAll("<rootnode>",
        getRootNodeName(versionNumber));        
    }
    
}