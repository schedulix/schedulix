#
# $Id: functions.sh,v 1.7 2013/02/05 10:26:00 ronald Exp $
#
# Copyright(C) 2008 independIT Integrative Technologies GmbH
#
if [ -z "$BICSUITECONFIG" ]; then
	BICSUITECONFIG=$BICSUITEHOME/etc
fi

. $BICSUITECONFIG/java.conf || exit 1
. $BICSUITECONFIG/bicsuite.conf || exit 1

SDMSTIMEOUT=${SDMSTIMEOUT:-15}
BICSUITEGREP=${BICSUITEGREP:-grep}
BICSUITEKILL=${BICSUITEKILL:-kill}
BICSUITEFUSER=${BICSUITEFUSER:-fuser}
BICSUITEMKFIFO=${BICSUITEMKFIFO:-mkfifo}
SCROLLLOG=$BICSUITEHOME/bin/scrolllog
SDMSH="$BICSUITEHOME/bin/sdmsh"
SDMSUSER=SYSTEM
SDMSPASS=""
SDMSHOST=""
SDMSPORT=""

export SDMSTIMEOUT BICSUITEGREP BICSUITEKILL BICSUITEFUSER BICSUITEMKFIFO SCROLLLOG SDMSH SDMSUSER SDMSPASS SDMSHOST SDMSPORT


ensure_pipe()
{
	LOGFILE=$1
	if [ "x$1" = "x" ]; then
		echo "Logfile not specified"
		exit 1
	fi
	if [ ! -p $LOGFILE ]; then
		rm -f $LOGFILE		# just in case
		if $BICSUITEMKFIFO $LOGFILE; then
			: do nothing
		else
			echo "Can't create the named pipe $LOGFILE"
			exit 1
		fi
	fi
}

which()
{
	for D in `echo $PATH | sed 's/:/ /g'`; do	# we don't expect blanks in PATH...
		if [ -x $D/$1 ]; then
			echo $D/$1
			exit 0
		fi
	done
	echo $1
	exit 1
}

parse_server_conf()
{
	SVRCNF=$BICSUITECONFIG/server.conf
	if [ ! -r $SVRCNF ]; then
		echo "No read access to $SVRCNF"
		exit 1
	fi
	SDMSPASS=`$BICSUITEGREP -e "^ *SysPasswd" $SVRCNF | sed 's/^ *SysPasswd *[:=] *//'`
	SDMSHOST=`$BICSUITEGREP -e "^ *Hostname" $SVRCNF | sed 's/^ *Hostname *[:=] *//'`
	SDMSPORT=`$BICSUITEGREP -e "^ *Port" $SVRCNF | sed 's/^ *Port *[:=] *//'`
	SDMSDBUSR=`$BICSUITEGREP -e "^ *DbUser" $SVRCNF | sed 's/^ *DbUser *[:=] *//'`
	SDMSDBPWD=`$BICSUITEGREP -e "^ *DbPasswd" $SVRCNF | sed 's/^ *DbPasswd *[:=] *//'`
	SDMSDATA=`$BICSUITEGREP -e "^ *DbUrl" $SVRCNF | sed 's/.*[:\/@]\([^:;\/@]*\);* *$/\1/'`
	SDMSDBMS=`$BICSUITEGREP -e "^ *DbUrl" $SVRCNF | sed 's/.*jdb[cs]:\([^:]*\):.*/\1/'`
}
