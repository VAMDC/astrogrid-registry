#!/bin/bash
# -----------------------------------------------------------------------------
# backup.sh - Backup tool start script
#
# $Id: startup.sh,v 1.6 2002/12/28 17:37:22 wolfgang_m Exp $
# -----------------------------------------------------------------------------

# Run this in the WEB-INF directory.
# Backing up Info:
# **Be sure to set a permanent/temporary variable of EXIST_HOME that points to the directory where 
# **your "conf.xml" is located if this is an embedded/internal registry.
# Keep in mind this will backup all the information in the eXist database as xml files on your harddisk.  You can optionally
# backup your data directory as a means of restoring.
# Command:
# ./backupandrestore.sh -d backup -u admin -b /db  -ouri=xmldb:exist://

# Break Down:
# -d backup = {output directory ex: c:\backup}
# -u admin =  {username}
# -p somepass = {password - optional and typically empty/blank in default registry config}
# -b /db  {collection/directories in eXist to backup, /db is all, another ex: /db/astrogridv0_10}
# -ouri=xmldb:exist://  {local/internal mode, external would be "xmldb:exist://host:port/exist/xmlrpc"}

# Restore Info:
# When backing up for each collection/directory in eXist a __contents__.xml file is made this  is waht is used for restoring.
# Command:
# ./backupandrestore.sh -r backup/db/__contents__.xml -u admin -ouri=xmldb:exist://
# BreakDown:
# -r backup/db/__contents__.xml {contents file to restore, you can also restore subcollection if need be ex: /db/astrogridv0_10/__contents.xml__}
# {everything else is the same as backup (see above)}

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

unset LANG
OPTIONS=

if [ -z "$EXIST_HOME" ]; then
	EXIST_HOME_1=`exist_home`
	EXIST_HOME="$EXIST_HOME_1/.."
fi

if [ ! -f "./start.jar" ]; then
	echo "Unable to find start.jar. Please set EXIST_HOME to point to your installation directory."
	exit 1
fi

OPTIONS="-Dexist.home=$EXIST_HOME"

# set java options
if [ -z "$JAVA_OPTIONS" ]; then
    export JAVA_OPTIONS="-Xms32000k -Xmx256000k -Dfile.encoding=UTF-8 -Djava.endorsed.dirs=/lib"
fi

$JAVA_HOME/bin/java $JAVA_OPTIONS $OPTIONS -jar "start.jar" backup $*
