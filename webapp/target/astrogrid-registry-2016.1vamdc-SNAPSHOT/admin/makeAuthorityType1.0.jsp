<ri:Resource xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"  status="active" created="2000-01-01T09:00:00" updated="2000-01-01T09:00:00" xsi:type="vg:Authority">
 <title><%= request.getParameter("Title") %></title>
 <identifier>ivo://<%= request.getParameter("AuthorityID") %></identifier>
 <curation>
  <publisher><%= request.getParameter("Publisher") %></publisher>
  <contact>
   <name><%= request.getParameter("ContactName") %></name>
   <email><%= request.getParameter("ContactEmail") %></email>
  </contact>
 </curation>
 <content>
  <subject>Authority</subject>
  <description><%= request.getParameter("ContentDescription") %></description>
  <referenceURL><%= request.getParameter("ContentRefURL") %></referenceURL>
 </content>
 <managingOrg ivo-id="ivo://<%= org.astrogrid.config.SimpleConfig.getSingleton().getString("reg.amend.authorityid") %>/organisation"></managingOrg>
</ri:Resource>