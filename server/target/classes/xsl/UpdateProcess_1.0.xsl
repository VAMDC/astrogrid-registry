<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet 
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:output method="xml" />
	<xsl:template match="@id">
	   <xsl:variable name="valofattr" select="." />
	   <xsl:variable name="valofident" select="translate(substring(../../../../identifier,7),'.+-!#/','______')" />
	   <xsl:variable name="valofattrsub" select="substring($valofattr,1,string-length($valofident))" />
	   <xsl:attribute name="id">
	   <xsl:if test="contains(namespace-uri(..),'http://www.ivoa.net/xml/STC')">
           <xsl:if test="(string-length($valofattrsub) = 0) or (string-length($valofattrsub) &lt; string-length($valofident)) or (not(contains($valofident,$valofattrsub)))">
	   		<xsl:value-of select="$valofident" /><xsl:text>_</xsl:text></xsl:if></xsl:if><xsl:value-of select="$valofattr"/>
	   </xsl:attribute>
    </xsl:template>

	<xsl:template match="@frame_id">
	   <xsl:variable name="valofattr" select="." />
	   <xsl:variable name="valofident" select="translate(substring(../../../../identifier,7),'.+-!#/','______')" />
	   <xsl:variable name="valofattrsub" select="substring($valofattr,1,string-length($valofident))" />
	   
           <xsl:attribute name="frame_id">
	   <xsl:if test="contains(namespace-uri(..),'http://www.ivoa.net/xml/STC')">
           <xsl:if test="(string-length($valofattrsub) = 0) or (string-length($valofattrsub) &lt; string-length($valofident)) or (not(contains($valofident,$valofattrsub)))">
	   <xsl:value-of select="$valofident" /><xsl:text>_</xsl:text></xsl:if></xsl:if><xsl:value-of select="$valofattr"/>
	   </xsl:attribute>
        </xsl:template>
	
	<xsl:template match="@idref">
	   <xsl:variable name="valofattr" select="." />
	   <xsl:variable name="valofident" select="translate(substring(../../../../identifier,7),'.+-!#/','______')" />
	   <xsl:variable name="valofattrsub" select="substring($valofattr,1,string-length($valofident))" />
	   <xsl:attribute name="idref">
	   <xsl:if test="contains(namespace-uri(..),'http://www.ivoa.net/xml/STC')">
           <xsl:if test="(string-length($valofattrsub) = 0) or (string-length($valofattrsub) &lt; string-length($valofident)) or (not(contains($valofident,$valofattrsub)))">
	   <xsl:value-of select="$valofident" /><xsl:text>_</xsl:text></xsl:if></xsl:if><xsl:value-of select="$valofattr"/>
	   </xsl:attribute>
        </xsl:template>

	<xsl:template match="@coord_system_id">
	   <xsl:variable name="valofattr" select="." />
	   <xsl:variable name="valofident" select="translate(substring(../../../../identifier,7),'.+-!#/','______')" />
	   <xsl:variable name="valofattrsub" select="substring($valofattr,1,string-length($valofident))" />
	   <xsl:attribute name="coord_system_id">
	   <xsl:if test="contains(namespace-uri(..),'http://www.ivoa.net/xml/STC')">
           <xsl:if test="(string-length($valofattrsub) = 0) or (string-length($valofattrsub) &lt; string-length($valofident)) or (not(contains($valofident,$valofattrsub)))">
	   <xsl:value-of select="$valofident" /><xsl:text>_</xsl:text></xsl:if></xsl:if><xsl:value-of select="$valofattr"/>
	   </xsl:attribute>
        </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
