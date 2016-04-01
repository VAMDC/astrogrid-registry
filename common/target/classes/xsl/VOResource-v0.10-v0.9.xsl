<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.ivoa.net/xml/VOResource/v0.9" 
                xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.9" 
                xmlns:vc="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.2" 
                xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.4" 
                xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.2" 
                xmlns:sia="http://www.ivoa.net/xml/SIA/v0.6"                 
                xmlns:cea="http://www.ivoa.net/xml/CEAService/v0.1"
                xmlns:ceapd="http://www.astrogrid.org/schema/AGParameterDefinition/v1"
                xmlns:ceab="http://www.astrogrid.org/schema/CommonExecutionArchitectureBase/v1"                
                xmlns:vor="http://www.ivoa.net/xml/RegistryInterface/v0.1"                
                xmlns:vc2="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:vg2="http://www.ivoa.net/xml/VORegistry/v0.3" 
                xmlns:vr2="http://www.ivoa.net/xml/VOResource/v0.10" 
                xmlns:vs2="http://www.ivoa.net/xml/VODataService/v0.5" 
                xmlns:cea2="http://www.ivoa.net/xml/CEAService/v0.2"
                xmlns:cs2="http://www.ivoa.net/xml/ConeSearch/v0.3" 
                xmlns:sia2="http://www.ivoa.net/xml/SIA/v0.7" 
                xmlns:tdb="urn:astrogrid:schema:vo-resource-types:TabularDB:v0.3"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                exclude-result-prefixes="vr2 vc2 vg2 vs2 cs2 sia2 vor cea2" 
                version="1.0">

   <!-- 
     -  Stylesheet to convert VOResource-v0.10 to VOResource-v0.9
     -  Version 1.0 - Initial Revision
     -    Ramon Williamson, National Center for SuperComputing Applications
     -    July 1, 2004
     -->
   <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

   <xsl:variable name="cr"><xsl:text>
