<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.registry.common.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.v1_0.RegistryQueryService,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Access Pages</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<h1>Welcome</h1>
<p>
 This is the VAMDC registry of resources. 
These pages are primarily for providers of VAMDC resources: you can register
your VAMDC services and look at the registrations of other services in the VAMDC
network. End users can get a better view of the available services in the 
<a href="http://portal.vamdc.org">VAMDC portal</a>.
</p>
<p>
The VAMDC registry is a specialized form of IVOA registry, and uses software
evolved from AstroGrid's registry-package.
</p>
<p>
<%
   RegistryQueryService server = new RegistryQueryService();
   
   Document entry = null;
   boolean dbInit = XMLDBManager.isInitialized();
   boolean logInit = Log4jInit.getLoggingInit();
   try {
      entry = DomHelper.newDocument(server.getQueryHelper().loadMainRegistry().getMembersAsResource().getContent().toString());
   }catch(Exception e) {
      //do nothing for now.  
      entry = null;
   }
   
   boolean newRegistry = true;
   String ivoStr = null;
   if(entry != null) {
      ivoStr = RegistryDOMHelper.getIdentifier(entry.getDocumentElement());
      if(ivoStr != null) {
        newRegistry = false;
      }//if
   }//if
   
   if(!dbInit) {
   	out.write("<font color='red'>Could not detect that your database was initalizaed with the eXist default xml database</font><br>");
   }
   if(!logInit) {
   	out.write("<font color='red'>Could not initalize logging, See <a href='../regdocs/configure.jsp'>Configure</a></font><br>");
   }
   
   if (newRegistry) {
      out.write("This Registry has not yet been configured; click <a href='../regdocs/configure.jsp'>here</a> to set it up.");
   }
   else {
      out.write("The main naming-authority for this registry is <b>"+SimpleConfig.getSingleton().getString("reg.amend.authorityid")+"</b>");
      out.write("<br><br>Click <a href='viewResourceEntry.jsp?IVORN=" + ivoStr + "'>here</a> to see the main Registry type for this registry and all authority IDs managed by this registry.");
   }
   
%>
</div>

<%@ include file="/style/footer.xml" %>

</body>
</html>
