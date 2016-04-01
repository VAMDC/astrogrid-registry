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
<title>Properties</title>
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

<h1>Properties</h1>
<!--
A web form in which properties of a web-application may be edited.
The properties are read from the deployment descriptor web.xml.
On submitting the form, the new values of the properties are
sent to a separate web-resource which is presumed to update
the depolyment descriptor.
-->
<p>
Properties defined as environment entries in the
deployment descriptor (<i>web.xml</i>):
</p>
<!-- 
A web form that displays the names, types and current values of the
properties and allows the end-user to change them. Submitting the
form encodes the value of each properties, whether changed or not,
as a form parameter named the same as the property and sends the
request to a separate web-resource that is expected to record the values.
-->
<form method="post" action="updateProperties.jsp">
<!-- 
Build an HTML table of property values by transforming web.xml.
See the Javadoc for the bean class for details.
-->
<jsp:useBean id="tabulator" class="org.astrogrid.common.j2ee.EnvEntriesTabulator"/>
<% tabulator.setContext(application); %>
<jsp:getProperty name="tabulator" property="table"/>
<p>
<input type="submit" value="Update properties">
</p>
</form>
<p>
Properties are name-value pairs that you may set
to customize this web-application.
Use the form above to change the property values
(you cannot remove or add properties).
</p>
<p>
Pushing the "update properties" button writes
the property values back to the 
deployment-descriptor file. This means that those
values will be used when you next restart the
web application.
</p>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
