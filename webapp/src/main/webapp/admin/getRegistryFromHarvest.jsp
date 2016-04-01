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
                 java.io.*,
                 org.astrogrid.registry.server.admin.*,
                 org.astrogrid.store.Ivorn,
                 java.net.URL,
                 org.astrogrid.io.Piper,
                 java.util.Date,
                 java.util.Locale,
                 java.text.*"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Get Registry Type Resource From Harvest URL</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
<!-- European format dd-mm-yyyy -->
<script language="JavaScript" src="calendar1.js" type="text/javascript">
</script>
<!-- Date only with year scrolling -->
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>
<h1>External Registry</h1>
Normally you only need to use this page one time for the RofR,
because all Registry Type Resources are listed in what is known as the 
Registry of Registries (RofR).  By simply hitting Submit below will put the
RofR record into  your Registry whereby you can go to the Harvest jsp page
to pick up all the known Registries to be harvestes (or let the automatic
harvester pick it up).
<br>
<br>
<br>
<b>Advanced</b><br>
<br>
Occasionally on private registries not known to the
RofR or other situations which may cause
a registry to not be in the RofR. Hence you as
the admin may wish to only harvest certain registries.  
You may use this JSP to add the Registry Type Resource
to 'this' Registry.  Once that
is done then you may go and harvest it via the
harvestResource page on the menu or let the
automatic harvest pick it up. 
For other registries put in the OAI harvest url below.
<br>
<br>
<br>
<b>For registry this is typcially</b><br>
<br>
http://{server}:{port}/{context}/OAIHandlerv{contractversion ex:1_0}
this will get the 1 (and only 1) Registry type entry to be harvested
then the harvester or harvest.jsp page to actually harvest the contens.
<br>
<br>
<form name="harvestURL" method="post" action="getRegistryFromHarvest.jsp">
URL:
<input type="text" size="100%" name="harvesturl" value="http://rofr.ivoa.net/cgi-bin/oai.pl">
<br>
<br>
<input type="submit" value="Submit">
</form>

<%

   //this version number is the actual resoruce version like 0.10  not the contract version.
   if(request.getParameter("harvesturl") != null) {
	    ISearch rqs = JSPHelper.getQueryService(request);
	    String reqHarvestURL = request.getParameter("harvesturl");
	    if(reqHarvestURL.indexOf("?") == -1) {
	    	reqHarvestURL += "?verb=Identify";
	    }
		URL url = new URL(reqHarvestURL);
		Document identDoc = DomHelper.newDocument(url);
		NodeList nl = identDoc.getElementsByTagNameNS("*","Resource");
		if(nl.getLength() > 0) {
		    Element resElem = (Element)nl.item(0);
		    Document submitDoc = DomHelper.newDocument(DomHelper.ElementToString(resElem));
	 	    IAdmin ras = JSPHelper.getAdminService(request);
			Document resultDoc = null;
			resultDoc = ras.updateInternal(submitDoc);
			if(resultDoc.getElementsByTagNameNS("*","Fault").getLength() > 0) {
				out.write("<br><br><pre>");
				out.write(DomHelper.DocumentToString(resultDoc));
				out.write("</pre>");
			}
		} else {
		  out.write("No Resource Element found at the url: " + reqHarvestURL);
		}
   }
%>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>

