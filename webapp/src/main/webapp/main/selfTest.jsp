<%@ page import="org.astrogrid.config.SimpleConfig,
 	  		     org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,   
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,                 
             	  org.astrogrid.registry.server.query.*"
   isThreadSafe="false"
   session="false"
%>

<html>
<head>
<title>Self Test of the Registry</title>
<style type="text/css" media="all">
	<%@ include file="/style/astrogrid.css" %>          
</style>
</title>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<h1>Welcome</h1>
<p>
Welcome to an AstroGrid Registry.  These are the direct access pages for
Registering your resource with the IVO, and/or investigating what resources
are available.
</p>
<p>
<%
   ISearch server = JSPHelper.getQueryService(request);
   
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
   out.write("<ul>");
//   SimpleConfig.getSingleton().getString("reg.amend.authorityid",null)
   if(!dbInit) {
   	out.write("<li><font color='red'>Error: Could not detect that your database was initalizaed with the eXist default xml database</font></li>");
   }else {
    out.write("<li>Success: Db Connection Has been Initialized.</li>");
   }
   
   if(!logInit) {
   	out.write("<li><font color='red'>Error: Could not initalize logging, See <a href='regdocs/configure.html'>Configure</a></font></li>");
   }else {
    out.write("<li>Success: Logging Initialized.</li>");
   }
   
   if("org.changethis".equals(SimpleConfig.getSingleton().getString("reg.amend.authorityid",null))) {
   	out.write("<li><font color='red'>Error: Authority ID in the properties is 'org.changethis' might be because your registry has not been setup yet. Public registries  should change the authority id  <a href='" + <%=request.getContextPath()%> + "/admin/environment-main.jsp'>here</a></font></li>");
   }
   
   if(entry != null && ivoStr != null) {
   	out.write("<li>Success: Self Registraty Entry Found.</li>");
   }else {
	out.write("<li><font color='red'>Error: Could not find Self Registration entry go <a href='" + <%=request.getContextPath()%> + "/admin/selfRegisterForm.jsp'>here</a></font></li>");
   }
   
   if(ivoStr != null && ivoStr.startsWith("ivo://org.changethis")) {
	   out.write("<li><font color='blue'>Warning: Identifier string has the default org.changethis authorityid, assuming this registry is for testing not for public use. Public use should change the authority id <a href='" + <%=request.getContextPath()%> + "/admin/environment-main.jsp'>here</a> and self register again.</font></li>");
   }
   
  
      if("xmldb:exist://".equals(SimpleConfig.getSingleton().getString("xmldb.uri")) &&
         (SimpleConfig.getSingleton().getString("reg.custom.exist.configuration",null) == null ||
          SimpleConfig.getSingleton().getString("reg.custom.exist.configuration").trim().length() == 0)) {
          out.write("<li><font color='blue'>Warning: Your registry is in embed/internal mode with the data storage inside your webapp. It is advisable" +
          " to have the data storage outside your webapp, see <a href='"  + <%=request.getContextPath()%> + "/docs/eXist_reference.html'>here</a></font></li>");
      }
      
   String oaiURL = SimpleConfig.getSingleton().getString("reg.amend.oaipublish.1.0");
   if(oaiURL.indexOf(request.getContextPath()) != -1) {
   		out.write("<li><font color='red'>Error: The property for OAI Web service calls are incorrect, this is not used often but should be corrected.  Please make sure the property 'reg.amend.oaipublish.1.0' has " + request.getContextPath() + " as the Context Path (localhost is ok just the context path needs to be changed.).</font></li>");
   }
   /*
   if(request.getRemoteAddr().equals("127.0.0.1")) {
     String xmldbURI = SimpleConfig.getSingleton().getString("xmldb.uri");
     String adminPassword = SimpleConfig.getSingleton().getString("xmldb.admin.password",null);
     String ipUpdates = SimpleConfig.getSingleton().getString("reg.custom.restrict.ipaddresses.updates");
     String ipDB = SimpleConfig.getSingleton().getString("reg.custom.restrict.ipaddresses");
     if(ipDB.indexOf("127.0.0.1") != -1 && xmldbURI.equals("xmldb:exist://") &&
        (adminPassword == null || adminPassword.trim().length == 0)) {
	     out.write("Warning: Your Remote Address is 127.0.0.1 (hence localhost) if this is a Public Registry and is on a Proxy which is causing all
	     Remote Addresses to be localhost and your running db internally then everybody has access to your database contents via WebDav or eXist client.
	     To Fix change property 'reg.custom.restrict.ipaddresses' to be other ip addresses or range of ip addresses.  Normally never need to go into the 
	     database you may make the property empty so no
	 }
   }
   */
   
   
%>
	<br />
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
