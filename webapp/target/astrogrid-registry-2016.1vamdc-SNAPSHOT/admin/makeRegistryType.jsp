<VODescription
  xmlns="http://www.ivoa.net/xml/VOResource/v0.9"
  xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.9"
  xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.2"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.ivoa.net/xml/VOResource/v0.9
                      http://www.ivoa.net/xml/VOResource/VOResource-v0.9.xsd
                      http://www.ivoa.net/xml/VORegistry/v0.2
                      http://www.ivoa.net/xml/VORegistry/VORegistry-v0.2.xsd">

 <vr:Resource xsi:type="vg:RegistryType" status="active" >
    <Identifier>
      <AuthorityID><%= request.getParameter("AuthorityID") %></AuthorityID>
      <ResourceKey>org.astrogrid.registry.RegistryService</ResourceKey>
    </Identifier>
    <Title><%= request.getParameter("Title") %></Title>
    <Summary>
      <Description>An AstroGrid Registry</Description>
      <ReferenceURL><%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %></ReferenceURL>
    </Summary>
    <Type>Project</Type>
    <Curation>
      <Publisher>
         <Title><%= request.getParameter("Publisher") %></Title>
      </Publisher>
      <Contact>
        <Name><%= request.getParameter("ContactName") %></Name>
       <Email><%= request.getParameter("ContactEmail") %></Email>
      </Contact>
    </Curation>
    <Interface>
      <Invocation>WebService</Invocation>
      <AccessURL use="full"><%= request.getScheme()+"://"+request.getServerName() +":" + request.getServerPort()+request.getContextPath() %>/services/RegistryQuery</AccessURL>
    </Interface>
    <vg:ManagedAuthority><%= request.getParameter("AuthorityID") %></vg:ManagedAuthority>    
  </vr:Resource>
</VODescription>