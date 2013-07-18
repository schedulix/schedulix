#!/bin/sh 
if [ $# -lt 1 ]; then
	SERVERS="localhost host_1 host_2"
else
	SERVERS="$*"
fi

if [ -z "$BICSUITECONFIG" ]; then
	BICSUITECONFIG=$BICSUITEHOME/etc
fi

cd $BICSUITEHOME/..
HOMEDIR=`pwd`

. $BICSUITECONFIG/bicsuite.conf

for JS in $SERVERS
do
	if $BICSUITEFUSER $HOMEDIR/log/$JS.out >/dev/null 2>/dev/null
	then
		echo "Stopping Jobserver $JS"
		$BICSUITEFUSER -k $HOMEDIR/log/$JS.out >/dev/null 2>/dev/null
	else
		echo "Jobserver $JS not running"
	fi
done
