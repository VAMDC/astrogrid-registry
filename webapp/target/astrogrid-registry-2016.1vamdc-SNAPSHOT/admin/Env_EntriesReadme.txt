The JSPs in this directory can be used to manage the configuration of an AstroGrid
component via its J2EE environment-entries (the configuration keys declared in
web.xml). 

You should copy the JSPs into the source tree of the web application where you need them to work. You may want or need to customize the JSPs.


To make the JSPs work, you need:

 - the servlet and Java bean classes in org.astrogrid.common.j2ee.environment;

 - an implementation of the Java standard tag library.


See the package description, in Javadocs, for org.astrogrid.common.j2ee.environment
for information on how this sub-system works.