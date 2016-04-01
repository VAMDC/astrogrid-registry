<%@ page import="org.astrogrid.registry.server.admin.*,
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

<h1>Self-registration of a registry</h1>




<p>
When a new registry is created, the first registration made in it must describe the registry itself. This form
makes that self-registration.
</p>



<p>
You must construct a VOResource of type registryType to describe your new registry. Use a text editor, or,
for preference, a specialist XML editor such Oxygen or XMLSpy. If possible, validate your XML document.
You may load your Registry type via url, local file, or text.
You can download a <a href="RegistryTypeWebServicev0_9.xml">template for a registryType document</a>.
</p>


<p>Enter the VODescription in the text area below. Include declarations of the
namespaces you use (the template linked above has the namespaces
already filled in). Once submitted and you are still in the process of setting up a Registry 
come back to the admin screens to finish the setup process.
</p>



Upload from a local file:
<form enctype="multipart/form-data" method="post" action="addResourceEntry.jsp">
<input type="file" name="docfile" />
<input type="hidden" name="addFromFile" value="true" />
<input type="submit" name="uploadFromFile" value="upload" />
</form>
<br />
Upload from a url:
<form method="post" action="addResourceEntry.jsp">
<input type="text" name="docurl" />
<input type="hidden" name="addFromURL" value="true" />
<input type="submit" name="uploadFromURL" value="upload" />
</form>

Upload from text:<br />
<form action="addResourceEntry.jsp" method="post">
<input type="hidden" name="addFromText" value="true" />
<p>
<textarea name="Resource" rows="24" cols="80">
<%= resource %>
</textarea>
</p>
<p>
<input type="submit" name="button" value="Submit"/>
</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
