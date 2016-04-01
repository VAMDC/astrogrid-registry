<%@page import=" org.astrogrid.config.SimpleConfig,
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
                 java.net.*,
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
<title>Add or Update Entry</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=UTF-8">
<style type="text/css" media="all">
    <%@ include file="../style/astrogrid.css" %>
</style>
<%@ include file="../style/link_options.xml" %>
</head>
<body>
<%@ include file="../style/header.xml" %>
<%@ include file="../style/navigation.xml" %>
<div id='bodyColumn'>

<%
   String resource = "";
   Document resourceDoc = null;
   if(request.getParameter("IVORN") != null && request.getParameter("IVORN").trim().length() > 0) {
       ISearch server = JSPHelper.getQueryService(request);
           resourceDoc = server.getQueryHelper().getResourceByIdentifier(request.getParameter("IVORN"));
           if (resourceDoc != null) {
               StringBuffer resContent = new StringBuffer(DomHelper.ElementToString(((Element)(resourceDoc.getDocumentElement().getElementsByTagNameNS("*","Resource").item(0)))));
               if(server.getContractVersion().equals("0.1")) {    	  
                   String temp=resContent.substring(0,resContent.indexOf(">"));
                   int tempIndex = resContent.indexOf("created=\"");
                   int tempIndex2;
                   if(tempIndex != -1 && tempIndex < temp.length()) {
                       tempIndex2 = resContent.indexOf("T",tempIndex);
                       tempIndex = (resContent.indexOf("\"",tempIndex +  5));
                       if(tempIndex2 != -1 && tempIndex2 < tempIndex) {
                           resContent.replace(tempIndex2,tempIndex,"");
                       }
                   temp = resContent.substring(0,resContent.indexOf(">"));
                   }//if
                   tempIndex = resContent.indexOf("updated=\"");
                   if(tempIndex != -1 && tempIndex < temp.length()) {
                       tempIndex2 = resContent.indexOf("T",tempIndex);
                       tempIndex=(resContent.indexOf("\"",tempIndex + 5));
                       if(tempIndex2 > 0 && tempIndex2 < tempIndex) {
                           resContent.replace(tempIndex2,tempIndex,"");
                       }//if
                   }//if
               }//if
               resource = resContent.toString();
           }//if
       }//if
%>

<h1>Edit Entry</h1>
Here you can update the resources in various ways. If the
Resources are already there then it will be updated, 
Validation is now always turned on and checked before
going into the Registry. You may checkmark the 'Validate'
box to have it validated before it is ever sent and checked
at the Server. <i>The schemaLocations are not particularly
needed for known ivoa schemas, but they will be preserved
if in the XML and may be required for validation on extensions.
These schemaLocations may also be desirable if you use other
applications for validation via the XML database using WebDav
or other interfaces.</i>
<br>
<br>
There is a range of various ways to insert or update records.  Mainly this jsp page or update web service calls.  Both take XML in a particular way
and the xml samples are at the bottom of this page.
<br>
<br>
Upload from a local file:
<form enctype="multipart/form-data" method="post" action="addResourceEntry.jsp">
<input type="checkbox" name="validate" value="true">Validate
<input type="file" name="docfile">
<input type="hidden" name="addFromFile" value="true">
<input type="submit" name="uploadFromFile" value="upload">
</form>
<br>
Upload from a url:
<form method="post" action="addResourceEntry.jsp">
<input type="checkbox" name="validate" value="true">Validate
<input type="text" name="docurl">
<input type="hidden" name="addFromURL" value="true">
<input type="submit" name="uploadFromURL" value="upload">
</form>
<br>
Upload from text:<br>
<form action="addResourceEntry.jsp" method="post">
<input type="checkbox" name="validate" value="true">Validate
<input type="hidden" name="addFromText" value="true">
<p>
<textarea name="Resource" rows="24" cols="80">
<%= resource %>
</textarea>
</p>
<p>
<input type="submit" name="button" value="Submit">
</p>
</form>
Sample of XML that can be placed on this page or via
web service call. Note that it is normally better to save
XML in a file and upload the file via this jsp page.
The html box to paste xml is good but requires escaping
certain characters hence a text file can be more of an advantage.
<pre>
<br>
Multiple Resources:<br>
&lt;ri:VOResources xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" from="1" numberReturned="5" more="false" &gt;
&lt;!-- You can place as many ri:Resource elements --&gt;
&lt;ri:Resource xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" .....
&lt;ri:Resource%gt;
&lt;/ri:VOResources&gt;
<br>
Single Resource (Remember you can use the Multiple Resources way as well):<br>
&lt;ri:Resource xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" .....
&lt;ri:Resource%gt;
</pre>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
