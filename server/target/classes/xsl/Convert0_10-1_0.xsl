<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0" 
                xmlns:vg="http://www.ivoa.net/xml/VORegistry/v1.0" 
                xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0" 
                xmlns:cs="http://www.ivoa.net/xml/ConeSearch/v1.0" 
                xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
                xmlns:sia="http://www.ivoa.net/xml/SIA/v1.0" 
                xmlns:sn="http://www.ivoa.net/xml/OpenSkyNode/v0.2" 
                xmlns:stc="http://www.ivoa.net/xml/STC/stc-v1.30.xsd" 
                xmlns:vg2="http://www.ivoa.net/xml/VORegistry/v0.3" 
                xmlns:vr2="http://www.ivoa.net/xml/VOResource/v0.10" 
                xmlns:vs2="http://www.ivoa.net/xml/VODataService/v0.5" 
                xmlns:cs2="http://www.ivoa.net/xml/ConeSearch/v0.3"
				xmlns:ri2="http://www.ivoa.net/xml/RegistryInterface/v0.1" 
                xmlns:sia2="http://www.ivoa.net/xml/SIA/v0.7" 
                xmlns:sn2="http://www.ivoa.net/xml/OpenSkyNode/v0.1" 
                xmlns:vc="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:vc2="http://www.ivoa.net/xml/VOCommunity/v0.2" 
                xmlns:xlink="http://www.w3.org/1999/xlink" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                exclude-result-prefixes="#all" 
                version="2.0">

   <!-- 
     -  Stylesheet to convert VOResource-v0.10 to VOResource-v1.0
     -  Version 1.0 - Initial Revision
     -    Ray Plante, NCSA
     -->
   <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

   <xsl:preserve-space elements="*"/>

   <!--
     -  If true, insert carriage returns and indentation to produce a neatly 
     -  formatted output.  If false, any spacing between tags in the source
     -  document will be preserved.  
     -->
   <xsl:param name="prettyPrint" select="false()"/>

   <!--
     -  the per-level indentation.  Set this to a sequence of spaces when
     -  pretty printing is turned on.
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

   <!--
     -  Set to 0 or greater if a default validationLevel should set
     -  as all possible locations.  If greater than -1, the value will 
     -  be the value of the validationLevel.  You must also provide
     -  an ID to the validatedBy parameter below in order for the level
     -  to be set.  
     -->
   <xsl:param name="validationLevel" select="-1"/>

   <!-- 
     -  Set to the IVOA identifier of your validating registry.  This 
     -  must be set for validation levels to be added.  
     -->
   <xsl:param name="validatedBy"/>

   <!--
     -  If set, the updated atribute will be set to this value
     -->
   <xsl:param name="today"/>

   <!--
     -  If set, the output document will have a root element of this
     -  name and a namespace given by $resourceNS and it will contain
     -  the VOResource metadata; all other wrapping elements from the
     -  input will be filtered out. 
     -
     -  the following setting will produce records
     -  that can be inserted directly into a Harvest response record.
     -->
   <xsl:param name="resourceElement">Resource</xsl:param>

   <!--
     -  If resourceName is set (with an non-empty value), the output 
     -  document will have a root element of $resourceName and a 
     -  this namespace.  It will contain the VOResource metadata; all 
     -  other wrapping elements from the input will be filtered out. 
     -->
   <xsl:param name="resourceNS">http://www.ivoa.net/xml/RegistryInterface/v1.0</xsl:param>

   <xsl:variable name="idrefprefix">
      <!--
         <xsl:value-of select="translate(//vr2:identifier,
                                         '/!~*()+=','________')"/>
      <xsl:value-of select="//vr2:identifier/vr2:authorityID"/>
      <xsl:text>_</xsl:text>
      <xsl:if test="//vr2:identifier/vr2:resourceKey">
         <xsl:value-of select="translate(//vr2:identifier/vr2:resourceKey,
                                         '/!~*()+=','________')"/>
         <xsl:text>_</xsl:text>
      </xsl:if>
      -->
   </xsl:variable>

   <xsl:variable name="addValidationLevel"
               select="$validationLevel &gt; -1 and $validationLevel &lt; 5
                       and $validatedBy != ''"/>

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

   <xsl:variable name="istep">
      <xsl:if test="$prettyPrint">
         <xsl:value-of select="$indent"/>
      </xsl:if>
   </xsl:variable>
   <xsl:variable name="isp">
      <xsl:value-of select="$cr"/>
   </xsl:variable>

   <!-- ==========================================================
     -  General templates
     -  ========================================================== -->

   <xsl:template match="/">
      <xsl:apply-templates select="*" mode="wrapper">
         <xsl:with-param name="isRoot" select="true()"/>
         <xsl:with-param name="step" select="$istep"/>
      </xsl:apply-templates>
   </xsl:template>
   
   <xsl:template match="ri2:VOResources">
	   <xsl:element name="ri:VOResources" namespace="http://www.ivoa.net/xml/RegistryInterface/v1.0">
 <xsl:attribute name="from">1</xsl:attribute>
              <xsl:attribute name="more">false</xsl:attribute>
              <xsl:attribute name="numberReturned">2</xsl:attribute>
	      <xsl:apply-templates select="*" mode="wrapper">
	         <xsl:with-param name="isRoot" select="true()"/>
	         <xsl:with-param name="step" select="$istep"/>
	      </xsl:apply-templates>
	   </xsl:element>
   </xsl:template>

   <!--
     -  This template handles any wrapping elements that are not part of 
     -  the VOResource schema proper.
     -->
   <xsl:template match="*" mode="wrapper">
      <xsl:param name="isRoot" select="false()"/>
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="nextsp">
         <xsl:choose>
            <xsl:when test="$isRoot and $sp='' and $prettyPrint">
               <xsl:value-of select="$cr"/>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="$sp"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:value-of select="$sp"/>

      <xsl:variable name="elname">
         <xsl:choose>
            <xsl:when test="normalize-space($resourceElement)!='' and 
                            vr2:identifier">
               <xsl:value-of 
                    select="concat('ri:',normalize-space($resourceElement))"/>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="local-name()"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:variable name="elns">
         <xsl:choose>
            <xsl:when test="normalize-space($resourceElement)!='' and 
                            vr2:identifier">
               <xsl:value-of select="normalize-space($resourceNS)"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="namespace-uri()"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:element name="{$elname}" namespace="{$elns}">
         <xsl:if test="vr2:identifier">
            <xsl:if test="$elname!='' and $elns!=''">
               <xsl:namespace name="ri" select="$elns"/>
            </xsl:if>
            <xsl:choose>
               <xsl:when test="contains(@xsi:type,'Resource') or 
                               contains(@xsi:type,'Organisation') or 
                               contains(@xsi:type,':Service')">
                  <xsl:namespace name="vr" 
                       select="'http://www.ivoa.net/xml/VOResource/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'Authority')">
                  <xsl:namespace name="vg" 
                       select="'http://www.ivoa.net/xml/VORegistry/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'Registry')">
                  <xsl:namespace name="vr" 
                       select="'http://www.ivoa.net/xml/VOResource/v1.0'"/>
                  <xsl:namespace name="vg" 
                       select="'http://www.ivoa.net/xml/VORegistry/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'DataCollection')">
                  <xsl:namespace name="vs" 
                       select="'http://www.ivoa.net/xml/VODataService/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'SkyService')">
                  <xsl:namespace name="vr" 
                       select="'http://www.ivoa.net/xml/VOResource/v1.0'"/>
                  <xsl:namespace name="vs" 
                       select="'http://www.ivoa.net/xml/VODataService/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'ConeSearch')">
                  <xsl:namespace name="vs" 
                       select="'http://www.ivoa.net/xml/VODataService/v1.0'"/>
                  <xsl:namespace name="cs" 
                       select="'http://www.ivoa.net/xml/ConeSearch/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'SimpleImageAccess')">
                  <xsl:namespace name="vs" 
                       select="'http://www.ivoa.net/xml/VODataService/v1.0'"/>
                  <xsl:namespace name="sia" 
                       select="'http://www.ivoa.net/xml/SIA/v1.0'"/>
               </xsl:when>
               <xsl:when test="contains(@xsi:type,'OpenSkyNode')">
                  <xsl:namespace name="vr" 
                       select="'http://www.ivoa.net/xml/VOResource/v1.0'"/>
                  <xsl:namespace name="vs" 
                       select="'http://www.ivoa.net/xml/VODataService/v1.0'"/>
                  <xsl:namespace name="sn" 
                       select="'http://www.ivoa.net/xml/OpenSkyNode/v0.2'"/>
               </xsl:when>
            </xsl:choose>
            <xsl:if test="vs2:coverage">
               <xsl:namespace name="xlink" 
                    select="'http://www.w3.org/1999/xlink'"/>
            </xsl:if>
            <xsl:apply-templates select="@xsi:type" mode="resourceType"/>
         </xsl:if>
         <xsl:for-each select="@*">
            <xsl:if test="not(../vr2:identifier and name()='xsi:type') and
                          local-name() != 'schemaLocation' and 
                          local-name() != 'updated' and 
                          local-name() != 'created'">
               <!-- 
                 -  if this element is the VOResource root, this will copy
                 -  over the Resource attributes (created, updated, & status)
                 -->
               <xsl:copy/>
            </xsl:if>
         </xsl:for-each>
         <xsl:if test="vr2:identifier">
            <xsl:if test="not(@status)">
               <xsl:attribute name="status">active</xsl:attribute>
            </xsl:if>

            <xsl:variable name="defupdated">
               <xsl:choose>
                  <xsl:when test="$today!=''">
                     <xsl:value-of select="$today"/>
                  </xsl:when>
                  <xsl:otherwise>1999-01-01T00:00:00</xsl:otherwise>
               </xsl:choose>
            </xsl:variable>

            <xsl:variable name="updated">
               <xsl:choose>
                  <xsl:when test="$today!=''">
                     <xsl:value-of select="$today"/>
                  </xsl:when>
                  <xsl:when test="@updated">
                     <xsl:choose>
                        <xsl:when test="starts-with(substring(normalize-space(@updated),
                         string-length(normalize-space(@updated))),'Z')">
                           <xsl:value-of 
                                select="substring(normalize-space(@updated), 0,
                                        string-length(normalize-space(@updated)))"/>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="@updated"/>
                           <xsl:if test="not(contains(@updated,'T'))">
                              <xsl:text>T00:00:00</xsl:text>
                           </xsl:if>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:when>
                  <xsl:when test="@created">
                     <xsl:choose>
                        <xsl:when test="starts-with(substring(normalize-space(@created),
                         string-length(normalize-space(@created))),'Z')">
                           <xsl:value-of 
                                select="substring(normalize-space(@created), 0,
                                        string-length(normalize-space(@created)))"/>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="@created"/>
                           <xsl:if test="not(contains(@created,'T'))">
                              <xsl:text>T00:00:00</xsl:text>
                           </xsl:if>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$defupdated"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:if test="not(@updated) and $today=''">
               <xsl:message>
                  <xsl:text>WARNING: No @updated found in input;</xsl:text>
                  <xsl:text> using </xsl:text>
                  <xsl:value-of select="$updated"/>
               </xsl:message>
            </xsl:if>
            <xsl:attribute name="updated">
               <xsl:value-of select="$updated"/>
            </xsl:attribute>

            <xsl:variable name="created">
               <xsl:choose>
                  <xsl:when test="@created">
                     <xsl:choose>
                        <xsl:when test="starts-with(substring(normalize-space(@created),
                         string-length(normalize-space(@created))),'Z')">
                           <xsl:value-of 
                                select="substring(normalize-space(@created), 0,
                                        string-length(normalize-space(@created)))"/>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="@created"/>
                           <xsl:if test="not(contains(@created,'T'))">
                              <xsl:text>T00:00:00</xsl:text>
                           </xsl:if>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:when>
                  <xsl:when test="@updated">
                     <xsl:choose>
                        <xsl:when test="starts-with(substring(normalize-space(@updated),
                         string-length(normalize-space(@updated))),'Z')">
                           <xsl:value-of 
                                select="substring(normalize-space(@updated), 0,
                                        string-length(normalize-space(@updated)))"/>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="@updated"/>
                           <xsl:if test="not(contains(@updated,'T'))">
                              <xsl:text>T00:00:00</xsl:text>
                           </xsl:if>
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>1999-01-01T00:00:00</xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:if test="not(@created)">
               <xsl:message>
                  <xsl:text>WARNING: No @created found in input;</xsl:text>
                  <xsl:text> using </xsl:text>
                  <xsl:value-of select="$created"/>
               </xsl:message>
            </xsl:if>
            <xsl:attribute name="created">
               <xsl:value-of select="$created"/>
            </xsl:attribute>

         </xsl:if>



         <xsl:if test="boolean($isRoot) and $setSL=1">
            <xsl:call-template name="addSL"/>
         </xsl:if>

         <xsl:choose>
            <xsl:when test="vr2:identifier">

               <!--
                 -  These children are part of the VOResource schema
                 -->
               <xsl:apply-templates select="." mode="resource">
                  <xsl:with-param name="sp" select="concat($nextsp,$step)"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>

            </xsl:when>
            <xsl:otherwise>

               <xsl:apply-templates select="child::node()" mode="wrapper">
                  <xsl:with-param name="sp" select="concat($nextsp,$step)"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>

            </xsl:otherwise>
         </xsl:choose>      
      </xsl:element>

   </xsl:template>

   <xsl:template match="text()" mode="wrapper">
      <!-- just call the non-wrapper version -->
      <xsl:apply-templates select="."/>
   </xsl:template>

   <!--
     -  The following @xsi:type templates map old types into new types
     -->
   <xsl:template match="@xsi:type" mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="."/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='Organisation' or 
                                  substring-after(.,':')='Organisation']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vr:Organisation'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='Service' or 
                                  substring-after(.,':')='Service']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vr:Service'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='SkyService' or 
                                  substring-after(.,':')='SkyService']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vs:DataService'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='TabularSkyService' or 
                                  substring-after(.,':')='TabularSkyService']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vs:CatalogService'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='ConeSearch' or 
                                  substring-after(.,':')='ConeSearch']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vs:CatalogService'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='SimpleImageAccess' or 
                                  substring-after(.,':')='SimpleImageAccess']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vs:CatalogService'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template match="@xsi:type[.='OpenSkyNode' or 
                                  substring-after(.,':')='OpenSkyNode']" 
                 mode="resourceType">
      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vs:CatalogService'"/>
      </xsl:attribute>
   </xsl:template>

   <xsl:template name="addSL">
      <xsl:attribute name="xsi:schemaLocation">
         <xsl:text>http://www.ivoa.net/xml/VOResource/v1.0 </xsl:text>
         <xsl:value-of select="$schemaLocationPrefix"/>
         <xsl:text>VOResource-v1.0.xsd</xsl:text>
         <xsl:if test="contains(@xsi:type,'Registry') or 
                       contains(@xsi:type,'Authority')">
            <xsl:text> http://www.ivoa.net/xml/VORegistry/v1.0</xsl:text>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$schemaLocationPrefix"/>
            <xsl:text>VORegistry-v1.0.xsd</xsl:text>
         </xsl:if>
         <xsl:if test="contains(@xsi:type,'SkyService') or 
                       contains(@xsi:type,'TabularSkyService') or 
                       contains(@xsi:type,'DataCollection') or 
                       contains(@xsi:type,'ConeSearch') or 
                       contains(@xsi:type,'SimpleImageAccess') or 
                       contains(@xsi:type,'OpenSkyNode')">
            <xsl:text> http://www.ivoa.net/xml/VODataService/v1.0</xsl:text>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$schemaLocationPrefix"/>
            <xsl:text>VODataService-v1.0.xsd</xsl:text>
         </xsl:if>
         <xsl:if test="contains(@xsi:type,'ConeSearch')">
            <xsl:text> http://www.ivoa.net/xml/ConeSearch/v1.0</xsl:text>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$schemaLocationPrefix"/>
            <xsl:text>ConeSearch-v1.0.xsd</xsl:text>
         </xsl:if>
         <xsl:if test="contains(@xsi:type,'SimpleImageAccess')">
            <xsl:text> http://www.ivoa.net/xml/SIA/v1.0</xsl:text>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$schemaLocationPrefix"/>
            <xsl:text>SIA-v1.0.xsd</xsl:text>
         </xsl:if>
         <xsl:if test="contains(@xsi:type,'OpenSkyNode')">
            <xsl:text> http://www.ivoa.net/xml/OpenSkyNode/v0.2</xsl:text>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$schemaLocationPrefix"/>
            <xsl:text>SkyNode-v0.2.xsd</xsl:text>
         </xsl:if>
      </xsl:attribute>
   </xsl:template>

   <!--
     -  Default complex element template.  This converts the copies the source 
     -  element into the output except that it places it into the default
     -  (no name) namespace.
     -->
   <xsl:template match="*" priority="-2">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="elname">
         <xsl:call-template name="uncapitalize">
            <xsl:with-param name="in" select="local-name()"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{$elname}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:apply-templates select="child::node()">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="$sp"/>
      </xsl:element>

   </xsl:template>

   <!--
     -  make sure we have at least one subject before the description
     -->
   <xsl:template match="vr2:description">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="not(preceding-sibling::vr2:subject)">
         <xsl:message>
            <xsl:text>WARNING: adding required but empty </xsl:text>
            <xsl:text>&lt;subject&gt;; please update! </xsl:text>
         </xsl:message>
         <xsl:value-of select="$sp"/>
         <subject/>
      </xsl:if>

      <xsl:variable name="elname">
         <xsl:call-template name="uncapitalize">
            <xsl:with-param name="in" select="local-name()"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{$elname}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:choose>
            <xsl:when test="$prettyPrint and @*">
               <xsl:value-of select="concat($sp,$step)"/>

               <xsl:value-of select="normalize-space(.)"/>

               <xsl:value-of select="$sp"/>
            </xsl:when>

            <xsl:otherwise>
               <xsl:value-of select="."/>
            </xsl:otherwise>
         </xsl:choose>

      </xsl:element>

   </xsl:template>

   <!--
     -  Default text element template.  This converts the copies the source 
     -  element into the output except that it places it into the default
     -  (no name) namespace.  The formatting is different from the complex
     -  element.  
     -->
   <xsl:template match="*[child::text() and not(*)]" priority="-1.5">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="elname">
         <xsl:call-template name="uncapitalize">
            <xsl:with-param name="in" select="local-name()"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{$elname}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:choose>
            <xsl:when test="$prettyPrint and @*">
               <xsl:value-of select="concat($sp,$step)"/>

               <xsl:value-of select="normalize-space(.)"/>

               <xsl:value-of select="$sp"/>
            </xsl:when>

            <xsl:otherwise>
               <xsl:value-of select="."/>
            </xsl:otherwise>
         </xsl:choose>

      </xsl:element>
   </xsl:template>

   <!--
     -  generic resource contents
     -->
   <xsl:template match="*[vr2:identifier]" mode="resource" priority="-0.5">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="@xsi:type">
         <xsl:message>
            <xsl:text>WARNING: unrecognized @xsi:type: </xsl:text>
            <xsl:value-of select="@xsi:type"/>
         </xsl:message>
      </xsl:if>

      <xsl:apply-templates select="child::node()">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='Service' or 
                          contains(@xsi:type,':Service')]" mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="service">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='SkyService' or 
                          contains(@xsi:type,':SkyService')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="service">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="skyservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='TabularSkyService' or 
                          contains(@xsi:type,':TabularSkyService')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="service">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="tabservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='SimpleImageAccess' or 
                          contains(@xsi:type,':SimpleImageAccess')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="sia2:capability">
         <xsl:apply-templates 
         select="sia2:capability/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="sia2:capability">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:apply-templates select="." mode="tabservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='ConeSearch' or 
                          contains(@xsi:type,':ConeSearch')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="cs2:capability">
         <xsl:apply-templates 
          select="cs2:capability/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="cs2:capability">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:apply-templates select="." mode="tabservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[@xsi:type='OpenSkyNode' or 
                          contains(@xsi:type,':OpenSkyNode')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="sn2:capability">
         <xsl:apply-templates 
          select="sn2:capability/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="sn2:capability">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:apply-templates select="." mode="tabservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[vr2:identifier]" mode="core">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates 
           select="child::node()[following-sibling::vr2:content]|vr2:content">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="*[vr2:identifier]" mode="service">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>
      <capability>

         <xsl:if test="$addValidationLevel">
            <xsl:apply-templates select="child::text()[1]" />
            <xsl:value-of select="concat($sp,$step)"/>

            <validationLevel validatedBy="{$validatedBy}">
               <xsl:value-of select="$validationLevel"/>
            </validationLevel>
         </xsl:if>

         <xsl:variable name="substep">
            <xsl:choose>
               <xsl:when test="$prettyPrint">
                  <xsl:value-of select="$step"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:apply-templates select="." mode="getIndentStep"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>

         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates select="../vr2:interface">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
         </xsl:apply-templates>

         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:value-of select="$sp"/>
      </capability>
   </xsl:template>

   <xsl:template match="*[vr2:identifier]" mode="skyservice">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="vs2:facility">
         <xsl:apply-templates 
            select="vr2:facility/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:facility">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:instrument">
         <xsl:apply-templates 
          select="vr2:instrument/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:instrument">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:coverage">
         <xsl:apply-templates 
            select="vs2:coverage/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vs2:coverage">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <xsl:template match="*[vr2:identifier]" mode="tabservice">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="skyservice">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="vs2:table">
         <xsl:apply-templates 
            select="vr2:table/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:table">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <!--
     -  handle the contents of a DataCollection.  This template assumes that
     -  the resource element has already been rendered
     -->
   <xsl:template match="*[@xsi:type='DataCollection' or 
                          contains(@xsi:type,':DataCollection')]"
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="vs2:facility">
         <xsl:apply-templates 
            select="vr2:facility/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:facility">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:instrument">
         <xsl:apply-templates 
          select="vr2:instrument/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:instrument">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:rights">
         <xsl:apply-templates 
          select="vr2:rights/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:rights">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:format">
         <xsl:apply-templates 
          select="vr2:format/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:format">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:coverage">
         <xsl:apply-templates 
            select="vs2:coverage/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vs2:coverage">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

      <xsl:if test="vs2:table">

         <!-- now wrap tables in a catalog -->
         <xsl:apply-templates 
              select="vr2:content/following-sibling::text()[1]" />
         <xsl:value-of select="$sp"/>
         <catalog>

            <xsl:variable name="substep">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="$step"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:apply-templates select="vs2:table[1]" 
                                          mode="getIndentStep"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>

            <xsl:for-each select="vs2:table[1]/preceding-sibling::text()">
               <xsl:if test="position()=last()">
                  <xsl:apply-templates select="." mode="trim"/>
               </xsl:if>
            </xsl:for-each>

            <xsl:apply-templates select="vs2:table | 
                              child::text()[preceding-sibling::vs2:table and 
                                            following-sibling::vs2:table]">
               <xsl:with-param name="sp" select="concat($sp,$substep)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>

            <xsl:apply-templates 
                 select="vs2:table/child::text()[position()=last()]"/>
            <xsl:value-of select="$sp"/>
         </catalog>
      </xsl:if>

      <xsl:if test="vs2:accessURL">
         <xsl:apply-templates 
          select="vr2:accessURL/preceding-sibling::text()[position()=last()]"/>
         <xsl:apply-templates select="vr2:accessURL">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>
      </xsl:if>

   </xsl:template>

   <!--
     -  template for Organisation
     -->
   <xsl:template match="*[@xsi:type='Organisation' or 
                          contains(@xsi:type,':Organisation')]"
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="child::node()">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <!--
     -  template for Authority
     -->
   <xsl:template match="*[@xsi:type='Authority' or 
                          contains(@xsi:type,':Authority')]" mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="vr2:content/following-sibling::node()[following-sibling::vg2:managingOrg]|vg2:managingOrg">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="not(vg2:managingOrg)">
         <xsl:element name="managingOrg">
            <xsl:if test="vr2:curation/vr2:publisher/@ivo-id">
               <xsl:attribute name="ivo-id">
                  <xsl:value-of select="vr2:curation/vr2:publisher/@ivo-id"/>
               </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="vr2:curation/vr2:publisher"/>
         </xsl:element>
      </xsl:if>
   </xsl:template>

   <xsl:template match="*[@xsi:type='Registry' or 
                          contains(@xsi:type,':Registry')]" 
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

   <xsl:message>WARNING: Registry record will require updating</xsl:message>

      <xsl:variable name="substep">
         <xsl:choose>
            <xsl:when test="$prettyPrint">
               <xsl:value-of select="$step"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:apply-templates select="vr2:interface" 
                                    mode="getIndentStep"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

      <xsl:variable name="subsp">
         <xsl:if test="$prettyPrint"><xsl:value-of select="$cr"/></xsl:if>
         <xsl:apply-templates 
              select="vr2:content/following-sibling::text()[1]"/>
         <xsl:value-of select="concat($sp,$substep)"/>
      </xsl:variable>

      <xsl:variable name="newline">
         <xsl:if test="$prettyPrint"><xsl:value-of select="$sp"/></xsl:if>
         <xsl:apply-templates mode="trim"
              select="vr2:content/following-sibling::text()[1]"/>
      </xsl:variable>

      <xsl:variable name="subnewline">
         <xsl:value-of select="$cr"/>
         <xsl:call-template name="afterLastCR">
            <xsl:with-param name="text" select="$subsp"/>
         </xsl:call-template>
      </xsl:variable>

      <!-- now render the core metadata -->
      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="vr2:interface">
         <xsl:apply-templates 
              select="vr2:content/following-sibling::text()[1]"/>
         <xsl:value-of select="$sp"/>
         <capability standardID="ivo://ivoa.net/std/Registry"
                     xsi:type="vg:Harvest">

            <xsl:if test="$addValidationLevel">
               <xsl:value-of select="$subsp"/>

               <validationLevel validatedBy="{$validatedBy}">
                  <xsl:value-of select="$validationLevel"/>
               </validationLevel>
            </xsl:if>

            <xsl:value-of select="$subsp"/>
            <xsl:comment>
               <xsl:text> ========================= ATTENTION ======================== </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  new v1.0 interface:  </xsl:text>
               <xsl:text>please update accessURL to new endpoint. </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  ============================================================ </xsl:text>
            </xsl:comment>

            <xsl:value-of select="$subnewline"/>
            <interface version="1.0" role="std" xsi:type="vg:OAIHTTP">

               <xsl:apply-templates select="vr2:interface/child::node()[following-sibling::vr2:accessURL]|vr2:interface/vr2:accessURL">
                  <xsl:with-param name="sp" 
                                  select="concat($sp,$step,$substep)"/>
                  <xsl:with-param name="step" select="$substep"/>
               </xsl:apply-templates>

               <xsl:value-of select="concat($sp,$substep)"/>
            </interface>

            <!-- now the old interface -->
            <xsl:value-of select="$subsp"/>
            <interface version="0.8" role="std" xsi:type="vg:OAIHTTP">

               <xsl:apply-templates select="vr2:interface/child::node()[following-sibling::vr2:accessURL]|vr2:interface/vr2:accessURL">
                  <xsl:with-param name="sp" 
                                  select="concat($sp,$step,$substep)"/>
                  <xsl:with-param name="step" select="$substep"/>
               </xsl:apply-templates>

               <xsl:value-of select="concat($sp,$substep)"/>
            </interface>

            <xsl:value-of select="$subsp"/>
            <maxRecords>0</maxRecords>

            <xsl:apply-templates 
                 select="vr2:interface/child::text()[position()=last()]" />
            <xsl:value-of select="$sp"/>
         </capability>

         <xsl:if test="$prettyPrint"><xsl:value-of select="$cr"/></xsl:if>
         <xsl:apply-templates 
              select="vr2:content/following-sibling::text()[1]"/>
         <xsl:value-of select="$sp"/>
         <xsl:comment>
            <xsl:text> ============================ ATTENTION ======================== </xsl:text>
            <xsl:value-of select="$newline"/>
            <xsl:text>  -  search capability:  remove this </xsl:text>
            <xsl:text>block if searching is not supported. </xsl:text>
            <xsl:value-of select="$newline"/>
            <xsl:text>  -  =============================================================== </xsl:text>
         </xsl:comment>
         <xsl:value-of select="$newline"/>
         <capability standardID="ivo://ivoa.net/std/Registry" 
                     xsi:type="vg:Search">

            <xsl:if test="$addValidationLevel">
               <xsl:apply-templates select="vr2:interface/child::text()[1]" />
               <xsl:value-of select="concat($sp,$step)"/>

               <validationLevel validatedBy="{$validatedBy}">
                  <xsl:value-of select="$validationLevel"/>
               </validationLevel>
            </xsl:if>

            <xsl:value-of select="$subsp"/>
            <xsl:comment>
               <xsl:text> ========================= ATTENTION ======================== </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  new v1.0 interface:  </xsl:text>
               <xsl:text>please update accessURL to new endpoint. </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  ============================================================ </xsl:text>
            </xsl:comment>

            <xsl:value-of select="$subnewline"/>

            <interface version="1.0" role="std" xsi:type="vr:WebService">
               <xsl:apply-templates select="vr2:interface/child::node()[following-sibling::vr2:accessURL]|vr2:interface/vr2:accessURL">
                  <xsl:with-param name="sp" 
                                  select="concat($sp,$step,$substep)"/>
                  <xsl:with-param name="step" select="$substep"/>
               </xsl:apply-templates>

               <xsl:value-of select="concat($sp,$substep)"/>
            </interface>

            <xsl:value-of select="$subsp"/>
            <xsl:comment>
               <xsl:text> ========================= ATTENTION ======================== </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  old interface:  </xsl:text>
               <xsl:text>please update accessURL to old search endpoint. </xsl:text>
               <xsl:value-of select="$subnewline"/>
               <xsl:text>  -  ============================================================ </xsl:text>
            </xsl:comment>

            <xsl:value-of select="$subnewline"/>

            <interface version="0.8" role="std" xsi:type="vr:WebService">
               <xsl:apply-templates select="vr2:interface/child::node()[following-sibling::vr2:accessURL]|vr2:interface/vr2:accessURL">
                  <xsl:with-param name="sp" 
                                  select="concat($sp,$step,$substep)"/>
                  <xsl:with-param name="step" select="$substep"/>
               </xsl:apply-templates>

               <xsl:value-of select="concat($sp,$substep)"/>
            </interface>

            <xsl:value-of select="$subsp"/>
            <maxRecords>0</maxRecords>
            <xsl:value-of select="$subsp"/>
            <extensionSearchSupport>partial</extensionSearchSupport>  

            <xsl:apply-templates mode="trim"
                 select="vr2:interface/child::text()[position()=last()]" />
            <xsl:value-of select="$sp"/>
         </capability>

      </xsl:if>

      <!-- this may need fixing -->
      <xsl:apply-templates select="vr2:interface/following-sibling::text()[1]"/>
      <xsl:value-of select="$sp"/>
      <full>true</full>

      <xsl:apply-templates 
           select="child::node()[preceding-sibling::vr2:interface]">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <!--
     -  template for handling ignorable whitespace
     -->
   <xsl:template match="text()" priority="-1">
      <xsl:if test="not($prettyPrint)">
         <xsl:copy/>
      </xsl:if>
   </xsl:template>

