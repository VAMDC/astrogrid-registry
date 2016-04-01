<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.query.*,
     	         org.astrogrid.registry.server.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 java.util.ArrayList"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Set Contract Version</title>
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>

<%
   if(request.getParameter("contractVersion") != null) {
   	JSPHelper.setContractVersion(request,request.getParameter("contractVersion"));
   }
%>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>

<h1>Contracts</h1>

<p>
This page is dedicated to setting your current contract version for this session for the use of the jsp pages.
The main difference is the return of VOResoruces to your screen may be different based on your Contract Version.<br>
<strong>Currently the jsp pages do not use the paging capability so the only reason to change your contract version is
to use a new VOResource version such as 1.0 versus 0.10</strong>
<br>
Possible Settings (Contract Version - VOResource Version):<br>
0.1 - 0.10 or 
1.0 - 1.0
</p>

<%
   ISearch server = JSPHelper.getQueryService(request);
   String currentContractVersion = server.getContractVersion();
   String currentVoResourceVersion = server.getResourceVersion();
   out.write("Current Settings = " + currentContractVersion + " - " + currentVoResourceVersion);
%>

<form method="post" ACTION="currentContractSession.jsp>
<p>
Setting Contract Version:
<select name="contractVersion">
	<option value="0.1">0.1 - 0.10</option>
   <option value="1.0">1.0 - 1.0</option>
</select>
 <br>
 <input type="submit" name="button" value="Set">
</p>
</form>
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
