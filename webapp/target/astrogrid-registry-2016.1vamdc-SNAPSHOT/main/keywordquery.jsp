<%@ page import="org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.astrogrid.store.Ivorn,
                 org.xmldb.api.base.ResourceSet,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.config.SimpleConfig,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,                 
                 java.net.*,
                 java.util.*,
                 org.apache.commons.fileupload.*,                  
                 java.io.*"
    session="false"
    	contentType="text/html; charset=UTF-8"
    		   pageEncoding="UTF-8"
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Keyword Search</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=UTF-8">
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<h1>Keyword Search</h1>

<p>
   Keyword search.  Place words seperated by spaces. <br>
</p>

<form method="post" action="keywordquery.jsp">
<input type="hidden" name="keywordquery" value="true">
<br>
Keywords: <input type="text" name="keywords"><br>
<br>
Require all words: <input type="checkbox" name="orValues" value="true">All words
<input type="submit" name="keywordquerysubmit" value="Query">
</form>


<%
  String error = "";
  boolean doQuery = false;
  String keywords = null;
  boolean orValue = false;
  if(request.getParameter("keywordquery") != null && request.getParameter("keywordquery").trim().equals("true")) {
   if(request.getParameter("keywords") != null && request.getParameter("keywords").trim().length() > 0) {
      keywords = request.getParameter("keywords");
      orValue = new Boolean(request.getParameter("orValues")).booleanValue();
      doQuery = true;
   }else {
      error = "Cannot find any words to query";
   }
   String maxCount = SimpleConfig.getSingleton().getString("exist.query.returncount", "25");
%>
<br>

<p>
<font color="red"><%=error%></font>
</p>

<strong>If no errors the Query results will be below:<br>Only a max of <%= maxCount %> entries will be shown:</strong><br>

<pre>
<%
   if(doQuery) {

      
      Document entry = null;
   	  ISearch server = JSPHelper.getQueryService(request);      
   	  ResourceSet resultSet = server.getQueryHelper().keywordQuery(keywords,!orValue);
	  if(resultSet == null || resultSet.getSize() == 0) {
                   entry = null;
	  } else {
	   	  if(resultSet.getSize() > 50) {
	   			do {
	   			  resultSet.removeResource(50);
	   			}while(resultSet.getSize() > 50);
	   		 }
	      entry = DomHelper.newDocument(resultSet.getMembersAsResource().getContent().toString());
      }
      if (entry == null) {
        out.write("<p>No entry returned</p>");
      }
      else {
      
      out.write("<table border=1>");
      out.write("<tr><td>AuthorityID</td><td>ResourceKey</td><td>View XML</td></tr>");
      NodeList resources = entry.getElementsByTagNameNS("*","Resource");
         
      for (int n=0; n < resources.getLength();n++) {
         out.write("<tr>\n");
         
			String authority = RegistryDOMHelper.getAuthorityID((Element)resources.item(n));
			String resource = RegistryDOMHelper.getResourceKey((Element)resources.item(n));

         String ivoStr = null;
         if (authority == null || authority.trim().length() <= 0) {
            out.write("<td>null?!</td>");
         } else {
               out.write("<td>"+authority+"</td>\n");
               ivoStr = authority;
         }//else

         if (resource == null || resource.trim().length() <= 0) {
            out.write("<td>null?!</td>");
         } else {
               out.write("<td>"+resource+"</td>\n");
               ivoStr += "/" + resource;
         }//if
         ivoStr = java.net.URLEncoder.encode(("ivo://" + ivoStr),"UTF-8");         
         
         String xsiType = ((Element)resources.item(n)).getAttribute("xsi:type");
         if(xsiType.indexOf(":") != -1) {
           xsiType = xsiType.substring(xsiType.indexOf(":")+1);
         }         

         out.write("<td><a href=viewResourceEntry.jsp?IVORN="+ivoStr+">View,</a>");
         out.write("<a href=../admin/editEntry.jsp?IVORN="+ivoStr+">Edit</a>");
//         out.write("<a href=../admin/xforms/XFormsProcessor.jsp?mapType="+xsiType+"&IVORN="+ ivoStr + ">XEdit</a></td>");         
         out.write("</tr>\n");
         
      }//for
         
         out.write("</table> <hr>");      
         out.write("The xml<br>");
        String testxml = DomHelper.DocumentToString(entry);
         testxml = testxml.replaceAll("<","&lt;");
        testxml = testxml.replaceAll(">","&gt;");
         out.write(testxml);
      }//else
   }//if
      
%>
</pre>

<% } %>
</div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
