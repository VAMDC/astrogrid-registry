package org.astrogrid.registry.server;

import org.astrogrid.config.Config;

public class ConfigExtractor {

    private static String defaultRoot = null;

    /**
     * conf - Config variable to access the configuration for the server normally
     * jndi to a config file.
     * @see org.astrogrid.config.Config
     */      
    protected static final Config conf;
    
    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
           conf = org.astrogrid.config.SimpleConfig.getSingleton();
           defaultRoot = conf.getString("reg.custom.rootNode.default",null);
    }
    
    public static String getRootNodeName(String versionNumber) {
        return conf.getString("reg.custom.rootNode." + versionNumber,defaultRoot);
    }
    
    public ConfigExtractor() {
        
    }
    
    
    
}