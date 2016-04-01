<%@ page import="org.astrogrid.registry.server.admin.*,
 				     org.astrogrid.registry.server.query.*,
 				     org.astrogrid.registry.server.*,
                 org.astrogrid.registry.client.query.*,
                 org.astrogrid.registry.client.*,
                 org.astrogrid.registry.server.*, 				     
 				     org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.store.Ivorn,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 java.net.*,
                 java.util.*,
                  org.apache.axis.utils.XMLUtils,                 
                 java.io.*"
    session="false" %>

<html>
<head>
<title>Adding External Registries</title>
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<p>To mirror ("harvest") the contents of other registries, you must
collect their registryType entries and add those entries to your own
registry. The form below causes your registry to copy these entries
from a specific, external registry. Note that harvesting another
registry adds its contents to the contents of your registry; nothing in
your registry is lost or overwritten.<br>

</p>

<form method="post">
<p>
Please enter the URL for a registry to be harvested.
A default registry in AstroGrid is pre-set.
</p>
<p>
<%
  String getregs = request.getParameter("getregs");
  if(getregs == null || getregs.trim().length() == 0) {
    getregs = "http://galahad.star.le.ac.uk:8080/galahad-registry";
  }
%>
<input name="doget" value="1" type="hidden" />
<input name="getregs" value="<%=getregs%>" size="64" type="text">
<input name="getregsubmit" value="Set up harvesting" type="submit">
</p>
</form>

<%
  String postregsubmit = request.getParameter("postregsubmit");
  String getregsubmit= request.getParameter("getregsubmit");
  if(getregs != null && getregs.trim().length() > 0 && 
     request.getParameter("doget") != null) {
  	     System.out.println("the url for the delegatefactory = " + getregs + "/services/RegistryQuery");
        RegistryService rs = RegistryDelegateFactory.createQuery(new URL(getregs + "/services/RegistryQuery"));
        rs.getRegistries();
%>

<h1>Grabbing and Adding Registry Entries From <%= getregs %></h1>

<p>Service returns:

<pre>
<%
IAdmin serverAdmin = JSPHelper.getAdminService(request);
out.write("<p>getregs: " + getregs + "</p><br />");
out.write("<p>Attempt at grabbing registries from above url and updating the registry, any errors in the updating of this registry will be below.<br /></p>");
Document doc = rs.getRegistries();
Document resultDoc = serverAdmin.updateInternal(doc);


out.write("<p><br /><br />Here were the entries attempted to be updated into the registry (Remember only the Resource elements are placed into the registry):<br /></p>");
if(doc != null) {
      String testxml = DomHelper.DocumentToString(doc);
      testxml = testxml.replaceAll("<","&lt;");
      testxml = testxml.replaceAll(">","&gt;");
      out.write(testxml);
}
   
%>
</pre>

<%}%>
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
