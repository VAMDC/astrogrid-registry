<ri:Resource xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" xsi:type="vg:Registry" status="active"  created="2000-01-01T09:00:00" updated="2000-01-01T09:00:00"  >
 <title><%= request.getParameter("Title") %></title>
 <identifier>ivo://<%= request.getParameter("AuthorityID") %>/org.astrogrid.registry.RegistryService</identifier>
 <curation>
  <publisher><%= request.getParameter("Publisher") %></publisher>
  <contact>
   <name><%= request.getParameter("ContactName") %></name>
   <email><%= request.getParameter("ContactEmail") %></email>
  </contact>
 </curation>
 <content>
  <subject>registry</subject>
  <description>Astrogrid Registry</description>
  <referenceURL><%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %></referenceURL>
  <type>Registry</type>
 </content>
 	  <% if(org.astrogrid.config.SimpleConfig.getSingleton().getString("reg.amend.oaipublish.1.0",null) != null) { %>
     <capability xsi:type="vg:Harvest"
                standardID="ivo://ivoa.net/std/Registry">
       <interface xsi:type="vg:OAIHTTP" role="std">
          <accessURL> <%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %>/OAIHandlerv1_0 </accessURL>
       </interface>
       <interface xsi:type="vg:OAISOAP" role="std">
          <accessURL> <%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %>/services/RegistryHarvestv1_0 </accessURL>
       </interface>       
       <!-- Max Records for OAI resources being returned a resumptionToken is given to the client if there are more
       whereby the client may page through all the OAI resources -->
       <maxRecords><%=org.astrogrid.config.SimpleConfig.getSingleton().getString("reg.amend.returncount")%></maxRecords>
    </capability>  
    <% } %>
        
    <capability xsi:type="vg:Search"
                standardID="ivo://ivoa.net/std/Registry">
       <interface xsi:type="vr:WebService" role="std">
         <accessURL><%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %>/services/RegistryQueryv1_0 </accessURL>
       </interface>
       <!-- Max Records if only for Search and KeywordSearch will return a max of this many records, but the client may still
       constrain things with a from and to parameters to page through all records.  XQuery has no constraints. -->
       <maxRecords><%= org.astrogrid.config.SimpleConfig.getSingleton().getString("reg.amend.returncount") %></maxRecords>
       <extensionSearchSupport>full</extensionSearchSupport>
       <optionalProtocol>XQuery</optionalProtocol>
    </capability>
    
    <full><%=request.getParameter("fullregistry") %></full>    
   <managedAuthority><%= request.getParameter("AuthorityID") %></managedAuthority>
</ri:Resource>