<%@ page import="org.astrogrid.config.SimpleConfig,
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
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
	   contentType="text/html; charset=UTF-8"
		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>AstroGrid Registry Setup Pages</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=UTF-8">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<h1>Self-registration</h1>

<p>
When a new registry is created, the first registration made in it must describe itself. This form
helps do that. Optionally you may go back and submit your own registry type resource entry by clicking on "Enter Resource" on the menu.
But this is a quicker way to fill in the majority of known values that change and to get all the xml elements defined.
</p>
<p>
<%
String authID = SimpleConfig.getSingleton().getString("reg.amend.authorityid", "");
%>

<form action="selfRegisterCheck.jsp" method="post">
<p>
<input type="hidden" name="version" value="<%=org.astrogrid.registry.server.http.servlets.helper.JSPHelper.getQueryService(request).getResourceVersion()%>">
<input type="hidden" name="AuthorityID" value="<%=authID%>">
<table>
 <tr><td>Authority ID </td><td> <%=authID%></td></tr>
 <tr><td>Title        </td><td> <input type="text" name="Title"></td></tr>
 <tr><td>Publisher    </td><td> <input type="text" name="Publisher"></td></tr>
 <tr><td>Contact Name </td><td> <input type="text" name="ContactName"></td></tr>
 <tr><td>Contact email</td><td> <input type="text" name="ContactEmail"></td></tr>
 <tr><td>Full Registry</td><td><select name="fullregistry"><option value="false">No</option><option value="true">Yes</option></select></td></tr>
  <tr><td>Description</td><td> <input type="text" name="ContentDescription"></td></tr>
  <tr><td>Reference URL</td><td> <input type="text" name="ContentRefURL"></td></tr>
  <tr><td>Facility Name</td><td> <input type="text" name="Facility"></td></tr>
</table>
<p>
<input name="button" value="Submit" type="submit">
</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>

</body></html>
