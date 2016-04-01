<%@page import=" org.astrogrid.config.SimpleConfig,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Node,
                 org.w3c.dom.Element,
                 org.w3c.dom.Document,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.registry.server.http.servlets.Log4jInit,
                 org.astrogrid.xmldb.client.XMLDBManager,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.query.*,
                 org.astrogrid.store.Ivorn,
                 org.apache.axis.utils.XMLUtils,
                 java.net.*,
                 java.util.*,
                 java.io.*"
   isThreadSafe="false"
   session="false"
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Service metadata form</title>
        <style type="text/css" media="all">
           <%@ include file="/style/astrogrid.css" %>
        </style>
        <%@ include file="/style/link_options.xml" %>
    </head>
    <body>

    <%@ include file="/style/header.xml" %>
    <%@ include file="/style/navigation.xml" %>

      <div id='bodyColumn'>

        <h1>Recording metadata from VOSI</h1>
        <%
        String capURL = "";
        ISearch server = JSPHelper.getQueryService(request);
        Document entry;   
        try {   
        	entry = server.getQueryHelper().getResourceByIdentifier(request.getParameter("IVORN"));
        	if(entry != null) {
        		 NodeList capList = entry.getDocumentElement().getElementsByTagName("capability");
        		 //String ceaURL = "";
        		 NodeList urlText;
        		    for(int k = 0;k < capList.getLength();k++) {
        		    	if( ((Element)capList.item(k)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI/v0.3#capabilities") ||
        						((Element)capList.item(k)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI/v0.4#capabilities")	||
        						((Element)capList.item(k)).getAttribute("standardID").equals("ivo://org.astrogrid/std/VOSI#capabilities") ||
        						((Element)capList.item(k)).getAttribute("standardID").equals("ivo://ivoa.net/std/VOSI#capabilities")) {
        		    		urlText= ((Element)capList.item(k)).getElementsByTagName("accessURL").item(0).getChildNodes();
        		    		
        		    	
        		    		if(capURL.length() == 0) {
        						for(int j = 0;j < urlText.getLength();j++) {
        							if(urlText.item(j).getNodeType() == Node.TEXT_NODE) {
        								//System.out.println("yes text node lets try to concat");
        								capURL += urlText.item(j).getNodeValue();
        							}//if
        						}//for
        		    		}//if
        		    		k = capList.getLength();
        		    	}//if
        		    }//for
        	}
        }catch(Exception e) {
         entry = null;
        }
        %>
        <form action="ServiceMetadata" method="post">
          <input type="hidden" name="IVORN" value="<%=request.getParameter("IVORN")%>"/>
          <table>
            <tr>
              <td>IVO identifier for resource</td>
              <td><%=request.getParameter("IVORN")%></td>
              
              
            <tr>
              <%if(request.getParameter("appResource") != null &&
                   request.getParameter("appResource").equals("true")) { %>
              <td>URL for getting application data</td>
              <td><input type="text" name="VOSI_AppData" size="48"/></td>
              <td><a href="../help/capabilities.html">help</a></td>                
              <% } else { %>
              <td>URL for getting service capabilities.</td>
              <td><input type="text" name="VOSI_Capabilities" size="48" value="<%=capURL%>" /></td>
              <td><a href="../help/capabilities.html">help</a></td>
              <% } %>
            </tr>
          </table>
          <p><input type="submit" value="Update the registry entry"/></p>
        </form>
      </div>
     <%@ include file="/style/footer.xml" %>
    </body>
</html>

