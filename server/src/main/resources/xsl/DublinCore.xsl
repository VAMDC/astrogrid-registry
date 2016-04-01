<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
  xmlns:agr="urn:astrogrid:schema:RegistryStoreResource:v1">

 <xsl:output 
      method="xml" 
      version="1.0" 
      encoding="UTF-8" />
  
  <xsl:param name="updated"/>
  
  <xsl:param name="status">active</xsl:param>
  <xsl:param name="validationLevel"/>
  <xsl:param name="title"/>
  <xsl:param name="shortName"/>
  <xsl:param name="publisherName"/>
  <xsl:param name="publisherId"/>
  <xsl:param name="creatorName"/>
  <xsl:param name="creatorId"/>
  <xsl:param name="creatorLogo"/>
  <xsl:param name="date"/>
  <xsl:param name="version"/>
  <xsl:param name="contactName"/>
  <xsl:param name="contactId"/>
  <xsl:param name="contactAddress"/>
  <xsl:param name="contactEmail"/>
  <xsl:param name="contactTelephone"/>
  <xsl:param name="subject"/>
  <xsl:param name="description"/>
  <xsl:param name="referenceURL">http://www.astrogrid.org/</xsl:param>
  <xsl:param name="type"/>
  <xsl:param name="contentLevel"/>
  <xsl:param name="contentSource"/>  
  <xsl:param name="relationship"/>
  <xsl:param name="vosiURL"/>
  <xsl:param name="webbrowserRefURL"/>  
  
  <!-- Copy all the existing structure and add the capabilities at the
       end inside the document element. -->
  <xsl:template match="ri:Resource">
    <!-- <agr:AstrogridResource> -->
      <ri:Resource updated="{$updated}" status="{$status}" xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
        xmlns:vs="http://www.ivoa.net/xml/VODataService/v1.0"
        xmlns:vg="http://www.ivoa.net/xml/VORegistry/v1.0" 
        xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0"
        xmlns:cea="http://www.ivoa.net/xml/CEA/v1.0rc1" 
        xmlns:va="http://www.ivoa.net/xml/VOApplication/v1.0rc1">
        
        <!-- Copy the fixed attributes -->
        <xsl:copy-of select="@xsi:type"/>
        <xsl:copy-of select="@created"/>

        <xsl:if test="$validationLevel">
          <xsl:element name="validationLevel"><xsl:value-of select="$validationLevel"/></xsl:element>
        </xsl:if>
        
        <xsl:element name="title"><xsl:value-of select="$title"/></xsl:element>
        
        <xsl:if test="$shortName">
          <xsl:element name="shortName"><xsl:value-of select="$shortName"/></xsl:element>
        </xsl:if>
        
        <xsl:copy-of select="identifier"/>
        
        <xsl:call-template name="curation"/>
        
        <xsl:call-template name="content"/>
        
        <!-- Copy everything following the Dublin Core -->
        <xsl:apply-templates select="content"/>
        
      </ri:Resource>
    <!-- </agr:AstrogridResource> -->
  </xsl:template>
  
  <!-- Write the curation information -->
  <xsl:template name="curation">
    <curation>
      
      <publisher>
        <xsl:if test="$publisherId">
          <xsl:attribute name="ivo-id"><xsl:value-of select="$publisherId"/></xsl:attribute>
        </xsl:if>
        <xsl:value-of select="$publisherName"/>
      </publisher>
      
      <creator>
        <xsl:if test="$creatorId">
          <xsl:attribute name="ivo-id"><xsl:value-of select="$creatorId"/></xsl:attribute>
        </xsl:if>
        <name><xsl:value-of select="$creatorName"/></name>
        <xsl:if test="$creatorLogo">
          <logo><xsl:value-of select="$creatorLogo"/></logo>
        </xsl:if>
      </creator>
      
      <xsl:copy-of select="contributor"/>
      <xsl:if test="$date">
      <date><xsl:value-of select="$date"/></date>
      </xsl:if>
      
      <xsl:if test="$version">
      <version><xsl:value-of select="$version"/></version>
      </xsl:if>
      
      <contact>
        <name><xsl:value-of select="$contactName"/></name>
        <xsl:if test="$contactId">
          <xsl:attribute name="ivo-id">$contactId</xsl:attribute>
        </xsl:if>
        <xsl:if test="$contactAddress">
          <address><xsl:value-of select="$contactAddress"/></address>
        </xsl:if>
        <xsl:if test="$contactEmail">
          <email><xsl:value-of select="$contactEmail"/></email>
        </xsl:if>
        <xsl:if test="$contactTelephone">
          <telephone><xsl:value-of select="$contactTelephone"/></telephone>
        </xsl:if>
      </contact>
      
    </curation>    
  </xsl:template>
  
  <!-- Write the content information -->
  <xsl:template name="content">
    <content>
      
      <subject><xsl:value-of select="$subject"/></subject>
      
      <description><xsl:value-of select="$description"/></description>
      <!-- 
      <xsl:copy-of select="content/source"/>
      -->
      <xsl:if test="$contentSource">      
	      <source>
		      <xsl:attribute name="format"></xsl:attribute>
		      <xsl:value-of select="$contentSource"/>
	      </source>
      </xsl:if>
      
      <referenceURL><xsl:value-of select="$referenceURL"/></referenceURL>
      
      <xsl:if test="$type">
        <type><xsl:value-of select="$type"/></type>
      </xsl:if>
      
      <xsl:if test="$contentLevel">
        <contentLevel><xsl:value-of select="$contentLevel"/></contentLevel>
      </xsl:if>
      
      <xsl:copy-of select="content/relationship"/>
      
    </content>
    
    <xsl:choose>
		<xsl:when test="not($webbrowserRefURL)"> 
			<!-- parameter has not been supplied don't do anything -->
		</xsl:when>
		<xsl:otherwise> 
    		<capability> 
   				<interface xsi:type="vr:WebBrowser"> 
      				<accessURL use="full"><xsl:value-of select="$webbrowserRefURL"/></accessURL>
   				</interface>
			</capability>
		</xsl:otherwise>
	</xsl:choose>
			
   	<xsl:choose>
		<xsl:when test="not($vosiURL)"> 
			<!-- parameter has not been supplied don't do anything -->
		</xsl:when>
		<xsl:otherwise> 
		  <capability standardID="ivo://org.astrogrid/std/VOSI/v0.3#capabilities">
	  		<interface xsi:type="vs:ParamHTTP">
  				<accessURL use="full"><xsl:value-of select="$vosiURL"/></accessURL>
  				<queryType>GET</queryType>
  				<resultType>application/xml</resultType>
			</interface>
			</capability>
		</xsl:otherwise>
	</xsl:choose>
  
  </xsl:template>
  
  <!-- Copy everything following the Dublin Core -->
  <xsl:template match="content">
    <xsl:copy-of select="following-sibling::*"/>
  </xsl:template>

</xsl:stylesheet>