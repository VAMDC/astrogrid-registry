@echo off

rem Run this in the WEB-INF directory.
rem Backing up Info:
rem **Be sure to set a permanent/temporary variable of EXIST_HOME that points to the directory where 
rem **your "conf.xml" is located if this is a embedded/internal registry.
rem  Keep in mind this will backup all the information in the eXist database as xml files on your harddisk.  You can optionally
rem backup your 'data' directory as a means of restoring. And is normally much quicker to restore with the data directory.
rem Command:
rem backupandrestore.bat -d backup -u admin -b /db  -ouri=xmldb:exist://

rem Break Down:
rem -d backup = {output directory ex: c:\backup}
rem -u admin =  {username}
rem -p somepass = {password - optional and typically empty/blank in default registry config}
rem -b /db  {collection/directories in eXist to backup, /db is all, another ex: /db/astrogridv0_10}
rem -ouri=xmldb:exist://  {local/internal mode, external would be "xmldb:exist://host:port/exist/xmlrpc"}

rem Restore Info:
rem When backing up for each collection/directory in eXist a __contents__.xml file is made this  is what is used for restoring.
rem Command:
rem backupandrestore.bat -r backup/db/__contents__.xml -u admin -ouri=xmldb:exist://
rem BreakDown:
rem -r backup/db/__contents__.xml {contents file to restore, you can also restore subcollection if need be ex: /db/astrogridv0_10/__contents.xml__}
rem {everything else is the same as backup (see above)}

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo Java environment not found. Please set
echo your JAVA_HOME environment variable to
echo the home of your JDK.
goto :eof

:gotJavaHome
if not "%EXIST_HOME%" == "" goto gotExistHome

rem try to guess
set EXIST_HOME=.
if exist .\start.jar goto gotExistHome
set EXIST_HOME=..
if exist %EXIST_HOME%\start.jar goto gotExistHome

echo EXIST_HOME not found. Please set your
echo EXIST_HOME environment variable to the
echo home directory of eXist.
goto :eof

:gotExistHome

%JAVA_HOME%\bin\java -Xms16000k -Xmx256000k -Dfile.encoding=UTF-8 -Djava.endorsed.dirs=\lib  -Dexist.home="%EXIST_HOME%" -jar "start.jar" backup %1 %2 %3 %4 %5 %6 %7 %8 %9
:eof

