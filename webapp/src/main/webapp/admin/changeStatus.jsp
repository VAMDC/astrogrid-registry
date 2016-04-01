<%@ page import="org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.store.Ivorn,
                 org.xmldb.api.base.ResourceSet,
                 org.w3c.dom.Document,
                 org.w3c.dom.Element,
                 org.w3c.dom.Node,
                 org.w3c.dom.NodeList,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 java.net.*,
                 java.util.*,
                 java.io.*"
    session="false"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Browse to change Status</title>
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
<%
    ISearch server = JSPHelper.getQueryService(request);
    long offset = 0;
    String off = request.getParameter("Index");
    if (off != null) {
        offset = Long.parseLong(off);
    }
    String ivornpart = request.getParameter("IvornPart");
    if (ivornpart == null) { ivornpart = ""; }
%>

<h1>Change Status</h1>

<!-- Navigation keys/controls -->
<form method="get" action="changeStatus.jsp">
<p>
<br>
Find IVORNs including: <input name="IvornPart" type="text" value='<%= ivornpart %>'>
<input type="submit" name="button" value='List'>
</p>
</form>
<p>
<form action="changeStatusResult.jsp" method="POST">
Change all Entries below to the chosen status.
Custom/None means do not change 'all' entries
just the custom or selected entries below.<br> 
<br>
<select name="changeAll">
    <option value="NONE">Custom</option>
    <option value="active">active</option>
    <option value="inactive">inactive</option>
    <option value="deleted">deleted</option>
</select><input type="submit" name="button" value="Set"><br>
<br>
<%
    Document entries = null;   
    ResourceSet resultSet;
    if ( (ivornpart != null) && (ivornpart.trim().length() > 0) ) {
        resultSet = server.getQueryHelper().getResourcesByAnyIdentifier(ivornpart);
        if(resultSet == null || resultSet.getSize() == 0) {
            entries = null;
        } else {   		
            if(resultSet.getSize() > 50) {
                do {
                    resultSet.removeResource(50);
                } while(resultSet.getSize() > 50);
            }
            entries = DomHelper.newDocument(resultSet.getMembersAsResource().getContent().toString());
        }
    } else {
         resultSet = server.getQueryHelper().getAll();
         if(resultSet == null || resultSet.getSize() == 0) {
             entries = null;
        } else {         
             if(resultSet.getSize() > 50) {
                 do {
                     resultSet.removeResource(50);
                 }while(resultSet.getSize() > 50);
             }
             entries = DomHelper.newDocument(resultSet.getMembersAsResource().getContent().toString());
        }
    }
    if (entries == null) {
        out.write("<p>No entries!</p>");
    } else {
        out.write("<table border=1>");
        out.write("<tr><th>Title</th><th>Type</th><th>Identifier</th><th>Status</th><th>Updated</th><th>Actions</th></tr>");
        NodeList resources = entries.getElementsByTagNameNS("*","Resource");
        for (int n=0;n<resources.getLength();n++) {
            Element resourceElement = (Element) resources.item(n);
            boolean deleted = false; 
            boolean inactive = false;
            if(resourceElement.getAttribute("status").length() > 0) {
                deleted = resourceElement.getAttribute("status").toLowerCase().equals("deleted");         
                inactive = resourceElement.getAttribute("status").toLowerCase().equals("inactive");
            }
            String bgColour = "#FFFFFF";
            String fgColour = "#000000";
            if (deleted) {
                bgColour = "#FFFFFF";
                fgColour = "#AAAAAA";
            }
            if (inactive) {
                bgColour = "#FFFFFF";
                fgColour = "#AAAAAA";
            }         
            String setFG = "<font color='"+fgColour+"'>";
            String endFG = "</font>";
            out.write("<tr bgcolor='"+bgColour+"'>\n");
            out.write("<td>"+setFG+DomHelper.getValue(resourceElement, "title")+endFG+"</td>");	         
            //type
            String xsiType = resourceElement.getAttribute("xsi:type");
            out.write("<td>"+setFG+xsiType+endFG+"</td>");
            if(xsiType.indexOf(":") != -1) {
                xsiType = xsiType.substring(xsiType.indexOf(":")+1);
            }
            //authr
            String authority = RegistryDOMHelper.getAuthorityID(resourceElement);
            String resource = RegistryDOMHelper.getResourceKey(resourceElement);
            String ivoStr = RegistryDOMHelper.getIdentifier(resourceElement);
            ivoStr = ivoStr.substring(6);
            //System.out.println("here is the ivoStr in changeStatus = " + ivoStr);
            if (resource == null || resource.trim().length() <= 0) {
                out.write("<td><a href='changeStatus.jsp?IvornPart="+authority+"'>ivo://"+authority+"</a></td>\n");
            } else { 
                out.write("<td><a href='changeStatus.jsp?IvornPart="+authority+"'>ivo://"+ivoStr+"</a></td>\n");
            }
            //ivoStr = java.net.URLEncoder.encode(("ivo://" + ivoStr),"iso-8859-1");
            if(resourceElement.getAttribute("status").length() > 0) {
                out.write("<td>" + resourceElement.getAttribute("status").toLowerCase() + "</td>");   
            }
            //last update date
            out.write("<td>"+setFG+resourceElement.getAttribute("updated")+endFG+"</td>");
            out.write("<td>");
            out.write("<a href='../main/viewResourceEntry.jsp?IVORN="+java.net.URLEncoder.encode(("ivo://" + ivoStr),"iso-8859-1")+"'>View,</a>  ");
            out.write("<a href='../main/viewResourceEntry_body.jsp?XML=true&amp;IVORN="+java.net.URLEncoder.encode(("ivo://" + ivoStr),"iso-8859-1")+"'>XML</a>, ");
            out.write(" <a href='harvestStatus.jsp?IVORN="+java.net.URLEncoder.encode(("ivo://" + ivoStr),"iso-8859-1")+"'>Status</a>"
);
            out.write("<select name='status_" + ivoStr + "'>");
            out.write("<option value='NONE'>No Change</option>");
            out.write("<option value='active'>active</option>");
            out.write("<option value='inactive'>inactive</option>");
            out.write("<option value='deleted'>deleted</option>");
            out.write("</select>");
            out.write("</td>");
            out.write("</tr>\n");
        }      
        out.write("</table>");
    }
%>
<br>
<input type="submit" name="button" value="Set">
</form>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
