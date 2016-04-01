<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.ivoa.net/xml/VOResource/v0.10" 
                xmlns:vc="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:vg="http://www.ivoa.net/xml/VORegistry/v0.3" 
                xmlns:vr="http://www.ivoa.net/xml/VOResource/v0.10" 
                xmlns:vs="http://www.ivoa.net/xml/VODataService/v0.5" 
                xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v0.3" 
                xmlns:sia="http://www.ivoa.net/xml/SIA/v0.7" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:vr2="http://www.ivoa.net/xml/VOResource/v0.9" 
                xmlns:vc2="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:vg2="http://www.ivoa.net/xml/VORegistry/v0.2" 
                xmlns:vs2="http://www.ivoa.net/xml/VODataService/v0.4" 
                xmlns:cs2="http://www.ivoa.net/xml/ConeSearch/v0.2" 
                xmlns:sia2="http://www.ivoa.net/xml/SIA/v0.6" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                exclude-result-prefixes="vr2 vc2 vg2 vs2 cs2 sia2" 
                version="1.0">
   <!--   
     -  Stylesheet to convert VOResource-v0.9 to VOResource-v0.10-proposed
     -  Version 1.0 - Initial Revision
     -    Ramon Williamson, National Center for SuperComputing Applications
     -    June 23, 2004
     -  Version 1.1 
     -    Ray Plante, NCSA
     -->
   <xsl:output method="xml" encoding="UTF-8" indent="yes" />

   <!--
     -  the per-level indentation.  Set this to a sequence of spaces.
     -->
   <xsl:param name="indent">
      <xsl:for-each select="/*/*[1]">
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

   <xsl:variable name="cr"><xsl:text>
