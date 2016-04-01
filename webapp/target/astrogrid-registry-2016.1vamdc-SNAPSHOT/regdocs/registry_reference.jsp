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

<h1>Registry Reference</h1>
This area is dedicated for more information on various areas in the
registry and some tips and tricks in certain areas.
<h2>Properties</h2>
Here is a more detailed list of each property in the registry.
There are a lot of properties and the majority you will not ever change.
But they are there for you to tweak. See following
sections on other certain ideas on manipulating the properties
to help you in certain objectives you wish to do with the registry.
Below are a list of all the properties of the registry,
most are custom and require no changing, but you 
might want to have a read through in case of another version of
the registry schema or small tweaks you wish to perform.
These are either a JNDI property (environment entry) or in the
WEB-INF/classes/astrogrid.properties in your webapp/context:
<ul>
<li>reg.amend.authorityid -- The Main Authority ID for this
Registry (Authority ID used for the Registry type)</li>
<li>xmldb.uri -- The XMLDB server location url.
By default it is set to use the internal eXist database.  
You may set it to xmldb:exist://host:port/exist/xmlrpc
for external eXist database.
e.g. xmldb:exist://localhost:8080/exist/xmlrpc.  
In general you can switch to another database that supports xmldb
but this is untested read more below on that.</li>
<li>xmldb.driver -- The XMLDB driver to be the registered xml database.</li>
<li>xmldb.admin.user -- The user used to create Resources or collections,
and startup/shutdown the internal eXist database if it is
internally being ran.</li>
<li>xmldb.admin.password -- The admin password used to create
resources or collections, and startup/shutdown the internal eXist
database if it is internally being ran.</li>
<li>xmldb.query.user -- The user used to query.</li>         
<li>xmldb.query.password -- The query user password.</li>
<li>server.cache -- Default is 'false' set to 'true' to
turn on server side caching, not much gain except for workshop
situations or stress testing.</li>
<li>server.cache.resource.count -- Default 100, what is the
number of Resources returned from the that must be reached to
store in the cache.</li>
<li>reg.amend.returncount -- The maximum number of results to
return from queries except for the main XQuery interface that
accepts straight XQuery and has no limit.</li>
<li>reg.custom.restrict.ipaddresses -- The IP addresses restricted
for WebDav and XMLRPC servlet. You may use expressions as well
ex: 127.*  by default it is set to 127.0.0.1.</li>
<li>reg.custom.restrict.ipaddresses.updates -- The IP addresses
restricted for web service Update calls normally from other
components. You may use expressions as well ex: 127.*
by default it is set to 127.0.0.1.</li>	  
<li>reg.custom.loggingdirectory -- The logging directory where logs are placed.</li>		 
<li>reg.amend.harvest -- Does this registry need to do harvesting.</li>
<li>reg.amend.oaipublish.1.0 -- The URL to the oai publishing URL
for 1.0, default is set to servlet, needs to be changed if your
context is not astrogrid-registry.</li>
<li>reg.custom.exist.configuration -- Point to another exist
configuration file on your disk hence a different data directory
as well (only for internal db use). This is recommended if you want
to use the internal database, but not have the configuration file
and data directory of eXist database with the war file so
undeploying/updating wars will not lose data. Also use if servlet
container does not unpack war file. Ignored if xmldb.uri is pointed
to an exteranl eXist db.</li>
<li>reg.custom.harvest.interval_hours -- Number of hours between
harvest (if harvest is enabled)</li>
<li>reg.amend.identify.repositoryName -- Used for the Identify verb
in OAI.  The repository Name.</li>
<li>reg.amend.identify.adminEmail -- Used for the Identify verb in OAI.
The administration Email of this OAI repository.</li>
<li>reg.custom.identify.earliestDatestamp -- Used for the Identify
verb in OAI.  The earliest date stamp for records/metadata
being published.</li>
<li>reg.custom.keywordxpaths.1.0 -- (Advanced) Specify xpath for
keyword search based on various versions in the registry.</li>
<li>reg.custom.rootNode.default -- (Advanced) The default root node
for elements in the Registry. Can override on various versions
in the registry.</li>
<li>reg.custom.declareNS.1.0 -- (Advanced) Declare the namespaces
used on the registry, based on registry resource version 1.0.</li>      
<li> several xquery properties that can be customized.</li>
<li>reg.custom.harvest.set-{main authority id} -- allows you to
harvest only a particular set for a registry.  By default ivo_managed
set is what is used.</li>
<li>Finaly various properties used in the OAI servlet and should
not need changing.</li>
</ul>
<h2>Extend the XML Resources and Schema</h2>
Lets say you needed to extend some of the xml resources,
meaning add your own namespaces with your own element extensions.
You can already do this by default, be sure your xml instance
documents has schemaLocations or what is needed 
to make it valid XML.  If your schema uses qualified namespace
elements which is frowned upon in the 1.0 version, but if used
you will need to change the property reg.custom.declareNS.1.0
(see above properties). This property allows you to declare a
namespace with a prefix for queries.  This property is not used
for the XQuery search though because you declare your full xquery
with namespaces on this interface method.  

<h2>Changing the XQueries</h2>
You might find a more powerfull way of doing xqueries or decide
to experiment on the xqueries on your own. Currently not located
in the environment entries web.xml piece, but located in the file
"astrogrid.properties" in the WEB-INF/classes directory under your
{context name} directory. You will find a set of internal queries
that you may change.  These internal queries are for the Search
and Keyword Search along with OAI queries that can occur.  It is not
used for the XQuery interface because this method you can define
your own XQuery.

</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