</xsl:text></xsl:variable>

   <!--
     -  the per-level indentation.  Set this to a sequence of spaces.
     -->
   <xsl:param name="indent">
      <xsl:for-each select="/*/*[2]">
         <xsl:call-template name="getindent"/>
      </xsl:for-each>
   </xsl:param>

   <!--
     -  The prefix to prepend to schema files listed in the xsi:schemaLocation
     -  (if used).  The value should include a trailing slash as necessary.
     -  The default is an empty string, which indicates the current working 
     -  directory (where output is used).  Note that the xsi:schemaLocation 
     -  is only set if it is set on the input.
     -->
   <xsl:param name="schemaLocationPrefix"/>

   <!--
     -  Set to 1 if the xsi:schemaLocation should be set or zero if it should
     -  not be.  If not set at all (default), xsi:schemaLocation is only set 
     -  if it is set on the input.
     -->
   <xsl:param name="setSchemaLocation"/>

   <xsl:variable name="setSL">
      <xsl:choose>
         <xsl:when test="$setSchemaLocation=''">
            <xsl:choose>
               <xsl:when test="/*/@xsi:schemaLocation">
                  <xsl:copy-of select="1"/>
               </xsl:when>
               <xsl:otherwise><xsl:copy-of select="0"/></xsl:otherwise>
            </xsl:choose>
         </xsl:when>
         <xsl:otherwise>
            <xsl:copy-of select="number($setSchemaLocation)"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <!--
     -  determine the indentation preceding the context element
     -->
   <xsl:template name="getindent">
      <xsl:choose>
         <xsl:when test="contains(
                           preceding-sibling::text()[position()=last()],$cr)">
            <xsl:value-of 
              select="substring-after(
                           preceding-sibling::text()[position()=last()],$cr)"/>
         </xsl:when>
         <xsl:when test="preceding-sibling::text()[position()=last()]">
            <xsl:value-of 
              select="preceding-sibling::text()[position()=last()]"/>
         </xsl:when>
         <xsl:otherwise><xsl:text>    </xsl:text></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--
     -  indent the given number of levels.  The amount of indentation will 
     -  be nlev times the value of the global $indent.
     -  @param nlev   the number of indentations to insert.
     -->
   <xsl:template name="doindent">
      <xsl:param name="nlev" select="2"/>
      <xsl:if test="$nlev &gt; 0">
         <xsl:value-of select="$indent"/>
         <xsl:if test="$nlev &gt; 1">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$nlev - 1"/>
            </xsl:call-template>
         </xsl:if>
      </xsl:if>
   </xsl:template>

   <!--
     -  convert the first character to a lower case
     -  @param in  the string to convert
     -->
   <xsl:template name="capitalize">
      <xsl:param name="in"/>
      <xsl:value-of select="translate(substring($in,1,1),
                                      'abcdefghijklmnopqrstuvwxyz',
                                      'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      <xsl:value-of select="substring($in,2)"/>
   </xsl:template>

   <xsl:template match="vor:VOResources">
      <vr:VODescription>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.9 </xsl:text>
               <xsl:text>VOResource-v0.9.xsd</xsl:text>
               <xsl:if test="contains(*/@xsi:type,'Organisation')">
                  <xsl:text> http://www.ivoa.net/xml/VOCommunity/v0.2</xsl:text>
                  <xsl:text> VOCommunity-v0.2.xsd</xsl:text>
               </xsl:if>
               <xsl:if test="contains(*/@xsi:type,'Registry') or 
                             contains(*/@xsi:type,'Authority')">
                  <xsl:text> http://www.ivoa.net/xml/VORegistry/v0.2</xsl:text>
                  <xsl:text> VORegistry-v0.2.xsd</xsl:text>
               </xsl:if>
               <xsl:if test="contains(*/@xsi:type,'Service') or 
                             contains(*/@xsi:type,'DataCollection')">
                <xsl:text> http://www.ivoa.net/xml/VODataService/v0.4</xsl:text>
                <xsl:text> VODataService-v0.4.xsd</xsl:text>
                <xsl:text> http://www.ivoa.net/xml/VOTable/v1.0</xsl:text>
                <xsl:text> VOTable.xsd</xsl:text>
               </xsl:if>
               <xsl:if test="contains(*/@xsi:type,'ConeSearch')">
                <xsl:text> http://www.ivoa.net/xml/VODataService/v0.4</xsl:text>
                <xsl:text> VODataService-v0.4.xsd</xsl:text>
                <xsl:text> http://www.ivoa.net/xml/VOTable/v1.0</xsl:text>
                <xsl:text> VOTable.xsd</xsl:text>
                  <xsl:text> http://www.ivoa.net/xml/ConeSearch/v0.2</xsl:text>
                  <xsl:text> ConeSearch-v0.2.xsd</xsl:text>
               </xsl:if>
               <xsl:if test="contains(*/@xsi:type,'SimpleImageAccess')">
                <xsl:text> http://www.ivoa.net/xml/VODataService/v0.4</xsl:text>
                <xsl:text> VODataService-v0.4.xsd</xsl:text>
                <xsl:text> http://www.ivoa.net/xml/VOTable/v1.0</xsl:text>
                <xsl:text> VOTable.xsd</xsl:text>
                  <xsl:text> http://www.ivoa.net/xml/SIA/v0.6</xsl:text>
                  <xsl:text> SIA-v0.6.xsd</xsl:text>
               </xsl:if>
            </xsl:attribute>
         </xsl:if>
         
         <xsl:apply-templates select="@*|node()"/>
         <!--         
         <xsl:apply-templates select="@*|node()"/>
  
         <xsl:value-of select="$cr"/>


         <xsl:apply-templates select="vor:Resource"/>
         <xsl:apply-templates select="vr2:resource"/>
        
         <xsl:apply-templates select="resource"/>
 -->
         
      </vr:VODescription>
      <xsl:value-of select="$cr"/>
   </xsl:template>
   
   
       <xsl:template match="@*|node()">
           <xsl:copy>
               <xsl:apply-templates select="@*|node()"/>
           </xsl:copy>
       </xsl:template>
   

   <!--
     -  add record maintenance attributes to resource element 
     -->
   <xsl:template name="setResourceAttrs">
      <xsl:if test="@created">
         <xsl:attribute name="created">
            <xsl:value-of select="@created"/>
         </xsl:attribute>
      </xsl:if>
      <xsl:if test="@updated">
         <xsl:attribute name="updated">
            <xsl:value-of select="@updated"/>
         </xsl:attribute>
      </xsl:if>
      <xsl:if test="@status">
         <xsl:attribute name="status">
            <xsl:value-of select="@status"/>
         </xsl:attribute>
      </xsl:if>
   </xsl:template>

   <!--Resource templates-->
   <!--Generic Resource -->
   
   <xsl:template match="*[@xsi:type='Resource' or @xsi:type='vr:Resource']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--Organisation = Resource + Facility + Instrument-->
   <xsl:template match="*[@xsi:type='Organisation' or 
                          @xsi:type='vr:Organisation']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
            <vr:Resource xsi:type="vc:OrganisationType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:facility"/>
         <xsl:apply-templates select="vr2:instrument"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!-- Authority = Resource +ManagingOrg-->
   <xsl:template match="*[@xsi:type='Authority' or 
                          @xsi:type='vg:Authority']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vg:AuthorityType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vg2:managingOrg"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--Registry = authority + interface -->
   
   <xsl:template match="*[@xsi:type='CeaApplicationType' or 
                          @xsi:type='cea:CeaApplicationType']">
      <xsl:call-template name="doindent">      
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="cea:CeaApplicationType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <xsl:apply-templates select="cea2:ApplicationDefinition"/>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
       </vr:Resource>
   </xsl:template>

   <xsl:template match="*[@xsi:type='cea:CeaHttpApplicationType' or
                          @xsi:type='CeaHttpApplicationType']">
      <xsl:call-template name="doindent">      
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="cea:CeaHttpApplicationType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <xsl:apply-templates select="cea2:ApplicationDefinition"/>
         <xsl:apply-templates select="cea2:CeaHttpAdapterSetup"/>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
       </vr:Resource>
   </xsl:template>

   
   <xsl:template match="cea2:ApplicationDefinition">
         <cea:ApplicationDefinition>
            <xsl:apply-templates select="@*|node()"/>
         </cea:ApplicationDefinition>
   </xsl:template>

   <xsl:template match="cea2:CeaHttpAdapterSetup">
         <cea:CeaHttpAdapterSetup>
            <xsl:apply-templates select="@*|node()"/>
         </cea:CeaHttpAdapterSetup>
   </xsl:template>

   <xsl:template match="cea2:ApplicationKind">
         <cea:ApplicationKind>
            <xsl:apply-templates select="@*|node()"/>
         </cea:ApplicationKind>
   </xsl:template>


   <xsl:template match="cea2:Parameters">
         <cea:Parameters>
            <xsl:apply-templates select="@*|node()"/>
         </cea:Parameters>
   </xsl:template>

   <xsl:template match="cea2:ParameterDefinition">
         <cea:ParameterDefinition>
            <xsl:apply-templates select="@*|node()"/>
         </cea:ParameterDefinition>
   </xsl:template>

   <xsl:template match="cea2:Interfaces">
         <cea:Interfaces>
            <xsl:apply-templates select="@*|node()"/>
         </cea:Interfaces>
   </xsl:template>
   
   <xsl:template match="cea2:ManagedApplications">
            <cea:ManagedApplications>
               <xsl:apply-templates select="@*|node()"/>
            </cea:ManagedApplications>
   </xsl:template>
   
   
   <xsl:template match="cea2:ApplicationReference">
      <xsl:variable name="newident" select="." />   
      <!--
      <xsl:text>begin new ident</xsl:text>
      <xsl:value-of select="$newident"/>
      <xsl:text>end new ident</xsl:text>
      
      <xsl:text>begin old ident</xsl:text>
      -->
      <xsl:variable name="oldident" select="substring-after($newident,'ivo://')" /> 
      
      <!--
      <xsl:value-of select="$oldident"/>
      
      <xsl:text>end old ident</xsl:text>
      -->
      <cea:ApplicationReference>
         <vr:AuthorityID>
            <xsl:value-of select="substring-before($oldident,'/')" />
         </vr:AuthorityID>
         <vr:ResourceKey>
            <xsl:value-of select="substring-after($oldident,'/')" />
         </vr:ResourceKey>    
      </cea:ApplicationReference>
         <!--
      <xsl:apply-templates select="@*|node()"/>
   
      <xsl:variable name="oldident" select="substring-after(" />
            <cea:ManagedApplications>
               <xsl:apply-templates select="@*|node()"/>
            </cea:ManagedApplications>
      -->
   </xsl:template>   
   
   <xsl:template match="*[@xsi:type='CeaServiceType' or 
                          @xsi:type='cea:CeaServiceType']">
      <xsl:call-template name="doindent">      
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="cea:CeaServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <Interface>
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="3"/>
            </xsl:call-template>
            <Invocation>WebService</Invocation>
            <xsl:value-of select="$cr"/>
            <xsl:apply-templates select="vr2:interface/vr2:accessURL">
               <xsl:with-param name="defuse">full</xsl:with-param>
            </xsl:apply-templates>
            <xsl:call-template name="doindent"/>
         </Interface>
         <xsl:apply-templates select="cea2:ManagedApplications"/>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
       </vr:Resource>
   </xsl:template>
   
   

   <xsl:template match="*[@xsi:type='Registry' or 
                          @xsi:type='vg:Registry']">
      <xsl:call-template name="doindent">      
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vg:RegistryType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <Interface>
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="3"/>
            </xsl:call-template>
            <Invocation>WebService</Invocation>
            <xsl:value-of select="$cr"/>
            <xsl:apply-templates select="vr2:interface/vr2:accessURL">
               <xsl:with-param name="defuse">full</xsl:with-param>
            </xsl:apply-templates>
            <xsl:call-template name="doindent"/>
         </Interface>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vg2:managedAuthority">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--service = resource + interface-->
   <xsl:template match="*[@xsi:type='Service' or @xsi:type='vr:Service']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vr:ServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <xsl:apply-templates select="vr2:interface" />
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <xsl:template match="*[@xsi:type='SkyService' or @xsi:type='vs:SkyService']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:SkyServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:apply-templates select="vr2:interface"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--ConeSearch-->
   <!--   Conesearch = TabularSkyService + CS parameters -->
   <xsl:template match="*[@xsi:type='ConeSearch' or @xsi:type='cs:ConeSearch']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:TabularSkyServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="cs2:capability"/>
         <xsl:apply-templates select="vr2:interface"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:apply-templates select="vs2:table"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!-- Generic TabularSkyService -->
   <!-- no extra params, interface like SkyService, 
     -  just added Table parameters
     -->
   <xsl:template match="*[@xsi:type='TabularSkyService' or 
                          @xsi:type='vs:TabularSkyService']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:TabularSkyServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:interface"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:apply-templates select="vs2:table"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <xsl:template match="*[@xsi:type='TabularDB' or 
                          @xsi:type='tdb:TabularDB']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:TabularSkyServiceType">
         <xsl:call-template name="setResourceAttrs"/>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:interface"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:apply-templates select="tdb:db/tdb:table"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <xsl:template match="tdb:db">
		<xsl:apply-templates select="@*|node()"/>
   </xsl:template>




   <xsl:template match="*[@xsi:type='DataCollection' or 
                          @xsi:type='vs:DataCollection']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:DataCollectionType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:call-template name="doindent"/>
         <vs:Access>
            <xsl:value-of select="$cr"/>
            <xsl:apply-templates select="vs2:format"/>
            <xsl:apply-templates select="vs2:rights"/>
            <xsl:apply-templates select="vs2:accessURL"/>
            <xsl:call-template name="doindent"/>
         </vs:Access>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--   SIA = TabularSkyService + SIA parameters -->
   <xsl:template match="*[@xsi:type='sia:SimpleImageAccess' or 
                          @xsi:type='SimpleImageAccess']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <vr:Resource xsi:type="vs:TabularSkyServiceType">
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="sia2:capability"/>
         <xsl:apply-templates select="vr2:interface"/>
         <xsl:apply-templates select="vs2:facility"/>
         <xsl:apply-templates select="vs2:instrument"/>
         <xsl:call-template name="doCoverage"/>
         <xsl:apply-templates select="vs2:table"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </vr:Resource>
   </xsl:template>

   <!--End of resources-->

   <!--
     -  a generic template for converting a simple element
     -  @context   an element with only text content (and no attributes)
     -->
   <xsl:template name="TextElement">
      <xsl:param name="element" select="concat(local-name(),'-ERROR')"/>
      <xsl:element name="{$element}">
         <xsl:value-of select="."/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="*[child::text() and not(attribute::*) and not(*)]"
                 priority="-1">
      <xsl:param name="indlev" select="3"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>

      <xsl:call-template name="TextElement">
         <xsl:with-param name="element">
            <xsl:call-template name="capitalize">
               <xsl:with-param name="in" select="local-name()"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>

      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:identifier">
      <xsl:call-template name="doindent"/>
      <Identifier>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertIDURI">
            <xsl:with-param name="str" select="."/>
         </xsl:call-template>
         <xsl:call-template name="doindent"/>
      </Identifier>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:title">
      <xsl:param name="indlev" select="2"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <Title>
         <xsl:value-of select="."/>
      </Title>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:shortName">
      <xsl:call-template name="doindent"/>
      <ShortName>
         <xsl:value-of select="."/>
      </ShortName>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!-- Curation -->
   <xsl:template match="vr2:curation">
      <xsl:call-template name="doindent"/>
      <Curation>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:publisher"/>
         <xsl:apply-templates select="vr2:contact"/>
         <xsl:apply-templates select="vr2:date"/>
         <xsl:apply-templates select="vr2:creator"/>
         <xsl:apply-templates select="vr2:contributor"/>
         <xsl:apply-templates select="vr2:version"/>
         <xsl:call-template name="doindent"/>
      </Curation>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  convert an element that is a reference to another resource
     -  @context  element of ResourceName to be converted 
     -  @param element   the output element name
     -  @param nameel    output element to contain the resource's name, 
     -                     either "Title" or "Name".  default=Name
     -  @param indlev    the indentation level
     -->
   <xsl:template name="ResourceReference">
      <xsl:param name="element"/>
      <xsl:param name="nameel">Name</xsl:param>
      <xsl:param name="indlev" select="3"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="{$element}">
         <xsl:call-template name="ResourceReferenceType">
            <xsl:with-param name="nameel" select="$nameel"/>
            <xsl:with-param name="indlev" select="number($indlev)+1"/>
         </xsl:call-template>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="$indlev"/>
         </xsl:call-template>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  create the content for a ResourceReferenceType
     -  @context  a v0.9 element containing an ivo-id attr and a title element 
     -  @param nameel    output element to contain the resource's name, 
     -                     either "Title" or "Name".  default=Name
     -  @param indlev    the indentation level
     -->   
   <xsl:template name="ResourceReferenceType">
      <xsl:param name="nameel">Name</xsl:param>
      <xsl:param name="indlev" select="4"/>

      <xsl:value-of select="$cr"/>
      <xsl:if test="@ivo-id">
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="$indlev"/>
         </xsl:call-template>
         <Identifier>
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="convertIDURI">
               <xsl:with-param name="str" select="@ivo-id"/>
               <xsl:with-param name="indlev" select="number($indlev)+1"/>
            </xsl:call-template>
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$indlev"/>
            </xsl:call-template>
         </Identifier>
         <xsl:value-of select="$cr"/>
      </xsl:if>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="{$nameel}">
         <xsl:value-of select="."/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:publisher">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">Publisher</xsl:with-param>
         <xsl:with-param name="nameel">Title</xsl:with-param>
         <xsl:with-param name="indlev" select="3"/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:creator">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Creator>
         <xsl:for-each select="vr2:name">
            <xsl:call-template name="ResourceReferenceType"/>
         </xsl:for-each>
         <xsl:apply-templates select="vr2:logo">
            <xsl:with-param name="indlev" select="4"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </Creator>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:contributor">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">Contributor</xsl:with-param>
         <xsl:with-param name="indlev" select="3"/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:date">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <xsl:element name="Date">
         <xsl:if test="@role">
            <xsl:attribute name="role">
               <xsl:value-of select="@role"/>
            </xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:contact">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Contact>
         <xsl:for-each select="vr2:name">
            <xsl:call-template name="ResourceReferenceType"/>
         </xsl:for-each>
         <xsl:apply-templates select="vr2:email">
            <xsl:with-param name="indlev" select="4"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </Contact>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:source">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Source>
         <xsl:attribute name="format">
            <xsl:value-of select="@format"/>
         </xsl:attribute>
         <xsl:value-of select="."/>
      </Source>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:type">
      <xsl:param name="indlev" select="2"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <Type>   
         <xsl:choose>
            <xsl:when test=".='Registry'">
               <!-- this type not part of v0.9 spec -->
               <xsl:text>Other</xsl:text>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
         </xsl:choose>
      </Type>
      <xsl:value-of select="$cr"/> 
   </xsl:template>

   <xsl:template match="vr2:subject">
      <Subject>
         <xsl:value-of select="."/>
      </Subject>
   </xsl:template>

   <xsl:template match="vr2:email">
      <Email>
         <xsl:value-of select="."/>
      </Email>
   </xsl:template>

   <xsl:template match="vr2:relatedResource">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">RelatedTo</xsl:with-param>
         <xsl:with-param name="nameel">Title</xsl:with-param>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:relationshipType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Relationship><xsl:value-of select="."/></Relationship>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:relationship">
      <xsl:call-template name="doindent"/>
      <RelatedResource>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:relationshipType" />
         <xsl:apply-templates select="vr2:relatedResource" />
         <xsl:call-template name="doindent"/>
      </RelatedResource>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:facility|vs2:facility">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">Facility</xsl:with-param>
         <xsl:with-param name="nameel">Title</xsl:with-param>
         <xsl:with-param name="indlev" select="2"/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:instrument|vs2:instrument">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">Instrument</xsl:with-param>
         <xsl:with-param name="nameel">Title</xsl:with-param>
         <xsl:with-param name="indlev" select="2"/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='WebBrowser' or 
                                      @xsi:type='vr:WebBrowser']">
      <xsl:call-template name="doindent"/>
      <Interface>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <Invocation>WebBrowser</Invocation>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:accessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
         </xsl:apply-templates>
         <xsl:call-template name="doindent"/>
      </Interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='GLUService' or 
                                      @xsi:type='vs:GLUService']">
      <xsl:call-template name="doindent"/>
      <Interface>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <Invocation>GLUService</Invocation>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:accessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
         </xsl:apply-templates>
         <xsl:call-template name="doindent"/>
      </Interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='WebService' or 
                                      @xsi:type='vs:WebService']">
      <xsl:call-template name="doindent"/>
      <Interface>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <Invocation>WebService</Invocation>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:accessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
         </xsl:apply-templates>
         <xsl:call-template name="doindent"/>
      </Interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vg2:managingOrg">
      <xsl:call-template name="doindent"/>
      <vg:ManagingOrg>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertIDURI">
            <xsl:with-param name="str" select="@ivo-id"/>
         </xsl:call-template>
         <xsl:call-template name="doindent"/>
      </vg:ManagingOrg>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vg2:managedAuthority">
      <xsl:call-template name="doindent"/>
      <vg:ManagedAuthority><xsl:value-of select="."/></vg:ManagedAuthority>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:format">
      <xsl:param name="indlev" select="3"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="vs:Format">
         <xsl:choose>
            <xsl:when test="@isMIMEType">
               <xsl:attribute name="isMIMEType">
                  <xsl:value-of select="@isMIMEType"/>
               </xsl:attribute>
            </xsl:when>
            <xsl:when test="contains(.,'/')">
               <xsl:attribute name="isMIMEType">true</xsl:attribute>
            </xsl:when>
         </xsl:choose>
         <xsl:value-of select="."/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:rights">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <vs:Rights>
         <xsl:value-of select="."/>
      </vs:Rights>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:accessURL">
      <xsl:param name="defuse" select="''"/>
      <xsl:param name="indlev" select="3"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <AccessURL>
         <xsl:if test="@use or $defuse != ''">
            <xsl:attribute name="use">
               <xsl:choose>
                  <xsl:when test="@use">
                     <xsl:value-of select="@use"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$defuse"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
      </AccessURL>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='ParamHTTP' or 
                                      @xsi:type='vs:ParamHTTP']">
      <xsl:call-template name="doindent"/>
      <ParamHTTP xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:if test="@qtype">
            <xsl:attribute name="qtype"><xsl:value-of select="@qtype"/></xsl:attribute>
         </xsl:if>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>

         <vr:Invocation>Extended</vr:Invocation>

         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:accessURL">
            <xsl:with-param name="defuse">base</xsl:with-param>
         </xsl:apply-templates>
         <xsl:apply-templates select="vs2:resultType"/>
         <xsl:apply-templates select="vs2:param"/>
         <xsl:call-template name="doindent"/>
      </ParamHTTP>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:resultType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <HTTPResults xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </HTTPResults>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:param">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Param xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="dovsName">
            <xsl:with-param name="node" select="vs2:name"/>
         </xsl:call-template>
         <xsl:apply-templates select="vs2:description"/>
         <xsl:apply-templates select="vs2:dataType"/>
         <xsl:apply-templates select="vs2:unit"/>
         <xsl:apply-templates select="vs2:ucd"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </Param>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:coverage">
      <xsl:call-template name="doCoverage">
         <xsl:with-param name="node" select="."/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template name="doCoverage">
      <xsl:param name="node" select="vs2:coverage"/>

      <xsl:choose>
         <xsl:when test="not($node)">
            <xsl:call-template name="doindent"/>
            <vs:Coverage/>
            <xsl:value-of select="$cr"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:for-each select="$node">
               <xsl:call-template name="doindent"/>
               <vs:Coverage xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
                  <xsl:value-of select="$cr"/>
                  <xsl:apply-templates select="vs2:spatial"/>
                  <xsl:apply-templates select="vs2:spectral"/>
                  <xsl:apply-templates select="vs2:temporal"/>
                  <xsl:call-template name="doindent"/>
               </vs:Coverage>
               <xsl:value-of select="$cr"/>
            </xsl:for-each>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template match="vs2:spatial">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <vs:Spatial xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:region"/>
         <xsl:if test="vs2:resolution">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="4"/>
            </xsl:call-template>
            <vs:SpatialResolution>
               <xsl:value-of select="vs2:resolution"/>
            </vs:SpatialResolution>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:apply-templates select="vs2:regionOfRegard"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </vs:Spatial>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:region[@xsi:type='AllSky' or 
                                   @xsi:type='vs:AllSky']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:AllSky xmlns="http://www.ivoa.net/xml/VODataService/v0.4"/>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:region[@xsi:type='CoordRange' or 
                                   @xsi:type='vs:CoordRange']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:CoordRange xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:coordFrame"/>
         <xsl:apply-templates select="vs2:long"/>
         <xsl:apply-templates select="vs2:lat"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
      </vs:CoordRange>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="*[@xsi:type='CircleRegion' or 
                          @xsi:type='vs:CircleRegion']">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:CircleRegion xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:coordFrame"/>
         <xsl:apply-templates select="vs2:center"/>
         <xsl:apply-templates select="vs2:radius"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
      </vs:CircleRegion>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:coordFrame">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="5"/>
      </xsl:call-template>
      <vs:CoordFrame xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:CoordFrame>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:long">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="5"/>
      </xsl:call-template>
      <vs:long xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:min>
            <xsl:value-of select="vs2:min"/>
         </vs:min>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:max>
            <xsl:value-of select="vs2:max"/>
         </vs:max>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
      </vs:long>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:lat">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="5"/>
      </xsl:call-template>
      <vs:lat xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:min>
            <xsl:value-of select="vs2:min"/>
         </vs:min>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:max>
            <xsl:value-of select="vs2:max"/>
         </vs:max>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
      </vs:lat>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:center">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="5"/>
      </xsl:call-template>
      <vs:CenterPosition xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:long>
            <xsl:value-of select="vs2:long"/>
         </vs:long>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="6"/>
         </xsl:call-template>
         <vs:lat>
            <xsl:value-of select="vs2:lat"/>
         </vs:lat>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
      </vs:CenterPosition>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:radius">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="5"/>
      </xsl:call-template>
      <vs:radius xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:radius>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:regionOfRegard">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:RegionOfRegard xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:RegionOfRegard>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:spectral">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <vs:Spectral xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:waveband"/>
         <xsl:apply-templates select="vs2:range"/>
         <xsl:if test="vs2:resolution">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="4"/>
            </xsl:call-template>
            <vs:SpectralResolution>
               <xsl:value-of select="vs2:resolution"/>
            </vs:SpectralResolution>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </vs:Spectral>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:waveband">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:Waveband xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:Waveband>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:range">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:WavelengthRange xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
         <min>
            <xsl:value-of select="vs2:min"/>
         </min>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
         <max>
            <xsl:value-of select="vs2:max"/>
         </max>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
      </vs:WavelengthRange>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:temporal">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <vs:Temporal xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:startTime"/>
         <xsl:apply-templates select="vs2:endTime"/>
         <xsl:if test="vs2:resolution">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="4"/>
            </xsl:call-template>
            <vs:TemporalResolution>
               <xsl:value-of select="vs2:resolution"/>
            </vs:TemporalResolution>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </vs:Temporal>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:startTime">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:StartTime xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:StartTime>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:endTime">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:EndTime xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vs:EndTime>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!-- Template to convert generic Resource elements -->
   <xsl:template name="convertResource">
      <xsl:apply-templates select="vr2:identifier"/>
      <xsl:apply-templates select="vr2:title"/>
      <xsl:apply-templates select="vr2:shortName"/>
      <xsl:call-template name="summary"/>
      <xsl:apply-templates select="vr2:content/vr2:type">
         <xsl:with-param name="indlev" select="2"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="vr2:content/vr2:relationship"/>
      <xsl:apply-templates select="vr2:curation"/>
      <xsl:apply-templates select="vr2:subject"/>
      <xsl:apply-templates select="vr2:content/vr2:contentLevel">
         <xsl:with-param name="indlev" select="2"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="vs2:table">
      <xsl:call-template name="doindent"/>
      <Table xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:if test="@role">
            <xsl:attribute name="role"><xsl:value-of select="@role"/></xsl:attribute>
         </xsl:if>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertTable"/>
         <xsl:call-template name="doindent"/>
      </Table>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="tdb:table">
      <xsl:call-template name="doindent"/>
      <Table xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:if test="@role">
            <xsl:attribute name="role"><xsl:value-of select="@role"/></xsl:attribute>
         </xsl:if>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertTable"/>
         <xsl:call-template name="doindent"/>
      </Table>
      <xsl:value-of select="$cr"/>
   </xsl:template>


   <xsl:template match="vs2:column">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <Column xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="dovsName">
            <xsl:with-param name="node" select="vs2:name"/>
         </xsl:call-template>
         <xsl:apply-templates select="vs2:description"/>
         <xsl:apply-templates select="vs2:dataType"/>
         <xsl:apply-templates select="vs2:unit"/>
         <xsl:apply-templates select="vs2:ucd"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </Column>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:dataType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <DataType xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:if test="@arraysize">
            <xsl:attribute name="arraysize"><xsl:value-of select="@arraysize"/></xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
      </DataType>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:name">
      <xsl:call-template name="dovsName"/>
   </xsl:template>

   <xsl:template name="dovsName">
      <xsl:param name="node" select="."/>
      <xsl:param name="indlev" select="4"/>

      <xsl:for-each select="$node">
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="$indlev"/>
         </xsl:call-template>
         <vr:Name xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
            <xsl:value-of select="."/>
         </vr:Name>
         <xsl:value-of select="$cr"/>
      </xsl:for-each>
   </xsl:template>

   <xsl:template match="vs2:description">
      <xsl:param name="indlev" select="4"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <vr:Description xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </vr:Description>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:unit">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <Unit xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </Unit>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:ucd">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <UCD xmlns="http://www.ivoa.net/xml/VODataService/v0.4">
         <xsl:value-of select="."/>
      </UCD>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="cs2:capability">
      <xsl:call-template name="doindent"/>
      <cs:ConeSearch>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>

         <cs:MaxSR>
            <xsl:value-of select="cs2:maxSR"/>
         </cs:MaxSR>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>

         <cs:MaxRecords>
            <xsl:value-of select="cs2:maxRecords"/>
         </cs:MaxRecords>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>

         <cs:Verbosity>
            <xsl:value-of select="cs2:verbosity"/>
         </cs:Verbosity>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </cs:ConeSearch>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:capability">
      <xsl:call-template name="doindent"/>
      <sia:SimpleImageAccess xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="sia2:imageServiceType"/>
         <xsl:apply-templates select="sia2:maxQueryRegionSize"/>
         <xsl:apply-templates select="sia2:maxImageExtent"/>
         <xsl:apply-templates select="sia2:maxImageSize"/>
         <xsl:apply-templates select="sia2:maxFileSize"/>
         <xsl:apply-templates select="sia2:maxRecords"/>
         <xsl:call-template name="doindent"/>
      </sia:SimpleImageAccess>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:imageServiceType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <ImageServiceType xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="."/>
      </ImageServiceType>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:maxQueryRegionSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <MaxQueryRegionSize xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <long>
            <xsl:value-of select="sia2:long"/>
         </long>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </MaxQueryRegionSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:maxImageExtent">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <MaxImageExtent xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <long>
            <xsl:value-of select="sia2:long"/>
         </long>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </MaxImageExtent>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:maxImageSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <MaxImageSize xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <long>
            <xsl:value-of select="sia2:long"/>
         </long>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>

         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>

         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </MaxImageSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:maxFileSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <MaxFileSize xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="."/>
      </MaxFileSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:maxRecords">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <MaxRecords xmlns="http://www.ivoa.net/xml/SIA/v0.6">
         <xsl:value-of select="."/>
      </MaxRecords>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--Named templates-->
   <xsl:template name="convertRegion">
      <xsl:apply-templates select="vs2:allSky"/>
      <xsl:apply-templates select="vs2:coordRange"/>
      <xsl:apply-templates select="vs2:circleRegion"/>
   </xsl:template>

   <xsl:template name="convertTable">
      <xsl:apply-templates select="vs2:name" />
      <xsl:apply-templates select="vs2:description">
         <xsl:with-param name="indlev" select="3"/>
      </xsl:apply-templates>
      <xsl:apply-templates select="vs2:column"/>
   </xsl:template>

   <xsl:template name="convertIDURI">
      <xsl:param name="str"/>
      <xsl:param name="indlev" select="3"/>

      <!--Identifier of form ivo://Authority/ResourceKey-->
      <xsl:variable name="idString" select="substring-after($str, 'ivo://')"/>

      <xsl:choose>
         <xsl:when test="contains($idString, '/')">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$indlev"/>
            </xsl:call-template>
            <AuthorityID>
               <xsl:value-of select="substring-before($idString,'/')"/>
            </AuthorityID>
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$indlev"/>
            </xsl:call-template>

            <xsl:variable name="rkString" 
                          select="substring-after($idString,'/')"/>
            <xsl:if test="string-length($rkString) >0">
               <ResourceKey>
                  <xsl:value-of select="substring-after($idString,'/')"/>
               </ResourceKey>
            </xsl:if>
            <xsl:if test="$rkString = ''">
               <ResourceKey xsi:nil="true"/>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:if test="string-length($idString) > 0">
               <xsl:call-template name="doindent">
                  <xsl:with-param name="nlev" select="$indlev"/>
               </xsl:call-template>
               <AuthorityID>
                  <xsl:value-of select="$idString"/>
               </AuthorityID>
               <xsl:value-of select="$cr"/>
               <xsl:call-template name="doindent">
                  <xsl:with-param name="nlev" select="$indlev"/>
               </xsl:call-template>
               <ResourceKey xsi:nil="true"/>
               <xsl:value-of select="$cr"/>
            </xsl:if>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template name="summary">
      <xsl:call-template name="doindent"/>
      <Summary>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:content/vr2:description"/>
         <xsl:apply-templates select="vr2:content/vr2:referenceURL"/>
         <xsl:apply-templates select="vr2:content/vr2:source"/>
         <xsl:call-template name="doindent"/>
      </Summary>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:description">
      <Description>
         <xsl:value-of select="."/>
      </Description>
   </xsl:template>

   <xsl:template match="vr2:referenceURL">
      <ReferenceURL>
         <xsl:value-of select="."/>
      </ReferenceURL>
   </xsl:template>

</xsl:stylesheet>
