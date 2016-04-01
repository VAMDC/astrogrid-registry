#!/bin/bash
# -----------------------------------------------------------------------------
# startup.sh - Start Script for Jetty + eXist
#
# $Id: startup.sh,v 1.6 2002/12/28 17:37:22 wolfgang_m Exp $
# -----------------------------------------------------------------------------

#Run this in the WEB-INF directory.
## **Be sure to set a permanent/temporary variable of EXIST_HOME that points to the directory where 
## **your "conf.xml" is located if this is a embedded/internal registry.
##The below commands you may add a "-u {username} -p {password}" for username and password to the db, typically registry eXists do
##not require a username or password, gui mode will ask for it, which you can use the default "admin" username with no password.
##Unless you setup certain usernames and passwords.

#if your using embedded/internal eXist
#shell mode
#./client.sh -s -ouri=xmldb:exist://
#gui mode
#./client.sh  -ouri=xmldb:exist://

#if your connecting to an external eXist
#shell mode
#./client.sh -s -ouri=xmldb:exist://host:port/exist/xmlrpc
#gui mode
#./client.sh -ouri=xmldb:exist://host:port/exist/xmlrpc



exist_home () {
	case "$0" in
		/*)
			p=$0
		;;
		*)
			p=`/bin/pwd`/$0
		;;
	esac
		(cd `/usr/bin/dirname $p` ; /bin/pwd)
}

if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME_1=`exist_home`
	EXIST_HOME="$EXIST_HOME_1/.."
fi

if [ ! -f "./start.jar" ]; then
	echo "Unable to find start.jar. Please run this script in the WEB-INF directory."
	exit 1
fi

OPTIONS="-Dexist.home=$EXIST_HOME"

# set java options
if [ -z "$JAVA_OPTIONS" ]; then
    export JAVA_OPTIONS="-Xms64m -Xmx256m -Dfile.encoding=UTF-8"
fi

JAVA_ENDORSED_DIRS=/lib

JAVA_OPTIONS="$JAVA_OPTIONS -Djava.endorsed.dirs=$JAVA_ENDORSED_DIRS"

# save LANG
if [ -n "$LANG" ]; then
	OLD_LANG="$LANG"
fi
# set LANG to UTF-8
LANG=en_US.UTF-8

# save LD_LIBRARY_PATH
if [ -n "$LD_LIBRARY_PATH" ]; then
	OLD_LIBRARY_PATH="$LD_LIBRARY_PATH"
fi
# add lib/core to LD_LIBRARY_PATH for readline support
export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$EXIST_HOME/lib"

$JAVA_HOME/bin/java $JAVA_OPTIONS $OPTIONS -jar "start.jar" client $*

if [ -n "$OLD_LIBRARY_PATH" ]; then
	LD_LIBRARY_PATH="$OLD_LIBRARY_PATH"
fi
if [ -n "$OLD_LANG" ]; then
	LANG="$OLD_LANG"
fi
