<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:vr="http://www.ivoa.net/xml/VOResource/v1.0"
    xmlns:ri="http://www.ivoa.net/xml/RegistryInterface/v1.0" 
    exclude-result-prefixes="vr ri">
  
  <!-- This assumes that the servlet or JSP emitting the result of this script
       will not add a DOCTYPE element to the output. --> 
  <xsl:output 
      method="html" 
      version="4.0" 
      indent="yes" 
      encoding="UTF-8"
      doctype-public="-//W3C//DTD HTML 4.01//EN"
      doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>
  
  <xsl:param name="vosiURL"/> 
  
    <xsl:template match="//vr:Resource|//ri:Resource">  
    <xsl:element name="html">
      <head>
        <title>Core metadata: editor</title>
        
        <link rel="stylesheet" type="text/css" href="style/astrogrid.css"></link>
      </head>
      <body>
        <div id='bodyColumn'> 

        <h1>Core metadata: editor</h1>
        <form action="DublinCore" method="post">
          <div>
            <input type="hidden" name="IVORN">
              <xsl:attribute name="value"><xsl:value-of select="identifier"/></xsl:attribute>
            </input>
          </div>
          <table>
            <tr>
              <td><strong>IVO identifier</strong></td>
              <td><xsl:value-of select="identifier"/></td>
              <td></td>
            </tr>
            <tr>
              <td><strong>Resource status</strong></td>
              <td>
                <select name="status">
					<xsl:choose>
                    <xsl:when test="@status='active'">
                      <option selected="yes">active</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>active</option>
                    </xsl:otherwise>
                  </xsl:choose>                
                  <xsl:choose>
                    <xsl:when test="@status='inactive'">
                      <option selected="yes">inactive</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>inactive</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="@status='deleted'">
                      <option selected="yes">deleted</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>deleted</option>
                    </xsl:otherwise>
                  </xsl:choose>
                </select>
              </td>
              <td><a href="../help/status.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Title</strong></td>
              <td>
                <input type="text" size="48" name="title">
                  <xsl:attribute name="value"><xsl:value-of select="title"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/title.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Publisher's name</strong></td>
              <td>
                <input type="text" size="48" name="curation.publisher">
                  <xsl:attribute name="value"><xsl:value-of select="curation/publisher"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/publisher.html">help</a></td>
            </tr>
            <tr>
              <td>Publisher's IVO identifier</td>
              <td>
                <input type="text" size="48" name="curation.publisher.ivo-id">
                  <xsl:attribute name="value"><xsl:value-of select="curation/publisher/@ivo-id"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/publisher.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Creator's name</strong></td>
              <td>
                <input type="text" size="48" name="curation.creator.name">
                  <xsl:attribute name="value"><xsl:value-of select="curation/creator/name"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/creator.html">help</a></td>
            </tr>
            <tr>
              <td>Creator's IVO identifier</td>
              <td>
                <input type="text" size="48" name="curation.creator.name.ivo-id">
                  <xsl:attribute name="value"><xsl:value-of select="curation/creator/name/@ivo-id"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/creator.html">help</a></td>
            </tr>
            <tr>
              <td>URL of creator's logo</td>
              <td>
                <input type="text" size="48" name="curation.logo">
                  <xsl:attribute name="value"><xsl:value-of select="curation/creator/logo"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/creator.html">help</a></td>
            </tr>
            <tr>
              <td>Release-date of resource</td>
              <td>
                <input type="text" size="48" name="curation.date">
                  <xsl:attribute name="value"><xsl:value-of select="curation/date"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/date-and-version.html">help</a></td>
            </tr>
            <tr>
              <td>Version of resource</td>
              <td>
                <input type="text" size="48" name="curation.version">
                  <xsl:attribute name="value"><xsl:value-of select="curation/version"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/date-and-version.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Name of contact person</strong></td>
              <td>
                <input type="text" size="48" name="curation.contact.name">
                  <xsl:attribute name="value"><xsl:value-of select="curation/contact/name"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/contact.html">help</a></td>
            </tr>
            <tr>
              <td>Postal address of contact person</td>
              <td>
                <input type="text" size="48" name="curation.contact.address">
                  <xsl:attribute name="value"><xsl:value-of select="curation/contact/address"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/contact.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Email address of contact person</strong></td>
              <td>
                <input type="text" size="48" name="curation.contact.email">
                  <xsl:attribute name="value"><xsl:value-of select="curation/contact/email"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/contact.html">help</a></td>
            </tr>
            <tr>
              <td>Telephone number of contact person</td>
              <td>
                <input type="text" size="48" name="curation.contact.telephone">
                  <xsl:attribute name="value"><xsl:value-of select="curation/contact/telephone"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/contact.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Keywords describing this resource</strong></td>
              <td>
                <input type="text" size="48" name="content.subject">
                  <xsl:attribute name="value"><xsl:value-of select="content/subject"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/subject.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Text describing this resource</strong></td>
              <td>
                <textarea cols="48" rows="5" name="content.description">
                  <xsl:value-of select="content/description"/>
                </textarea>
              </td>
              <td><a href="../help/description.html">help</a></td>
            </tr>
            <tr>
              <td>Source of the resource content</td>
              <td>
                <input type="text" size="48" name="content.source">
                  <xsl:attribute name="value"><xsl:value-of select="content/source"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/source.html">help</a></td>
            </tr>
            <tr>
              <td><strong>URL for web page describing this resource</strong></td>
              <td>
                <input type="text" size="48" name="content.referenceURL">
                  <xsl:attribute name="value"><xsl:value-of select="content/referenceURL"/></xsl:attribute>
                </input>
              </td>
              <td><a href="../help/referenceURL.html">help</a></td>
            </tr>
            <tr>
              <td><strong>Type of the resource content</strong></td>
              <td>
                <select name="content.type">
                  <xsl:choose>
                    <xsl:when test="content/type='Other'">
                      <option selected="yes">Other</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Other</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Archive'">
                      <option selected="yes">Archive</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Archive</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Bibliography'">
                      <option selected="yes">Bibliography</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Bibliography</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Catalog'">
                      <option selected="yes">Catalog</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Catalog</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Journal'">
                      <option selected="yes">Journal</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Journal</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Library'">
                      <option selected="yes">Library</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Library</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Simulation'">
                      <option selected="yes">Simulation</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Simulation</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Survey'">
                      <option selected="yes">Survey</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Survey</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Transformation'">
                      <option selected="yes">Transformation</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Transformation</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Education'">
                      <option selected="yes">Education</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Education</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Outreach'">
                      <option selected="yes">Outreach</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Outreach</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='EPOResource'">
                      <option selected="yes">EPOResource</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>EPOResource</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Animation'">
                      <option selected="yes">Animation</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Animation</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Artwork'">
                      <option selected="yes">Artwork</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Artwork</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Background'">
                      <option selected="yes">Background</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Background</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='BasicData'">
                      <option selected="yes">BasicData</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>BasicData</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Historical'">
                      <option selected="yes">Historical</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Historical</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Photographic'">
                      <option selected="yes">Photographic</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Photographic</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Press'">
                      <option selected="yes">Press</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Press</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Organisation'">
                      <option selected="yes">Organisation</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Organisation</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Project'">
                      <option selected="yes">Project</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Project</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/type='Registry'">
                      <option selected="yes">Registry</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Registry</option>
                    </xsl:otherwise>
                  </xsl:choose>
                </select>
              </td>
              <td><a href="../help/type.html">help</a></td>
            </tr>
            <tr>
              <td>Intended audience</td>
              <td>
                <select name="content.contentLevel">
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='Research'">
                      <option selected="yes">Research</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Research</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='General'">
                      <option selected="yes">General</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>General</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='Elementary Education'">
                      <option selected="yes">Elementary education</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Elementary education</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='Middle School Education'">
                      <option selected="yes">Middle School Education</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Middle School Education</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='Secondary Education'">
                      <option selected="yes">Secondary Education</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Secondary Education</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='Community College'">
                      <option selected="yes">Community College</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>Community College</option>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="content/contentLevel='University'">
                      <option selected="yes">University</option>
                    </xsl:when>
                    <xsl:otherwise>
                      <option>University</option>
                    </xsl:otherwise>
                  </xsl:choose>
                </select>
              </td>
              <td><a href="../help/contentLevel.html">help</a></td>
            </tr>
           	<xsl:choose>
				<xsl:when test="not($vosiURL)"> 
					<!-- parameter has not been supplied don't do anything -->
					<xsl:if test="count(capability) &lt;= 1">
						<xsl:if test="contains(@xsi:type,'Service')">
				            <tr>
				              <td>WebBrowser Capability URL <xsl:value-of select="@type"/> </td>
				              <td>
				                <input type="text" size="48" name="capability.interface.webbrowserRefURL">
				                  <xsl:attribute name="value"><xsl:value-of select="capability/interface/accessURL"/></xsl:attribute>
				                </input>
				              </td>
				              <td><a href="../help/contact.html">help</a></td>
				            </tr>
				         </xsl:if>
			         </xsl:if>					
				</xsl:when>
				<xsl:otherwise> 
					<input type="hidden" name="vosiURL">
						<xsl:attribute name="value"><xsl:value-of select="$vosiURL"/></xsl:attribute>
					</input>
				</xsl:otherwise>
			</xsl:choose>
          </table>
          <p>
            <input type="submit" value="Record this information in the registry"/>
          </p>
        </form>
        <hr/>
        <p>You may wish to open the Resource Help (see menu) in a separate window. It has help for all core and coverage information.</p>
        <p>
          Items in bold must have entries; others may be left blank and
          will not then appear in the registry entry.
        </p>
        </div>
      </body>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="text()|@*">
    <xsl:apply-templates/>
  </xsl:template>

</xsl:stylesheet>
