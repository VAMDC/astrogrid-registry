<%@page contentType="text/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core"%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Web-application environment</title>
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
<h1>Edit Properties</h1>
<p>
<strong>BackGround Info:</strong> For webapps properties are stored in
the WEB-INF/web.xml but this xml contains a lot more than just 
properties and can be a bit cumbersome to edit by hand.  These
properties are known as JNDI environment entries.  
Typically servlet containers will have a special GUI for changing
these properties and this is what is advised for you to use.
Tomcat GUI by default is no longer installed with tomcat 5.5 and
the GUI is not known to be good anyways.<br>
Tomcat will also allow 'you' to place a special xml properties file
in the {tomcat}/conf/Catalina/localhost named {context}.xml.
</p>
<p>
The environment entries of this web application define its configuration.
You can set the configuration by editing the environment and re-applying
it to the application.
</p>
<ul>
<li><a href="environment-edit.jsp">View and edit the environment in your web browser (Tomcat only).</a></li>
<li><a href="environment-tomcat.jsp">List the environment as a Tomcat context file.</a></li>
<li><a href="environment-properties.jsp">List the environment as a properties file.</a></li>
</ul>
<p>
Editing properties does not normally require a restart of tomcat,
but tomcat 5.5 does not seem to catch the
environment entry changes which requires a restart of the tomcat.
Use the 'Fingerprint' in the menu to check all
the current properties.<br>
</p>
<p>
Many other servlet containers should provide some kind of environment entry
editor themselves and is recommended for changing entries, but you may
try the below link to directly edit the Environment Entries in the web.xml
if your servlet container does not give you an editor.  It will require a
restart of your servlet container (this will not work for all servlet
containers because some containers may move the web.xml to a
different location espcially for containers that do not unpack the war file)
</p>
<dl>
<dt><dd><a href="viewProperties.jsp">View and edit the web.xml</a>
</dl>
<jsp:useBean class="org.astrogrid.common.j2ee.environment.Environment"
    id="environment" scope="application" />
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
