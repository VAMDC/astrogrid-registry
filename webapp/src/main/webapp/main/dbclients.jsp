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
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Connect to Database</title>
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

<h1>Client &amp; WebDav</h1>
If you are using the recommended XML database eXist
(which also is bundled up with the registry),
then you may like to know you can get right into the database.
The XML database eXist provides you with a WebStartable
client allowing you to login in via a GUI and perform
numerous amounts of tasks such as changing usernames,
backups, reindex, xquery, add/remove everything 
possible to manipulate the database.
Also it supports WebDav which allows you to connect
various Applications like XMLSpy or Oxygen, as well as
your typical File Manager normally support WebDav meaning
you can view the whole database just like any other
network drive on your system.
Below is the information to get you connected as well as some hints.
<h1>Requirements</h1>
<ul>
<li>
If you are running in the default mode of eXist database
embedded internally with your webapp 
because you would not wish to make your database public,
you must make a change to a JNDI environment entry (property).  
Edit JNDI property called "reg.custom.restrict.IPaddresses" allowing
you to restrict who has access to the WebDav and client Gui.  
Just make a comma seperated list of IP addresses you allow.
You may even do expressions if desired such as 128.9.*.
Much like changing properties during Configure go to the
'Edit Properties' in the menu to change this property.
</li>
<li>
The eXist database is not fully initialized until a query
happens. This can be done by simply going to almost any jsp page
such as the home (index.jsp) page of your registry or
when any component calls the registry via a web service call.
Hence if you connect via WebDav or client GUI and says the
'eXist' database is not initialized then please go to the
home page of your registry which will initialize it.
This usually happens when there is a restart of your servlet
container or eXist then you immediately tried to access it via WebDav.
</li>
<li>
Many people like the use of XML editors such as Oxygen or
XMLSpy to validate and edit the XML. But it is easy
just to edit the XML directly in the Database with the
Client GUI or general text editor via WebDav. <font color='red'>
Please be sure to take caution and validate the XML.</font>
Be sure to set schemaLocations in your XML Editor, so you
can validate or to take the XML; and by using the simple
"Enter Resource" jsp pages here at the registry put in the XML.
So it is validated and certain to go into the registry correctly.
</li>
</ul>

<h1>The Client</h1>
You can launch a web start client go to
<dl>
<dt><dd><a href='http://exist.sourceforge.net'>eXist</a>
</dl>
and click on the 'Launch' button on the left hand side to
start a client and follow the options below:
<dl>
<dt>
By default the username is "admin" password is empty
<dd>
Type: Remote<br>
URL: xmldb:exist://{host}:{port}/{context}/xmlrpc
<dt>
Sample URL for embedded:
<dd>
URL: xmldb:exist://registry.astrogrid.org/astrogrid-registry/xmlrpc
<dt>
or most likely this:
<dd>
URL: xmldb:exist://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/xmlrpc
<dt>
Sample url for external:
<dd>
URL: xmldb:exist://mssl.ucl.ac.uk:6080/xmlrpc
</dl>
The external eXist if downloaded is by default on port 8080,
but if you download the external zip file this software gives you
which has a few of the settings already set then default is 6080 port.
This is all you need to know to get into the database and try reindexing,
xqueries, backups, setting up users and other. Do read
the next 2 important notes:
<ul>
<li>
Be certain if you change usernames and passwords around
then you will need to change the registry JNDI properties
to reflect your changes in the database.</li>
<li>
Do not rename xml files in the astrogrid collections,
the names are based on the identifier and used for getting
one single Resource directly (on the GetResource interface)
instead of querying for a Resource.</li>
</ul>
<dl>
<dt><dd><a href="http://exist.sourceforge.net/documentation.html">Other Documentation</a>
</dl>

<h1>WebDAV</h1>
WebDav can be used with your database to map your
database like a regular network drive, where by you
can edit, delete, add various content. Several
popular apps that may interest you to connect via
WebDav are XMLSpy and Oxygen allowing you to get a more
easy interface to the xml documents. All of this can be
setup and is explained on the following link:
<dl>
<dt><dd><a href="http://exist.sourceforge.net/webdav.html">eXist WebDav Doc</a>
</dl>
N.B. Many XML Registry Resources will not have the
schemaLocation stored in the database XML.
Which means for validation and to make an easy to use
interface with Oxygen or XMLSpy you will
need to set the schemalocation yourself.  Down below
are a list of various places to obtain
the schemaLocation.
<br>
<br>
<i>Sample Schema Location Places</i>
<dl>
<dt>
The schemaLocations below can be found at:
<dd>
http://software.astrogrid.org<br>
ex: http://software.astrogrid.org/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd
<dt>
or your local registry at:
<dd>
http://{yourhost}:{yourport}/{registry context}<br>
ex: http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd
<br>---<br>
/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd
1.0 schemas.<br>
/schema/vo-resource-types/VOResource/v1.0/VOResource.xsd<br>
/schema/vo-resource-types/VORegistry/v1.0/VORegistry.xsd<br>
/schema/vo-resource-types/VODataService/v1.0/VODataService.xsd<br>
/schema/vo-resource-types/ConeSearch/v1.0/ConeSearch.xsd<br>
/schema/vo-resource-types/SIA/v1.0/SIA.xsd<br>
/schema/registry/RegistryUpdate/v1.0/RegistryDBStore.xsd
<dt>
At the time of this writing these schemas were not published:
<dd>
/schema/vo-resource-types/OpenSkyNode/v1.0/OpenSkyNode.xsd<br>
/schema/vo-resource-types/CEAService/v1.0/CEAService.xsd
</dl>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
