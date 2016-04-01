<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
   version="1.0" 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
   xmlns:ri1_0="http://www.ivoa.net/xml/RegistryInterface/v1.0"
   xmlns:exist="http://exist.sourceforge.net/NS/exist"
   xmlns:oai="http://www.openarchives.org/OAI/2.0/">

   <xsl:output method="xml" />

    <xsl:template match="exist:match">
         <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="ri1_0:VOResources">
      <xsl:element name="OAIINFO">
         <xsl:apply-templates />
      </xsl:element>
    </xsl:template>


    <xsl:template match="ri1_0:Resource">
      <xsl:variable name="dtVar">
            <xsl:choose>
               <xsl:when test="@updated">
                  <xsl:value-of select="@updated"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="@created"/>
               </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>
        <xsl:element name="OAIRecord">
         <oai:record>
       		<oai:header>
			   <oai:identifier>

                  <xsl:value-of select="./identifier"/>
                </oai:identifier>
				<oai:datestamp>
                  <xsl:choose>
                     <xsl:when test="string-length($dtVar) &gt;= 19">
                        <xsl:value-of select="concat(substring($dtVar,1,19),'Z')" />
                     </xsl:when>
                     <xsl:when test="string-length($dtVar) = 10">
                        <xsl:value-of select="concat(substring($dtVar,1,10),'T00:00:00Z')" />
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$dtVar" />
                     </xsl:otherwise>
                  </xsl:choose>
               </oai:datestamp>
               <oai:setSpec>
                  <xsl:text>I</xsl:text>
               </oai:setSpec>
               <xsl:if test="@status = 'deleted'">
                  <oai:status>
                     <xsl:text>deleted</xsl:text>
                  </oai:status>
               </xsl:if>
            </oai:header>
            <oai:metadata>
                 <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
                            xmlns:dc="http://purl.org/dc/elements/1.1/">
                  <xsl:if test="./title">
                     <xsl:element name="dc:title">
                        <xsl:value-of select="./title"/>
                     </xsl:element>
                  </xsl:if>
                  <xsl:if test="./curation/publisher">
                     <xsl:element name="dc:creator">
                        <xsl:value-of select="./curation/publisher"/>
                     </xsl:element>                  
                  </xsl:if>                  
                  <xsl:if test="./curation/creator/name">
                     <xsl:element name="dc:creator">
                        <xsl:value-of select="./curation/creator/name"/>
                     </xsl:element>                  
                  </xsl:if>
                  <xsl:if test="./curation/contributer/name">
                     <xsl:element name="dc:creator">
                        <xsl:value-of select="./curation/creator/name"/>
                     </xsl:element>                  
                  </xsl:if>                  
                  <xsl:if test="./content/description">
                     <xsl:element name="dc:description">
                        <xsl:value-of select="./content/description"/>
                     </xsl:element>
                  </xsl:if>
                  <xsl:for-each select="./content/subject">
                     <xsl:element name="dc:subject">
                        <xsl:value-of select="."/>
                     </xsl:element>                  
                  </xsl:for-each>
                  <xsl:for-each select="./type">
                     <xsl:element name="dc:type">
                        <xsl:value-of select="."/>
                     </xsl:element>
                  </xsl:for-each>
                  <xsl:element name="dc:date">
                     <xsl:choose>
                        <xsl:when test="string-length($dtVar) &gt;= 19">
                           <xsl:value-of select="concat(substring($dtVar,1,19),'')" />
                        </xsl:when>
                        <xsl:when test="string-length($dtVar) = 10">
                           <xsl:value-of select="concat(substring($dtVar,1,10),'T00:00:00')" />
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:value-of select="$dtVar" />
                        </xsl:otherwise>
                     </xsl:choose>
                  </xsl:element>
                  <xsl:element name="dc:identifier">
                     <xsl:value-of select="./identifier"/>
                  </xsl:element>
                  <xsl:if test="./content/source/@format">
                     <xsl:element name="dc:format">
                        <xsl:value-of select="./content/source/@format"/>
                     </xsl:element>
                  </xsl:if>
                  <xsl:if test="./content/source">
                     <xsl:element name="dc:format">
                        <xsl:value-of select="./content/source"/>
                     </xsl:element>
                  </xsl:if>
               </oai_dc:dc>
               <!--
  				<Resource xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0">
                  <xsl:apply-templates select="@*|node()"/>
               </Resource>
               -->
               	<xsl:copy>
        		    <xsl:apply-templates select="@*|node()"/>
		        </xsl:copy>
            </oai:metadata>
         </oai:record>
        </xsl:element>
    </xsl:template>    
        
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>