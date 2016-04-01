<%@page contentType="text/html"%>
<%@page pageEncoding="iso-8859-1"%>
<%@page import=" org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 java.net.*,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <title>Coverage form</title>
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="../style/link_options.xml" %>
    </head>
    <body>
      
    <%@ include file="/style/header.xml" %>
    <%@ include file="/style/navigation.xml" %>
    
      <div id='bodyColumn'>
        
        <h1>Coverage of the resource</h1>
 
        <form action="Coverage" method="post">
	   <input type="hidden" name="IVORN" value="<%=request.getParameter("IVORN")%>">
           <h2>Frequency coverage</h2>
           <p>
             Please select all wavebands that apply to this resource.
           </p>
           <dl>
             <dt><dd>
             <input type="checkbox" value="Radio" name="Radio">Radio
             <dt><dd>
             <input type="checkbox" value="Millimeter" name="mm">Millimetre
             <dt><dd>
             <input type="checkbox" value="Infrared" name="IR">IR
             <dt><dd>
             <input type="checkbox" value="Optical" name="Optical">Optical
             <dt><dd>
             <input type="checkbox" value="UV" name="UV">UV
             <dt><dd>
             <input type="checkbox" value="EUV" name="EUV">EUV
             <dt><dd>
             <input type="checkbox" value="X-ray" name="X-ray">X-ray
             <dt><dd>
             <input type="checkbox" value="Gamma-ray" name="Gamma-ray">Gamma-ray
           </dl>
           <h2>Spatial coverage</h2>
           <p>
             Please choose the region that most closely describes the
             distribution of your data (see notes below).
           </p>
           <dl>
             <dt><dd>
               <input type="radio" name="region" value="Allsky" checked>All-sky
             <dt><dd>
               <input type="radio" name="region" value="Box">Box,
               centre = (<input type="text" name="box.C1" size="5">,
               <input type="text" name="box.C2" size="5">) degrees,
               size = <input type="text" name="box.S1" size="5"> &#215;
               <input type="text" name="box.S2" size="5"> degrees,
               coordinate system
               <input type="radio" name="box.coordsys" value="IRCS" checked>Equatorial
               <input type="radio" name="box.coordsys" value="GALACTIC_II">Galactic
               <input type="radio" name="box.coordsys" value="SUPERGALACTIC">Super-galactic
             <dt><dd>
               <input type="radio" name="region" value="Circle">Circle,
               centre = (<input type="text" name="circle.C1" size="5">,
               <input type="text" name="circle.C2" size="5">) degrees,
               radius = <input type="text" name="circle.Radius" size="5"> degrees,
               coordinate system
               <input type="radio" name="circle.coordsys" value="IRCS" checked>Equatorial
               <input type="radio" name="circle.coordsys" value="GALACTIC_II" >Galactic
               <input type="radio" name="circle.coordsys" value="SUPERGALACTIC">Super-galactic
           </dl>
          <p><input type="submit" value="Update the registry entry"></p>
        </form>
        <hr>
        <p>
          Spatial coverage is approximate. The main use for this information is
          to allow readers of the registry to exclude resources that
          <em>do not</em> cover a region of interest, assuming that all
          other resource <em>might</em> cover that region. Therefore,
          if you fill this in, choose a
          region on the sky that includes all the data of your resource rather
          than one that precisely describes where most of your data lie. If you
          don't want to specify anything "all sky" is the best answer.
        </p>
        <p>
          When specifying the equatorial position of a region, the coordinates
          should be given in the IRCS frame. Galactic positions should be in
          the seccond system of galactic coordinates. In practice, the scale and
          the uncertainties are so great that the differences between systems
          don't matter very much.
        </p>
        <p>
          If you need to specify more than one region then you could edit the
          XML of the registry entry directly.
        </p>
      </div>
      <%@ include file="../style/footer.xml" %>
    </body>
</html>
