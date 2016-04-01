# astrogrid-registry

Code for VAMDC's registry service, reclaimed from the remains of the AstroGrid project.

To build this, use Maven 3. You need to use the legacy repository of AstroGrid artefacts at http://agdevel.jb.man.ac.uk:8080/nexus/content/repositories/public together with VAMDC's repository at http://dev.vamdc.org/nexus/content/groups/public/; these two repositories should be configured in your Maven settings.xml file.

The client-lite package is needed to build the web application, but is not recommended for other use. Instead, use VAMDC's separate registry-client project.
