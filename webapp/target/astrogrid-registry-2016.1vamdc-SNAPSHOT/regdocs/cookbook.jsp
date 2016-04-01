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
<title>Registry FAQ Pages</title>
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

<h1>FAQ</h1>
Below are the most common questions and answers to the registry:
<br>
<br>
<b>What is the differences of Publishing, Full, (or even Special)
type Registries?</b>
<br>
<br>
<i>Publishing</i> - is the smallest of all the types of registries and
contains Resources managed by this Registry and only this registry.
Other FULL Registries will harvest the publishing registries.
It's primary purpose is to publish its contents when the Full
Registries ask for the data.  It does not have to have a Query
interface but all Astrogrid Registries have both a Query and
Harvest (Publishing) type interfaces.  A publishing registry
can also be part of a Full Registry but it is not recommended,
we strongly encourage to not mix FULL and Publishing keep them separate.
<br>
<br>
<i>Full</i> - this is a registry that contains all known resources and
harvests all publishing registries known to the RofR (Registry of
Registries). Note in the past we have found
having a external eXist database to be more of an advantage for Full
registries.
<br>
<br>
<i>Special</i> - rarely done but a registry that is setup to hold many
resources but not ALL resources. Hence it usually harvests particular
Registries of interest.
<br>
<br>

