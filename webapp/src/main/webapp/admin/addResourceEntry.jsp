<%@ page import="org.astrogrid.registry.server.admin.v1_0.RegistryAdminService,
                 org.w3c.dom.Document,
                 org.astrogrid.registry.common.DomHelper,
                 org.apache.commons.fileupload.*,
                 java.net.URL,
                 java.net.URLDecoder,
                 java.util.List,
                 java.util.Iterator"
   session="false"
   contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<%
  if (request.getCharacterEncoding() == null) {
    request.setCharacterEncoding("UTF-8");
  }
  Document doc = null;
  boolean update = false;
  String errorTemp = "";
  boolean isMultipart = FileUpload.isMultipartContent(request);
//System.out.println("the ismultipart = " + isMultipart + " doc url = " + request.getParameter("docurl") + " and addFromURL = " + request.getParameter("addFromURL"));
//System.out.println("validate = " + request.getParameter("validate"));
 if(isMultipart) {
   DiskFileUpload upload = new DiskFileUpload();  
   List /* FileItem */ items = upload.parseRequest(request);
   Iterator iter = items.iterator();
   while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();
       if (!item.isFormField()) {
         doc = DomHelper.newDocument(item.getInputStream());
         update = true;
       }
   }

   
  } else if(request.getParameter("addFromURL") != null &&
     request.getParameter("addFromURL").trim().length() > 0) {
      doc = DomHelper.newDocument(new URL(request.getParameter("docurl")));
      update = true;
   } else if(request.getParameter("addFromText") != null &&
     request.getParameter("addFromText").trim().length() > 0) {
     String encodedXml = request.getParameter("Resource").trim();
     String xml = URLDecoder.decode(encodedXml, request.getCharacterEncoding());
     doc = DomHelper.newDocument(xml);
     update = true;
   }
%>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Edit Registry Entry</title>
<meta http-equiv="Content-type" content="text/xhtml; charset=UTF-8">
<style type="text/css" media="all">
   <%@ include file="/style/astrogrid.css" %>          
</style>
<%@ include file="../style/link_options.xml" %>
</head>

<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>

<div id='bodyColumn'>


<p>Service returns:</p>

   <font color="red"><%=errorTemp%></font>
<pre>
<%
   if(update) {
      Document result = null;
      RegistryAdminService server = new RegistryAdminService();	  
      out.write("<p>Attempt at updating Registry, if any errors occurred it will be printed below<br /></p>");
      result = server.updateInternal(doc);
      if (result != null) {
        DomHelper.DocumentToWriter(result, out);
      }
     
   }
%>
</pre>
</div>
<%@ include file="/style/footer.xml" %>

</body>
</html>
