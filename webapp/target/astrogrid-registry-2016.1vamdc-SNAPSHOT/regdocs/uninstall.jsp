<%@ page import="org.astrogrid.config.SimpleConfig"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Registry Pages</title>
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

<h1>Uninstall</h1>
If you are uninstalling the registry to not be used again and
Resources have potentially been harvested to
other external Registries in the IVOA community then advise to either:
<ol type='a'>
<li>Let another Registry handle/manage your authority ID to maintain
those resources.
<li>Or set the status of your resources to 'deleted' and wait a few
days for those Resources to be harvested.
</ol>
Now to uninstall:
<ul>
<li>First go into your Tomcat Manager</li>
<li>Find your registry context and click on "undeploy".
This should delete the war file and the directory.<br>
<i>*If it doesn't then shutdown tomcat and delete it
war file and directory, then start tomcat back up.</i></li>
<li>Your registry is uninstalled, but your eXist XML database
area may still be around if it is outside your webapp.
If the XML database is not going to be used anymore then
simply remove the main directory and all its files.</li>
</ul>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