</xsl:text></xsl:variable>

   <xsl:template match="/">
      <xsl:apply-templates select="/*"/>
   </xsl:template>

   <!--
     -  determine the indentation preceding the context element
     -->
   <xsl:template name="getindent">
      <xsl:choose>
         <xsl:when test="contains(preceding-sibling::text(),$cr)">
            <xsl:value-of 
              select="substring-after(preceding-sibling::text(),$cr)"/>
         </xsl:when>
         <xsl:when test="preceding-sibling::text()">
            <xsl:value-of select="preceding-sibling::text()"/>
         </xsl:when>
         <xsl:otherwise><xsl:value-of select="''"/></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template match="/*">
      <xsl:apply-templates select="*"/>
   </xsl:template>

   <!--Resource templates-->
   <xsl:template match="vr2:Resource">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vr:Resource</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
      </xsl:element>
   </xsl:template>

   <!--service = resource + interface-->
   <xsl:template match="vr2:Service">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vr:Service</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>

         <xsl:if test="not(vr2:Interface)">
            <xsl:message terminate="yes">
                <xsl:text>Error: no Interface found for Service </xsl:text>
                <xsl:text>resource type</xsl:text>
            </xsl:message>
         </xsl:if>
         <xsl:apply-templates select="vr2:Interface|vr2:ParamHTTP" />
      </xsl:element>
   </xsl:template>

   <!--authority = service + managingOrg-->
   <xsl:template match="vg2:Authority">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vg:Authority</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VORegistry/v0.3 </xsl:text>
               <xsl:text>VORegistry-v0.3.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vg2:ManagingOrg"/>
      </xsl:element>
   </xsl:template>

   <!--registry = authority + interface -->
   <xsl:template match="vg2:Registry">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vg:Registry</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VORegistry/v0.3 </xsl:text>
               <xsl:text>VORegistry-v0.3.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="doindent"/>
         <vr:interface xsi:type="vs:WebService">
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="2"/>
            </xsl:call-template>
            <accessURL use="full">
               <xsl:value-of select="vr2:Interface/vr2:AccessURL"/>
            </accessURL>
            <xsl:value-of select="$cr"/>
            <xsl:call-template name="doindent"/>
         </vr:interface>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vg2:ManagedAuthority"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vc:Organisation">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vr:Organisation</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:Facility|vr2:Instrument">
            <xsl:with-param name="nsprefix">vr</xsl:with-param>
         </xsl:apply-templates>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vc:Project">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vr:Organisation</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:SkyService">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vs:SkyService</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
         <xsl:call-template name="SkyServiceInterface"/>
         <xsl:apply-templates select="vs2:Coverage"/>
      </xsl:element>
   </xsl:template>

  <!--
    -  TabularSkyService = SkyService + Table component
    -  SIA = TabularSkyService + SIA parameters
    -->
   <xsl:template match="vs2:TabularSkyService[sia2:SimpleImageAccess]">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">sia:SimpleImageAccess</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/SIA/v0.7 </xsl:text>
               <xsl:text>SIA-v0.7.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vs2:ParamHTTP"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
         <xsl:apply-templates select="vs2:Coverage"/>
         <xsl:apply-templates select="vs2:Table"/>
         <xsl:apply-templates select="sia2:SimpleImageAccess"/>
      </xsl:element>
   </xsl:template>

  <!--
    -  TabularSkyService = SkyService + Table component
    -  Conesearch = TabularSkyService + CS parameters 
    -->
   <xsl:template match="vs2:TabularSkyService[cs2:ConeSearch]">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">cs:ConeSearch</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/ConeSearch/v0.3 </xsl:text>
               <xsl:text>ConeSearch-v0.3.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vs2:ParamHTTP"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
         <xsl:apply-templates select="vs2:Coverage"/>
         <xsl:apply-templates select="vs2:Table"/>
         <xsl:apply-templates select="cs2:ConeSearch"/>
      </xsl:element>
   </xsl:template>

  <!--
    -  Generic TabularSkyService = SkyService + Table component
    -  no extra params, interface like SkyService, just added Table parameters
    -->
   <xsl:template match="vs2:TabularSkyService">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vs:TabularSkyService</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd </xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:call-template name="SkyServiceInterface"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
         <xsl:apply-templates select="vs2:Coverage"/>
         <xsl:apply-templates select="vs2:Table"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:DataCollection">
      <xsl:element name="resource">
         <xsl:attribute name="xsi:type">vs:DataCollection</xsl:attribute>
         <xsl:if test="$setSL=1">
            <xsl:attribute name="xsi:schemaLocation">
               <xsl:text>http://www.ivoa.net/xml/VOResource/v0.10 </xsl:text>
               <xsl:text>VOResource-v0.10.xsd </xsl:text>
               <xsl:text>http://www.ivoa.net/xml/VODataService/v0.5 </xsl:text>
               <xsl:text>VODataService-v0.5.xsd</xsl:text>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="setResourceAttrs"/>

         <xsl:value-of select="concat($cr,$cr)"/>
         <xsl:call-template name="convertResource"/>
         <xsl:apply-templates select="vr2:Facility"/>
         <xsl:apply-templates select="vr2:Instrument"/>
         <xsl:apply-templates select="vs2:Coverage"/>
         <xsl:apply-templates select="vs2:Access/vs2:Format"/>
         <xsl:apply-templates select="vs2:Access/vs2:Rights"/>
         <xsl:apply-templates select="vs2:Access/vs2:AccessURL"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vr2:Identifier">
      <xsl:call-template name="doindent"/>
      <identifier>
         <xsl:call-template name="IDString"/>
      </identifier>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  create the URI form of an IVOA identifier from the XML form.
     -  @context  an element of type IdentifierType
     -->
   <xsl:template name="IDString">
      <xsl:text>ivo://</xsl:text>
      <xsl:value-of select="vr2:AuthorityID"/>
      <xsl:if test="vr2:ResourceKey[not(@xsi:nil) or @xsi:nil!='true']">
         <xsl:text>/</xsl:text>
         <xsl:value-of select="vr2:ResourceKey"/>
      </xsl:if>
   </xsl:template>

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
      <xsl:param name="indlev" select="1"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>

      <xsl:call-template name="TextElement">
         <xsl:with-param name="element">
            <xsl:call-template name="uncapitalize">
               <xsl:with-param name="in" select="local-name()"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>

      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  indent the given number of levels.  The amount of indentation will 
     -  be nlev times the value of the global $indent.
     -  @param nlev   the number of indentations to insert.
     -->
   <xsl:template name="doindent">
      <xsl:param name="nlev" select="1"/>
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
   <xsl:template name="uncapitalize">
      <xsl:param name="in"/>
      <xsl:value-of select="translate(substring($in,1,1),
                                      'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
                                      'abcdefghijklmnopqrstuvwxyz')"/>
      <xsl:value-of select="substring($in,2)"/>
   </xsl:template>

   <!-- Curation -->
   <xsl:template match="vr2:Curation">
      <xsl:call-template name="doindent"/>
      <curation>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:Publisher"/>
         <xsl:apply-templates select="vr2:Creator"/>
         <xsl:apply-templates select="vr2:Contributor"/>
         <xsl:apply-templates select="vr2:Date"/>
         <xsl:apply-templates select="vr2:Version"/>
         <xsl:apply-templates select="vr2:Contact"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="indlev" select="1"/>
         </xsl:call-template>
      </curation>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Summary">
      <xsl:call-template name="doindent"/>
      <summary>
         <xsl:apply-templates select="*"/>
      </summary>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Publisher">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">publisher</xsl:with-param>
      </xsl:call-template>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  convert an element that is a reference to another resource
     -  @context  element of ResourceReferenceType
     -  @param element   the output element name
     -->
   <xsl:template name="ResourceReference">
      <xsl:param name="element"/>

      <xsl:element name="{$element}">
         <xsl:call-template name="ResourceReferenceType"/>
      </xsl:element>
   </xsl:template>

   <!--
     -  create an ivo-id attribute 
     -  @context  a v0.9 element of type IdentifierType (i.e. having an 
     -              AuthorityID and a ResourceKey children).
     -->
   <xsl:template name="IDReferenceAttr">
      <xsl:param name="idnode" select="."/>
      <xsl:attribute name="ivo-id">
         <xsl:for-each select="$idnode">
            <xsl:call-template name="IDString"/>
         </xsl:for-each>
      </xsl:attribute>
   </xsl:template>

   <!--
     -  create the content for a ResourceReferenceType
     -  @context  a v0.9 element containing an Identifier and a Title 
     -->   
   <xsl:template name="ResourceReferenceType">
      <xsl:if test="vr2:Identifier">
         <xsl:call-template name="IDReferenceAttr">
            <xsl:with-param name="idnode" select="vr2:Identifier"/>
         </xsl:call-template>
      </xsl:if>
      <xsl:value-of select="vr2:Title"/>
   </xsl:template>

   <!--
     -  convert an element that is a named reference to another resource
     -  @context  element of NameReferenceType
     -  @param element   the output element name
     -->
   <xsl:template name="NameReference">
      <xsl:param name="element">name</xsl:param>
      <xsl:param name="indlev" select="1"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="{$element}">
         <xsl:call-template name="NameReferenceType"/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  create the content for a NameReferenceType
     -  @context  a v0.9 element containing an Identifier and a Name
     -->   
   <xsl:template name="NameReferenceType">
      <xsl:if test="vr2:Identifier">
         <xsl:call-template name="IDReferenceAttr">
            <xsl:with-param name="idnode" select="vr2:Identifier"/>
         </xsl:call-template>
      </xsl:if>
      <xsl:value-of select="vr2:Name"/>
   </xsl:template>

   <xsl:template match="vr2:Creator">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <xsl:element name="creator">
         <xsl:value-of select="$cr"/>

         <xsl:call-template name="NameReference">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:call-template>
 
         <xsl:apply-templates select="vr2:Logo">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:apply-templates>

         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Contributor">
      <xsl:call-template name="NameReference">
         <xsl:with-param name="element">contributor</xsl:with-param>
         <xsl:with-param name="indlev" select="2"/>
      </xsl:call-template>
   </xsl:template>

   <xsl:template match="vr2:Date">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <xsl:element name="date">
         <xsl:if test="@role">
            <xsl:attribute name="role">
               <xsl:value-of select="@role"/>
            </xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Contact">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <xsl:element name="contact">
         <xsl:value-of select="$cr"/>

         <xsl:call-template name="NameReference">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:call-template>
 
         <xsl:apply-templates select="vr2:Email">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:apply-templates>

         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Source">
      <xsl:param name="indlev" select="2"/>
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <source>
         <xsl:attribute name="format">
            <xsl:value-of select="@format"/>
         </xsl:attribute>
         <xsl:value-of select="."/>
      </source>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:RelatedResource">
      <xsl:param name="indlev" select="2"/>
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <relationship>
         <xsl:apply-templates select="vr2:Relationship" />
         <xsl:apply-templates select="vr2:RelatedTo" />
      </relationship>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Relationship">
      <xsl:call-template name="doindent">
         <xsl:with-param name="indlev" select="2"/>
      </xsl:call-template>
      <xsl:call-template name="TextElement">
         <xsl:with-param name="element">relationshipType</xsl:with-param>
      </xsl:call-template>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:RelatedTo">
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">relatedResource</xsl:with-param>
         <xsl:with-param name="indlev" select="2"/>
      </xsl:call-template>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:AccessURL">
      <xsl:param name="defuse" select="''"/>
      <xsl:param name="indlev" select="2"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="accessURL">
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
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Interface[vr2:Invocation='WebBrowser']">
      <xsl:call-template name="doindent"/>
      <interface xsi:type="vr:WebBrowser">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:AccessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent"/>
      </interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Interface[vr2:Invocation='GLUService']">
      <xsl:call-template name="doindent"/>
      <interface xsi:type="vs:GLUService">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:AccessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent"/>
      </interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Interface[vr2:Invocation='WebService']">
      <xsl:call-template name="doindent"/>
      <interface xsi:type="vr:WebService">
         <xsl:apply-templates select="vr2:AccessURL">
            <xsl:with-param name="defuse">full</xsl:with-param>
         </xsl:apply-templates>
      </interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vr2:Interface" priority="-0.75">
      <xsl:message terminate="yes">
         <xsl:text>Error: generic Interface not allowed in v0.10</xsl:text>
      </xsl:message>
   </xsl:template>

   <xsl:template name="SkyServiceInterface">
      <xsl:apply-templates select="vr2:Interface" />
      <xsl:apply-templates select="vs2:ParamHTTP" />
   </xsl:template>

   <xsl:template match="vg2:ManagingOrg">
      <xsl:call-template name="doindent"/>
      <xsl:element name="vg:managingOrg">
         <xsl:call-template name="IDReferenceAttr"/>
      </xsl:element>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vg2:ManagedAuthority">
      <xsl:call-template name="doindent"/>
      <vg:managedAuthority>
         <xsl:value-of select="."/>
      </vg:managedAuthority>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Format">
      <xsl:param name="indlev" select="1"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      <xsl:element name="vs:format">
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

   <xsl:template match="vs2:Rights">
      <xsl:call-template name="doindent"/>
      <vs:rights>
         <xsl:value-of select="."/>
      </vs:rights>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:ParamHTTP">
      <xsl:call-template name="doindent"/>
      <vr:interface xsi:type="vs:ParamHTTP" xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:if test="@qtype">
            <xsl:attribute name="qtype"><xsl:value-of select="@qtype"/></xsl:attribute>
         </xsl:if>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:AccessURL">
            <xsl:with-param name="defuse">base</xsl:with-param>
         </xsl:apply-templates>
         <xsl:apply-templates select="vs2:HTTPResults"/>
         <xsl:apply-templates select="vs2:Param"/>
         <xsl:call-template name="doindent"/>
      </vr:interface>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:HTTPResults">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <resultType xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </resultType>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Param">
      <xsl:param name="indlev" select="2"/>

      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="$indlev"/>
      </xsl:call-template>
      
      <param xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>

         <name xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
            <xsl:value-of select="vr2:Name"/>
         </name>

         <xsl:value-of select="$cr"/>

         <xsl:if test="vr2:Description">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="3"/>
            </xsl:call-template>
            <description xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
               <xsl:value-of select="vr2:Description"/>
            </description>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:apply-templates select="vs2:DataType"/>
         <xsl:apply-templates select="vs2:Unit"/>
         <xsl:apply-templates select="vs2:UCD"/>

         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="$indlev"/>
         </xsl:call-template>
      </param>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--
     -  convert Facility and Instrument.  The output namespace depends on the
     -  namespace of the resource type.  
     -->
   <xsl:template match="vr2:Facility|vr2:Instrument">
      <xsl:param name="nsprefix">vs</xsl:param>

      <xsl:variable name="prefix">
         <xsl:choose>
            <xsl:when test="$nsprefix='vr' or $nsprefix=''">
               <xsl:value-of select="''"/>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="$nsprefix"/>:</xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:call-template name="doindent"/>
      <xsl:call-template name="ResourceReference">
         <xsl:with-param name="element">
            <xsl:value-of select="$prefix"/>
            <xsl:call-template name="uncapitalize">
               <xsl:with-param name="in" select="local-name()"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Coverage">
      <xsl:call-template name="doindent"/>
      <vs:coverage xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:Spatial"/>
         <xsl:apply-templates select="vs2:Spectral"/>
         <xsl:apply-templates select="vs2:Temporal"/>
         <xsl:call-template name="doindent"/>
      </vs:coverage>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Spatial">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <spatial xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="convertRegion"/>
         <xsl:apply-templates select="vs2:SpatialResolution">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vs2:RegionOfRegard"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </spatial>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:AllSky">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <region xmlns="http://www.ivoa.net/xml/VODataService/v0.5" 
              xsi:type="AllSky"/>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:CoordRange">
      <region xmlns="http://www.ivoa.net/xml/VODataService/v0.5" xsi:type="CoordRange">
         <xsl:apply-templates select="vs2:CoordFrame"/>
         <xsl:apply-templates select="vs2:long"/>
         <xsl:apply-templates select="vs2:lat"/>
      </region>
   </xsl:template>

   <xsl:template match="vs2:CircleRegion">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <region xsi:type="CircleRegion" 
              xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:CoordFrame"/>
         <xsl:apply-templates select="vs2:CenterPosition"/>
         <xsl:apply-templates select="vs2:radius"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </region>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:CoordFrame" 
                 xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <coordFrame>
         <xsl:value-of select="."/>
      </coordFrame>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:long">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <long xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
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
      </long>
   </xsl:template>

   <xsl:template match="vs2:lat">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <vs:lat xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
         <vs:min>
            <xsl:value-of select="vs2:min"/>
         </vs:min>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="5"/>
         </xsl:call-template>
         <vs:max>
            <xsl:value-of select="vs2:max"/>
         </vs:max>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
      </vs:lat>
   </xsl:template>

   <xsl:template match="vs2:CenterPosition">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <center xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
         <long>
            <xsl:value-of select="vs2:long"/>
         </long>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="4"/>
         </xsl:call-template>
         <lat>
            <xsl:value-of select="vs2:lat"/>
         </lat>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
      </center>
   </xsl:template>

   <xsl:template match="vs2:radius">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="4"/>
      </xsl:call-template>
      <radius xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </radius>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:SpatialResolution|vs2:SpectralResolution|
                        vs2:TemporalResolution">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <resolution xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </resolution>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:RegionOfRegard">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <regionOfRegard xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </regionOfRegard>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Spectral">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <spectral xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:Waveband">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vs2:WavelengthRange"/>
         <xsl:apply-templates select="vs2:SpectralResolution">
            <xsl:with-param name="indlev" select="3"/>
         </xsl:apply-templates>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </spectral>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Waveband">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <waveband xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </waveband>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:WavelengthRange">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <range xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <min>
            <xsl:value-of select="vs2:min"/>
         </min>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <max>
            <xsl:value-of select="vs2:max"/>
         </max>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </range>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Temporal">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <temporal xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vs2:StartTime"/>
         <xsl:apply-templates select="vs2:EndTime"/>
         <xsl:apply-templates select="vs2:TemporalResolution"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </temporal>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:StartTime">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <startTime xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </startTime>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:EndTime">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <endTime xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </endTime>
      <xsl:value-of select="$cr"/>
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

   <!-- Template to convert generic Resource elements -->
   <xsl:template name="convertResource">
      <xsl:apply-templates select="vr2:Title"/>
      <xsl:apply-templates select="vr2:ShortName"/>
      <xsl:apply-templates select="vr2:Identifier"/>
      <xsl:apply-templates select="vr2:Curation"/>
      <xsl:call-template name="content"/>
   </xsl:template>

   <xsl:template match="vs2:Table">
      <xsl:call-template name="doindent"/>
      <table xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:if test="@role">
            <xsl:attribute name="role">
               <xsl:value-of select="@role"/>
            </xsl:attribute>
         </xsl:if>
         <xsl:call-template name="convertTable"/>
         <xsl:call-template name="doindent"/>
      </table>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Column">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <column xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="$cr"/>
         <xsl:if test="vr2:Name">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="3"/>
            </xsl:call-template>
            <name xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
               <xsl:value-of select="vr2:Name"/>
            </name>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:if test="vr2:Description">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="3"/>
            </xsl:call-template>
            <description xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
               <xsl:value-of select="vr2:Description"/>
            </description>
            <xsl:value-of select="$cr"/>
         </xsl:if>
         <xsl:apply-templates select="vs2:DataType"/>
         <xsl:apply-templates select="vs2:Unit"/>
         <xsl:apply-templates select="vs2:UCD"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </column>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:DataType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <dataType xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:if test="@arraysize">
            <xsl:attribute name="arraysize"><xsl:value-of select="@arraysize"/></xsl:attribute>
         </xsl:if>
         <xsl:value-of select="."/>
      </dataType>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:Unit">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <unit xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </unit>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:UCD">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="3"/>
      </xsl:call-template>
      <ucd xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
         <xsl:value-of select="."/>
      </ucd>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="cs2:ConeSearch">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <cs:capability>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
         <cs:maxSR>
            <xsl:value-of select="cs2:MaxSR"/>
         </cs:maxSR>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
         <cs:maxRecords>
            <xsl:value-of select="cs2:MaxRecords"/>
         </cs:maxRecords>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
         <cs:verbosity>
            <xsl:value-of select="cs2:Verbosity"/>
         </cs:verbosity>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </cs:capability>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:SimpleImageAccess">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="1"/>
      </xsl:call-template>
      <sia:capability xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="sia2:ImageServiceType"/>
         <xsl:apply-templates select="sia2:MaxQueryRegionSize"/>
         <xsl:apply-templates select="sia2:MaxImageExtent"/>
         <xsl:apply-templates select="sia2:MaxImageSize"/>
         <xsl:apply-templates select="sia2:MaxFileSize"/>
         <xsl:apply-templates select="sia2:MaxRecords"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="1"/>
         </xsl:call-template>
      </sia:capability>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:ImageServiceType">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <imageServiceType xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="."/>
      </imageServiceType>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:MaxQueryRegionSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <maxQueryRegionSize xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <long>
            <xsl:value-of select="sia2:long"/>
         </long>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </maxQueryRegionSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:MaxImageExtent">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <maxImageExtent xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <long>
            <xsl:value-of select="sia2:long"/>
         </long>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </maxImageExtent>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:MaxImageSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <maxImageSize xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <long>
            <xsl:value-of select="sia2:long"/>
         </long>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="3"/>
         </xsl:call-template>
         <lat>
            <xsl:value-of select="sia2:lat"/>
         </lat>
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
      </maxImageSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:MaxFileSize">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <maxFileSize xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="."/>
      </maxFileSize>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="sia2:MaxRecords">
      <xsl:call-template name="doindent">
         <xsl:with-param name="nlev" select="2"/>
      </xsl:call-template>
      <maxRecords xmlns="http://www.ivoa.net/xml/SIA/v0.7">
         <xsl:value-of select="."/>
      </maxRecords>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <!--Named templates-->
   <xsl:template name="content">
      <xsl:call-template name="doindent"/>
      <content>
         <xsl:value-of select="$cr"/>
         <xsl:apply-templates select="vr2:Subject">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vr2:Summary/vr2:Description">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vr2:Summary/vr2:Source"/>
         <xsl:apply-templates select="vr2:Summary/vr2:ReferenceURL">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vr2:Type">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vr2:ContentLevel">
            <xsl:with-param name="indlev" select="2"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="vr2:RelatedResource"/>
         <xsl:call-template name="doindent"/>
      </content>
      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template name="convertRegion">
      <xsl:apply-templates select="vs2:AllSky"/>
      <xsl:apply-templates select="vs2:CoordRange"/>
      <xsl:apply-templates select="vs2:CircleRegion"/>
   </xsl:template>

   <xsl:template name="convertTable">
      <xsl:value-of select="$cr"/>
      <xsl:if test="vr2:Name">
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
         <name xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
            <xsl:value-of select="vr2:Name"/>
         </name>
         <xsl:value-of select="$cr"/>
      </xsl:if>
      <xsl:if test="vr2:Description">
         <xsl:call-template name="doindent">
            <xsl:with-param name="nlev" select="2"/>
         </xsl:call-template>
         <description xmlns="http://www.ivoa.net/xml/VODataService/v0.5">
            <xsl:value-of select="vr2:Description"/>
         </description>
         <xsl:value-of select="$cr"/>
      </xsl:if>
      <xsl:apply-templates select="vs2:Column"/>
   </xsl:template>
</xsl:stylesheet>
