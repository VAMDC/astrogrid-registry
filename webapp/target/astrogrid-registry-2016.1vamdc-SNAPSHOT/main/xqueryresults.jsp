<%@ page import="org.astrogrid.registry.server.query.*,
                 org.astrogrid.registry.server.*,
                 org.astrogrid.registry.common.RegistryDOMHelper,
                 org.astrogrid.registry.server.http.servlets.helper.JSPHelper, 
                 org.astrogrid.store.Ivorn,
                 org.w3c.dom.Document,
                 org.astrogrid.io.Piper,
                 org.astrogrid.util.DomHelper,
                 org.astrogrid.config.SimpleConfig,
                 org.w3c.dom.NodeList,
                 org.w3c.dom.Element,  
                 javax.xml.stream.*,
                 javax.xml.parsers.DocumentBuilder,
                 javax.xml.parsers.DocumentBuilderFactory,
                 org.codehaus.xfire.util.STAXUtils,               
                 java.net.*,
                 java.util.*,
                 org.apache.commons.fileupload.*,                  
                 java.io.*"
         contentType="text/xml"                  
    	 session="false"
%>
<%
   if(request.getParameter("ResourceXQuery").trim().length() > 0) {  
       Document xqueryDOM;
       ISearch server = JSPHelper.getQueryService(request);
	   xqueryDOM = DomHelper.newDocument("<XQuery>" + request.getParameter("ResourceXQuery").trim() + "</XQuery>");
	   
       	XMLStreamReader reader = server.XQuerySearch(xqueryDOM);
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = factory.createXMLStreamWriter(out);
		STAXUtils.copy(reader,writer);

/*
             DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
             DocumentBuilder builder = dbf.newDocumentBuilder();
             Document entry = STAXUtils.read(builder,reader,true);         
          	if (entry == null) {
        		out.write("<p>No entry returned</p>");
      		}
      		else {      
        		out.write(DomHelper.ElementToString(entry.getDocumentElement()));
      		}    	
*/
	}else {
	  out.write("<p>Did not find anything in the request, no xquery given.  Hit back and put xquery in the text box.</p>");
	}
%>
