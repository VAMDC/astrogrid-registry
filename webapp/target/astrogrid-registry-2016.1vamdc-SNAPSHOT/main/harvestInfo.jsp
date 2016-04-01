<%@ page import="org.astrogrid.registry.server.admin.*,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.harvest.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.w3c.dom.*,
                 org.astrogrid.util.DomHelper,
                 java.text.*,					  
                 java.util.ArrayList,
                 java.util.Date,
                 java.util.Locale,
                 java.io.*"
    session="false"
%>
<%
   ISearch server = JSPHelper.getQueryService(request);
   IHarvest rhs = JSPHelper.getHarvestService(request);
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Harvest Status</title>
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
<h1>Harvest Status</h1>
<p>Status information on previous Harvests.</p>
<p>
<%
    Document entries = DomHelper.newDocument(server.getQueryHelper().getRegistriesQuery().getMembersAsResource().getContent().toString());
    if (entries == null) {
        out.write("<p>No entries?!</p>");
    } else {
        out.write("<table border=1>");
        out.write("<tr><th>Title</th><th>Type</th><th>Authority ID</th><th>Resource Key</th><th>Updated</th><th>Actions</th></tr>");
        NodeList resources = entries.getElementsByTagNameNS("*","Resource");
        for (int n=0;n<resources.getLength();n++) {
            Element resourceElement = (Element) resources.item(n);
            String authority = RegistryDOMHelper.getAuthorityID(resourceElement);
            String resource = RegistryDOMHelper.getResourceKey(resourceElement);
            String ivoStr = "ivo://" + authority;
            if (resource != null || resource.trim().length() > 0) {
     	        ivoStr = ivoStr+"/"+resource;
            }
            boolean deleted = false; 
            if(resourceElement.getAttribute("status").length() > 0)
                deleted = resourceElement.getAttribute("status").toLowerCase().equals("deleted");
            String bgColour = "#FFFFFF";
            String fgColour = "#000000";
            if (deleted) {
                bgColour = "#FFFFFF";
                fgColour = "#AAAAAA";
            }
            String setFG = "<font color='"+fgColour+"'>";
            String endFG = "</font>";
            out.write("<tr bgcolor='"+bgColour+"'>\n");
            out.write("<td>");
            out.write(" " + setFG+DomHelper.getValue(resourceElement, "title")+endFG+"</td>");
            //type
            out.write("<td>"+setFG+resourceElement.getAttribute("xsi:type")+endFG+"</td>");
            if (authority == null || authority.trim().length() <= 0) {
                out.write("<td>null?!</td>");
            } else {
                out.write("<td><a href='browse.jsp?IvornPart="+authority+"'>"+authority+"</a></td>\n");
            }
            if (resource == null || resource.trim().length() <= 0) {
                out.write("<td>null?!</td>");
            } else { 
                out.write("<td>"+setFG+resource+endFG+"</td>\n");
            }
            //last update date
            out.write("<td>"+setFG+resourceElement.getAttribute("updated")+endFG+"</td>");
            out.write("<td>");
            out.write("<a href='viewResourceEntry.jsp?IVORN="+ivoStr+"'>View</a>,  ");
            out.write("<a href='viewResourceEntry_body.jsp?XML=true&amp;IVORN="+
ivoStr+"'>XML</a>, ");
            out.write("<a href='harvestStatus.jsp?IVORN="+ivoStr+"'>Status</a>");
            out.write("</td>");                        
            out.write("</tr>\n");
        }
        out.write("</table>");
    }
%>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
