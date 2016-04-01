package org.astrogrid.registry.server.http.filters;

import java.io.*;
import javax.servlet.*;
import org.astrogrid.config.Config;

/**
 * Class: AdminCheckFilter
 * Description: Small filter to let admin restrict to certain ipaddresses for particular security restricted locations.
 * 
 * This is typically used for access to the database via a servlet example WebDav or webstart client GUI.  
 * 
 * @author Kevin Benson
 *
 */
public final class RestrictFilter implements Filter {


    private FilterConfig filterConfig = null;
    
    public static Config conf = null;    
    
    static {
        if(conf == null) {
           conf = org.astrogrid.config.SimpleConfig.getSingleton();
        }
    }    
    

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        
      String ipFilter = conf.getString("reg.custom.restrict.ipaddresses",null);
      String []ipAddresses = null;
      String remoteAddr = request.getRemoteAddr();
      //String remoteHost = request.getRemoteHost();
      
      if(ipFilter != null) {
          ipAddresses = ipFilter.split(",");
          //System.out.println("the remoteAddr = " + remoteAddr + " and remotehost = " + remoteHost);
          for(int j = 0;j < ipAddresses.length;j++) {
        	  //System.out.println("the ip address j = " + j + " addr = " + ipAddresses[j]);
              if(ipAddresses[j].trim().equals(remoteAddr) || 
                 remoteAddr.matches(ipAddresses[j].trim()) ) {
                  chain.doFilter(request, response);
                  return;              
              }
          }//for
      }

      PrintWriter out = response.getWriter();
      out.print("<html><head><title>Access Error</title></head><body><font color='red'>You tried to access a resource that has restrictions where by only certain ip addresses may acquire the resource. Ask Admin to set your ip address for access.  Your ip address is - " + remoteAddr + "</font></body></html>");
      out.close();
    }


    public void destroy() {
    }


    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }    
}