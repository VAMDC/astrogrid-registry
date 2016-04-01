package org.astrogrid.registry.server.http.servlets;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


import org.astrogrid.config.Config;
import org.astrogrid.registry.server.harvest.IHarvest;
import org.astrogrid.registry.server.harvest.HarvestFactory;
import org.astrogrid.registry.RegistryException;
//import org.apache.axis.AxisFault;


/**
 * Class: HarvestDaemon
 * Description: Small servlet class to be placed in the web.xml with a load-on-startup value set.  Used for
 * automatic harvesting.  By setting up a timer which will kick off automatic harvest at certain intervals.
 * It can also be used on the web browser to do harvesting, but now there is a seperate jsp page for harvesting,
 * so this servlet should rarely be used for actually coming in on a web browser.
 * 
 * @author Kevin Benson
 *
 */
public class HarvestDaemon extends HttpServlet //implements Runnable
{
   //Thread myThread;
   int myCounter;
   
   /**
    * harvest interval in hours, read from the property.
    */
   int harvestInterval;
   
   Date lastHarvestTime;
   Date servletInitTime;
   
   /**
    * Main harvest service class.
    */
   IHarvest rhs;
   
   boolean harvestOnLoad = false;
   boolean valuesSet = false;
   
   /**
    * a static boolean to check that a harvest is currentlygoing. Don't want to have 2 harvests going at the same time.
    */
   public static boolean harvestStarted;

   
   public static final String INTERVAL_HOURS_PROPERTY =
       "reg.custom.harvest.interval_hours";
   public static Config conf = null;
   
   /**
    * Timer class for schedulding a harvest from a manual request through the servlet.
    */
   private Timer timer = null;
   
   /**
    * Timer class for Scheduling the automatic harvesting.
    */
   private Timer scheduleTimer = null;

