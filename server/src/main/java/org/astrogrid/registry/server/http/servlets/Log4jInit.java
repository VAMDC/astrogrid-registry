/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-03 Wolfgang M. Meier
 *  wolfgang@exist-db.org
 *  http://exist.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  $Id: Log4jInit.java,v 1.3 2007-02-21 16:35:39 KevinBenson Exp $
 */

package org.astrogrid.registry.server.http.servlets;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.LogManager;

import org.astrogrid.config.Config;
import java.net.URL;

/**
 * Helper servlet for initializing the log4j framework in a webcontainer.
 */
public class Log4jInit extends HttpServlet {
    
    //Constant variable to deterimen if this is an internal eXist db being ran. 
    private static final String DEFAULT_EXIST_DRIVER_URI = "xmldb:exist://";
    
    private static boolean loggingInit = false;
    
    
    /**
     * conf - Config variable to access the configuration for the server normally
     * jndi to a config file.
     * @see org.astrogrid.config.Config
     */      
    protected static Config conf = null;
    
    //xmldb uri to the location of the database for using the XMLDB:API
    private static String xmldbURI = null;   
    

    /**
     * Static to be used on the initiatian of this class for the config
     */   
    static {
       if(conf == null) {  
           conf = org.astrogrid.config.SimpleConfig.getSingleton();
           xmldbURI = conf.getString("xmldb.uri",DEFAULT_EXIST_DRIVER_URI);
       }
    }
    
    /**
     * Method: destroy
     * Description: destroy method for the servlet conatiner to call when killing the servlet. Cleans up any
     * timer information making sure they are cancelled.  Otherwise the timer class may not let the servlet container
     * shutdown till ti runs.
     *
     */
    public void destroy() {
        super.destroy();
        System.out.println("cleaning up log4j");
        LogManager.shutdown();
        System.out.println("done with shutdown of log4j");
    }
    
    
    
    /**
     * Initialize servlet for log4j purposes in servlet container (war file).
     */
    public void init() throws ServletException {

        String file = getInitParameter("log4j-init-file-registry");
        
        // We need to check how eXist is running. If eXist is started in a
        // servlet container like Tomcat, then initialization *is* needed.
        //
        // If eXist is started in its own jetty server, the logging is 
        // already initialized.
        if(runningInternalEXist()) {
            file = getInitParameter("log4j-init-file");
        }
        
        ClassLoader loader = this.getClass().getClassLoader();
        URL logURLResource = loader.getResource("logging_config/" +  file);
        
        // Get path where eXist is running
        String logsDirectory = conf.getString("reg.custom.loggingdirectory",getInitParameter("logdirectory"));
        if(logsDirectory == null) {
            logsDirectory = "/";
        }
        loggingInit = false;
        if(logsDirectory.equals("/")) {
            //bummer no logging directory set so try catalina.home if it is not there
            //then default to the realcontext path see if we can create the directories there.
            logsDirectory = conf.getString("catalina.home","/");
            if(logsDirectory.equals("/")) {
                logsDirectory = getServletContext().getRealPath("/");
                if(logsDirectory == null) {
                    //darn its packaged in a webapp can't get to it.
                    logsDirectory = conf.getString("user.dir",null);
                }
            }else {
                logsDirectory += "/logs";
            }
        }
        if(logsDirectory != null) {
            // Define location of logfiles
            File logsdir = new File(logsDirectory, "registry_logs" );
            if(!logsdir.exists()) {
                logsdir.mkdirs();
            }
            System.setProperty("logger.dir.registry", logsdir.getAbsolutePath() );      
                        
            // Configure log4j
            DOMConfigurator.configure(logURLResource);
            //things are good lets set it be initialized.
            loggingInit = true;
        }
    }
    
    public static boolean getLoggingInit() {
        return loggingInit;
    }
    
    /**
     * Method: runningInternalEXist
     * Purpose: check if were running the eXist XML database internally.
     * @return boolean if the database is eXist and running internally.
     */
    private boolean runningInternalEXist() {
        if(DEFAULT_EXIST_DRIVER_URI.equals(xmldbURI)) {
            return true;
        }
        return false;
    }
    
    /**
     *  Empty method.
     *
     * @param req HTTP Request object
     * @param res HTTP Response object
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        //
    }
    
}
