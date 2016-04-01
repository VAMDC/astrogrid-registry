package org.astrogrid.registry.server.http.servlets.helper;

import javax.servlet.http.HttpServletRequest;
import org.astrogrid.registry.server.query.ISearch;
import org.astrogrid.registry.server.admin.IAdmin;
import org.astrogrid.registry.server.harvest.IHarvest;
import org.astrogrid.registry.server.query.QueryConfigExtractor;
import org.astrogrid.registry.server.query.QueryFactory;
import org.astrogrid.registry.server.harvest.HarvestFactory;
import org.astrogrid.registry.server.admin.AdminFactory;


public class JSPHelper {
    
    public static ISearch getQueryService(HttpServletRequest hsr)  throws IllegalAccessException, 
    InstantiationException, 
    ClassNotFoundException {
        String contractVersion = getContractVersion(hsr);        
        return QueryFactory.createQueryService(contractVersion);
    }
    
    public static IAdmin getAdminService(HttpServletRequest hsr)  throws IllegalAccessException, 
    InstantiationException, 
    ClassNotFoundException {
        String contractVersion = getContractVersion(hsr);
        return AdminFactory.createAdminService(contractVersion);
    }    
    
    public static IHarvest getHarvestService(HttpServletRequest hsr)  throws IllegalAccessException, 
    InstantiationException, 
    ClassNotFoundException {
        String contractVersion = getContractVersion(hsr);        
        return HarvestFactory.createHarvestService(contractVersion);
    }
    
    
    public static String setContractVersion(HttpServletRequest hsr, String contractVersion) {
        hsr.getSession().setAttribute("reg.defaultContractVersion",contractVersion);        
        return contractVersion;
    }
    
    public static String getContractVersion(HttpServletRequest hsr) {
    	
    	if(hsr.getParameter("contract_version") != null) {
    		return setContractVersion(hsr,hsr.getParameter("contract_version"));
    	}
    	
        return hsr.getSession().getAttribute("reg.defaultContractVersion") == null ? 
                setContractVersion(hsr,QueryConfigExtractor.getDefaultContractVersion()) :
                (String)hsr.getSession().getAttribute("reg.defaultContractVersion");
    }
    
}