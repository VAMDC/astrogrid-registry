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
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Setup and Administration of Registry</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<h1>Setup &amp; Admin</h1>
Setup and Administration of Registry:

<h2>Pre-Requisits</h2>
We assume that you have already carried out the following tasks:
<ul>
<li>Installed the Registry WAR - that is, that you are viewing
these pages on your browser on the site you have installed the
WAR file on.
<li>Installed the eXist database.
</ul>

<h2>Indexes</h2>
Located here is something new whereby you can
manage the indexes in the database.  
This page will allow you to add Indexes to your database,
by default only indexes on text is done.  Click on this
link to add more indexes and a default set of indexes
are shown that is commonly added to the full registries.
<dl>
<dt><dd><a href="addIndexCheck.jsp">Click here</a> to add or update indexes.
</dl>

<h2>Initial Configuration</h2>
You must 'self register' the Registry with itself in
order for it to work. You must also set the configuration
property <code>reg.amend.authorityid</code> to
the authority that this Registry will manage. The second
link for the custom sends you to the Add/Edit
entry page where you may upload a custom full Registry
type XML Resource; or you may elect to choose
an automated creator which is the first link.
<ul>
<li><a href="selfRegisterForm.jsp">Self Registration</a></li>
<li><a href="editEntry.jsp">Self Registration Custom</a></li>
</ul>

<h2>Getting your Registry known</h2>
To get your registry known to the world you must register
it with the 'Registry of Registries' (RofR) by placing in
a url to what is known as a OAI interface. Go to
<dl>
<dt><dd><a href="ivoa_registry.jsp">RofR Validation and Registration</a>
</dl>
All registries have an OAI interface url and
for this registry it should be (Note if your on a proxy system
the below url might not be correct please correct it to
the correct url/domain)
<dl>
<dt><dd><%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %>/OAIHandlerv1_0
</dl>

<h2>Harvesting other Registries</h2>
To make your registry harvest other registries
- you must get the other known Registry type Resources.
This is typically done by getting all the Registry Type
Resources from Registry of Registries (RofR).
Click on the link below to do this.
<dl>
<dt><dd><a href="getRegistryFromHarvest.jsp">Harvesting other Registries</a>
</dl>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
