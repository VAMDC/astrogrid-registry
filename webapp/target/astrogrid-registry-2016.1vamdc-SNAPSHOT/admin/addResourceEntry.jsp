<%@ page import="org.astrogrid.registry.server.admin.*,
                 org.astrogrid.store.Ivorn,
                 org.astrogrid.registry.common.RegistryValidator,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 junit.framework.AssertionFailedError,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 java.net.*,                 
                 java.util.*,
                 org.apache.axis.utils.XMLUtils, 
                 org.apache.commons.fileupload.*, 
                 java.io.*"
   session="false"
   contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"
%>
<%
  boolean validateError = false;
  boolean doValidate = false;
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
       }else {
         //System.out.println("FIELd name = " + item.getFieldName());
         if("validate".equals(item.getFieldName())) {
            if("true".equals(item.getString())) {
               doValidate = true;
            }
         }
       }
   }
   //update = true;
   if(doValidate) {
      try {
         RegistryValidator.isValid(doc);
      }catch(AssertionFailedError afe) {
            update = false;
            validateError = true;
            errorTemp = afe.getMessage();
      }
   }//if

   
  } else if(request.getParameter("addFromURL") != null &&
     request.getParameter("addFromURL").trim().length() > 0) {
      doc = DomHelper.newDocument(new URL(request.getParameter("docurl")));
      update = true;
      if(request.getParameter("validate") != null &&
         request.getParameter("validate").equals("true")) {      
         try {
            RegistryValidator.isValid(doc);
         }catch(AssertionFailedError afe) {
            update = false;
            validateError = true;
            errorTemp = afe.getMessage();
         }
      }//if
      
   } else if(request.getParameter("addFromText") != null &&
     request.getParameter("addFromText").trim().length() > 0) {
    doc = DomHelper.newDocument(request.getParameter("Resource").trim());
     update = true;
      if(request.getParameter("validate") != null &&
         request.getParameter("validate").equals("true")) {     
         try {
            RegistryValidator.isValid(doc);
         }catch(AssertionFailedError afe) {
            update = false;
            validateError = true;
            errorTemp = afe.getMessage();
         }     
      }//if
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
      IAdmin server = JSPHelper.getAdminService(request);	  
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
