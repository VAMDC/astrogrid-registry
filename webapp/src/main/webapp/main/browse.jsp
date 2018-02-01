<%@page import="org.xmldb.api.base.ResourceSet"%>
<%@ page import="org.astrogrid.registry.server.query.v1_0.RegistryQueryService,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.w3c.dom.Document,
                 org.w3c.dom.Element,
                 org.w3c.dom.Node,
                 org.w3c.dom.NodeList,
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
<title>Browse Registered Resources</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=UTF-8"/>
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
  RegistryQueryService server = new RegistryQueryService();

  String ivornpart = request.getParameter("IvornPart");
  ivornpart = (ivornpart == null)? "" : ivornpart.trim();
%>

<h1>Browse Registry</h1>

<!-- Navigation keys/controls -->

<form method="get" action="browse.jsp">
<p>
<br>
Find IVORNs including: <input name="IvornPart" type="text" value='<%= ivornpart %>'>
<input type="submit" name="button" value='List'>
</p>
</form>

<table border=1>
  <tr>
    <th>Title</th>
    <th>Type</th>
    <th>Authority ID</th>
    <th>Resource Key</th>
    <th>Updated</th>
    <th>Actions</th>
  </tr>
   
<% 
    Document entries = null;   
    ResourceSet resultSet;
    if (ivornpart.length() > 0) {
        resultSet = server.getQueryHelper().getResourcesByAnyIdentifier(ivornpart);
        
        if(resultSet == null || resultSet.getSize() == 0) {
            entries = null;
        } else {   		
          /*
            if(resultSet.getSize() > 50) {
            do {
                resultSet.removeResource(50);
            } while(resultSet.getSize() > 50);
          
            }*/
        entries = DomHelper.newDocument(resultSet.getMembersAsResource().getContent().toString());
        }
    } else {
        resultSet = server.getQueryHelper().getAll();
        if(resultSet == null || resultSet.getSize() == 0) {
            entries = null;
        } else {         
            /*
            if(resultSet.getSize() > 50) {
                do {
                    resultSet.removeResource(50);
                } while(resultSet.getSize() > 50);
           }
           */
           entries = DomHelper.newDocument(resultSet.getMembersAsResource().getContent().toString());
       }
   }

   if (entries == null) {
      out.write("<p>No entries!</p>");
   }
   else {

      out.write("");
      
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
            String ivorn = RegistryDOMHelper.getIdentifier(resourceElement);
            String ivoStr = ivorn.substring(6);
            
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
            ivoStr = java.net.URLEncoder.encode(("ivo://" + ivoStr),"UTF-8");
   
            //last update date
            out.write("<td>"+setFG+resourceElement.getAttribute("updated")+endFG+"</td>");
            out.write("<td>");
            out.write("<a href='viewResourceEntry.jsp?IVORN="+ivoStr+"'>View</a>, ");
            out.write("<a href='viewResourceEntry_body.jsp?XML=true&amp;IVORN="+ivoStr+"'>XML</a>, ");
            out.write("<a href='../registration/EditorLinks.jsp?IVORN="+ivoStr+"'>Edit</a>");

            out.write("</td>");
         out.write("</tr>\n");
      }
   
   }
%>
</table>

</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
