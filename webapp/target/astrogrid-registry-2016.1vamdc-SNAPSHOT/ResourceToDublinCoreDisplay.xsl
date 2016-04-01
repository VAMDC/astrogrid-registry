<?xml version="1.0" encoding="UTF-8"?>

<!-- A stylesheet to display an arbitrary registration-document
  as a page of HTML. The Dublin-core information is transcribed
  to an HTML table (based on the form for editing the Dublin core),
  and the extension elements are transscribed verbatim. This 
  stylesheet is intended to be used in a processing instruction to
  a web browser. Hence, it generates a complete page of HTML rather
  than a fragment. -->

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
  xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0">
  
  <xsl:output method="html"  encoding="UTF-8"/>
  
  <xsl:template match="//vr:Resource|//ri:Resource">
    <html>
      <head>
        <title>Registry entry for <xsl:value-of select="identifier"/></title>
      </head>
      <body>
        <h1>Registry entry for <xsl:value-of select="identifier"/></h1>
        <table>
          <tr>
            <td><strong>IVO identifier</strong></td>
            <td><xsl:value-of select="identifier"/></td>
          </tr>
          <tr>
            <td><strong>Resource status</strong></td>
            <td><xsl:value-of select="@status"/></td>
          </tr>
          <tr>
            <td><strong>Title</strong></td>
            <td><xsl:value-of select="title"/></td>
          </tr>
          <tr>
            <td><strong>Publisher's name</strong></td>
            <td><xsl:value-of select="curation/publisher"/></td>
          </tr>
          <tr>
            <td>Publisher's IVO identifier</td>
            <td><xsl:value-of select="curation/publisher/@ivo-id"/></td>
          </tr>
          <tr>
            <td><strong>Creator's name</strong></td>
            <td><xsl:value-of select="curation/creator/name"/></td>
          </tr>
          <tr>
            <td>Creator's IVO identifier</td>
            <td><xsl:value-of select="curation/creator/name/@ivo-id"/></td>
          </tr>
          <tr>
            <td>URL of creator's logo</td>
            <td><xsl:value-of select="curation/creator/logo"/></td>
          </tr>
          <tr>
            <td><strong>Date of publication</strong></td>
            <td><xsl:value-of select="curation/date"/></td>
          </tr>
          <tr>
            <td><strong>Version of publication</strong></td>
            <td><xsl:value-of select="curation/version"/></td>
          </tr>
          <tr>
            <td><strong>Name of contact person</strong></td>
            <td><xsl:value-of select="curation/contact/name"/></td>
          </tr>
          <tr>
            <td>Postal address of contact person</td>
            <td><xsl:value-of select="curation/contact/address"/></td>
          </tr>
          <tr>
            <td><strong>Email address of contact person</strong></td>
            <td><xsl:value-of select="curation/contact/email"/></td>
          </tr>
          <tr>
            <td>Telephone number of contact person</td>
            <td><xsl:value-of select="curation/contact/telephone"/></td>
          </tr>
          <tr>
            <td><strong>Keywords describing this resource</strong></td>
            <td><xsl:value-of select="content/subject"/></td>
          </tr>
          <tr>
            <td><strong>Text describing this resource</strong></td>
            <td><xsl:value-of select="content/description"/></td>
          </tr>
          <tr>
            <td>Source of the resource content</td>
            <td><xsl:value-of select="content/source"/></td>
          </tr>
          <tr>
            <td><strong>URL for web page describing this resource</strong></td>
            <td><xsl:value-of select="content/referenceURL"/></td>
          </tr>
          <tr>
            <td><strong>Type of the resource content</strong></td>
            <td><xsl:value-of select="content/type"/></td>
          </tr>
          <tr>
            <td>Intended audience</td>
            <td><xsl:value-of select="content/contentLevel"/></td>
          </tr>
        </table>
        <hr/>
        <p>
          <xsl:for-each select="*">
            <xsl:call-template name="transcribe-XML"/>
          </xsl:for-each>
        </p>
      </body>
    </html>
  </xsl:template>
  
  <!-- Dublin-core elements are dealt with above in the match on the Resource element.
    Do nothing when a Dublin-core element is matched separately. -->
  <xsl:template match="validationLevel|title|shortName|identifier|curation|content"/>
  
  <xsl:template name="transcribe-XML" match="*">
    <xsl:param name="indent"/>
    <br/><xsl:value-of select="$indent"/>
    <xsl:text>&lt;</xsl:text>
    <xsl:value-of select="local-name(.)"/>
    <xsl:for-each select="@*"><xsl:text> </xsl:text><xsl:value-of select="name(.)"/>="<xsl:value-of select="."/>"</xsl:for-each>
    <xsl:text>&gt;</xsl:text>
    <xsl:value-of select="text()"/>
    <xsl:for-each select="*">
      <xsl:call-template name="transcribe-XML">
        <xsl:with-param name="indent" select="concat($indent,'&#160;&#160;&#160;')"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:if test="count(child::*) > 0">
      <br/><xsl:value-of select="$indent"/>
    </xsl:if>
    <xsl:text>&lt;</xsl:text>
    <xsl:text>/</xsl:text>
    <xsl:value-of select="local-name(.)"/>
    <xsl:text>&gt;</xsl:text>
  </xsl:template>
  
  <xsl:template match="text()|@*">
    <xsl:apply-templates/>
  </xsl:template>
  
</xsl:stylesheet>