<b>What is an Authority ID and what is this error about Authority ID
not managed?</b>
<br>
<br>
All XML/Resources in a registry have a unique identifier which is a URI.
An identifier example is 'ivo://{authorityid}/{some unique string}' A 
registry may manage many authority ID's and they will be owned by that
particular registry.  When creating this registry initially a main
authority ID was created, you may click on the menu to create more
Authority ID's.  If you received an error about Authority ID not
managed then you tried to enter XML with authority ID you do not
manage and you need to begin managing it. Note that an authority
ID can be owned by only one registry, the Registry of Registries
validator may detect that another registry already owns the authority ID.
In the past an authority ID tends to go to a domain like syntax with
the institute or project such as ucl.ac.uk or mssl.ucl.ac.uk
<br>
<br>
<b>Other Departments or even Institutes want to Register Resources
do they all need a publishing Registry?</b>
<br>
<br>
It can be nice but no it is not necessary and in fact it is much
easier to set it up with a registry that is already
functioning.  You can click on the 'Add Authority'
to add a new authority ID then they will be allow to submit
resources under that authority into the registry.
<br>
<br>
<b>Is the web application the only way to upload new Resources?</b>
<br>
<br>
No, but is the most common.  You can use the registry client library
or your own library to connect to the Registry Admin endpoint to
submit new/update Resources located at endpoint
.../services/RegistryUpdatev1_0. You will need to change the property
'reg.custom.restrict.ipaddresses.updates' to a comma
separated list of IP addresses you may use wildcards '*'.
This is needed so everybody is not allowed to update the Registry.
By default localhost(127.0.0.1) is allowed.
<br>
<br>
<b>How do I move the internal Database?</b>
<br>
<br>
Most common install of the registry has an XML database named
eXist installed inside the webapp and could
potentially be dangerous when uninstalling or upgrading the
registry may lose the data requiring a restore.  
See <a href='eXist_reference.jsp'>eXist Reference</a>
on how to move the internal database out. 
<br>
<br>
<b>How to do backups and restore?</b>
<br>
<br>
There are two particular backups described below you may elect
to do none, one, or both.  Note that the database does have a
built in recovery system though uncertain how much should be
relied on this recovery.
<ol type='A'>
<li> Binary - this method is the easiest because you are only
copying one directory. The database eXist will store all the XML
in a binary format inside a directory called 'data'  
<%
String xmldbURI = SimpleConfig.getSingleton().getString("xmldb.uri");
String existLoc = SimpleConfig.getSingleton().getString("reg.custom.contextFile", "");
if(existLoc != null && existLoc.trim().length() > 0) {
%>
which is located at <%=existLoc%>.
<%	
    } else {
        if(xmldbURI.equals("xmldb:exist://")) {
%>
which is currently still located in your webapp
'$CATALINA_HOME/webapps/<%=request.getContextPath()%>/WEB-INF/'
you may wish to read
the above information about moving your database outside the webapp.
<%
    } else {
%>
which is connected to an external eXist that you have setup,
possibly check $EXIST_HOME/webapp/WEB-INF.
<%
    }
}
%>
Since all the data is located in this directory you can backup
and restore this directory if needed.
</li>
<li>XML - A script is used to make backups and restores of your
database xml content. This has a small advantage if you are moving
to an external database that is a different version or if the
above A. somehow still got corrupted.  At the same location as
your data directory you will see a backupandrestore.{sh/bat}
at the top of this file is directions and options on how to run
the script.
</li>
</ol>
<br>
<b>Can I use an external database?</b>
<br>
<br>
Yes, you can use an external eXist database.  And does have an
advantage because it can be on a different server, clustered,
up to date version with bug fixes, along with other features.
To do this go to <a href="http://exist.sourceforge.net">eXist</a>
and learn how to install the database. The only property that must
change in the registry is to change the xmldb.uri to
'xmldb:exist://{server}:{port}/exist/xmlrpc'
If your using tomcat you can change the property
<a href="../admin/environment-edit.jsp">here</a>.
<br>
<br>
<b>Where can I find Schema for validation?</b>
<br>
<br>
There are two places to find XML schemas that the registry can use:
<ol type='a'>
<li>http://software.astrogrid.org/schema/ --- official schemas
and any extensions used at Astrogrid.
<li>http://ivoa.net/xml/index.html --- official schemas. 
</ol>
<br>
<b>Can I extend the registry and how with my own XML?</b>
<br>
<br>
This question assumes you have knowledge of schema and XML. There are 4 steps:
<ol>
<li>Create your extension schema.
<li>Place the schema on a public location so schemaLocation validation can
occur.
<li>Now create your XML instance document and try to upload it to the
registry. YOU MUST ENSURE you define the schemaLocation attribute
(and namespaces) on each Resource element (or one of it's sub elements).
Hence do not define it at the top on 'VOResources' element those
namespaces are not copied into the individual 'Resource' elements.
<li>For the Search and keywordSearch queries you must edit the
WEB-INF/classes/astrogrid.properties and add your declaration of
namespace at the property 'reg.custom.declareNS.1.0'.
</ol>
<br>
<b>What is 'Harvest Particular' option?</b>
<br>
<br>
The registry much like harvesting other registries can contact another
service that has an interface and ask the service for any new
information it may have to go into the registry. A good example is a
component such as Data Set Access 'DSA' or VOSpace may
have a new capability with the Registry you can go to Harvest link and
click on that service to get any new information.  Currently though it only
harvests services that are managed by this registry and it does
NOT automatically harvest the services.
<br>
<br>
<b>What is Advance Update?</b>
<br>
<br>
Editing the XML in the registry can be done by HTML forms, upload raw
XML, or pasting straight raw xml into a text box but all these updates
must be on resources that you manage hence the authority ID is managed
by this registry. But there are times when you really need to make an
edit on another particular resource this is mainly in FULL registries.
This link allows you to bypass extra checks and submit edits to resources
you do not manage; this web page though requires you to submit the raw
XML (usually by copying the XML from the browser and pasting).
NOTE that if automatic harvests are on then there is a potential this
resource will be overwritten when a change happens to the other
harvestee registry.
<br>
<br>
<b>Can I harvest just certain Registries?</b>
<br>
<br>
This is handy if you want to be what is known as a Special registry
hence you cater to certain areas of Resources and do not have
all resources in your registry. The easiest thing to do is read
instructions on the 'Get Registry' option in the right menu.
Note one small trick is to get everything for Registry of Registries
(RofR) then using Remove Resource to remove the ones you do not
wish to have.  Then you can go to the Harvest page to do harvesting
or let the automatic harvester pick up entries.
<br>
<br>
<b>How do I know if a harvest succeeded?</b>
<br>
<br>
Check your menus you will see a 'Harvest Status' link to check
the Registries and see information of when harvests took
place and any errors or validation problems.
Note that the information is probably at the very bottom of the page
for the last harvest.  In the Administration Section is another similiar
'Harvest' page but this is not public and allows you to carry out
manual harvests.
</div>
<%@ include file="/style/footer.xml" %>
</body >
</html>
