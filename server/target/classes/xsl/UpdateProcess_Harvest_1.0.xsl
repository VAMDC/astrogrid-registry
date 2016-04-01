<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:oai="http://www.openarchives.org/OAI/2.0/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                version="1.0">

   <!-- 
                   exclude-result-prefixes="vr2 vc2 vg2 vs2 cs2 sia2 vor cea2" 
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

   <xsl:template match="ListRecordsResponse">
            <xsl:apply-templates />
   </xsl:template>
   
   <xsl:template match="oairegws:ListRecordsResponse" xmlns:oairegws="http://www.ivoa.net/wsdl/RegistryHarvest/v1.0">
            <xsl:apply-templates />
   </xsl:template>
   
   <xsl:template match="oai:OAI-PMH">
     <ri:VOResources xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" from="1" numberReturned="100" more="false">
	 	<xsl:apply-templates select="*/oai:record/oai:metadata"/>
     </ri:VOResources>

   </xsl:template>
   
   <xsl:template match="oai:metadata">
            <xsl:apply-templates />
   </xsl:template>
   

       <xsl:template match="@*|node()">
           <xsl:copy>
               <xsl:apply-templates select="@*|node()"/>
           </xsl:copy>
       </xsl:template>

</xsl:stylesheet>