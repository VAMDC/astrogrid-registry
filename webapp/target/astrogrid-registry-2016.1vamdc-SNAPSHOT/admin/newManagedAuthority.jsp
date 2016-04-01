<%@ page import="org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper"
   isThreadSafe="false"
   session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Self-registration</title>
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

<h1>Add Authority</h1>
<p>
This page is for adding a new authority ID to be
managed by your registry hence no other registry
can manage this authority ID, 'only this registry'.
By filling in the form and hitting submit will create
an Authority type xml to be sent to the Registry
which in turn will cause it to add to the 'Registry'
type managedAuthority element.
</p>
<p>
<%
    String authID = SimpleConfig.getSingleton().getString("reg.amend.authorityid", "");
%>
<%
    ISearch server = JSPHelper.getQueryService(request);
    String currentContractVersion = server.getContractVersion();
    if(Double.valueOf(currentContractVersion).doubleValue() >= 1.0) {
%>
<form action="newManagedAuthorityCheck.jsp" method="post">
<p>
<input type="hidden" name="version" value="1.0">
<table>
    <tr><td>Authority ID </td><td> <input type="text" name="AuthorityID" value="" ></td></tr>
    <tr><td>Title        </td><td> <input type="text" name="Title"></td></tr>
    <tr><td>Publisher    </td><td> <input type="text" name="Publisher"></td></tr>
    <tr><td>Contact Name </td><td> <input type="text" name="ContactName"></td></tr>
    <tr><td>Contact email</td><td> <input type="text" name="ContactEmail"></td></tr>
    <tr><td>Description  </td><td> <input type="text" name="ContentDescription"></td></tr>
    <tr><td>Reference URL</td><td> <input type="text" name="ContentRefURL"></td></tr>
</table>
<p>
<input name="button" value="Submit" type="submit">
</p>
</form>
<%
} else {
    out.write("Current contract version cannot use this jsp page, please change to 1.0");
}
%>
</div>
<%@ include file="/style/footer.xml" %>
</body></html>