<!-- for debugging:  uncomment this template to override the default
  -  handling of ignorable whitespace
   <xsl:template match="text()" priority="-0.8">
      <xsl:if test="not($prettyPrint)">
         <xsl:text>[</xsl:text><xsl:copy/><xsl:text>]</xsl:text>
      </xsl:if>
   </xsl:template>
  -->

   <!--
     -  template for handling ignorable whitespace.  This version will
     -    shave off all but the last carriage return
     -->
   <xsl:template match="text()" priority="-1" mode="trim">
      <xsl:if test="not($prettyPrint)">
         <xsl:choose>
            <xsl:when test="contains(.,$cr)">
               <xsl:value-of select="$cr"/>
               <xsl:call-template name="afterLastCR">
                  <xsl:with-param name="text" select="."/>
               </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
               <xsl:copy/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:template>

   <xsl:template match="text()" priority="-1" mode="wrapper">
      <xsl:apply-templates select="." />
   </xsl:template>

   <!--
     -  convert coverage descriptions to STC
     -->
   <xsl:template match="vs2:coverage">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>
      <coverage>

      <xsl:if test="vs2:spatial |
                    vs2:spectral/*[local-name()!='waveband'] |
                    vs2:temporal">

         <xsl:apply-templates select="child::text()[1]" />
         <xsl:value-of select="concat($sp,$step)"/>

         <stc:STCResourceProfile>
            <xsl:variable name="indlev" select="1"/>

            <!-- figure out the spacing to use for sub-elements -->
            <xsl:variable name="substep">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="$step"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:apply-templates select="." mode="getIndentStep"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:variable name="subsp">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="concat($sp,$step,$step)"/>
                  </xsl:when>
                  <xsl:when test="contains(child::text()[1],$cr)">
                     <xsl:value-of select="concat(child::text()[1],$substep)"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$cr"/>
                     <xsl:call-template name="doindent">
                        <xsl:with-param name="nlev" select="3"/>
                     </xsl:call-template>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>

            <xsl:apply-templates select="." mode="system">
               <xsl:with-param name="sp" select="$subsp"/>
               <xsl:with-param name="step" select="$substep"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="." mode="pos">
               <xsl:with-param name="sp" select="$subsp"/>
               <xsl:with-param name="step" select="$substep"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="." mode="area">
               <xsl:with-param name="sp" select="$subsp"/>
               <xsl:with-param name="step" select="$substep"/>
            </xsl:apply-templates>

            <xsl:apply-templates 
                select="*[position()=last()]/child::text()[position()=last()]"/>
            <xsl:value-of select="concat($sp,$step)"/>
  
         </stc:STCResourceProfile>
         <xsl:value-of select="$cr"/>
      </xsl:if>

      <xsl:apply-templates select="vs2:spectral/vs2:waveband">
         <xsl:with-param name="sp" select="concat($sp,$step)"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="child::text()[position()=last()]" />
      <xsl:value-of select="$sp"/>

      </coverage>
   </xsl:template>

   <xsl:template match="vs2:waveband">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="../../child::text()[1]" mode="trim"/>
      <xsl:value-of select="$sp"/>

      <waveband><xsl:value-of select="."/></waveband>
   </xsl:template>

   <!--
     -  define the needed STC coordinate systems
     -->
   <xsl:template match="vs2:coverage" mode="system">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="coordframes">
         <xsl:apply-templates select="vs2:spatial" mode="getframes"/>
      </xsl:param>

      <xsl:if test="contains($coordframes,' ')">
        <xsl:message>Warning: multiple coordinate frames provided;
some regions may be lost.</xsl:message>
      </xsl:if>

      <xsl:call-template name="defineSystems">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
         <xsl:with-param name="coordframes" select="$coordframes"/>
      </xsl:call-template>
   </xsl:template>

   <!--
     -  obtain a unique list of coordinate frame names cited within
     -  a spatial coverage element.  
     -->
   <xsl:template match="vs2:spatial" mode="getframes">
      <xsl:param name="default">ICRS</xsl:param>
      <xsl:variable name="frames">
         <xsl:apply-templates select="descendant::vs2:coordFrame[1]" 
                              mode="getframes"/>
      </xsl:variable>

      <xsl:choose>
         <xsl:when test="normalize-space($frames)=''">
            <xsl:value-of select="$default"/>
         </xsl:when>
         <xsl:otherwise><xsl:value-of select="$frames"/></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--
     -  append remaining unique coordinate system names at the current 
     -  position and beyond.
     -->
   <xsl:template match="vs2:coordFrame" mode="getframes">
      <xsl:param name="list"/>

      <xsl:variable name="newlist">
         <xsl:value-of select="$list"/>
         <xsl:if test="not(contains($list, .))">
            <xsl:if test="string-length($list) > 0">
               <xsl:text> </xsl:text>
            </xsl:if>
            <xsl:value-of select="string(.)"/>
         </xsl:if>
      </xsl:variable>

      <xsl:choose>
         <xsl:when test="following-sibling::vs2:coordFrame">
            <xsl:apply-templates select="following-sibling::vs2:coordFrame[1]" 
                                 mode="getframes">
               <xsl:with-param name="list" select="$newlist"/>
            </xsl:apply-templates>
         </xsl:when>
         <xsl:otherwise><xsl:value-of select="$newlist"/></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--
     -  For each coordinate frame name listed in $coordframes, define
     -  a standard STC coordinate system.
     -->
   <xsl:template name="defineSystems">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="coordframes"/>

      <xsl:variable name="firstframe">
         <xsl:choose>
            <xsl:when test="contains($coordframes,' ')">
               <xsl:value-of select="substring-before($coordframes,' ')"/>
            </xsl:when>
            <xsl:when test="string-length($coordframes) > 0">
               <xsl:value-of select="$coordframes"/>
            </xsl:when>
         </xsl:choose>
      </xsl:variable>

      <xsl:if test="string-length($firstframe) > 0">
         <xsl:variable name="systag" 
                       select="concat('UTC-',$firstframe,'-TOPO')"/>
         <xsl:variable name="sysurl" select="concat('ivo://STClib/CoordSys#',
                                                    $systag)"/>
         <xsl:variable name="idtrans" select="substring(translate(../vr2:identifier,
                                         '/!~*()+=:','_________'),7)"/>

         <xsl:value-of select="$cr"/>
         <xsl:value-of select="$sp"/>

         <stc:AstroCoordSystem xlink:type="simple"
                               xlink:href="{$sysurl}"
                               id="{concat($idtrans,'_',$systag)}"/>

      </xsl:if>

      <xsl:variable name="next" select="substring-after($coordframes,' ')"/>
      <xsl:if test="string-length($coordframes) > 0">
         <xsl:call-template name="defineSystems">
            <xsl:with-param name="sp" select="$sp"/>
            <xsl:with-param name="step" select="$step"/>
            <xsl:with-param name="coordframes" select="$next"/>
         </xsl:call-template>
      </xsl:if>
   </xsl:template>

   <!--
     -  define the needed coordinate position data
     -->
   <xsl:template match="vs2:coverage" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="coordframe">
         <xsl:value-of select="vs2:spatial/vs2:region[1]/vs2:coordFrame"/>
      </xsl:param>

      <xsl:variable name="systag">
         <xsl:choose>
            <xsl:when test="string-length($coordframe) > 0">
               <xsl:value-of select="concat('UTC-',$coordframe,'-TOPO')"/>
            </xsl:when>
            <xsl:otherwise>UTC-ICRS-TOPO</xsl:otherwise>
         </xsl:choose>
      </xsl:variable>

         <xsl:variable name="idtrans" select="substring(translate(../vr2:identifier,
                                         '/!~*()+=:','_________'),7)"/>


      <xsl:if test="*/vs2:resolution|*/vs2:regionOfRegard">

         <xsl:value-of select="$cr"/>
         <xsl:value-of select="$sp"/>

         <stc:AstroCoords coord_system_id="{concat($idtrans,'_',$systag)}">

            <xsl:apply-templates select="vs2:temporal" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="vs2:spatial" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="vs2:spectral" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>

            <xsl:value-of select="$sp"/>
         </stc:AstroCoords>

      </xsl:if>
   </xsl:template>

   <xsl:template match="vs2:temporal" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="vs2:resolution">
         <xsl:value-of select="$sp"/>
         <stc:Time unit="s">

            <xsl:apply-templates select="vs2:resolution" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="$sp"/>
         </stc:Time>
      </xsl:if>
   </xsl:template>

   <xsl:template match="vs2:spatial" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="vs2:resolution|vs2:regionOfRegard">
         <xsl:value-of select="$sp"/>
         <stc:Position1D>

            <xsl:apply-templates select="vs2:resolution" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="uname">pos_unit</xsl:with-param>
               <xsl:with-param name="unit">deg</xsl:with-param>
            </xsl:apply-templates>
            
            <xsl:apply-templates select="vs2:regionOfRegard" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="$sp"/>
         </stc:Position1D>
      </xsl:if>
   </xsl:template>

   <xsl:template match="vs2:spectral" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:if test="boolean(vs2:resolution) and boolean(vs2:range)">

         <xsl:value-of select="$sp"/>
         <stc:Spectral unit="m">

            <xsl:apply-templates select="vs2:resolution" mode="pos">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
               <xsl:with-param name="val" select="number(vs2:range/vs2:min) * 
                                                  number(vs2:resolution)"/>
            </xsl:apply-templates>
            
            <xsl:value-of select="$sp"/>
         </stc:Spectral>
      
      </xsl:if>

   </xsl:template>

   <xsl:template match="vs2:resolution" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="val" select="."/>
      <xsl:param name="unit"/>
      <xsl:param name="uname">unit</xsl:param>

      <xsl:value-of select="$sp"/>
      <xsl:element name="stc:Resolution">
         <xsl:if test="$unit!=''">
            <xsl:attribute name="{$uname}">
               <xsl:value-of select="$unit"/>
            </xsl:attribute>
         </xsl:if>
         <xsl:value-of select="$val"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:regionOfRegard" mode="pos">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$cr"/>
      <xsl:value-of select="$sp"/>
      <xsl:comment> Coverage.RegionOfRegard </xsl:comment>

      <xsl:value-of select="$sp"/>
      <stc:Size pos_unit="arcsec">
         <xsl:value-of select="."/>
      </stc:Size>
   </xsl:template>

   <xsl:template match="vs2:coverage" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="coordframe">
         <xsl:choose>
            <xsl:when test="vs2:spatial/vs2:region[1]/vs2:coordFrame">
              <xsl:value-of select="vs2:spatial/vs2:region[1]/vs2:coordFrame"/>
            </xsl:when>
            <xsl:otherwise>ICRS</xsl:otherwise>
         </xsl:choose>
      </xsl:param>

      <xsl:variable name="idtrans" select="substring(translate(../vr2:identifier,
                                         '/!~*()+=:','_________'),7)"/>


      <xsl:variable name="systag" select="concat('UTC-',$coordframe,'-TOPO')"/>

      <xsl:if test="vs2:spatial/vs2:region">
         <xsl:value-of select="$cr"/>
         <xsl:value-of select="$sp"/>

         <stc:AstroCoordArea coord_system_id="{concat($idtrans,'_',$systag)}">
            <xsl:variable name="indlev" select="3"/>
            <xsl:variable name="spacing">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="$cr"/>
                     <xsl:call-template name="doindent">
                        <xsl:with-param name="nlev" select="$indlev+1"/>
                     </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="*/*[*]/child::text()">
                     <xsl:value-of select="*/*[*]/child::text()[1]"/>
                  </xsl:when>
                  <xsl:when test="string-length(*/child::text()) > 0">
                     <xsl:value-of select="concat(*/child::text(),$indent)"/>
                  </xsl:when>
               </xsl:choose>
            </xsl:variable>

            <xsl:apply-templates select="vs2:temporal" mode="area">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="vs2:spatial" mode="area">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
            <xsl:apply-templates select="vs2:spectral" mode="area">
               <xsl:with-param name="sp" select="concat($sp,$step)"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>

            <xsl:value-of select="$sp"/>
         </stc:AstroCoordArea>

      </xsl:if>
   </xsl:template>

   <xsl:template match="vs2:temporal" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <stc:TimeInterval>
         <xsl:value-of select="concat($sp,$step)"/>
         <xsl:if test="vs2:startTime">
            <stc:StartTime>
               <xsl:value-of select="concat($sp,$step,$step)"/>
               <stc:ISOTime>
                  <xsl:value-of select="vs2:startTime"/>
                  <xsl:text>T00:00:00.0Z</xsl:text>
               </stc:ISOTime>
               <xsl:value-of select="concat($sp,$step)"/>
            </stc:StartTime>
         </xsl:if>
         <xsl:if test="vs2:endTime">
            <stc:StopTime>
               <xsl:value-of select="concat($sp,$step,$step)"/>
               <stc:ISOTime>
                  <xsl:value-of select="vs2:endTime"/>
                  <xsl:text>T00:00:00.0Z</xsl:text>
               </stc:ISOTime>
               <xsl:value-of select="concat($sp,$step)"/>
            </stc:StopTime>
         </xsl:if>

         <xsl:value-of select="$sp"/>
      </stc:TimeInterval>
   </xsl:template>

   <xsl:template match="vs2:spatial" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:choose>
         <xsl:when test="count(vs2:region) > 1">
            <xsl:value-of select="$sp"/>
            <stc:Union>
               <xsl:apply-templates select="vs2:region" mode="area">
                  <xsl:with-param name="sp" select="concat($sp,$step)"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>

               <xsl:value-of select="$sp"/>
            </stc:Union>
         </xsl:when>
         <xsl:otherwise>
            <xsl:apply-templates select="vs2:region" mode="area">
               <xsl:with-param name="sp" select="$sp"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template match="vs2:region" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
   </xsl:template>

   <xsl:template match="vs2:region[contains(@xsi:type,'AllSky')]" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>
      <stc:AllSky/>
   </xsl:template>

   <!-- 
     -  express an RA range as an STC convex
     -->
   <xsl:template match="vs2:long" mode="convex">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>
      <xsl:variable name="subsubsp" select="concat($subsp,$step)"/>
      <xsl:variable name="subsubsubsp" select="concat($subsubsp,$step)"/>

      <xsl:value-of select="$sp"/>
      <stc:Convex>
         <xsl:value-of select="$subsp"/>
         <stc:Halfspace>
            <xsl:value-of select="$subsubsp"/>
            <stc:Vector>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C1>
                  <xsl:text>sin(</xsl:text>
                  <xsl:value-of select="number(vs2:min)+90"/>
                  <xsl:text>)</xsl:text>
               </stc:C1>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C2>
                  <xsl:text>cos(</xsl:text>
                  <xsl:value-of select="number(vs2:min)+90"/>
                  <xsl:text>)</xsl:text>
               </stc:C2>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C3>0</stc:C3>
               <xsl:value-of select="$subsubsp"/>
            </stc:Vector>
            <xsl:value-of select="$subsp"/>
         </stc:Halfspace>

         <xsl:value-of select="$subsp"/>
         <stc:Halfspace>
            <xsl:value-of select="$subsubsp"/>
            <stc:Vector>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C1>
                  <xsl:text>sin(</xsl:text>
                  <xsl:value-of select="number(vs2:max)-90"/>
                  <xsl:text>)</xsl:text>
               </stc:C1>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C2>
                  <xsl:text>cos(</xsl:text>
                  <xsl:value-of select="number(vs2:max)-90"/>
                  <xsl:text>)</xsl:text>
               </stc:C2>
               <xsl:value-of select="$subsubsubsp"/>
               <stc:C3>0</stc:C3>
               <xsl:value-of select="$subsubsp"/>
            </stc:Vector>
            <xsl:value-of select="$subsp"/>
         </stc:Halfspace>
         <xsl:value-of select="$sp"/>
      </stc:Convex>
   </xsl:template>

   <!--
     -  express a Dec range as an STC convex
     -->
   <xsl:template match="vs2:lat" mode="convex">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>
      <xsl:variable name="subsubsp" select="concat($subsp,$step)"/>
      <xsl:variable name="subsubsubsp" select="concat($subsubsp,$step)"/>

      <xsl:value-of select="$sp"/>
      <stc:Convex>
         <xsl:if test="number(vs2:max) &gt; -90">
            <xsl:value-of select="$subsp"/>
            <stc:Halfspace>
               <xsl:value-of select="$subsubsp"/>
               <stc:Vector>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C1>0</stc:C1>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C2>0</stc:C2>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C3>1</stc:C3>
                  <xsl:value-of select="$subsubsp"/>
               </stc:Vector>
               <xsl:value-of select="$subsubsp"/>
               <stc:Offset>
                  <xsl:text>sin(</xsl:text>
                  <xsl:value-of select="vs2:min"/>
                  <xsl:text>)</xsl:text>
               </stc:Offset>
              <xsl:value-of select="$subsp"/>
            </stc:Halfspace>
         </xsl:if>

         <xsl:if test="number(vs2:max) &lt; 90">
            <xsl:value-of select="$subsp"/>
            <stc:Halfspace>
               <xsl:value-of select="$subsubsp"/>
               <stc:Vector>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C1>0</stc:C1>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C2>0</stc:C2>
                  <xsl:value-of select="$subsubsubsp"/>
                  <stc:C3>1</stc:C3>
                  <xsl:value-of select="$subsubsp"/>
               </stc:Vector>
               <xsl:value-of select="$subsubsp"/>
               <stc:Offset>
                  <xsl:text>sin(</xsl:text>
                  <xsl:value-of select="vs2:max"/>
                  <xsl:text>)</xsl:text>
               </stc:Offset>
               <xsl:value-of select="$subsp"/>
            </stc:Halfspace>
         </xsl:if>
         <xsl:value-of select="$sp"/>
      </stc:Convex>

   </xsl:template>

   <!-- 
     -  Render a range of RA/Dec coordinates as an STC (convex) region
     -->
   <xsl:template match="vs2:region[contains(@xsi:type,'CoordRange')]" 
                 mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>
      <xsl:variable name="subsubsp" select="concat($subsp,$step)"/>

      <xsl:choose>

         <!-- the all-sky degenerate case -->
         <xsl:when test="number(vs2:lat/vs2:min) &lt;= -90 and 
                         number(vs2:lat/vs2:max) &gt;= 90  and 
           ((number(vs2:long/vs2:max) > number(vs2:long/vs2:min) and
             number(vs2:long/vs2:max) - number(vs2:long/vs2:min) &gt;= 360) or
            (number(vs2:long/vs2:min) > number(vs2:long/vs2:max) and
             number(vs2:long/vs2:max) + number(vs2:long/vs2:min) &gt;= 520))">

             <xsl:value-of select="$sp"/>
             <stc:AllSky/>
         </xsl:when>

         <xsl:otherwise>
            <xsl:value-of select="$sp"/>
            <stc:Position2VecInterval unit="deg">

               <xsl:value-of select="$subsp"/>
               <stc:LoLimit2Vec>
                  <xsl:value-of select="$subsubsp"/>
                  <stc:C1><xsl:value-of select="vs2:long/vs2:min"/></stc:C1>
                  <xsl:value-of select="$subsubsp"/>
                  <stc:C2><xsl:value-of select="vs2:lat/vs2:min"/></stc:C2>
                  <xsl:value-of select="$subsp"/>
               </stc:LoLimit2Vec>
               <xsl:value-of select="$subsp"/>
               <stc:HiLimit2Vec>
                  <xsl:value-of select="$subsubsp"/>
                  <stc:C1><xsl:value-of select="vs2:long/vs2:max"/></stc:C1>
                  <xsl:value-of select="$subsubsp"/>
                  <stc:C2><xsl:value-of select="vs2:lat/vs2:max"/></stc:C2>
                  <xsl:value-of select="$subsp"/>
               </stc:HiLimit2Vec>
               <xsl:value-of select="$sp"/>
            </stc:Position2VecInterval>
         </xsl:otherwise>
      </xsl:choose>

   </xsl:template>

   <!-- 
     -  Render a range of RA/Dec coordinates as an STC (convex) region
     -->
   <xsl:template match="vs2:region[contains(@xsi:type,'CoordRange')]" 
                 mode="convex">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>

      <xsl:value-of select="$cr"/>
      <xsl:value-of select="$sp"/>
      <xsl:comment>
         <xsl:text> A range in RA and Dec: RA = [</xsl:text> 
         <xsl:value-of select="vs2:long/vs2:min"/>
         <xsl:text>, </xsl:text>
         <xsl:value-of select="vs2:long/vs2:max"/>
         <xsl:text>], Dec = [</xsl:text>
         <xsl:value-of select="vs2:lat/vs2:min"/>
         <xsl:text>, </xsl:text>
         <xsl:value-of select="vs2:lat/vs2:max"/>
         <xsl:text>] </xsl:text>         
      </xsl:comment>

      <xsl:choose>

         <!-- the all-sky degenerate case -->
         <xsl:when test="number(vs2:lat/vs2:min) &lt;= -90 and 
                         number(vs2:lat/vs2:max) &gt;= 90  and 
           ((number(vs2:long/vs2:max) > number(vs2:long/vs2:min) and
             number(vs2:long/vs2:max) - number(vs2:long/vs2:min) &gt;= 360) or
            (number(vs2:long/vs2:min) > number(vs2:long/vs2:max) and
             number(vs2:long/vs2:max) + number(vs2:long/vs2:min) &gt;= 520))">

             <xsl:value-of select="$sp"/>
             <stc:AllSky/>
         </xsl:when>

         <!-- restrict only in RA -->
         <xsl:when test="number(vs2:lat/vs2:min) &lt;= -90 and 
                         number(vs2:lat/vs2:max) &gt;= 90">

            <xsl:value-of select="$sp"/>
            <xsl:comment>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -  WARNING: creating non-compliant STC </xsl:text>
              <xsl:text>regions;</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -    sin(...) and cos(...) must be </xsl:text>
              <xsl:text>evaluated to real numbers</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  </xsl:text>
            </xsl:comment>

            <!-- longitude range -->
            <xsl:apply-templates select="vs2:long" mode="convex">
               <xsl:with-param name="sp" select="$sp"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
         </xsl:when>

         <!-- restrict only in Dec -->
         <xsl:when test="
           ((number(vs2:long/vs2:max) > number(vs2:long/vs2:min) and
             number(vs2:long/vs2:max) - number(vs2:long/vs2:min) &gt;= 360) or
            (number(vs2:long/vs2:min) > number(vs2:long/vs2:max) and
             number(vs2:long/vs2:max) + number(vs2:long/vs2:min) &gt;= 520))">

            <xsl:value-of select="$sp"/>
            <xsl:comment>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -  WARNING: creating non-compliant STC </xsl:text>
              <xsl:text>regions;</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -    sin(...) and cos(...) must be </xsl:text>
              <xsl:text>evaluated to real numbers</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  </xsl:text>
            </xsl:comment>

            <!-- latitude range -->
            <xsl:apply-templates select="vs2:lat" mode="convex">
               <xsl:with-param name="sp" select="$sp"/>
               <xsl:with-param name="step" select="$step"/>
            </xsl:apply-templates>
         </xsl:when>

         <xsl:otherwise>
            <xsl:value-of select="$sp"/>
            <xsl:comment>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -  WARNING: creating non-compliant STC </xsl:text>
              <xsl:text>regions;</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  -    sin(...) and cos(...) must be </xsl:text>
              <xsl:text>evaluated to real numbers</xsl:text>
              <xsl:value-of select="$sp"/>
              <xsl:text>  </xsl:text>
            </xsl:comment>

            <xsl:value-of select="$sp"/>
            <stc:Intersection>

               <!-- longitude range -->
               <xsl:apply-templates select="vs2:long" mode="convex">
                  <xsl:with-param name="sp" select="$subsp"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>

               <!-- latitude range -->
               <xsl:apply-templates select="vs2:lat" mode="convex">
                  <xsl:with-param name="sp" select="$subsp"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>

               <xsl:value-of select="$sp"/>
            </stc:Intersection>
         </xsl:otherwise>
      </xsl:choose>

      <xsl:value-of select="$cr"/>
   </xsl:template>

   <xsl:template match="vs2:region[contains(@xsi:type,'CircleRegion')]" 
                 mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>
      <xsl:variable name="subsubsp" select="concat($subsp,$step)"/>

      <xsl:value-of select="$sp"/>
      <stc:Circle>
         <xsl:value-of select="$subsp"/>
         <stc:Center unit="deg">
            <xsl:value-of select="$subsubsp"/>
            <stc:C1><xsl:value-of select="vs2:center/vs2:long"/></stc:C1>
            <xsl:value-of select="$subsubsp"/>
            <stc:C2><xsl:value-of select="vs2:center/vs2:lat"/></stc:C2>
            <xsl:value-of select="$subsp"/>
         </stc:Center>

         <xsl:value-of select="$subsp"/>
         <stc:Radius pos_unit="deg">
            <xsl:value-of select="vs2:radius"/>
         </stc:Radius>
         <xsl:value-of select="$sp"/>
      </stc:Circle>
   </xsl:template>

   <!--
     -  handle spectral coverage
     -->
   <xsl:template match="vs2:spectral" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:apply-templates select="vs2:range" mode="area">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <!--
     -  render a spectral range as an STC SpectralInterval
     -->
   <xsl:template match="vs2:range" mode="area">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="subsp" select="concat($sp,$step)"/>

      <xsl:value-of select="$sp"/>
      <stc:SpectralInterval unit="m">
         <xsl:value-of select="$subsp"/>
         <stc:LoLimit><xsl:value-of select="vs2:min"/></stc:LoLimit>
         <xsl:value-of select="$subsp"/>
         <stc:HiLimit><xsl:value-of select="vs2:max"/></stc:HiLimit>
         <xsl:value-of select="$sp"/>
      </stc:SpectralInterval>   
   </xsl:template>   

   <!--
     -  override template for the table element.  
     -->
   <xsl:template match="vs2:table">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{local-name()}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:apply-templates select="child::node()">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="$sp"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:column|vs2:param">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{local-name()}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:choose>
            <xsl:when test="vs2:dataType">

               <!-- handle all elements (and space) before dataType -->
               <xsl:if test="*[following-sibling::vs2:dataType]">
                  <xsl:variable name="prepos">
                     <xsl:for-each 
                        select="child::node()[following-sibling::vs2:dataType]">
                        <xsl:if test="position()=last()">
                           <xsl:choose>
                              <xsl:when test="self::text()">
                                 <xsl:value-of select="position()-1"/>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:value-of select="position()"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:if> 
                     </xsl:for-each>
                  </xsl:variable>

                  <xsl:for-each 
                     select="child::node()[position() &lt;= number($prepos)]">
                     <xsl:apply-templates select=".">
                        <xsl:with-param name="sp" select="concat($sp,$step)"/>
                        <xsl:with-param name="step" select="$step"/>
                     </xsl:apply-templates>
                  </xsl:for-each>
               </xsl:if>

               <!-- handle all nodes after the dataType -->
               <xsl:if test="*[preceding-sibling::vs2:dataType]">
                  <xsl:variable name="postpos">
                     <xsl:for-each 
                        select="child::node()[preceding-sibling::vs2:dataType]">
                        <xsl:if test="position()=last()">
                           <xsl:choose>
                              <xsl:when test="self::text()">
                                 <xsl:value-of select="position()-1"/>
                              </xsl:when>
                              <xsl:otherwise>
                                 <xsl:value-of select="position()"/>
                              </xsl:otherwise>
                           </xsl:choose>
                        </xsl:if> 
                     </xsl:for-each>
                  </xsl:variable>

                  <xsl:for-each 
                     select="child::node()[preceding-sibling::vs2:dataType]">
                     <xsl:if test="position() &lt;= number($postpos)">
                        <xsl:apply-templates select=".">
                           <xsl:with-param name="sp" 
                                           select="concat($sp,$step)"/>
                           <xsl:with-param name="step" select="$step"/>
                        </xsl:apply-templates>
                     </xsl:if>
                  </xsl:for-each>
               </xsl:if>

               <xsl:apply-templates select="child::text()[1]" mode="trim"/>
               <xsl:apply-templates select="vs2:dataType">
                  <xsl:with-param name="sp" select="concat($sp,$step)"/>
                  <xsl:with-param name="step" select="$step"/>
               </xsl:apply-templates>
            </xsl:when>
         </xsl:choose>

         <xsl:value-of select="child::text()[position()=last()]"/>
         <xsl:value-of select="$sp"/>
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:dataType" mode="param">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{local-name()}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         
      </xsl:element>
   </xsl:template>

   <xsl:template match="vs2:dataType">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{local-name()}">

         <xsl:choose>
            <xsl:when test="parent::node()[local-name()='param']">
               <xsl:for-each select="@*[local-name()!='arraysize' and 
                                        (parent::node()!='char' or 
                                         parent::node()!='unicodeChar')]">
                  <xsl:copy/>
               </xsl:for-each>

               <xsl:choose>
                  <xsl:when test="self::node()='float' or 
                                  self::node()='double'">
                     <xsl:text>real</xsl:text>
                  </xsl:when>
                  <xsl:when test="self::node()='int' or self::node()='long' or 
                                  self::node()='short'">
                     <xsl:text>integer</xsl:text>
                  </xsl:when>
                  <xsl:when test="(self::node()='char' or 
                                   self::node()='unicodeChar') and 
                                  @arraysize='*'">
                     <xsl:text>string</xsl:text>
                  </xsl:when>
                  <xsl:when test="self::node()='bit'">
                     <xsl:text>boolean</xsl:text>
                  </xsl:when>
                  <xsl:when test="self::node()='complex' or 
                                  self::node()='doubleComplex'">
                     <xsl:text>complex</xsl:text>
                  </xsl:when>
                  <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
               </xsl:choose>
            </xsl:when>

            <xsl:otherwise>
               <xsl:for-each select="@*">
                  <xsl:copy/>
               </xsl:for-each>
               <xsl:value-of select="."/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:element>
   </xsl:template>

   <!-- 
     -  override template for the interface element.  
     -->
   <xsl:template match="vr2:interface">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="role"/>

      <xsl:value-of select="$sp"/>

      <xsl:element name="{local-name()}">
         <xsl:for-each select="@*[local-name()!='qtype' and
                                  local-name()!='type']">
            <xsl:copy/>
         </xsl:for-each>
         <xsl:if test="$role != ''">
            <xsl:attribute name="role">
               <xsl:value-of select="$role"/>
            </xsl:attribute>
         </xsl:if>

         <xsl:apply-templates select="." mode="resource">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="$sp"/>
      </xsl:element>
   </xsl:template>

   <!-- 
     -  override template for the interface element.  
     -->
   <xsl:template match="vr2:interface" mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:for-each select="@xsi:type">
         <xsl:copy/>
      </xsl:for-each>      

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='ParamHTTP' or 
                                      contains(@xsi:type,':ParamHTTP')]"
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>
      <xsl:param name="role"/>

      <xsl:for-each select="@xsi:type">
         <xsl:copy/>
      </xsl:for-each>

      <xsl:if test="$role!=''">
         <xsl:attribute name="role">
            <xsl:value-of select="$role"/>
         </xsl:attribute>
      </xsl:if>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:if test="@qtype">
         <xsl:apply-templates select="vr2:accessURL/following::text()[1]" />
         <xsl:value-of select="$sp"/>
         <queryType><xsl:value-of select="@qtype"/></queryType>
      </xsl:if>

      <xsl:apply-templates 
           select="child::node()[preceding-sibling::vr2:accessURL]">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
   </xsl:template>

   <xsl:template match="vr2:interface" mode="core">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="acpos">
         <xsl:for-each select="vr:accessURL[1]">
            <xsl:value-of select="position()"/>
         </xsl:for-each>
      </xsl:variable>

      <xsl:apply-templates 
           select="vr2:accessURL |
                   child::node()[following-sibling::vr2:accessURL]">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='WebBrowser' or 
                                      contains(@xsi:type,':WebBrowser')]"
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:for-each select="@xsi:type">
         <xsl:copy/>
      </xsl:for-each>      

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="vr2:accessURL/following-sibling[1]">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
      
   </xsl:template>

   <xsl:template match="vr2:interface[@xsi:type='WebService' or 
                                      contains(@xsi:type,':WebService')]"
                 mode="resource">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:variable name="url">
         <xsl:choose>
            <xsl:when test="contains(accessURL,'?wsdl')">
               <xsl:value-of select="substring-before(accessURL,'?wsdl')"/>
            </xsl:when>
         </xsl:choose>
      </xsl:variable>

      <xsl:attribute name="type" 
                     namespace="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:value-of select="'vr:WebService'"/>
      </xsl:attribute>

      <xsl:apply-templates select="." mode="core">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="vr2:accessURL/following-sibling::text()[1]">
         <xsl:with-param name="sp" select="$sp"/>
         <xsl:with-param name="step" select="$step"/>
      </xsl:apply-templates>
      
   </xsl:template>

   <xsl:template match="vr2:accessURL">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>
      <xsl:element name="{local-name()}">
         <xsl:for-each select="@*">
            <xsl:copy/>
         </xsl:for-each>

         <xsl:choose>
            <xsl:when test="contains(.,'?wsdl')">
               <xsl:value-of select="substring-after(.,'?wsdl')"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="."/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:element>
   </xsl:template>

   <!--
     - transform an SIA capability
     -->
   <xsl:template match="sia2:capability">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <capability standardID="ivo://ivoa.net/std/SIA"
                  xsi:type="sia:SimpleImageAccess">

         <xsl:if test="$addValidationLevel">
            <xsl:apply-templates select="child::text()[1]" />
            <xsl:value-of select="concat($sp,$step)"/>

            <validationLevel validatedBy="{$validatedBy}">
               <xsl:value-of select="$validationLevel"/>
            </validationLevel>
         </xsl:if>

         <xsl:variable name="substep">
            <xsl:choose>
               <xsl:when test="$prettyPrint">
                  <xsl:value-of select="$step"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:apply-templates select="." mode="getIndentStep"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>

         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vr2:interface[contains(@xsi:type,'ParamHTTP')]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
            <xsl:with-param name="role">std</xsl:with-param>
         </xsl:apply-templates>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vr2:interface[not(contains(@xsi:type,'ParamHTTP'))]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
         </xsl:apply-templates>

         <xsl:apply-templates select="child::node()">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="concat($sp,$substep)"/>
         <testQuery>

            <!-- figure out the spacing to use for sub-elements -->
            <xsl:variable name="subsp">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="concat($sp,$step)"/>
                  </xsl:when>
                  <xsl:when test="contains(child::text()[1],$cr)">
                     <xsl:apply-templates select="child::text()[1]" 
                                          mode="trim"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$cr"/>
                     <xsl:call-template name="doindent">
                        <xsl:with-param name="nlev" select="3"/>
                     </xsl:call-template>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>

            <xsl:value-of select="concat($subsp,$substep)"/>
            <pos>
               <xsl:value-of select="concat($subsp,$substep,$substep)"/>
               <long>120</long>
               <xsl:value-of select="concat($subsp,$substep,$substep)"/>
               <lat>20</lat>
               <xsl:value-of select="concat($subsp,$substep)"/>
            </pos>
            <xsl:value-of select="concat($subsp,$substep)"/>
            <size>
               <xsl:value-of select="concat($subsp,$substep,$substep)"/>
               <long>1</long>
               <xsl:value-of select="concat($subsp,$substep,$substep)"/>
               <lat>1</lat>
               <xsl:value-of select="concat($subsp,$substep)"/>
            </size>
            <xsl:value-of select="$subsp"/>
         </testQuery>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:value-of select="$sp"/>
      </capability>
   </xsl:template>

   <!--
     - transform an cone search capability
     -->
   <xsl:template match="cs2:capability">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <capability standardID="ivo://ivoa.net/std/ConeSearch"
                  xsi:type="cs:ConeSearch">

         <xsl:if test="$addValidationLevel">
            <xsl:apply-templates select="child::text()[1]" />
            <xsl:value-of select="concat($sp,$step)"/>

            <validationLevel validatedBy="{$validatedBy}">
               <xsl:value-of select="$validationLevel"/>
            </validationLevel>
         </xsl:if>

         <xsl:variable name="substep">
            <xsl:choose>
               <xsl:when test="$prettyPrint">
                  <xsl:value-of select="$step"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:apply-templates select="." mode="getIndentStep"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>

         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vr2:interface[contains(@xsi:type,'ParamHTTP')]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
            <xsl:with-param name="role">std</xsl:with-param>
         </xsl:apply-templates>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vr2:interface[not(contains(@xsi:type,'ParamHTTP'))]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
         </xsl:apply-templates>

         <xsl:apply-templates select="child::node()">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="concat($sp,$substep)"/>
         <testQuery>

            <!-- figure out the spacing to use for sub-elements -->
            <xsl:variable name="subsp">
               <xsl:choose>
                  <xsl:when test="$prettyPrint">
                     <xsl:value-of select="concat($sp,$step)"/>
                  </xsl:when>
                  <xsl:when test="contains(child::text()[1],$cr)">
                     <xsl:apply-templates select="child::text()[1]" 
                                          mode="trim"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$cr"/>
                     <xsl:call-template name="doindent">
                        <xsl:with-param name="nlev" select="3"/>
                     </xsl:call-template>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>

            <xsl:value-of select="concat($subsp,$substep)"/>
            <ra> 120 </ra>
            <xsl:value-of select="concat($subsp,$substep)"/>
            <dec> 20 </dec>
            <xsl:value-of select="concat($subsp,$substep)"/>
            <sr> 0.5 </sr>
            <xsl:value-of select="$subsp"/>
         </testQuery>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:value-of select="$sp"/>
      </capability>
   </xsl:template>

   <!--
     - transform a SkyNode capability
     -->
   <xsl:template match="sn2:capability">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <capability standardID="ivo://ivoa.net/std/OpenSkyNode"
                  xsi:type="sn:OpenSkyNode">

         <xsl:if test="$addValidationLevel">
            <xsl:apply-templates select="child::text()[1]" />
            <xsl:value-of select="concat($sp,$step)"/>

            <validationLevel validatedBy="{$validatedBy}">
               <xsl:value-of select="$validationLevel"/>
            </validationLevel>
         </xsl:if>

         <xsl:variable name="substep">
            <xsl:choose>
               <xsl:when test="$prettyPrint">
                  <xsl:value-of select="$step"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:apply-templates select="." mode="getIndentStep"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>

         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vr2:interface[contains(@xsi:type,'WebService')]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:apply-templates
              select="../vs2rinterface[not(contains(@xsi:type,'WebService'))]">
            <xsl:with-param name="sp" select="concat($sp,$substep)"/>
            <xsl:with-param name="substep" select="$substep"/>
         </xsl:apply-templates>

         <xsl:apply-templates select="child::node()">
            <xsl:with-param name="sp" select="concat($sp,$step)"/>
            <xsl:with-param name="step" select="$step"/>
         </xsl:apply-templates>

         <xsl:value-of select="concat($sp,$substep)"/>
         <xsl:apply-templates select="preceding-sibling::text()[1]" 
                              mode="trim"/>
         <xsl:value-of select="$sp"/>
      </capability>
   </xsl:template>

   <xsl:template match="sn2:Compliance">
      <xsl:param name="sp"/>
      <xsl:param name="step"/>

      <xsl:value-of select="$sp"/>

      <compliance>
         <xsl:call-template name="lowerandcap">
            <xsl:with-param name="in" select="."/>
         </xsl:call-template>
      </compliance>
   </xsl:template>

   <!-- ==========================================================
     -  Utility templates
     -  ========================================================== -->

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

   <!--
     -  determine the indentation preceding the context element
     -->
   <xsl:template name="getindent">
      <xsl:variable name="prevsp">
         <xsl:for-each select="preceding-sibling::text()">
            <xsl:if test="position()=last()">
               <xsl:value-of select="."/>
            </xsl:if>
         </xsl:for-each>
      </xsl:variable>

      <xsl:choose>
         <xsl:when test="contains($prevsp,$cr)">
            <xsl:call-template name="afterLastCR">
               <xsl:with-param name="text" select="$prevsp"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:when test="$prevsp">
            <xsl:value-of select="$prevsp"/>
         </xsl:when>
         <xsl:otherwise><xsl:text>    </xsl:text></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--
     -  return the text that appears after the last carriage return
     -  in the input text
     -  @param text  the input text to process
     -->
   <xsl:template name="afterLastCR">
      <xsl:param name="text"/>
      <xsl:choose>
         <xsl:when test="contains($text,$cr)">
            <xsl:call-template name="afterLastCR">
               <xsl:with-param name="text" select="substring-after($text,$cr)"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise><xsl:value-of select="$text"/></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--
     -  indent the given number of levels.  The amount of indentation will 
     -  be nlev times the value of the global $indent.
     -  @param nlev   the number of indentations to insert.
     -  @param sp     the string representing the per-indentation space;
     -                  defaults to the value of $indent
     -->
   <xsl:template name="doindent">
      <xsl:param name="nlev" select="2"/>
      <xsl:param name="sp" select="$indent"/>

      <xsl:if test="$nlev &gt; 0">
         <xsl:value-of select="$sp"/>
         <xsl:if test="$nlev &gt; 1">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$nlev - 1"/>
               <xsl:with-param name="sp" select="$sp"/>
            </xsl:call-template>
         </xsl:if>
      </xsl:if>
   </xsl:template>

   <!--
     -  convert all input characters to lower case
     -  @param in  the string to convert
     -->
   <xsl:template name="lower">
      <xsl:param name="in"/>
      <xsl:value-of select="translate($in,
                                      'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
                                      'abcdefghijklmnopqrstuvwxyz')"/>
   </xsl:template>

   <!--
     -  convert the first character to an upper case
     -  @param in  the string to convert
     -->
   <xsl:template name="capitalize">
      <xsl:param name="in"/>
      <xsl:value-of select="translate(substring($in,1,1),
                                      'abcdefghijklmnopqrstuvwxyz',
                                      'ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
      <xsl:value-of select="substring($in,2)"/>
   </xsl:template>

   <!--
     -  convert all input characters to lower case and then capitalize
     -  @param in  the string to convert
     -->
   <xsl:template name="lowerandcap">
      <xsl:param name="in"/>
      <xsl:call-template name="capitalize">
         <xsl:with-param name="in">
            <xsl:call-template name="lower">
               <xsl:with-param name="in" select="$in"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:template>

   <!--
     -  measure the indentation inside a node
     -  @param text   a text node containing the indentation
     -->
   <xsl:template name="measIndent">
      <xsl:param name="text"/>

      <xsl:variable name="indnt">
         <xsl:call-template name="afterLastCR">
            <xsl:with-param name="text" select="$text"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:value-of select="string-length($indnt)"/>
   </xsl:template>

   <!--
     -  attempt to return the extra indentation applied to children 
     -    of the current context node.  If a positive indentation cannot
     -    be returned, return the default indentation.
     -->
   <xsl:template match="*" mode="getIndentStep">
      <xsl:variable name="prevind">
         <xsl:call-template name="measIndent">
            <xsl:with-param name="text">
               <xsl:call-template name="getindent"/>
            </xsl:with-param>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="childind">
         <xsl:call-template name="measIndent">
            <xsl:with-param name="text">
               <xsl:for-each select="*[1]">
                  <xsl:call-template name="getindent"/>
               </xsl:for-each>
            </xsl:with-param>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="diff" select="$childind - $prevind"/>

      <xsl:choose>
         <xsl:when test="$childind &gt; $prevind and $prevind &gt; 0">
            <xsl:call-template name="doindent">
               <xsl:with-param name="nlev" select="$diff"/>
               <xsl:with-param name="sp" select="' '"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise><xsl:value-of select="$indent"/></xsl:otherwise>
      </xsl:choose>
   </xsl:template>

</xsl:stylesheet>
