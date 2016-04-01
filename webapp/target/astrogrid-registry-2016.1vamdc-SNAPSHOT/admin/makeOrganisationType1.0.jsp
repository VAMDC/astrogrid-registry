<ri:Resource xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" xsi:type="vr:Organisation"  status="active" created="2000-01-01T09:00:00" updated="2000-01-01T09:00:00">
 <title><%= request.getParameter("Title") %></title>
 <identifier>ivo://<%= request.getParameter("AuthorityID") %>/organisation</identifier>
 <curation>
  <publisher><%= request.getParameter("Publisher") %></publisher>
  <contact>
   <name><%= request.getParameter("ContactName") %></name>
   <email><%= request.getParameter("ContactEmail") %></email>
  </contact>
 </curation>
 <content>
  <subject>Organisation</subject>
  <description><%= request.getParameter("ContentDescription") %></description>
  <referenceURL><%= request.getParameter("ContentRefURL") %></referenceURL>
  <type>Organisation</type>
 </content>
 <facility><%= request.getParameter("Facility") %></facility>
</ri:Resource>