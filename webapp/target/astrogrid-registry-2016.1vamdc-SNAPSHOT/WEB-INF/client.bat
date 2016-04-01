@echo off

rem Run this in the WEB-INF directory.
rem **Be sure to set a permanent/temporary variable of EXIST_HOME that points to the directory where 
rem **your "conf.xml" is located if this is a embedded/internal registry.
rem The below commands you may add a "-u {username} -p {password}" for username and password to the db, typically registry eXists do
rem not require a username or password, gui mode will ask for it, which you can use the default "admin" username with no password.
rem Unless you setup certain usernames and passwords.

rem if your using embedded/internal eXist
rem shell mode
rem client.bat -s -ouri=xmldb:exist://
rem gui mode
rem client.bat  -ouri=xmldb:exist://

rem if your connecting to an external eXist
rem shell mode
rem client.bat -s -ouri=xmldb:exist://host:port/exist/xmlrpc
rem gui mode
rem client.bat -ouri=xmldb:exist://host:port/exist/xmlrpc


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

%JAVA_HOME%\bin\java -Xms16000k -Xmx256000k -Dfile.encoding=UTF-8 -Djava.endorsed.dirs=\lib  -Dexist.home="%EXIST_HOME%" -jar "start.jar" client %1 %2 %3 %4 %5 %6 %7 %8 %9
:eof

