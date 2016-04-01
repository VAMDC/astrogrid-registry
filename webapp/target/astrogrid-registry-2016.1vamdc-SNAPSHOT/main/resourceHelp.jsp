<%@ page session="false" %>
<!DOCTYPE HTML  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>XForms</title>
<meta http-equiv="Content-type" content="text/xhtml;charset=iso-8859-1">
<style type="text/css" media="all">
    <%@ include file="/style/astrogrid.css" %>
</style>
<%@ include file="/style/link_options.xml" %>
</head>
<body>
<%@ include file="/style/header.xml" %>
<%@ include file="/style/navigation.xml" %>
<div id='bodyColumn'>

<h1>Resource Help</h1>

<TABLE cellSpacing=1 border=1>
  <TBODY>
  <TR>
    <TD vAlign=top><A name=AuthorityID></A>AuthorityID </TD>
    <TD vAlign=top>The identifier of a namespace under the control of a single 
      naming Authority. </TD></TR>
  <TR>
    <TD vAlign=top><A name=ResourceKey></A>ResourceKey </TD>
    <TD vAlign=top>A name for the resource that is unique within an 
      Authority's namespace. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Title></A>Title </TD>
    <TD vAlign=top>A name given to the resource.<BR><BR>Typically, a Title 
      will be a name by which the resource is formally known. </TD></TR>
  <TR>
    <TD vAlign=top><A name=ShortName></A>Short Name </TD>
    <TD>A shortened name or nickname for the title of the resource. 
      <BR><BR>This is supplied for ease of reference to the resource. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Type></A>Type </TD>
    <TD vAlign=top>Nature or genre of the content of the 
      resource.<BR><BR>Enumerated values are Education, Outreach, EPOResource, 
      Animation, Artwork, Background, BasicData, Historical, Photographic, 
      Press, Organisation, Project, Person. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Description></A>Description </TD>
    <TD vAlign=top>An account of the content of the 
      resource.<BR><BR>Description may include but is not limited to: an 
      abstract, table of contents, reference to a graphical representation of 
      content or a free-text account of the content. </TD></TR>
  <TR>
    <TD vAlign=top><A name=ReferenceURL></A>Reference URL </TD>
    <TD vAlign=top>A URL pointing to additional information about the 
      resource. <BR><BR>In general, this should be a human-readable document. 
  </TD></TR>
  <TR>
    <TD vAlign=top><A name=Source></A>Source </TD>
    <TD vAlign=top>A bibliographic reference from which the present resource 
      is derived or extracted.<BR><BR>The present resource may be derived from 
      the Source in whole or in part. Recommended best practice is to use the 
      standard bibcode (see <A 
      href="http://cdsweb.u-strasbg.fr/simbad/refcode.html">http://cdsweb.u-strasbg.fr/simbad/refcode.html</A>), 
      where available. If no bibcode is available, Source should use a string or 
      number conforming to a formal identification or citation system. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Subject></A>Subject </TD>
    <TD vAlign=top>A list of the topics, object types, or other descriptive 
      keywords about the resource.<BR><BR>Subject is intended to provide 
      additional information about the nature of the<BR>information provided by 
      the resource. Is this a catalog of quasars? Of planetary nebulae? Is this 
      a tool for computing ephemerides?<BR><BR>Terms for Subject should be based 
      on the IAU Astronomy Thesaurus (<A 
      href="http://msowww.anu.edu.au/library/thesaurus/">http://msowww.anu.edu.au/library/thesaurus/</A>).<BR><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=ContentLevel></A>Content Level<BR></TD>
    <TD vAlign=top>
      <TABLE cellPadding=4 border=0>
        <TBODY>
        <TR>
          <TD colSpan=2>A description of the content level, or intended 
            audience.<BR><BR>VO resources will be available to professional 
            astronomers, amateur astronomers, educators, and the general 
            public.&nbsp; These different audiences need a way to find material 
            appropriate for their needs. </TD>
        <TR>
          <TD>ContentLevel</TD>
          <TD>Definition</TD></TR>
        <TR>
          <TD>General<BR><BR></TD>
          <TD>Resource provides information appropriate for all users</TD></TR>
        <TR>
          <TD>Elementary Education</TD>
          <TD>Resource provides information appropriate for grades K-5 
            education</TD></TR>
        <TR>
          <TD>Middle School Education</TD>
          <TD>Resource provides information appropriate for grades 6-8 
            education</TD></TR>
        <TR>
          <TD>Secondary Education</TD>
          <TD>Resource provides information appropriate for grades 9-12 
            education</TD></TR>
        <TR>
          <TD>University<BR><BR></TD>
          <TD>Resource provides information appropriate for university-level 
            education</TD></TR>
        <TR>
          <TD>Research<BR><BR><BR></TD>
          <TD>Resource provides information appropriate for professional-level 
            research and graduate school education</TD></TR>
        <TR>
          <TD>Amateur<BR><BR></TD>
          <TD>Resource provides information of interest to amateur 
          astronomers</TD></TR></TBODY></TABLE></TD></TR>
  <TR>
    <TD vAlign=top><A name=Publisher></A>Publisher </TD>
    <TD vAlign=top>An entity responsible for making the resource 
      available<BR><BR>Examples of a Publisher include a person or an 
      organization.<BR>Users of the resource should include Publisher in 
      subsequent credits and acknowledgments.<BR><BR>Select a previously 
      registered Publisher from the pulldown list if there, or fill in the 
      Publisher's Title in the field. </TD></TR>
  <TR>
    <TD vAlign=top><A name=CreatorName></A>Creator Name </TD>
    <TD vAlign=top>An entity primarily responsible for making the content of 
      the resource.<BR><BR>Examples of a Creator include a person or an 
      organization.<BR>Users of the resource should include Creator in 
      subsequent credits and acknowledgments. </TD></TR>
  <TR>
    <TD vAlign=top><A name=CreatorLogo></A>URL to Creator Logo</TD>
    <TD vAlign=top>A URL pointing to a graphical logo, which may be used to 
      help identify the information resource. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Contributor></A>Contributor </TD>
    <TD vAlign=top>An entity responsible for making contributions to the 
      content of the resource.<BR><BR>Examples of a Contributor include a person 
      or an organization.<BR>Users of the resource should include Contributor in 
      subsequent credits and acknowledgments. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Date></A>Date </TD>
    <TD vAlign=top>A date associated with an event in the life cycle of the 
      resource. <BR><BR>Typically, Date will be associated with the creation or 
      availability (i.e., most recent release or version) of the 
      resource.<BR><BR>ISO8601 is the preferred format (YYYY-MM-DD). </TD></TR>
  <TR>
    <TD vAlign=top><A name=Version></A>Version </TD>
    <TD vAlign=top>A label associated with the creation or availability (i.e., 
      most recent release or version) of the resource. </TD></TR>
  <TR>
    <TD vAlign=top><A name=ContactName></A>Contact Name </TD>
    <TD vAlign=top>The name of the contact. <BR><BR>A person's name, "John P. 
      Jones", or a group, "Archive Support Team". </TD></TR>
  <TR>
    <TD vAlign=top><A name=ContactAddress></A>Contact Address </TD>
    <TD vAlign=top>The contact mailing address. <BR><BR>All components of the 
      mailing address are given in one string, e.g. "3700 San Martin Drive, 
      Baltimore, MD 21218 USA". </TD></TR>
  <TR>
    <TD vAlign=top><A name=ContactEmail></A>Contact Email </TD>
    <TD vAlign=top>The e-mail address of the contact.<BR><BR>For example, 
      "mailto:John.P.Jones@navy.gov", or "mailto:archive@datacenter.org". </TD></TR>
  <TR>
    <TD vAlign=top><A name=ContactTelephone></A>Contact Telephone </TD>
    <TD vAlign=top>Complete international dialing codes should be given, e.g. 
      "+1-410-338-1234". </TD></TR>
  <TR>
    <TD vAlign=top><A name=Facility></A>Facility<BR></TD>
    <TD vAlign=top>The observatory or facility where the data was 
      obtained.<BR><BR>The facility may also be in the registry and should be 
      identified by it's identifier in the pull-down menu. </TD></TR>
  <TR>
    <TD vAlign=top><A name=Instrument></A>Instrument<BR></TD>
    <TD vAlign=top>The instrument used to collect the data.<BR>The instrument 
      may also be in the registry and should be identified by it's identifier in 
      the pull-down menu. <BR><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=Relationship></A>Relationship<BR></TD>
    <TD vAlign=top>A description of the relationship between one resource and 
      one or more other resources. <BR><BR>Accepted values include "mirror-of", 
      "service-for", "derived-from", and "related-to". <BR><BR>If the related 
      resource is in the registry, use the pull-down menu and select the 
      resource from the list. If not, give a title to the resource in the field. 
      <BR></TD>
  <TR>
    <TD vAlign=top><A name=Formats></A>Formats<BR></TD>
    <TD vAlign=top>The encoding format of data provided by the 
      resource.<BR><BR>Typical values would be "FITS", "ASCII text", "HTML", 
      "XML", "VOTable", "GIF", etc.&nbsp; <BR>We recommend employing MIME types 
      here in order to utilize existing standards.<BR><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=Rights></A>Rights<BR></TD>
    <TD vAlign=top>Information about rights held in and over the 
    resource.<BR></TD></TR>
  <TR>
    <TD vAlign=top bgcolor='#eeeeee'><b>Coverage</b>:</TD>
    <TD bgcolor='#eeeeee'> </TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=SpatialCoverage></A>Spatial Coverage</TD>
    <TD vAlign=top>The sky coverage of the resource.<BR><BR>The spatial 
      coverage may be described in one of three ways: 
      <TABLE cellPadding=4 border=0>
        <TBODY>
        <TR>
          <TD>All Sky</TD>
          <TD colSpan=2>The resource covers the whole sky.</TD></TR>
        <TR>
          <TD>Circle Region</TD>
          <TD colSpan=2>The spatial coverage of the resource is described as a 
            central position and a radius around that position. </TD></TR>
        <TR>
          <TD>
          <TD colSpan=2>Parameters needed for this spatial coverage 
        include:</TD></TR>
        <TR>
          <TD>
          <TD>Longitude Position</TD>
          <TD>The longitude of the central position in the described 
            coordinate frame, given in degrees.</TD></TR>
        <TR>
          <TD>
          <TD>Latitude Position</TD>
          <TD>The Longitude of the central position in the described 
            coordinate frame, given in degrees.</TD></TR>
        <TR>
          <TD>
          <TD>Radius</TD>
          <TD>The extent of the circular region in degrees. </TD></TR>
        <TR>
          <TD>Coordinate Range</TD>
          <TD colSpan=2>The spatial coverage of the resource is described as a 
            range in positions in longitude and latitude in a particular frame. 
          </TD></TR>
        <TR>
          <TD>
          <TD colSpan=2>Parameters needed for this spatial coverage 
        include:</TD></TR>
        <TR>
          <TD>
          <TD>Minimum Longitude Position</TD>
          <TD>The minimum longitude of the region in the described coordinate 
            frame, given in degrees. </TD></TR>
        <TR>
          <TD>
          <TD>Maximum Longitude Position</TD>
          <TD>The maximum longitude of the region in the described coordinate 
            frame, given in degrees. </TD></TR>
        <TR>
          <TD>
          <TD>Minimum Latitude Position</TD>
          <TD>The minimum latitude of the region in the described coordinate 
            frame, given in degrees. </TD></TR>
        <TR>
          <TD>
          <TD>Maximum Latitude Position</TD>
          <TD>The maximum latitude of the region in the described coordinate 
            frame, given in degrees. </TD></TR>
        <TR>
          <TD><A name=CoordinateFrame></A>Coordinate Frame </TD>
          <TD colSpan=2>The Coordinate Frames supported include:</TD></TR>
        <TR>
          <TD>
          <TD>Coordinate Frame</TD>
          <TD>Description</TD></TR>
        <TR>
          <TD>
          <TD>ICRS</TD>
          <TD>International Celestial Reference System</TD></TR>
        <TR>
          <TD>
          <TD>FK5</TD>
          <TD>Equatorial coordinates, FK5 system (J2000)</TD></TR>
        <TR>
          <TD>
          <TD>FK4</TD>
          <TD>Equatorial coordinates, FK4 system (B1950)</TD></TR>
        <TR>
          <TD>
          <TD>Ecliptic</TD>
          <TD>Ecliptic coordinates (J2000)</TD></TR>
        <TR>
          <TD>
          <TD>Galactic</TD>
          <TD>Galactic coordinates (J2000)</TD></TR>
        <TR>
          <TD>
          <TD>SuperGalactic</TD>
          <TD>Supergalactic coordinates (J2000)</TD></TR>
        <TR>
          <TD><A name=SpatialResolution></A>Spatial Resolution</TD>
          <TD colSpan=2>The spatial (angular) resolution that is typical of 
            the observations of interest, in decimal degrees.</TD></TR>
        <TR>
          <TD><A name=RegionofRegard></A>Region of Regard</TD>
          <TD colSpan=2>The intrinsic size scale, given in arcseconds, 
            associated with data items contained in a resource.</TD></TR>
        </TBODY></TABLE></TD>
  <TR>
    <TD vAlign=top><A name=SpectralDescription></A>Spectral<BR></TD>
    <TD vAlign=top>The spectral coverage of the resource.<BR><BR>
      <TABLE cellPadding=4 border=0>
        <TBODY>
        <TR>
          <TD vAlign=top><A name=Waveband></A>Waveband<BR></TD>
          <TD>Spectral coverage at the resource level will be in terms of 
            general spectral regions (gamma-ray, x-ray, extreme UV, UV, optical, 
            infrared, millimeter, radio). <BR><BR>The general spectral regions 
            are defined specifically as follows: <BR>
            <TABLE cellPadding=4 border=0>
              <TBODY>
              <TR>
                <TD>Coverage.Spectral</TD>
                <TD>Represents</TD></TR>
              <TR>
                <TD>Radio<BR><BR></TD>
                <TD>&#955; &#8805; 100 µ<BR>&nbsp;&#957; &#8804; 3000 GHz</TD></TR>
              <TR>
                <TD>Millimeter<BR><BR></TD>
                <TD>0.1 mm &#8804; &#955; &#8804; 10 mm<BR>3000 GHz &#8805; &#957; &#8805; 30 GHz</TD></TR>
              <TR>
                <TD>Infrared</TD>
                <TD>1 µ &#8804; &#955; &#8804; 100 µ</TD></TR>
              <TR>
                <TD>Optical<BR><BR><BR></TD>
                <TD>0.3 µ &#8804; &#955; &#8804; 1 µ<BR>300 nm &#8804; &#955; &#8804; 1000 nm<BR>3000 Å &#8804; &#955; &#8804; 
                  10000 Å </TD></TR>
              <TR>
                <TD>UV<BR><BR></TD>
                <TD>0.1 µ &#8804; &#955; &#8804; 0.3 µ<BR>1000 Å &#8804; &#955; &#8804; 3000 Å</TD></TR>
              <TR>
                <TD>EUV<BR><BR></TD>
                <TD>100 Å &#8804; &#955; &#8804; 1000 Å<BR>12 eV &#8804; E &#8804; 120 eV</TD></TR>
              <TR>
                <TD>X-ray<BR><BR></TD>
                <TD>0.1 Å &#8804; &#955; &#8804; 100 Å<BR>0.12 keV &#8804; E &#8804; 120 keV</TD></TR>
              <TR>
                <TD>Gamma-ray</TD>
                <TD>E &#8805; 120 keV</TD></TR></TBODY></TABLE><BR>Resources containing 
            data in multiple spectral regions may give a list (e.g., "Radio, 
            Infrared").&nbsp; <BR><BR></TD></TR>
        <TR>
          <TD vAlign=top><A name=WavelengthRange>Wavelength Range </A></TD>
          <TD>A range of the electro-magnetic spectrum specified by a lower 
            and upper wavelength limit. </TD></TR>
        <TR>
          <TD vAlign=top><A name=SpectralResolution>Spectral Resolution 
</A></TD>
          <TD>The spectral resolution that is typical of the observations of 
            interest, given as a ratio of the wavelength width (delta-lambda) to 
            the observing wavelength (lambda). </TD></TR></TBODY></TABLE></TD>
  <TR>
    <TD vAlign=top><A name=Temporal></A>Temporal<BR></TD>
    <TD vAlign=top>The temporal coverage of the resource.<BR><BR>Temporal 
      coverage specifications will be given in years, with decimal years 
      permitted.<BR>&nbsp; Ranges are specified with a hyphen, e.g., "1987-1993" 
      or "1998.275- ".&nbsp; Disjoint time <BR>spans may be given as a list, 
      e.g., "1981-1984, 1987-1990".<BR><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=TemporalResolution></A>Temporal Resolution<BR></TD>
    <TD vAlign=top>The temporal resolution that is typical of the observations 
      of interest, in seconds. </TD></TR>
  <TR>
    <TD vAlign=top bgcolor='#eeeeee'><b>Service Based Parameters</b>:</TD>
    <TD bgcolor='#eeeeee'> </TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=ImageServiceType></A>Image Service Type<BR></TD>
    <TD vAlign=top>The class of image service.<BR><BR>May be one of: Cutout, 
      Mosaic, Atlas, or Pointed<BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxRegionSize></A>Maximum Query Region Size<BR></TD>
    <TD vAlign=top>The maximum image query region size, expressed in decimal 
      degrees, both in the RA and DEC directions.<BR><BR>Both values should be 
      filled in.<BR><BR>A value of 360 degrees indicates there is no limit and 
      the entire data collection (entire sky) can be queried.<BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxImExtent></A>Maximum Image Extent<BR></TD>
    <TD vAlign=top>The maximum image extent, expressed in decimal degress, 
      both in RA and DEC directions.<BR><BR>Both values should be filled 
    in.<BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxImSize></A>Maximum Image Size<BR></TD>
    <TD vAlign=top>The largest image that can be requested, given in integer 
      degrees, both in the RA and DEC directions.<BR><BR>Both values should be 
      filled in.<BR><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxFSize></A>Maximum File Size<BR></TD>
    <TD vAlign=top>The maximum image size given in bytes.<BR></TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxRec></A>Maximum Number of Records to 
    Return<BR></TD>
    <TD vAlign=top>The largest number of records that the Image Query will 
      return.<BR></TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=MaxSR></A>Maximum Search Radius<BR></TD>
    <TD vAlign=top>The largest search radius, in degrees, that will be 
      accepted by the service without returning an error condition. <BR></TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=Verbosity></A>Verbosity<BR></TD>
    <TD vAlign=top>A boolean that is true if the service supports the VERB 
      keyword; false otherwise.<BR></TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=BaseURL></A>Base URL<BR></TD>
    <TD vAlign=top>A URL that points to a document that presents or describes 
      a service interface.<BR></TD>
    <TD vAlign=top><BR></TD></TR>
  <TR>
    <TD vAlign=top><A name=Parameter></A>Parameter<BR></TD>
    <TD vAlign=top>A keyword used as input to a CGI-Get 
      Service.<BR><BR>Parameters may be required or optional, and should be 
      typed by the format of the input for this keyword. </TD></TR></TBODY></TABLE>
  </div>
<%@ include file="/style/footer.xml" %>
</body>
</html>
