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

<h1>eXist Reference</h1>
Below you will sections about eXist database and information
of procedures you may wish to implement. Advise if your
using eXist bundled inside your web application to move it
out to another directory.


<h2>General</h2>
eXist is a native XML database used by the registry to store its
metadata in a xml format and to peform XQuery operations to
retrieve the metadata. You can find all the information about
exist <a href='http://exist.sourceforge.net/'>here</a>.
Below you will find sections about information about eXist and
how it works with the Registry.

<h2>eXist embedded (Moving your Data Out)</h2>
By default eXist is embedded into the registry web application
meaning you don't have to download it or setup any settings.  The Registry
already has all the necessary libraries to run eXist.  The default
of the registry though puts all the information (XML data storage)
inside the webapp and this in general is not good and is recommended
to change because of upgrading or undeploying of the registry would lose
the data. You could of course make backups of the data directory and
after doing an upgrade or deploy simply copy back over the data
directory and restart your servlet/j2ee container. But there is a
easier way to make settings use a different directory to store your data:
<ul>
<li>Create some other directory not under tomcat of where you
would like your data to be stored for eXist.</li>
<li>Copy the conf.xml, and the whole "data" directory from your
{context name}/WEB-INF directory and the whole "lib" directory.
So inside your directory you should now have conf.xml,
data {directory}, lib {directory}.</li>
<li>Optionally if this is a brand new registry and your not able
to get at your directory because the servlet container did not
unpack the war then you can download an empty at
<a href='http://software.astrogrid.org/eXist/empty_exist_for_internal_registry.zip'>Registry eXist Zip</a>.</li>
<li>Change the JNDI property of "reg.custom.exist.configuration"
and point to this directory plus conf.xml ex: /exist/db/conf.xml.</li>
<li>Restarting the Registry webapp to make certain property is
set may be needed.</li>
<li>That is all you, now when data is being stored it is using
this new data directory, be sure during upgrades to set that
property to your directory. Also note "Windows" OS users "may"
need to escape there slashes ex: c:\\existstore.</li>
</ul>		
<h2>Making backups and upgrades</h2>
We thought this deserved a special section to make attention to
this small fact. eXist stores all its information in a directory
called "data". In embed mode you will find it in the
{context name}/WEB-INF/data (if you did not move the data directory
out (see above)). In external it is the same
$EXIST_HOME/webapp/WEB-INF/data. It can sometimes
be advisable to make a backup of this directory ever now and then.
No matter how badly something happens to eXist restoring this
directory will put everything correct again.
To make backups of the xml files which is also good you can do
this in a number of ways.
<ol>
<li>commandline. You will find a backupandrestore.{sh/bat}
scripts bundled for embedded eXist located in WEB-INF. Or found here:
<a href='http://exist.sourceforge.net/backup.html'>Info</a>
For embedded users the -ouri parameter will be
xmldb:exist://{server}:{port}/{registry context}/xmlrpc</li>            
<li>eXist provides a client GUI to allow you to do many things
internally to your db including backups and restores.
You should be able to click on the 'Launch' button located at
<a href='http://exist.sourceforge.net/'>eXist</a></li>
</ol>

<h2>Installing eXist externally</h2>
You may for your own reasons want to install eXist straight
from eXist website at http://exist.sourceforge.net.
The download file is on the home page of eXist and see the
<a href="http://exist.sourceforge.net/quickstart.html">quickstart guide</a>
for other info on installing such as on headless systems.
Once installed here are some things to be customized for
the astrogrid registry:
<ul>
<li>in the bin/functions.d/eXist-settings.sh set
set_exist_options() to have a -Xmx400M for 400mb memory.
Only needed for Full Registries.<br>
*For windows change the 128000k to 400M in the startup.bat.
Memory is not much of an issue as much with its now streaming
api service, but advised to increase.</li>
<li>Finally remember much like the above instructions to
change your xmldb.uri JNDI property to your eXist and by
default eXist runs on port 8080 this can be changed
at EXIST_HOME/tools/jetty/etc/jetty.xml you can change
the 8080 to 6080 for instance</li>
</ul>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