   static {
      if(conf == null) {
         conf = org.astrogrid.config.SimpleConfig.getSingleton();
         harvestStarted = false;
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
       System.out.println("exiting HarvestDaemon; cancelling harvest timer");
       if(timer != null) {
           timer.cancel();
       }//if
       if(scheduleTimer != null) {
           scheduleTimer.cancel();
       }
   }
   
   /**
    * Method: init
    * Description: initalization of the servlet, main purpose is to setup the timers for harvesting.
    * @param config
    * @throws ServletException
    */
   public void init(ServletConfig config)
                throws ServletException
   {
	    super.init(config);
      
       System.out.println("initialized HarvestDaemon");
       
       String contractVersion = getInitParameter("registry_contract_version");
       System.out.println("Contract Version = " + contractVersion);
       //get the current date and time.
       servletInitTime = new Date();
       //instantiate teh harvest service.
       try {
           rhs = HarvestFactory.createHarvestService(contractVersion);
       }catch(Exception e) { 
           System.out.println("something happen trying to get harveste service for contractversion = " + contractVersion);
           e.printStackTrace();
       }
       //hmmm lets make sure there is no timer going at the moment.
       if(scheduleTimer != null) {
       		System.out.println("scheduleTimer not null in init so cancel");
           //hmmm just in case the servlet container decides to re-initialize
           //this servlet.
           scheduleTimer.cancel();
           scheduleTimer = null;
       }
       //instantiate teh timer class, and see if they are enabling harvesting.
       scheduleTimer = new Timer();
       boolean harvestEnabled = conf.getBoolean("reg.amend.harvest",false);
       if(harvestEnabled) {
           //check if we have already setup all the interval time values.
          if(!valuesSet) {
              System.out.println("harvest is enabled");
              valuesSet = true;
              //okay get the harvest interval time.
              harvestInterval = conf.getInt(INTERVAL_HOURS_PROPERTY);
              //lets not start a harvest off the bat, wait till next cycle.
              //origianlly this was in the properties file, but it is now just waits
              //till the next cycle.
              harvestOnLoad = false;
              //hmmm invalid property set on harvst interval so default it to 2.
              if(harvestInterval <= 0) {
                  System.out.println("ERROR CANNOT HAVE A HARVESTINTERVAL LESS THAN 1; DEFAULTING TO 2");
                  harvestInterval = 2;
              }
          }
          System.out.println("in init of harvestDaemon and starting thread.");
          
          //schedule the timer class.
          System.out.println("scheduling for contractVersion = " + contractVersion);
          scheduleTimer.scheduleAtFixedRate(new HarvestTimer("HarvestNow"),
                       (long)harvestInterval*3600*1000,
                       (long)harvestInterval*3600*1000);          
          //Thread myThread = new Thread(this);
          //myThread.start();
       }else {
           System.out.println("harvest not enabled.");
           //if harvest is not enabled then just leave.
           return;
       }//else
      
   }
   

   /**
    * Method: doGet
    * Description: Rarely used anymore because of the jsp pages with the Registry, but this servlet can be used for
    * doing harvests or replications.
    * @param req
    * @param res
    * @throws IOException
    * @throws ServletException
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res)
                throws IOException, ServletException
   {
	  String nowParam = req.getParameter("HarvestNow");
     if(timer != null) {
         //hmmm just in case the servlet container decides to re-initialize
         //this servlet.
         timer.cancel();
         timer = null;
     }
     timer = new Timer();
     
	  if( nowParam != null && !harvestStarted ) {
        //start the harvest in 5 seconds
        timer.schedule(new HarvestTimer("HarvestNow"),5000);
        harvestStarted = true;
      }
     String replicate = req.getParameter("ReplicateNow");
      if( replicate != null && !harvestStarted ) {
         //start a replication in 5 seconds
         timer.schedule(new HarvestTimer("ReplicateNow"),5000);
         harvestStarted = true;         
       }
      
      
      ServletOutputStream out = res.getOutputStream();
      res.setContentType("text/html");
      out.println("<html><head><title>Astrogrid Registry Harvest</title></head>");
      out.println("<body><a href=\"http://www.astrogrid.org\">");
      out.println("<img src=\"http://www.astrogrid.org/image/AGlogo\"" +
                  "align=right alt=\"AG logo\"> </a>");
      out.println("<h1>Astrogrid Registry Harvest Control Servlet</h1>" +
                  "<br><h2>Servlet initialized " + servletInitTime +
                  "<br>Harvest interval = " + harvestInterval + " hours" +
                  "<br>Number of Harvests initiated = " + myCounter +
                  "<br>Last harvest time = " + lastHarvestTime +
                  "<form method=\"get\"><input type=\"submit\" " +
                  " name=\"HarvestNow\" value=\"Harvest now!\">" +
                  "<br /><strong>Replication can be dangerous replicates all resources not by date</strong><br />" +
                  "<input type=\"submit\" name=\"ReplicateNow\" value=\"Replicate Now\" /></form>");
                  if(harvestStarted) {
                      out.println("<br /><i>A Harvest/Replicate has began or still running.</i>");
                  }
                  out.println("</h2></body></html>");
   }

   /**
    * Class: HarvestTimer
    * Description: Actual TimerTask class to start the harvesting.
    * @author Kevin Benson
    *
    * TODO To change the template for this generated type comment go to
    * Window - Preferences - Java - Code Style - Code Templates
    */
   class HarvestTimer extends TimerTask {
       private String task = null;
       
       public HarvestTimer(String harvestTask) {
           task = harvestTask;
       }
       
       /**
        * Method: run
        * Description: Runs the HarvestNow or ReplicateNow, for automatic harvesting which is what this is
        * normally used for it will always be HarvestNow.
        */
       public void run() {

           try {
               if("HarvestNow".equals(task)) {
                   System.out.println("Immediate harvesting will be commenced!");
                   try {
                       rhs.harvestAll(true,true);
                       //rhs.harvestAll();
                   }catch(RegistryException e) {
                        e.printStackTrace();
                   }
               }
               if("ReplicateNow".equals(task)) {
                   System.out.println("Immediate replicate is beginning!");
                   try {
                      rhs.harvestAll(true,false);
                       //rhs.harvestAll();
                   }catch(RegistryException e) {
                      e.printStackTrace();
                   }
               }//if
           } finally {
               //turn off
               HarvestDaemon.harvestStarted = false;
           }
       }//run       
   }

   
   
}
