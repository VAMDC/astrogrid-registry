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
.notactive {color : gray;}
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
    <th>IVORN</th>
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

    NodeList resources = entries.getElementsByTagNameNS("*","Resource");

    for (int n=0;n<resources.getLength();n++) {
      Element resourceElement = (Element) resources.item(n); 
       
      // Work out how to style the td elementsd such that the active
      // resources are distinguished from the rest.
      String status = resourceElement.getAttribute("status");
      boolean notActive = (!"active".equalsIgnoreCase(status));
      String td = (notActive)? "<td class='notactive'>" : "<td>";
         
      out.write("<tr>\n");
         
      out.write(td);
      out.write(DomHelper.getValue(resourceElement, "title"));
      out.write("</td>");	         
      
      String xsiType = resourceElement.getAttribute("xsi:type");
      out.write(td);
      out.write(xsiType);
      out.write("</td>");
         
            
      String ivorn = RegistryDOMHelper.getIdentifier(resourceElement);
      out.write(td);
      out.write(ivorn);
      out.write("</td>");
            
      out.write(td);
      out.write(resourceElement.getAttribute("updated"));
      out.write("</td>");
            
      String encodedIvorn = java.net.URLEncoder.encode(ivorn,"UTF-8");
      out.write(td);
      out.write("<a href='viewResourceEntry.jsp?IVORN="+encodedIvorn+"'>View</a>, ");
      out.write("<a href='viewResourceEntry_body.jsp?XML=true&amp;IVORN="+encodedIvorn+"'>XML</a>, ");
      out.write("<a href='../registration/EditorLinks.jsp?IVORN="+encodedIvorn+"'>Edit</a>");
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
